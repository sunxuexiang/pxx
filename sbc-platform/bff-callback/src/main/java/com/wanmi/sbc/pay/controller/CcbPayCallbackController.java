package com.wanmi.sbc.pay.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.advertising.api.provider.AdActivityProvider;
import com.wanmi.sbc.advertising.api.request.activity.AdRefundCallbackRequest;
import com.wanmi.sbc.advertising.bean.constant.AdConstants;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.order.api.provider.refund.RefundOrderProvider;
import com.wanmi.sbc.pay.api.constant.CcbPayConstants;
import com.wanmi.sbc.pay.api.provider.CcbPayProvider;
import com.wanmi.sbc.pay.api.response.CcbPayRecordResponse;
import com.wanmi.sbc.pay.service.CcbPayCallbackService;
import com.wanmi.sbc.returnorder.api.provider.refundfreight.RefundFreightProvider;
import com.wanmi.sbc.returnorder.api.request.refundfreight.RefundFreightCallbackRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/6/13 10:19
 */
@RestController
@RequestMapping("/ccb/callback")
@Slf4j
public class CcbPayCallbackController {

    @Autowired
    private CcbPayProvider ccbPayProvider;

    @Autowired
    private RefundOrderProvider refundOrderProvider;

    @Autowired
    private AdActivityProvider adActivityProvider;

    @Autowired
    private RefundFreightProvider refundFreightProvider;

    @Autowired
    private CcbPayCallbackService ccbPayCallbackService;

    @Autowired
    private RedissonClient redissonClient;


    /**
     * 建行支付通知
     * @param request
     * @return
     */
    @RequestMapping(value = "/pay/notify", method = {RequestMethod.POST, RequestMethod.GET})
    public Map<String, Object> ccbCallback(@RequestBody String request) {
        log.info("建行支付通知：{}", JSON.toJSONString(request));
        Map<String, Object> result = new HashMap<>();

        try {
            JSONObject obj = JSON.parseObject(request);
            // 主订单编号
            String out_trade_no = obj.getString("Main_Ordr_No");
            // 支付流水号
            String pyTrnNo = obj.getString("Py_Trn_No");
            // 支付金额
            String total_amount = obj.getString("Ordr_Amt");
            // 订单状态代码 2成功 3失败 4失效
            String ordr_stcd = obj.getString("Ordr_Stcd");
            // 支付时间 yyyyMMddHHmmssSSS
            String pay_time = obj.getString("Pay_Time");

            Boolean verifySign = ccbPayProvider.verifySign(request).getContext();
            if (!verifySign) {
                log.info("建行支付通知验签失败：{}", JSON.toJSONString(request));
                result.put("Svc_Rsp_St", "01");
                return result;
            }

            if (Objects.equals("2", ordr_stcd)) {
                RLock rLock = redissonClient.getFairLock(CcbPayConstants.CCB_PAY_STATUS_LOCK_KEY + pyTrnNo);
                rLock.lock();
                try {
                    CcbPayRecordResponse ccbPayRecordResponse = ccbPayProvider.queryCcbPayRecordByPyTrnNo(pyTrnNo).getContext();
                    if (Objects.isNull(ccbPayRecordResponse)) {
                        log.info("建行支付通知,支付流水号:{},支付记录不存在,不再处理", pyTrnNo);
                        result.put("Svc_Rsp_St", "00");
                        return result;
                    }
                    if (Objects.equals(1, ccbPayRecordResponse.getStatus())) {
                        log.info("建行支付通知,支付流水号:{},订单状态已支付,不再处理", pyTrnNo);
                        result.put("Svc_Rsp_St", "00");
                        return result;
                    }
                    ccbPayCallbackService.ccbPayStatusSuccess(request, pyTrnNo);
                } finally {
                    rLock.unlock();
                }

            }
        } catch (Exception e) {
            log.error("建行支付通知失败:", e);
            result.put("Svc_Rsp_St", "01");
            return result;
        }
        result.put("Svc_Rsp_St", "00");
        return result;
    }




    /**
     * 建行退款通知 建行惠市宝确认没有退款回调,只有在延迟退款的时候才有回调 20230721
     * @param request
     * @return
     */
    @RequestMapping(value = "/refund/notify", method = {RequestMethod.POST, RequestMethod.GET})
    public Map<String, Object> refundCallback(@RequestBody String request) {
        log.info("建行退款通知：{}", JSON.toJSONString(request));
        Map<String, Object> result = new HashMap<>();
        result.put("Ittparty_Tms", DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmssSSS"));
        try {
            Boolean verifySign = ccbPayProvider.verifySign(request).getContext();
            if (!verifySign) {
                result.put("Svc_Rsp_St", "01");
                return result;
            }

            JSONObject obj = JSON.parseObject(request);
            // 退款响应状态 00-退款成功 01-退款失败
            String refund_rsp_st = obj.getString("Refund_Rsp_St");
            // 发起方时间戳 yyyyMMddHHmmssfff
            String ittparty_tms = obj.getString("Ittparty_Tms");
            // 发起方流水号
            String ittparty_jrnl_no = obj.getString("Ittparty_Jrnl_No");
            // 支付流水号
            String py_trn_no = obj.getString("Py_Trn_No");
            // 客户方退款流水号
            String cust_rfnd_trcno = obj.getString("Cust_Rfnd_Trcno");
            // 退款流水号
            String super_refund_no = obj.getString("Super_Refund_No");
            // 退款金额
            String rfnd_amt = obj.getString("Rfnd_Amt");
            // 退款响应信息
            String refund_rsp_inf = obj.getString("Refund_Rsp_Inf");

            String[] ts = cust_rfnd_trcno.split("T");
            if (ts.length > 1) {
                cust_rfnd_trcno = ts[0];
            }

            Boolean refunded = false;
            if (Objects.equals("00", refund_rsp_st)) {
                refunded = true;
                //  建行退款通知成功
                ccbPayProvider.ccbRefundSuccess(cust_rfnd_trcno);
            }
            String businessId = ccbPayProvider.queryBusinessIdByPyTrnNo(py_trn_no).getContext();
            if (Objects.nonNull(businessId)) {
                if (cust_rfnd_trcno.startsWith(AdConstants.AD_ACTIVITY_ID_PREFIX)) {
                    // 广告充值退款
                    adActivityProvider.adRefundCallback(AdRefundCallbackRequest
                            .builder()
                            .refundNo(cust_rfnd_trcno)
                            .refundStatus(refunded ? 1 : 3)
                            .failedMsg(refund_rsp_inf)
                            .build());
                } else if (cust_rfnd_trcno.startsWith("F")){
                    // 退运费
                    refundFreightProvider.callback(RefundFreightCallbackRequest
                            .builder()
                            .refundStatus(refunded ? 1 : 3)
                            .rid(cust_rfnd_trcno)
                            .build());
                } else if (cust_rfnd_trcno.startsWith("E")){
                    // 退运费加收
                    refundFreightProvider.extraCallback(RefundFreightCallbackRequest
                            .builder()
                            .refundStatus(refunded ? 1 : 3)
                            .rid(cust_rfnd_trcno)
                            .build());
                } else {
                    refundOrderProvider.refundOrderSuccess(businessId, cust_rfnd_trcno, refunded, refund_rsp_inf);
                }
            }else {
                log.info("建行退款通知,支付记录不存在，建行支付流水号：{}", py_trn_no);
            }

        } catch (Exception e) {
            log.error("建行退款通知失败:", e);
            result.put("Svc_Rsp_St", "01");
            return result;
        }
        result.put("Svc_Rsp_St", "00");
        return result;
    }



    /**
     * 建行商家信息通知
     * @param request
     * @return
     */
    @RequestMapping(value = "/business/notify", method = {RequestMethod.POST, RequestMethod.GET})
    public Map<String, Object> businessNotify(@RequestBody String request) {
        log.info("建行商家信息通知：{}", JSON.toJSONString(request));
        Map<String, Object> result = new HashMap<>();

        try {
            Boolean verifySign = ccbPayProvider.verifySign(request).getContext();
            if (!verifySign) {
                result.put("Svc_Rsp_St", "01");
                return result;
            }
            ccbPayProvider.businessNotify(request);
        } catch (Exception e) {
            log.error("建行商家信息通知:", e);
            result.put("Svc_Rsp_St", "01");
            return result;
        }
        result.put("Svc_Rsp_St", "00");
        return result;
    }

    /**
     * 查询建行商户信息
     * @return
     */
    @GetMapping("/business/query")
    public BaseResponse businessQuery() {
        return ccbPayProvider.businessQuery();
    }


    /**
     * 建行分账规则通知 不需要分账规则
     * @param request
     * @return
     */
    @RequestMapping(value = "/rule/notify", method = {RequestMethod.POST, RequestMethod.GET})
    public Map<String, Object> ruleNotify(@RequestBody String request) {
        log.info("建行分账规则通知：{}", JSON.toJSONString(request));
        Map<String, Object> result = new HashMap<>();

        try {
            Boolean verifySign = ccbPayProvider.verifySign(request).getContext();
            if (!verifySign) {
                result.put("Svc_Rsp_St", "01");
                return result;
            }
            ccbPayProvider.ruleNotify(request);
        } catch (Exception e) {
            log.error("建行分账规则通知:", e);
            result.put("Svc_Rsp_St", "01");
            return result;
        }
        result.put("Svc_Rsp_St", "00");
        return result;
    }

    /**
     * 查询建行商户分账规则
     * @return0
     */
    @GetMapping("/rule/query")
    public BaseResponse ruleQuery() {
        return ccbPayProvider.ruleQuery();
    }

    @Bean(name = "multipartResolver")
    public StandardServletMultipartResolver getStandardServletMultipartResolver(){
        return new StandardServletMultipartResolver();
    }


    /**
     * 接收建行zip文件
     * @param request
     * @param response
     * @throws UnsupportedEncodingException
     */
    @PostMapping(value = "/receive/files", produces = "multipart/form-data; charset=utf-8")
    public void receiveDivsionFiles(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        log.info("接收到建行文件的参数: [File_Smry_Inf = " + request.getParameter("File_Smry_Inf") + ", Sign_Inf = " + request.getParameter("Sign_Inf"));
        //验签原串
        String oriString = "File_Smry_Inf=" + request.getParameter("File_Smry_Inf");
        String signInf = request.getParameter("Sign_Inf");

        Boolean verifySign = ccbPayProvider.verifySign2(oriString, signInf).getContext();

        if(!verifySign) {
            log.error("接收建行文件验签失败");
            return;
        }
        //存放路径
        String dstPath = "/data/ccb/receive";
        //设置编码格式
        request.setCharacterEncoding("utf-8");
        //判断是否有文件
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if(isMultipart) {
            StandardMultipartHttpServletRequest req = (StandardMultipartHttpServletRequest) request;
            Iterator<String> iterator = req.getFileNames();
            while(iterator.hasNext()) {
                try {
                    MultipartFile file = req.getFile(iterator.next());
                    String fileName = file.getOriginalFilename();
                    log.info("接收到建行文件，保存本地，文件名：{}", fileName);

                    // 获取文件输入流
                    InputStream inputStream = file.getInputStream();
                    String filePath = dstPath + "/" + fileName;
                    File desFile = new File(filePath);
                    if(!desFile.getParentFile().exists()) {
                        desFile.getParentFile().mkdirs();
                    }
                    // 创建输出流，将文件保存到指定路径
                    OutputStream outputStream = Files.newOutputStream(Paths.get(filePath));
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    // 关闭输入流和输出流
                    inputStream.close();
                    outputStream.close();

                } catch(Exception e) {
                    log.error("接收到建行文件保存失败:", e);
                }
            }

        }
    }
}
