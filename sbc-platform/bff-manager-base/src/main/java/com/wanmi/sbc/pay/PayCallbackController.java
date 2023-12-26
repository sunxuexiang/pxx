package com.wanmi.sbc.pay;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.chinapay.secss.SecssUtil;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.github.pagehelper.util.StringUtil;
import com.pingplusplus.model.*;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.wanmi.sbc.account.api.provider.wallet.*;
import com.wanmi.sbc.account.api.request.wallet.*;
import com.wanmi.sbc.account.bean.vo.CustomerWalletVO;
import com.wanmi.sbc.account.bean.vo.VirtualGoodsVO;
import com.wanmi.sbc.account.bean.vo.WalletRecordVO;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.constant.ErrorCodeConstant;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.Platform;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.GeneratorService;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.address.CustomerDeliveryAddressProvider;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponActivityProvider;
import com.wanmi.sbc.marketing.api.request.coupon.SendCouponRechargeRequest;
import com.wanmi.sbc.marketing.api.response.coupon.GetCouponGroupResponse;
import com.wanmi.sbc.order.api.provider.paycallbackresult.PayCallBackResultProvider;
import com.wanmi.sbc.order.api.provider.payorder.PayOrderQueryProvider;
import com.wanmi.sbc.order.api.provider.pickuprecord.PickUpRecordProvider;
import com.wanmi.sbc.order.api.provider.refund.RefundOrderQueryProvider;
import com.wanmi.sbc.order.api.provider.returnorder.ReturnOrderProvider;
import com.wanmi.sbc.order.api.provider.returnorder.ReturnOrderQueryProvider;
import com.wanmi.sbc.order.api.provider.returnorder.ReturnPileOrderProvider;
import com.wanmi.sbc.order.api.provider.returnorder.ReturnPileOrderQueryProvider;
import com.wanmi.sbc.order.api.provider.trade.PileTradeProvider;
import com.wanmi.sbc.order.api.provider.trade.PileTradeQueryProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.order.api.request.paycallbackresult.PayCallBackResultAddRequest;
import com.wanmi.sbc.order.api.request.paycallbackresult.PayCallBackResultModifyResultStatusRequest;
import com.wanmi.sbc.order.api.request.pickuprecord.PickUpRecordAddRequest;
import com.wanmi.sbc.order.api.request.refund.RefundOrderByReturnOrderCodeRequest;
import com.wanmi.sbc.order.api.request.returnorder.ReturnOrderByIdRequest;
import com.wanmi.sbc.order.api.request.returnorder.ReturnOrderOnlineRefundRequest;
import com.wanmi.sbc.order.api.request.trade.*;
import com.wanmi.sbc.order.api.response.refund.RefundOrderByReturnCodeResponse;
import com.wanmi.sbc.order.api.response.trade.TradeGetByIdResponse;
import com.wanmi.sbc.order.api.response.trade.TradeGetPayOrderByIdResponse;
import com.wanmi.sbc.order.bean.dto.*;
import com.wanmi.sbc.order.bean.enums.FlowState;
import com.wanmi.sbc.order.bean.enums.PayCallBackResultStatus;
import com.wanmi.sbc.order.bean.enums.PayCallBackType;
import com.wanmi.sbc.order.bean.enums.PayState;
import com.wanmi.sbc.order.bean.vo.PayOrderVO;
import com.wanmi.sbc.order.bean.vo.ReturnOrderVO;
import com.wanmi.sbc.order.bean.vo.TradeVO;
import com.wanmi.sbc.pay.api.provider.*;
import com.wanmi.sbc.pay.api.request.*;
import com.wanmi.sbc.pay.api.response.*;
import com.wanmi.sbc.pay.bean.enums.PayGatewayEnum;
import com.wanmi.sbc.pay.bean.enums.TradeStatus;
import com.wanmi.sbc.pay.bean.enums.TradeType;
import com.wanmi.sbc.pay.bean.vo.PayChannelItemVO;
import com.wanmi.sbc.pay.weixinpaysdk.WXPayConstants;
import com.wanmi.sbc.pay.weixinpaysdk.WXPayUtil;
import com.wanmi.sbc.util.DateUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import com.wanmi.sbc.wallet.api.request.wallet.TradePayWalletOnlineCallBackRequest;
import com.wanmi.sbc.wallet.bean.enums.PayWalletCallBackType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

/**
 * 交易回调
 * Created by sunkun on 2017/8/8.
 */
@Api(tags = "PayCallbackController", description = "交易回调")
@RestController
@RequestMapping("/tradeCallback")
@Slf4j
public class PayCallbackController {

    public static final Logger LOGGER = LoggerFactory.getLogger(PayCallbackController.class);

    @Autowired
    private ReturnOrderProvider returnOrderProvider;

    @Autowired
    private ReturnPileOrderProvider returnPileOrderProvider;

    @Autowired
    private ReturnOrderQueryProvider returnOrderQueryProvider;

    @Autowired
    private ReturnPileOrderQueryProvider returnPileOrderQueryProvider;

    @Autowired
    private PayProvider payProvider;

    @Autowired
    private TradeProvider tradeProvider;

    @Autowired
    private PileTradeProvider pileTradeProvider;

    @Autowired
    private TradeQueryProvider tradeQueryProvider;

    @Autowired
    private PileTradeQueryProvider pileTradeQueryProvider;

    @Autowired
    private PayQueryProvider payQueryProvider;

    @Autowired
    private RefundOrderQueryProvider refundOrderQueryProvider;

    @Autowired
    private AliPayProvider aliPayProvider;

    @Autowired
    private WxPayProvider wxPayProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private Semaphore payCallbackSemaphore;

    @Autowired
    private PickUpRecordProvider pickUpRecordProvider;

    @Autowired
    private PayCallBackTaskService payCallBackTaskService;

    @Autowired
    private PayCallBackResultProvider payCallBackResultProvider;

    @Autowired
    private PayOrderQueryProvider payOrderQueryProvider;

    @Value("${toggle.new.unionB2B.verify.sign}")
    private boolean toggleVerifySign = false;

    @Value("${china.pay.security.path}")
    private String url;

    @Autowired
    private WalletRecordProvider walletRecordProvider;

    @Autowired
    private CustomerWalletQueryProvider customerWalletQueryProvider;

    @Autowired
    private TicketsFormProvider ticketsFormProvider;

    @Autowired
    private VirtualGoodsQueryProvider virtualGoodsQueryProvider;

    @Autowired
    private CustomerWalletProvider customerWalletProvider;

    @Autowired
    private CouponActivityProvider couponActivityProvider;

    @Autowired
    private CustomerDeliveryAddressProvider customerDeliveryAddressProvider;

    @Autowired
    private CupsPayProvider cupsPayProvider;


    /**
     * 获取所有回调的参数
     * @param request
     * @return 参数Map
     */
    private static Map<String, String> getAllRequestParam(
            final HttpServletRequest request) {
        Map<String, String> res = new HashMap<>();
        Enumeration<?> temp = request.getParameterNames();
        if (null != temp) {
            while (temp.hasMoreElements()) {
                String en = (String) temp.nextElement();
                String value = request.getParameter(en);
                res.put(en, value);
                // 在报文上送时，如果字段的值为空，则不上送<下面的处理为在获取所有参数数据时，判断若值为空，则删除这个字段>
                if (res.get(en) == null || "".equals(res.get(en))) {
                    res.remove(en);
                }
            }
        }
        return res;
    }

    @ApiOperation(value = "ping++回调方法")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", dataType = "Map",
                    name = "json", value = "回调报文", required = true),
            @ApiImplicitParam(paramType = "header", dataType = "String",
                    name = "x-pingplusplus-signature", value = "签名", required = true)
    })
    @RequestMapping(value = "/pingCallBack", method = RequestMethod.POST)
    @LcnTransaction
    public ResponseEntity pingCallBack(@RequestBody Map<String, Object> json,
                                       @RequestHeader("x-pingplusplus-signature") String signatureStr) {
        boolean flag = true;
        String body = null;
        Event event;
        //渠道方交易回调请求参数
        TradeCallbackRequest tradeCallbackRequest = new TradeCallbackRequest();
        String objectId = null;
        try {
            body = JSON.toJSONString(json, SerializerFeature.WriteMapNullValue);
            event = Webhooks.eventParse(body);
            /*
             * 签名校验
             */
            PayGatewayConfigResponse payGatewayConfig = payQueryProvider.getGatewayConfigByGateway(new
                    GatewayConfigByGatewayRequest(PayGatewayEnum.PING, Constants.BOSS_DEFAULT_STORE_ID)).getContext();
            byte[] signatureBytes = Base64.decodeBase64(signatureStr);
            Signature signature = Signature.getInstance("SHA256withRSA");
            PublicKey publicKey = getPubKey(payGatewayConfig.getPublicKey());
            signature.initVerify(publicKey);
            signature.update(body.getBytes("UTF-8"));
            boolean isCheck = signature.verify(signatureBytes);
            System.err.println(isCheck);
            if (!isCheck) {
                return new ResponseEntity(HttpStatus.UNAUTHORIZED);
            }
            PingppObject obj = Webhooks.getObject(body);
            PayTradeRecordResponse payTradeRecord;
            Operator operator = Operator.builder().ip(HttpUtil.getIpAddr()).adminId("-1").name("PING")
                    .account("PING").platform(Platform.THIRD).build();
            System.err.println("-----------------------------0----------------------------------");
            System.err.println(event.getType());
            System.err.println("-----------------------------0----------------------------------");
            switch (event.getType()) {
                //支付回调
                case "charge.succeeded":
                    Charge charge = (Charge) obj;
                    objectId = charge.getId();
                    //查询交易记录
                    payTradeRecord = payQueryProvider.getTradeRecordByChargeId(new TradeRecordByChargeRequest
                            (objectId)).getContext();
                    TradeGetByIdResponse trade =
                            tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(payTradeRecord.getBusinessId()).build()).getContext();

                    TradeGetPayOrderByIdResponse tradeGetPayOrderByIdResponse =
                            tradeQueryProvider.getPayOrderById(TradeGetPayOrderByIdRequest.builder().
                                    payOrderId(trade.getTradeVO().getPayOrderId()).build()).getContext();


                    TradePayCallBackOnlineRequest tradePayCallBackOnlineRequest =

                            TradePayCallBackOnlineRequest.builder()
                                    .trade(KsBeanUtil.convert(trade.getTradeVO(), TradeDTO.class))
                                    .payOrderOld(KsBeanUtil.convert(tradeGetPayOrderByIdResponse.getPayOrder(),
                                            PayOrderDTO.class))
                                    .operator(operator)
                                    .build();

                    tradeProvider.payCallBackOnline(tradePayCallBackOnlineRequest);

//                    tradeService.payCallBackOnline(trade, tradeGetPayOrderByIdResponse.getPayOrder(), operator);
                    tradeCallbackRequest.setAmount(new BigDecimal(charge.getAmount() / 100));
                    tradeCallbackRequest.setFinishTime(DateUtil.getLocalDateTimeFromUnixTimestamp(charge.getTimePaid()));
                    tradeCallbackRequest.setTradeType(TradeType.PAY);
                    break;
                case "refund.succeeded":
                    //退款回调
                    Refund refund = (Refund) obj;
                    objectId = refund.getId();
                    //查询交易记录
                    payTradeRecord = payQueryProvider.getTradeRecordByChargeId(new TradeRecordByChargeRequest
                            (objectId)).getContext();
                    ReturnOrderVO returnOrder = returnOrderQueryProvider.getById(ReturnOrderByIdRequest.builder()
                            .rid(payTradeRecord.getBusinessId()).build()).getContext();
                    RefundOrderByReturnCodeResponse refundOrder =
                            refundOrderQueryProvider.getByReturnOrderCode(new RefundOrderByReturnOrderCodeRequest(payTradeRecord.getBusinessId())).getContext();
                    returnOrderProvider.onlineRefund(
                            ReturnOrderOnlineRefundRequest.builder().operator(operator)
                                    .returnOrder(KsBeanUtil.convert(returnOrder, ReturnOrderDTO.class))
                                    .refundOrder(KsBeanUtil.convert(refundOrder, RefundOrderDTO.class)).build());
                    tradeCallbackRequest.setAmount(new BigDecimal(refund.getAmount() / 100));
                    tradeCallbackRequest.setFinishTime(DateUtil.getLocalDateTimeFromUnixTimestamp(refund.getTimeSucceed
                            ()));
                    tradeCallbackRequest.setTradeType(TradeType.REFUND);
                    break;
            }
        } catch (Exception e) {
            flag = false;
            LOGGER.error("Callback business handles exceptions,json:{},signature={},", new Object[]{body,
                    signatureStr}, e);
        }
        //业务状态修改成功，修改支付记录状态
        tradeCallbackRequest.setObjectId(objectId);
        tradeCallbackRequest.setTradeStatus(TradeStatus.SUCCEED);
        tradeCallbackRequest.setTradeCount(1);
        try {
            System.err.println("------------------------------1--------------------------------------------");
            System.err.println(JSON.toJSONString(tradeCallbackRequest));
            System.err.println("-------------------------------1-------------------------------------------");
            payProvider.callback(tradeCallbackRequest);
        } catch (Exception e) {
            LOGGER.error("Execute System method [PayService.callBack] exceptions,request={},", tradeCallbackRequest, e);
        }
        return flag ? ResponseEntity.ok().build() : new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 银联企业支付异步回调
     * @param request
     * @param response
     */
    @ApiOperation(value = "银联企业支付异步回调")
    @RequestMapping(value = "/unionB2BCallBack", method = RequestMethod.POST)
    @LcnTransaction
    public void unionB2BCallBack(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        log.info("=========================银联企业支付异步回调接收报文返回开始=======================");
        String encoding = request.getParameter("encoding");
        log.info("返回报文中encoding=[" + encoding + "]");
        Map<String, String> respParam = getAllRequestParam(request);
        //验签
        if (!payProvider.unionCheckSign(respParam).getContext()) {
            log.info("验证签名结果[失败].");
        } else {
            log.info("验证签名结果[成功].");
            log.info("-------------银联支付回调,respParam：{}------------", JSONObject.toJSONString(respParam));
            String respCode = respParam.get("respCode");
            if ("00".equals(respCode)) {
                //判断respCode=00 后，对涉及资金类的交易，请再发起查询接口查询，确定交易成功后更新数据库。
                UnionPayRequest unionPayRequest = new UnionPayRequest();
                unionPayRequest.setApiKey(request.getParameter("merId"));
                unionPayRequest.setBusinessId(request.getParameter("orderId"));
                unionPayRequest.setTxnTime(request.getParameter("txnTime"));
                Map<String, String> resultMap = payQueryProvider.getUnionPayResult(unionPayRequest).getContext();
                payProvider.unionCallBack(resultMap);

                PayTradeRecordResponse payTradeRecord;
                //交易成功，更新商户订单状态
                if (resultMap != null && "00".equals(resultMap.get("respCode"))) {
                    payTradeRecord = payQueryProvider.getTradeRecordByOrderCode(new TradeRecordByOrderCodeRequest
                            (resultMap.get("orderId"))).getContext();
                    Operator operator =
                            Operator.builder().ip(HttpUtil.getIpAddr()).adminId("-1").name(PayGatewayEnum.UNIONB2B.name())
                                    .account(PayGatewayEnum.UNIONB2B.name()).platform(Platform.THIRD).build();

                    //单笔支付与组合支付区分，获取支付订单的信息
                    boolean isMergePay = isMergePayOrder(payTradeRecord.getBusinessId());
                    List<TradeVO> trades = new ArrayList<>();
                    if (!isMergePay) {
                        trades.add(tradeQueryProvider.getById(TradeGetByIdRequest
                                .builder().tid(payTradeRecord.getBusinessId()).build()).getContext().getTradeVO());
                    } else {
                        trades.addAll(tradeQueryProvider.getListByParentId(TradeListByParentIdRequest.builder()
                                .parentTid(payTradeRecord.getBusinessId()).build()).getContext().getTradeVOList());
                    }
                    payCallbackOnline(trades, operator, isMergePay);
                }
                response.getWriter().print("ok");
            }

        }

        log.info("银联企业支付异步回调接收报文返回结束");

    }

    /**
     * 银联退款回调
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @ApiOperation(value = "银联退款回调")
    @RequestMapping(value = "/unionRefundCallBack", method = RequestMethod.POST)
    @LcnTransaction
    public void unionRefundCallBack(HttpServletRequest request, HttpServletResponse response) throws ServletException
            , IOException {
        log.info("退款回调接收后台通知开始");
        //String encoding = "UTF-8";
        // 获取银联通知服务器发送的后台通知参数
        Map<String, String> reqParam = getAllRequestParam(request);
        //重要！验证签名前不要修改reqParam中的键值对的内容，否则会验签不过
        if (!payProvider.unionCheckSign(reqParam).getContext()) {
            log.info("验证签名结果[失败].");
            //验签失败，需解决验签问题

        } else {
            log.info("验证签名结果[成功].");
            //【注：为了安全验签成功才应该写商户的成功处理逻辑】交易成功，更新商户订单状态
            //判断respCode=00、A6后，对涉及资金类的交易，请再发起查询接口查询，确定交易成功后更新数据库。
            String respCode = reqParam.get("respCode");
            if ("00".equals(respCode)) {
                PayTradeRecordResponse payTradeRecord =
                        payQueryProvider.getTradeRecordByOrderCode(new TradeRecordByOrderCodeRequest
                                (reqParam.get("orderId"))).getContext();
                ReturnOrderVO returnOrder = returnOrderQueryProvider.getById(ReturnOrderByIdRequest.builder()
                        .rid(payTradeRecord.getBusinessId()).build()).getContext();

                RefundOrderByReturnCodeResponse refundOrder =
                        refundOrderQueryProvider.getByReturnOrderCode(new RefundOrderByReturnOrderCodeRequest(payTradeRecord.getBusinessId())).getContext();
                Operator operator = Operator.builder().ip(HttpUtil.getIpAddr()).adminId("-1").name("UNIONB2B")
                        .account("UNIONB2B").platform(Platform.THIRD).build();
                payProvider.unionCallBack(reqParam);
                returnOrderProvider.onlineRefund(
                        ReturnOrderOnlineRefundRequest.builder().operator(operator)
                                .returnOrder(KsBeanUtil.convert(returnOrder, ReturnOrderDTO.class))
                                .refundOrder(KsBeanUtil.convert(refundOrder, RefundOrderDTO.class)).build());
                response.getWriter().print("ok");
            }
        }
        log.info("退款回调接收后台通知结束");
    }

    /**
     * 新银联退款回调
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @ApiOperation(value = "新银联退款回调")
    @RequestMapping(value = "/newUnionRefundCallBack", method = RequestMethod.POST)
    @LcnTransaction
    public void newUnionRefundCallBack(HttpServletRequest request, HttpServletResponse response) throws ServletException
            , IOException {
        log.info("=========================新退款回调接收后台通知开始=======================");
        log.info("原始的HttpServletRequest：{}",request);
        Map<String, String> reqParam = getAllRequestParamNew(request);
        log.info("处理后的参数:{}",reqParam);
        if (StringUtil.isNotEmpty(reqParam.get("Signature"))) {
            if (!verifySign(reqParam)){
                log.error("验签失败！");
                return;
            }
            PayTradeRecordResponse payTradeRecord =
                    payQueryProvider.getTradeRecordByOrderCode(new TradeRecordByOrderCodeRequest
                            (reqParam.get("MerOrderNo"))).getContext();
            ReturnOrderVO returnOrder = returnOrderQueryProvider.getById(ReturnOrderByIdRequest.builder()
                    .rid(payTradeRecord.getBusinessId()).build()).getContext();

            RefundOrderByReturnCodeResponse refundOrder =
                    refundOrderQueryProvider.getByReturnOrderCode(new RefundOrderByReturnOrderCodeRequest(payTradeRecord.getBusinessId())).getContext();
            Operator operator = Operator.builder().ip(HttpUtil.getIpAddr()).adminId("-1").name("UNIONB2B")
                    .account("UNIONB2B").platform(Platform.THIRD).build();
            payProvider.unionCallBack(reqParam);
            returnOrderProvider.onlineRefund(
                    ReturnOrderOnlineRefundRequest.builder().operator(operator)
                            .returnOrder(KsBeanUtil.convert(returnOrder, ReturnOrderDTO.class))
                            .refundOrder(KsBeanUtil.convert(refundOrder, RefundOrderDTO.class)).build());
            response.getWriter().print("ok");
        }else {
            log.info("未获取到签名!");
        }
        log.info("退款回调接收后台通知结束");
    }

    /**
     * 微信支付异步回调
     * @param request
     * @param response
     */
    @ApiOperation(value = "微信支付异步回调")
    @RequestMapping(value = "/WXPaySuccessCallBack/{storeId}", method = {RequestMethod.POST, RequestMethod.GET})
    @LcnTransaction
    public void wxPayBack(HttpServletRequest request, HttpServletResponse response ,@PathVariable("storeId") Long storeId) throws Exception {
        log.info("======================微信支付异步通知回调start======================");
        //支付回调结果
        String result = WXPayConstants.SUCCESS;
        //微信回调结果参数对象
        WxPayResultResponse wxPayResultResponse = new WxPayResultResponse();
        try {
            //获取回调数据输入流
            BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String line;
            StringBuilder retXml = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                retXml.append(line);
            }
            //微信支付异步回调结果xml
            log.info("微信支付异步通知回调结果xml====" + retXml);
            String retXmlStr = retXml.toString();
//            String retXmlStr = "<xml><appid><![CDATA[wxb67fac0bbb6b4031]]></appid><bank_type><![CDATA[OTHERS]]></bank_type><cash_fee><![CDATA[1]]></cash_fee><fee_type><![CDATA[CNY]]></fee_type><is_subscribe><![CDATA[N]]></is_subscribe><mch_id><![CDATA[1489104242]]></mch_id><nonce_str><![CDATA[jr4itIa2Kin4j9p5VRV1UDAmZLsZGA8P]]></nonce_str><openid><![CDATA[o6wq55YZ9xrgyQwQeka6btYt5HOQ]]></openid><out_trade_no><![CDATA[O202007011444302542]]></out_trade_no><result_code><![CDATA[SUCCESS]]></result_code><return_code><![CDATA[SUCCESS]]></return_code><sign><![CDATA[DA4F5784AEA7A17B608267EF8131A87A]]></sign><time_end><![CDATA[20200701144442]]></time_end><total_fee>1</total_fee><trade_type><![CDATA[JSAPI]]></trade_type><transaction_id><![CDATA[4200000616202007015217733087]]></transaction_id></xml>";
            retXmlStr = retXmlStr.replaceAll("<coupon_id_[0-9]{0,11}[^>]*>(.*?)</coupon_id_[0-9]{0,11}>", "");
            retXmlStr = retXmlStr.replaceAll("<coupon_type_[0-9]{0,11}[^>]*>(.*?)</coupon_type_[0-9]{0,11}>", "");
            retXmlStr = retXmlStr.replaceAll("<coupon_fee_[0-9]{0,11}[^>]*>(.*?)</coupon_fee_[0-9]{0,11}>", "");

            //将回调数据写入数据库
            XStream xStream = new XStream(new XppDriver(new XmlFriendlyNameCoder("_-", "_")));
            xStream.alias("xml", WxPayResultResponse.class);
            wxPayResultResponse = (WxPayResultResponse) xStream.fromXML(retXmlStr);
            //如果线程池队列已满，则采取拒绝策略（AbortPolicy），抛出RejectedExecutionException异常，则将对应的回调改为处理失败，然后通过定时任务处理补偿
            // PayTradeRecordResponse payOrderResponse = getPayTradeRecordResponse(wxPayResultResponse.getOut_trade_no());
            String businessId = payQueryProvider.queryBusinessIdByPayOrderNo(wxPayResultResponse.getOut_trade_no()).getContext();
            try {
                addPayCallBackResult(PayCallBackResultAddRequest.builder()
                        //.businessId(payOrderResponse.getBusinessId())
                        .businessId(businessId)
                        .resultXml(retXml.toString())
                        .resultContext(retXmlStr)
                        .resultStatus(PayCallBackResultStatus.HANDLING)
                        .errorNum(0)
                        .payType(PayCallBackType.WECAHT)
                        .build());
                payCallBackTaskService.payCallBack(TradePayOnlineCallBackRequest.builder().payCallBackType(PayCallBackType.WECAHT)
                        .wxPayCallBackResultStr(retXmlStr)
                        .wxPayCallBackResultXmlStr(retXml.toString())
                        .businessId(businessId)
                        .build());
            } catch (RejectedExecutionException e) {
                addPayCallBackResult(PayCallBackResultAddRequest.builder()
                         //.businessId(payOrderResponse.getBusinessId())
                        .businessId(businessId)
                        .resultContext(retXmlStr)
                        .resultXml(retXml.toString())
                        .resultStatus(PayCallBackResultStatus.TODO)
                        .errorNum(0)
                        .payType(PayCallBackType.WECAHT)
                        .build());
            }
        } catch (Exception e) {
            log.error("微信异步通知处理失败:", e);
            result = "fail";
            throw e;
        } finally {
            // 异步消息接收后处理结果返回微信
            wxCallbackResultHandle(response, result);
            log.info("微信异步通知完成：结果=" + result + "，微信交易号=" + wxPayResultResponse.getTransaction_id());
        }
    }

    private PayTradeRecordResponse getPayTradeRecordResponse(String payOrderNo) {
        PayTradeRecordRequest recordRequest = new PayTradeRecordRequest();
        recordRequest.setPayOrderNo(payOrderNo);
        PayTradeRecordResponse payOrderResponse =  payQueryProvider.findByPayOrderNo(recordRequest).getContext();
        return payOrderResponse;
    }

    /**
     * 囤货微信支付异步回调
     * @param request
     * @param response
     */
    @ApiOperation(value = "囤货微信支付异步回调")
    @RequestMapping(value = "/WXPayPileOrderSuccessCallBack/{storeId}", method = {RequestMethod.POST, RequestMethod.GET})
    @LcnTransaction
    public void WXPayPileOrderSuccessCallBack(HttpServletRequest request, HttpServletResponse response ,@PathVariable("storeId") Long storeId) throws Exception {
        log.info("======================微信支付异步通知回调start======================");
        //支付回调结果
        String result = WXPayConstants.SUCCESS;
        //微信回调结果参数对象
        WxPayResultResponse wxPayResultResponse = new WxPayResultResponse();
        try {
            //获取回调数据输入流
            BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String line;
            StringBuilder retXml = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                retXml.append(line);
            }
            //微信支付异步回调结果xml
            log.info("微信支付异步通知回调结果xml====" + retXml);
            String retXmlStr = retXml.toString();
//            String retXmlStr = "<xml><appid><![CDATA[wxb67fac0bbb6b4031]]></appid><bank_type><![CDATA[OTHERS]]></bank_type><cash_fee><![CDATA[1]]></cash_fee><fee_type><![CDATA[CNY]]></fee_type><is_subscribe><![CDATA[N]]></is_subscribe><mch_id><![CDATA[1489104242]]></mch_id><nonce_str><![CDATA[jr4itIa2Kin4j9p5VRV1UDAmZLsZGA8P]]></nonce_str><openid><![CDATA[o6wq55YZ9xrgyQwQeka6btYt5HOQ]]></openid><out_trade_no><![CDATA[O202007011444302542]]></out_trade_no><result_code><![CDATA[SUCCESS]]></result_code><return_code><![CDATA[SUCCESS]]></return_code><sign><![CDATA[DA4F5784AEA7A17B608267EF8131A87A]]></sign><time_end><![CDATA[20200701144442]]></time_end><total_fee>1</total_fee><trade_type><![CDATA[JSAPI]]></trade_type><transaction_id><![CDATA[4200000616202007015217733087]]></transaction_id></xml>";
            retXmlStr = retXmlStr.replaceAll("<coupon_id_[0-9]{0,11}[^>]*>(.*?)</coupon_id_[0-9]{0,11}>", "");
            retXmlStr = retXmlStr.replaceAll("<coupon_type_[0-9]{0,11}[^>]*>(.*?)</coupon_type_[0-9]{0,11}>", "");
            retXmlStr = retXmlStr.replaceAll("<coupon_fee_[0-9]{0,11}[^>]*>(.*?)</coupon_fee_[0-9]{0,11}>", "");

            //将回调数据写入数据库
            XStream xStream = new XStream(new XppDriver(new XmlFriendlyNameCoder("_-", "_")));
            xStream.alias("xml", WxPayResultResponse.class);
            wxPayResultResponse = (WxPayResultResponse) xStream.fromXML(retXmlStr);
            PayTradeRecordResponse payOrderResponse = getPayTradeRecordResponse(wxPayResultResponse.getOut_trade_no());
            //如果线程池队列已满，则采取拒绝策略（AbortPolicy），抛出RejectedExecutionException异常，则将对应的回调改为处理失败，然后通过定时任务处理补偿
            try {
                addPayCallBackResult(PayCallBackResultAddRequest.builder()
                        .businessId(payOrderResponse.getBusinessId())
                        .resultXml(retXml.toString())
                        .resultContext(retXmlStr)
                        .resultStatus(PayCallBackResultStatus.HANDLING)
                        .errorNum(0)
                        .payType(PayCallBackType.WECAHT)
                        .build());
                payCallBackTaskService.payPileOrderCallBack(TradePayOnlineCallBackRequest.builder().payCallBackType(PayCallBackType.WECAHT)
                        .wxPayCallBackResultStr(retXmlStr)
                        .wxPayCallBackResultXmlStr(retXml.toString())
                        .build());
            } catch (RejectedExecutionException e) {
                addPayCallBackResult(PayCallBackResultAddRequest.builder()
                        .businessId(payOrderResponse.getBusinessId())
                        .resultContext(retXmlStr)
                        .resultXml(retXml.toString())
                        .resultStatus(PayCallBackResultStatus.TODO)
                        .errorNum(0)
                        .payType(PayCallBackType.WECAHT)
                        .build());
            }
        } catch (Exception e) {
            log.error("微信异步通知处理失败:", e);
            result = "fail";
            throw e;
        } finally {
            // 异步消息接收后处理结果返回微信
            wxCallbackResultHandle(response, result);
            log.info("微信异步通知完成：结果=" + result + "，微信交易号=" + wxPayResultResponse.getTransaction_id());
        }
    }



    /**
     * 微信支付异步回调
     * @param request
     * @param response
     */
    @ApiOperation(value = "微信提货支付异步回调")
    @RequestMapping(value = "/WXPayTakeGoodSuccessCallBack/{storeId}", method = {RequestMethod.POST, RequestMethod.GET})
    @LcnTransaction
    public void WXPayTakeGoodSuccessCallBack(HttpServletRequest request, HttpServletResponse response ,@PathVariable("storeId") Long storeId) throws Exception {
        log.info("======================微信支付提货异步通知回调start======================");
        //支付回调结果
        String result = WXPayConstants.SUCCESS;
        //微信回调结果参数对象
        WxPayResultResponse wxPayResultResponse = new WxPayResultResponse();
        try {
            //获取回调数据输入流
            BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String line;
            StringBuilder retXml = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                retXml.append(line);
            }
            //微信支付异步回调结果xml
            log.info("微信支付提货异步通知回调结果xml====" + retXml);
            String retXmlStr = retXml.toString();
//            String retXmlStr = "<xml><appid><![CDATA[wxb67fac0bbb6b4031]]></appid><bank_type><![CDATA[OTHERS]]></bank_type><cash_fee><![CDATA[1]]></cash_fee><fee_type><![CDATA[CNY]]></fee_type><is_subscribe><![CDATA[N]]></is_subscribe><mch_id><![CDATA[1489104242]]></mch_id><nonce_str><![CDATA[jr4itIa2Kin4j9p5VRV1UDAmZLsZGA8P]]></nonce_str><openid><![CDATA[o6wq55YZ9xrgyQwQeka6btYt5HOQ]]></openid><out_trade_no><![CDATA[O202007011444302542]]></out_trade_no><result_code><![CDATA[SUCCESS]]></result_code><return_code><![CDATA[SUCCESS]]></return_code><sign><![CDATA[DA4F5784AEA7A17B608267EF8131A87A]]></sign><time_end><![CDATA[20200701144442]]></time_end><total_fee>1</total_fee><trade_type><![CDATA[JSAPI]]></trade_type><transaction_id><![CDATA[4200000616202007015217733087]]></transaction_id></xml>";
            retXmlStr = retXmlStr.replaceAll("<coupon_id_[0-9]{0,11}[^>]*>(.*?)</coupon_id_[0-9]{0,11}>", "");
            retXmlStr = retXmlStr.replaceAll("<coupon_type_[0-9]{0,11}[^>]*>(.*?)</coupon_type_[0-9]{0,11}>", "");
            retXmlStr = retXmlStr.replaceAll("<coupon_fee_[0-9]{0,11}[^>]*>(.*?)</coupon_fee_[0-9]{0,11}>", "");

            //将回调数据写入数据库
            XStream xStream = new XStream(new XppDriver(new XmlFriendlyNameCoder("_-", "_")));
            xStream.alias("xml", WxPayResultResponse.class);
            wxPayResultResponse = (WxPayResultResponse) xStream.fromXML(retXmlStr);
            //如果线程池队列已满，则采取拒绝策略（AbortPolicy），抛出RejectedExecutionException异常，则将对应的回调改为处理失败，然后通过定时任务处理补偿
            try {
                addPayCallBackResult(PayCallBackResultAddRequest.builder()
                        .businessId(wxPayResultResponse.getOut_trade_no())
                        .resultXml(retXml.toString())
                        .resultContext(retXmlStr)
                        .resultStatus(PayCallBackResultStatus.HANDLING)
                        .errorNum(0)
                        .payType(PayCallBackType.WECAHT)
                        .build());
                payCallBackTaskService.payTakeGoodCallBack(TradePayOnlineCallBackRequest.builder().payCallBackType(PayCallBackType.WECAHT)
                        .wxPayCallBackResultStr(retXmlStr)
                        .wxPayCallBackResultXmlStr(retXml.toString())
                        .build());
            } catch (RejectedExecutionException e) {
                addPayCallBackResult(PayCallBackResultAddRequest.builder()
                        .businessId(wxPayResultResponse.getOut_trade_no())
                        .resultContext(retXmlStr)
                        .resultXml(retXml.toString())
                        .resultStatus(PayCallBackResultStatus.TODO)
                        .errorNum(0)
                        .payType(PayCallBackType.WECAHT)
                        .build());
            }
        } catch (Exception e) {
            log.error("微信异步通知处理失败:", e);
            result = "fail";
            throw e;
        } finally {
            // 异步消息接收后处理结果返回微信
            wxCallbackResultHandle(response, result);
            log.info("微信异步通知完成：结果=" + result + "，微信交易号=" + wxPayResultResponse.getTransaction_id());
        }
    }



    /**
     * 微信支付异步回调
     * @param request
     * @param response
     */
    @ApiOperation(value = "微信充值支付异步回调")
    @RequestMapping(value = "/WXPayRechargeSuccessCallBack/{storeId}", method = {RequestMethod.POST, RequestMethod.GET})
    @LcnTransaction
    public void WXPayRechargeSuccessCallBack(HttpServletRequest request, HttpServletResponse response ,@PathVariable("storeId") Long storeId) throws Exception {
        log.info("======================微信支付异步通知回调start======================");
        //支付回调结果
        String result = WXPayConstants.SUCCESS;
        //微信回调结果参数对象
        WxPayResultResponse wxPayResultResponse = new WxPayResultResponse();
        try {
            //获取回调数据输入流
            BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String line;
            StringBuilder retXml = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                retXml.append(line);
            }
            //微信支付异步回调结果xml
            log.info("微信充值支付异步回调结果xml====" + retXml);
            String retXmlStr = retXml.toString();
//            String retXmlStr = "<xml><appid><![CDATA[wxb67fac0bbb6b4031]]></appid><bank_type><![CDATA[OTHERS]]></bank_type><cash_fee><![CDATA[1]]></cash_fee><fee_type><![CDATA[CNY]]></fee_type><is_subscribe><![CDATA[N]]></is_subscribe><mch_id><![CDATA[1489104242]]></mch_id><nonce_str><![CDATA[jr4itIa2Kin4j9p5VRV1UDAmZLsZGA8P]]></nonce_str><openid><![CDATA[o6wq55YZ9xrgyQwQeka6btYt5HOQ]]></openid><out_trade_no><![CDATA[O202007011444302542]]></out_trade_no><result_code><![CDATA[SUCCESS]]></result_code><return_code><![CDATA[SUCCESS]]></return_code><sign><![CDATA[DA4F5784AEA7A17B608267EF8131A87A]]></sign><time_end><![CDATA[20200701144442]]></time_end><total_fee>1</total_fee><trade_type><![CDATA[JSAPI]]></trade_type><transaction_id><![CDATA[4200000616202007015217733087]]></transaction_id></xml>";
            retXmlStr = retXmlStr.replaceAll("<coupon_id_[0-9]{0,11}[^>]*>(.*?)</coupon_id_[0-9]{0,11}>", "");
            retXmlStr = retXmlStr.replaceAll("<coupon_type_[0-9]{0,11}[^>]*>(.*?)</coupon_type_[0-9]{0,11}>", "");
            retXmlStr = retXmlStr.replaceAll("<coupon_fee_[0-9]{0,11}[^>]*>(.*?)</coupon_fee_[0-9]{0,11}>", "");

            //将回调数据写入数据库
            XStream xStream = new XStream(new XppDriver(new XmlFriendlyNameCoder("_-", "_")));
            xStream.alias("xml", WxPayResultResponse.class);
            wxPayResultResponse = (WxPayResultResponse) xStream.fromXML(retXmlStr);
            //如果线程池队列已满，则采取拒绝策略（AbortPolicy），抛出RejectedExecutionException异常，则将对应的回调改为处理失败，然后通过定时任务处理补偿
            try {
                addPayCallBackResult(PayCallBackResultAddRequest.builder()
                        .businessId(wxPayResultResponse.getOut_trade_no())
                        .resultXml(retXml.toString())
                        .resultContext(retXmlStr)
                        .resultStatus(PayCallBackResultStatus.HANDLING)
                        .errorNum(0)
                        .payType(PayCallBackType.WECAHT)
                        .build());
                payCallBackTaskService.payRechargeCallBack(TradePayOnlineCallBackRequest.builder().payCallBackType(PayCallBackType.WECAHT)
                        .wxPayCallBackResultStr(retXmlStr)
                        .wxPayCallBackResultXmlStr(retXml.toString())
                        .build());
            } catch (RejectedExecutionException e) {
                addPayCallBackResult(PayCallBackResultAddRequest.builder()
                        .businessId(wxPayResultResponse.getOut_trade_no())
                        .resultContext(retXmlStr)
                        .resultXml(retXml.toString())
                        .resultStatus(PayCallBackResultStatus.TODO)
                        .errorNum(0)
                        .payType(PayCallBackType.WECAHT)
                        .build());
            }
        } catch (Exception e) {
            log.error("微信异步通知处理失败:", e);
            result = "fail";
            throw e;
        } finally {
            // 异步消息接收后处理结果返回微信
            wxCallbackResultHandle(response, result);
            log.info("微信异步通知完成：结果=" + result + "，微信交易号=" + wxPayResultResponse.getTransaction_id());
        }
    }
    /**
     * @Author lvzhenwei
     * @Description 保存支付回调结果
     * @Date 14:08 2020/7/20
     * @Param [resultAddRequest]
     * @return void
     **/
    private void addPayCallBackResult(PayCallBackResultAddRequest resultAddRequest){
        try{
            payCallBackResultProvider.add(resultAddRequest);
        } catch (SbcRuntimeException e) {
            //business_id唯一索引报错捕获，不影响流程处理
            if(!e.getErrorCode().equals(ErrorCodeConstant.PAY_CALL_BACK_RESULT_EXIT)){
                throw e;
            }
            e.printStackTrace();
        }
    }


    /**
     * 微信支付异步回调
     * @param request
     * @param response
     */
    @ApiOperation(value = "微信支付异步回调")
    @RequestMapping(value = "/WXPaySuccessCallBack1/{storeId}", method = {RequestMethod.POST, RequestMethod.GET})
    public void paySuc1(HttpServletRequest request, HttpServletResponse response ,
                      @PathVariable("storeId") Long storeId) throws Exception {
        // 通过信号量控制回调流量

        /*if (payCallbackSemaphore.hasQueuedThreads()) {
            response.getWriter().write(WXPayUtil.setXML("FAIL", "ERROR"));
            log.info("微信支付异步通知回调失败的消息回复结束");
        }*/

        payCallbackSemaphore.acquire();

        try {
            paySuc(request, response, storeId);
        } catch (Exception e) {
            response.getWriter().write(WXPayUtil.setXML("FAIL", "ERROR"));
        }

        payCallbackSemaphore.release();
    }


    @LcnTransaction
    public void paySuc(HttpServletRequest request, HttpServletResponse response , Long storeId) throws Exception {
        log.info("======================微信支付异步通知回调start======================");

        PayGatewayConfigResponse payGatewayConfig = payQueryProvider.getGatewayConfigByGateway(new
                GatewayConfigByGatewayRequest(PayGatewayEnum.WECHAT, storeId)).getContext();
        String apiKey = payGatewayConfig.getApiKey();
        //支付回调结果
        String result = WXPayConstants.SUCCESS;
        //微信回调结果参数对象
        WxPayResultResponse wxPayResultResponse = new WxPayResultResponse();
        try {
            //获取回调数据输入流
            BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String line;
            StringBuilder retXml = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                retXml.append(line);
            }
            //微信支付异步回调结果xml
            log.info("微信支付异步通知回调结果xml====" + retXml);
            String retXmlStr = retXml.toString();
            retXmlStr = retXmlStr.replaceAll("<coupon_id_[0-9]{0,11}[^>]*>(.*?)</coupon_id_[0-9]{0,11}>", "");
            retXmlStr = retXmlStr.replaceAll("<coupon_type_[0-9]{0,11}[^>]*>(.*?)</coupon_type_[0-9]{0,11}>", "");
            retXmlStr = retXmlStr.replaceAll("<coupon_fee_[0-9]{0,11}[^>]*>(.*?)</coupon_fee_[0-9]{0,11}>", "");

            XStream xStream = new XStream(new XppDriver(new XmlFriendlyNameCoder("_-", "_")));

            xStream.alias("xml", WxPayResultResponse.class);
            wxPayResultResponse = (WxPayResultResponse) xStream.fromXML(retXmlStr);
            log.info("-------------微信支付回调,wxPayResultResponse：{}------------", wxPayResultResponse);
            //判断当前回调是否是合并支付
            String businessId = wxPayResultResponse.getOut_trade_no();
            boolean isMergePay = isMergePayOrder(businessId);
            String lockName;
            //非组合支付，则查出该单笔订单。
            if (!isMergePay) {
                TradeVO tradeVO =
                        tradeQueryProvider.getById(new TradeGetByIdRequest(businessId)).getContext().getTradeVO();
                // 锁资源：无论是否组合支付，都锁父单号，确保串行回调
                lockName = tradeVO.getParentId();
            } else {
                lockName = businessId;
            }
            //redis锁，防止同一订单重复回调
            RLock rLock = redissonClient.getFairLock(lockName);
            rLock.lock();
            //执行回调
            try {
                //支付回调事件成功
                if (wxPayResultResponse.getReturn_code().equals(WXPayConstants.SUCCESS) &&
                        wxPayResultResponse.getResult_code().equals(WXPayConstants.SUCCESS)) {
                    log.info("微信支付异步通知回调状态---成功");
                    //微信回调参数数据map
                    Map<String, String> params = WXPayUtil.xmlToMap(retXml.toString());
                    String trade_type = wxPayResultResponse.getTrade_type();
                    //app支付回调对应的api key为开放平台对应的api key
                    if (trade_type.equals("APP")) {
                        apiKey = payGatewayConfig.getOpenPlatformApiKey();
                    }
                    //微信签名校验
                    if (WXPayUtil.isSignatureValid(params, apiKey)) {
                        //签名正确，进行逻辑处理--对订单支付单以及操作信息进行处理并添加交易数据
                        List<TradeVO> trades = new ArrayList<>();
                        //查询交易记录
                        TradeRecordByOrderCodeRequest tradeRecordByOrderCodeRequest =
                                new TradeRecordByOrderCodeRequest(businessId);
                        PayTradeRecordResponse recordResponse =
                                payQueryProvider.getTradeRecordByOrderCode(tradeRecordByOrderCodeRequest).getContext();
                        if (isMergePay) {
                            /*
                             * 合并支付
                             * 查询订单是否已支付或过期作废
                             */
                            trades = tradeQueryProvider.getOrderListByParentId(
                                    new TradeListByParentIdRequest(businessId)).getContext().getTradeVOList();
                            //订单合并支付场景状态采样
                            boolean paid =
                                    trades.stream().anyMatch(i -> i.getTradeState().getPayState() == PayState.PAID);

                            boolean cancel =
                                    trades.stream().anyMatch(i -> i.getTradeState().getFlowState() == FlowState.VOID);


                            if (cancel || (paid && !recordResponse.getTradeNo().equals(wxPayResultResponse.getTransaction_id()))) {
                                //同一批订单重复支付或过期作废，直接退款
                                wxRefundHandle(wxPayResultResponse, businessId,storeId);
                            } else {
                                wxPayCallbackHandle(payGatewayConfig, wxPayResultResponse, businessId, trades, true);
                            }

                        } else {
                            //单笔支付
                            TradeVO tradeVO = tradeQueryProvider.getOrderById(new TradeGetByIdRequest(businessId))
                                    .getContext().getTradeVO();
                            if (tradeVO.getTradeState().getFlowState() == FlowState.VOID || (tradeVO.getTradeState()
                                    .getPayState() == PayState.PAID
                                    && !recordResponse.getTradeNo().equals(wxPayResultResponse.getTransaction_id()))) {
                                //同一批订单重复支付或过期作废，直接退款
                                wxRefundHandle(wxPayResultResponse, businessId,storeId);
                            } else {
                                trades.add(tradeVO);
                                wxPayCallbackHandle(payGatewayConfig, wxPayResultResponse, businessId, trades, false);
                            }

                        }
                    } else {
                        log.info("微信支付异步回调验证签名结果[失败].");
                        result = "fail";
                    }
                } else {
                    log.info("微信支付异步通知回调状态---失败");
                    result = "fail";
                }
                log.info("微信支付异步通知回调end---------");
            } finally {
                //解锁
                rLock.unlock();
            }
        } catch (Exception e) {
            log.error("微信异步通知处理失败:", e);
            result = "fail";
            throw e;
        } finally {
            // 异步消息接收后处理结果返回微信
            wxCallbackResultHandle(response, result);
            log.info("微信异步通知完成：结果=" + result + "，微信交易号=" + wxPayResultResponse.getTransaction_id());
        }
    }

    /**
     * 微信支付退款成功异步回调
     * @param request
     * @param response
     */
    @ApiOperation(value = "微信支付退款成功异步回调")
    @RequestMapping(value = "/WXPayRefundSuccessCallBack/{storeId}", method = {RequestMethod.POST, RequestMethod.GET})
    @LcnTransaction
    public void wxPayRefundSuccessCallBack(HttpServletRequest request, HttpServletResponse response, @PathVariable("storeId") Long storeId) throws IOException {
        PayGatewayConfigResponse payGatewayConfig = payQueryProvider.getGatewayConfigByGateway(new
                GatewayConfigByGatewayRequest(PayGatewayEnum.WECHAT, storeId)).getContext();
        String apiKey = payGatewayConfig.getApiKey();
        InputStream inStream;
        String refund_status = "";
        try {
            inStream = request.getInputStream();
            ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }
            log.info("refund:微信退款----start----");
            // 获取微信调用我们notify_url的返回信息
            String result = new String(outSteam.toByteArray(), "utf-8");
            log.info("refund:微信退款----result----=" + result);
            // 关闭流
            outSteam.close();
            inStream.close();

            // xml转换为map
            Map<String, String> map = WXPayUtil.xmlToMap(result);
            WxPayRefundCallBackResponse refundCallBackResponse = (WxPayRefundCallBackResponse) WXPayUtil.
                    mapToObject(map, WxPayRefundCallBackResponse.class);
            if (WXPayConstants.SUCCESS.equalsIgnoreCase(refundCallBackResponse.getReturn_code())) {
                if (refundCallBackResponse.getAppid().equals(payGatewayConfig.getOpenPlatformAppId())) {
                    apiKey = payGatewayConfig.getOpenPlatformApiKey();
                }
                log.info("refund:微信退款----返回成功");
                /** 以下字段在return_code为SUCCESS的时候有返回： **/
                // 加密信息：加密信息请用商户秘钥进行解密，详见解密方式
                String req_info = refundCallBackResponse.getReq_info();
                /**
                 * 解密方式
                 * 解密步骤如下：
                 * （1）对加密串A做base64解码，得到加密串B
                 * （2）对商户key做md5，得到32位小写key* ( key设置路径：微信商户平台(pay.weixin.qq.com)-->账户设置-->API安全-->密钥设置 )
                 * （3）用key*对加密串B做AES-256-ECB解密（PKCS7Padding）
                 */
                java.util.Base64.Decoder decoder = java.util.Base64.getDecoder();
                byte[] b = decoder.decode(req_info);
                SecretKeySpec key = new SecretKeySpec(WXPayUtil.MD5(apiKey).toLowerCase().getBytes(), "AES");
                Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
                cipher.init(Cipher.DECRYPT_MODE, key);
                String resultStr = new String(cipher.doFinal(b), "utf-8");

                // log.info("refund:解密后的字符串:" + resultStr);
                Map<String, String> aesMap = WXPayUtil.xmlToMap(resultStr);
                WxPayRefundCallBackDataResponse dataResponse = (WxPayRefundCallBackDataResponse) WXPayUtil.
                        mapToObject(aesMap, WxPayRefundCallBackDataResponse.class);

                /** 以下为返回的加密字段： **/
                //  商户退款单号  是   String(64)  1.21775E+27 商户退款单号
                String out_refund_no = dataResponse.getOut_refund_no();
                //  退款状态   SUCCESS SUCCESS-退款成功、CHANGE-退款异常、REFUNDCLOSE—退款关闭
                refund_status = dataResponse.getRefund_status();

                if (refund_status.equals(WXPayConstants.SUCCESS)) {
                    PayTradeRecordResponse payTradeRecord =
                            payQueryProvider.getTradeRecordByOrderCode(new TradeRecordByOrderCodeRequest
                                    (out_refund_no)).getContext();
                    ReturnOrderVO returnOrder = returnOrderQueryProvider.getById(ReturnOrderByIdRequest.builder()
                            .rid(payTradeRecord.getBusinessId()).build()).getContext();

                    RefundOrderByReturnCodeResponse refundOrder =
                            refundOrderQueryProvider.getByReturnOrderCode(new RefundOrderByReturnOrderCodeRequest(payTradeRecord.getBusinessId())).getContext();
                    Operator operator = Operator.builder().ip(HttpUtil.getIpAddr()).adminId("-1").name("UNIONB2B")
                            .platform(Platform.THIRD).build();
                    returnOrderProvider.onlineRefund(
                            ReturnOrderOnlineRefundRequest.builder().operator(operator)
                                    .returnOrder(KsBeanUtil.convert(returnOrder, ReturnOrderDTO.class))
                                    .refundOrder(KsBeanUtil.convert(refundOrder, RefundOrderDTO.class)).build());
                    //微信支付异步回调添加交易数据

                    //异步回调添加交易数据
                    PayTradeRecordRequest payTradeRecordRequest = new PayTradeRecordRequest();
                    //微信支付订单号--及流水号
                    payTradeRecordRequest.setTradeNo(dataResponse.getRefund_id());
                    //商户订单号--业务id(商品退单号)
                    payTradeRecordRequest.setBusinessId(dataResponse.getOut_refund_no());
                    //微信支付订单号--及流水号
//                    payTradeRecordRequest.setTradeNo(dataResponse.getTransaction_id());
                    //商户订单号--业务id(商品订单号)
//                    payTradeRecordRequest.setBusinessId(dataResponse.getOut_trade_no());
                    payTradeRecordRequest.setResult_code(dataResponse.getRefund_status());
                    payTradeRecordRequest.setApplyPrice(new BigDecimal(dataResponse.getRefund_fee()).
                            divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_DOWN));
                    payTradeRecordRequest.setPracticalPrice(new BigDecimal(dataResponse.getTotal_fee()).
                            divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_DOWN));
                    payProvider.wxPayCallBack(payTradeRecordRequest);
                }

            } else {
                log.error("refund:支付失败,错误信息：" + refundCallBackResponse.getReturn_msg());
            }
        } catch (Exception e) {
            log.error("refund:微信退款回调发布异常：", e);
        } finally {
            wxCallbackResultHandle(response, refund_status);
        }
    }

    /**
     * 囤货微信支付退款成功异步回调
     * @param request
     * @param response
     */
    @ApiOperation(value = "囤货微信支付退款成功异步回调")
    @RequestMapping(value = "/WXPayPileRefundSuccessCallBack/{storeId}", method = {RequestMethod.POST, RequestMethod.GET})
    @LcnTransaction
    public void wxPayPileRefundSuccessCallBack(HttpServletRequest request, HttpServletResponse response, @PathVariable("storeId") Long storeId) throws IOException {
        PayGatewayConfigResponse payGatewayConfig = payQueryProvider.getGatewayConfigByGateway(new
                GatewayConfigByGatewayRequest(PayGatewayEnum.WECHAT, storeId)).getContext();
        String apiKey = payGatewayConfig.getApiKey();
        InputStream inStream;
        String refund_status = "";
        try {
            inStream = request.getInputStream();
            ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }
            log.info("refund:微信囤货退款----start----");
            // 获取微信调用我们notify_url的返回信息
            String result = new String(outSteam.toByteArray(), "utf-8");
            log.info("refund:微信囤货退款----result----=" + result);
            // 关闭流
            outSteam.close();
            inStream.close();

            // xml转换为map
            Map<String, String> map = WXPayUtil.xmlToMap(result);
            WxPayRefundCallBackResponse refundCallBackResponse = (WxPayRefundCallBackResponse) WXPayUtil.
                    mapToObject(map, WxPayRefundCallBackResponse.class);
            if (WXPayConstants.SUCCESS.equalsIgnoreCase(refundCallBackResponse.getReturn_code())) {
                if (refundCallBackResponse.getAppid().equals(payGatewayConfig.getOpenPlatformAppId())) {
                    apiKey = payGatewayConfig.getOpenPlatformApiKey();
                }
                log.info("refund:微信囤货退款----返回成功");
                /** 以下字段在return_code为SUCCESS的时候有返回： **/
                // 加密信息：加密信息请用商户秘钥进行解密，详见解密方式
                String req_info = refundCallBackResponse.getReq_info();
                /**
                 * 解密方式
                 * 解密步骤如下：
                 * （1）对加密串A做base64解码，得到加密串B
                 * （2）对商户key做md5，得到32位小写key* ( key设置路径：微信商户平台(pay.weixin.qq.com)-->账户设置-->API安全-->密钥设置 )
                 * （3）用key*对加密串B做AES-256-ECB解密（PKCS7Padding）
                 */
                java.util.Base64.Decoder decoder = java.util.Base64.getDecoder();
                byte[] b = decoder.decode(req_info);
                SecretKeySpec key = new SecretKeySpec(WXPayUtil.MD5(apiKey).toLowerCase().getBytes(), "AES");
                Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
                cipher.init(Cipher.DECRYPT_MODE, key);
                String resultStr = new String(cipher.doFinal(b), "utf-8");

                // log.info("refund:解密后的字符串:" + resultStr);
                Map<String, String> aesMap = WXPayUtil.xmlToMap(resultStr);
                WxPayRefundCallBackDataResponse dataResponse = (WxPayRefundCallBackDataResponse) WXPayUtil.
                        mapToObject(aesMap, WxPayRefundCallBackDataResponse.class);

                /** 以下为返回的加密字段： **/
                //  商户退款单号  是   String(64)  1.21775E+27 商户退款单号
                String out_refund_no = dataResponse.getOut_refund_no();
                //  退款状态   SUCCESS SUCCESS-退款成功、CHANGE-退款异常、REFUNDCLOSE—退款关闭
                refund_status = dataResponse.getRefund_status();

                if (refund_status.equals(WXPayConstants.SUCCESS)) {
                    PayTradeRecordResponse payTradeRecord =
                            payQueryProvider.getTradeRecordByOrderCode(new TradeRecordByOrderCodeRequest
                                    (out_refund_no)).getContext();
                    ReturnOrderVO returnOrder = returnPileOrderQueryProvider.getById(ReturnOrderByIdRequest.builder()
                            .rid(payTradeRecord.getBusinessId()).build()).getContext();

                    RefundOrderByReturnCodeResponse refundOrder =
                            refundOrderQueryProvider.getByReturnOrderCode(new RefundOrderByReturnOrderCodeRequest(payTradeRecord.getBusinessId())).getContext();
                    Operator operator = Operator.builder().ip(HttpUtil.getIpAddr()).adminId("-1").name("UNIONB2B")
                            .platform(Platform.THIRD).build();
                    returnPileOrderProvider.onlineRefund(
                            ReturnOrderOnlineRefundRequest.builder().operator(operator)
                                    .returnOrder(KsBeanUtil.convert(returnOrder, ReturnOrderDTO.class))
                                    .refundOrder(KsBeanUtil.convert(refundOrder, RefundOrderDTO.class)).build());
                    //微信支付异步回调添加交易数据

                    //异步回调添加交易数据
                    PayTradeRecordRequest payTradeRecordRequest = new PayTradeRecordRequest();
                    //微信支付订单号--及流水号
                    payTradeRecordRequest.setTradeNo(dataResponse.getRefund_id());
                    //商户订单号--业务id(商品退单号)
                    payTradeRecordRequest.setBusinessId(dataResponse.getOut_refund_no());
                    //微信支付订单号--及流水号
//                    payTradeRecordRequest.setTradeNo(dataResponse.getTransaction_id());
                    //商户订单号--业务id(商品订单号)
//                    payTradeRecordRequest.setBusinessId(dataResponse.getOut_trade_no());
                    payTradeRecordRequest.setResult_code(dataResponse.getRefund_status());
                    payTradeRecordRequest.setApplyPrice(new BigDecimal(dataResponse.getRefund_fee()).
                            divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_DOWN));
                    payTradeRecordRequest.setPracticalPrice(new BigDecimal(dataResponse.getTotal_fee()).
                            divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_DOWN));
                    payProvider.wxPayCallBack(payTradeRecordRequest);
                }

            } else {
                log.error("refund:囤货支付失败,错误信息：" + refundCallBackResponse.getReturn_msg());
            }
        } catch (Exception e) {
            log.error("refund:微信囤货退款回调发布异常：", e);
        } finally {
            wxCallbackResultHandle(response, refund_status);
        }
    }

    private PublicKey getPubKey(String pubKeyString) throws Exception {
        pubKeyString = pubKeyString.replaceAll("(-+BEGIN PUBLIC KEY-+\\r?\\n|-+END PUBLIC KEY-+\\r?\\n?)", "");
        byte[] keyBytes = Base64.decodeBase64(pubKeyString);

        // generate public key
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(spec);
        return publicKey;
    }

    /*
     * @Description:  支付宝支付回调
     * @Param:
     * @Author: Bob
     * @Date: 2019-02-26 12:00
     */
    @ApiOperation(value = "支付宝回调方法")
    @RequestMapping(value = "/aliPayCallBack/{storeId}", method = RequestMethod.POST)
    @LcnTransaction
    public void aliPayCallBack(HttpServletRequest request, HttpServletResponse response,@PathVariable("storeId") Long storeId) throws IOException {
        log.info("===============支付宝回调开始==============");
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();
        //返回的参数放到params中
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = iter.next();
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }

        //支付和退款公用一个回调，所以要判断回调的类型
        if (params.containsKey("refund_fee")) {
            //退款只有app支付的订单有回调，退款的逻辑在同步方法中已经处理了，这儿不再做处理
            log.info("APP退款回调,单号：{}", params.containsKey("out_trade_no"));
            try {
                response.getWriter().print("success");
                response.getWriter().flush();
                response.getWriter().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no")
                    .getBytes("ISO-8859-1"), "UTF-8");

            String result = JSONObject.toJSONString(params);
            try {
                //保存支付宝支付回调
                addPayCallBackResult(PayCallBackResultAddRequest.builder()
                        .businessId(out_trade_no)
                        .resultXml(result)
                        .resultContext(result)
                        .resultStatus(PayCallBackResultStatus.HANDLING)
                        .errorNum(0)
                        .payType(PayCallBackType.ALI)
                        .build());

                payCallBackTaskService.payCallBack(TradePayOnlineCallBackRequest.builder().payCallBackType(PayCallBackType.ALI)
                        .aliPayCallBackResultStr(result)
                        .build());

            }catch (RejectedExecutionException e) {
                addPayCallBackResult(PayCallBackResultAddRequest.builder()
                        .businessId(out_trade_no)
                        .resultContext(result)
                        .resultXml(result)
                        .resultStatus(PayCallBackResultStatus.TODO)
                        .errorNum(0)
                        .payType(PayCallBackType.ALI)
                        .build());
                response.getWriter().print("failure");
                response.getWriter().flush();
                response.getWriter().close();
            }

            response.getWriter().print("success");
            response.getWriter().flush();
            response.getWriter().close();
            log.info("支付回调返回success");
        }
    }

    @ApiOperation(value = "商户充值鲸币支付宝回调")
    @RequestMapping(value = "/aliPayMerchantCallBack", method = RequestMethod.POST)
    @LcnTransaction
    public void aliPayMerchantCallBack(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("===============支付宝回调开始==============");
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();
        //返回的参数放到params中
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = iter.next();
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }

        //支付和退款公用一个回调，所以要判断回调的类型
        if (params.containsKey("refund_fee")) {
            //退款只有app支付的订单有回调，退款的逻辑在同步方法中已经处理了，这儿不再做处理
            log.info("APP退款回调,单号：{}", params.containsKey("out_trade_no"));
            try {
                response.getWriter().print("success");
                response.getWriter().flush();
                response.getWriter().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no")
                    .getBytes("ISO-8859-1"), "UTF-8");

            String result = JSONObject.toJSONString(params);
            try {
                //保存支付宝支付回调
                addPayCallBackResult(PayCallBackResultAddRequest.builder()
                        .businessId(out_trade_no)
                        .resultXml(result)
                        .resultContext(result)
                        .resultStatus(PayCallBackResultStatus.HANDLING)
                        .errorNum(0)
                        .payType(PayCallBackType.ALI)
                        .build());
                payCallBackTaskService.payMerchantCallBack(TradePayWalletOnlineCallBackRequest.builder()
                        .aliPayCallBackResultStr(result)
                        .payCallBackType(PayWalletCallBackType.ALI)
                        .build());

            }catch (RejectedExecutionException e) {
                addPayCallBackResult(PayCallBackResultAddRequest.builder()
                        .businessId(out_trade_no)
                        .resultContext(result)
                        .resultXml(result)
                        .resultStatus(PayCallBackResultStatus.TODO)
                        .errorNum(0)
                        .payType(PayCallBackType.ALI)
                        .build());
                response.getWriter().print("failure");
                response.getWriter().flush();
                response.getWriter().close();
            }

            response.getWriter().print("success");
            response.getWriter().flush();
            response.getWriter().close();
            log.info("支付回调返回success");
        }
    }
    @ApiOperation(value = "商户充值鲸币微信回调")
    @RequestMapping(value = "/wxMerchantCallBack", method = RequestMethod.POST)
    @LcnTransaction
    public void WxMerchantCallBack(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("======================微信支付鲸币异步通知回调start======================");
        //支付回调结果
        String result = WXPayConstants.SUCCESS;
        //微信回调结果参数对象
        WxPayResultResponse wxPayResultResponse = new WxPayResultResponse();
        String refund_status = "";
        try {
            //获取回调数据输入流
            BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String line;
            StringBuilder retXml = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                retXml.append(line);
            }
            //微信支付异步回调结果xml
            log.info("微信支付异步通知回调结果xml====" + retXml);
            String retXmlStr = retXml.toString();
//            String retXmlStr = "<xml><appid><![CDATA[wxb67fac0bbb6b4031]]></appid><bank_type><![CDATA[OTHERS]]></bank_type><cash_fee><![CDATA[1]]></cash_fee><fee_type><![CDATA[CNY]]></fee_type><is_subscribe><![CDATA[N]]></is_subscribe><mch_id><![CDATA[1489104242]]></mch_id><nonce_str><![CDATA[jr4itIa2Kin4j9p5VRV1UDAmZLsZGA8P]]></nonce_str><openid><![CDATA[o6wq55YZ9xrgyQwQeka6btYt5HOQ]]></openid><out_trade_no><![CDATA[O202007011444302542]]></out_trade_no><result_code><![CDATA[SUCCESS]]></result_code><return_code><![CDATA[SUCCESS]]></return_code><sign><![CDATA[DA4F5784AEA7A17B608267EF8131A87A]]></sign><time_end><![CDATA[20200701144442]]></time_end><total_fee>1</total_fee><trade_type><![CDATA[JSAPI]]></trade_type><transaction_id><![CDATA[4200000616202007015217733087]]></transaction_id></xml>";
            retXmlStr = retXmlStr.replaceAll("<coupon_id_[0-9]{0,11}[^>]*>(.*?)</coupon_id_[0-9]{0,11}>", "");
            retXmlStr = retXmlStr.replaceAll("<coupon_type_[0-9]{0,11}[^>]*>(.*?)</coupon_type_[0-9]{0,11}>", "");
            retXmlStr = retXmlStr.replaceAll("<coupon_fee_[0-9]{0,11}[^>]*>(.*?)</coupon_fee_[0-9]{0,11}>", "");

            //将回调数据写入数据库
            XStream xStream = new XStream(new XppDriver(new XmlFriendlyNameCoder("_-", "_")));
            xStream.alias("xml", WxPayResultResponse.class);
            wxPayResultResponse = (WxPayResultResponse) xStream.fromXML(retXmlStr);
            //如果线程池队列已满，则采取拒绝策略（AbortPolicy），抛出RejectedExecutionException异常，则将对应的回调改为处理失败，然后通过定时任务处理补偿
            PayTradeRecordResponse payOrderResponse = getPayTradeRecordResponse(wxPayResultResponse.getOut_trade_no());
            String out_trade_no = payOrderResponse.getPayOrderNo();
            refund_status = wxPayResultResponse.getReturn_code();
            try {
                //保存微信支付回调
                addPayCallBackResult(PayCallBackResultAddRequest.builder()
                        .businessId(out_trade_no)
                        .resultXml(retXmlStr)
                        .resultContext(retXmlStr)
                        .resultStatus(PayCallBackResultStatus.HANDLING)
                        .errorNum(0)
                        .payType(PayCallBackType.WECAHT)
                        .build());
                payCallBackTaskService.payMerchantCallBack(TradePayWalletOnlineCallBackRequest.builder()
                        .payCallBackType(PayWalletCallBackType.WECAHT)
                        .wxPayCallBackResultStr(retXmlStr)
                        .build());
            }catch (RejectedExecutionException e) {
                addPayCallBackResult(PayCallBackResultAddRequest.builder()
                        .businessId(out_trade_no)
                        .resultContext(refund_status)
                        .resultXml(retXmlStr)
                        .resultStatus(PayCallBackResultStatus.TODO)
                        .errorNum(0)
                        .payType(PayCallBackType.WECAHT)
                        .build());
                response.getWriter().print("failure");
                response.getWriter().flush();
                response.getWriter().close();
            }

            response.getWriter().print("success");
            response.getWriter().flush();
            response.getWriter().close();
            log.info("支付回调返回success");
        }catch (Exception e) {
            log.error("refund:商户充值鲸币微信回调：", e);
        } finally {
            wxCallbackResultHandle(response, refund_status);
        }

    }





    /*
     * @Description:  支付宝支付回调
     * @Param:
     * @Author: Bob
     * @Date: 2019-02-26 12:00
     */
    @ApiOperation(value = "支付宝回调方法")
    @RequestMapping(value = "/set/aliPayCallBack/{storeId}", method = RequestMethod.POST)
    @LcnTransaction
    public void setAliPayCallBack(HttpServletRequest request, HttpServletResponse response,@PathVariable("storeId") Long storeId) throws IOException {
        log.info("===============支付宝回调开始==============");
        GatewayConfigByGatewayRequest gatewayConfigByGatewayRequest = new GatewayConfigByGatewayRequest();
        gatewayConfigByGatewayRequest.setGatewayEnum(PayGatewayEnum.ALIPAY);
        gatewayConfigByGatewayRequest.setStoreId(storeId);
        //查询支付宝配置信息
        PayGatewayConfigResponse payGatewayConfigResponse =
                payQueryProvider.getGatewayConfigByGateway(gatewayConfigByGatewayRequest).getContext();
        //支付宝公钥
        String aliPayPublicKey = payGatewayConfigResponse.getPublicKey();
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();
        //返回的参数放到params中
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = iter.next();
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }

        //支付和退款公用一个回调，所以要判断回调的类型
        if (params.containsKey("refund_fee")) {
            //退款只有app支付的订单有回调，退款的逻辑在同步方法中已经处理了，这儿不再做处理
            log.info("APP退款回调,单号：{}", params.containsKey("out_trade_no"));
            try {
                response.getWriter().print("success");
                response.getWriter().flush();
                response.getWriter().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            boolean signVerified = false;
            try {
                signVerified = AlipaySignature.rsaCheckV1(params, aliPayPublicKey, "UTF-8", "RSA2"); //调用SDK验证签名
            } catch (AlipayApiException e) {
                log.error("支付宝回调签名校验异常：", e);
            }
            if (signVerified) {
                try {
                    //商户订单号
                    String out_trade_no = new String(request.getParameter("out_trade_no")
                            .getBytes("ISO-8859-1"), "UTF-8");
                    //支付宝交易号
                    String trade_no = new String(request.getParameter("trade_no")
                            .getBytes("ISO-8859-1"), "UTF-8");
                    //交易状态
                    String trade_status = new String(request.getParameter("trade_status")
                            .getBytes("ISO-8859-1"), "UTF-8");
                    //订单金额
                    String total_amount = new String(request.getParameter("total_amount")
                            .getBytes("ISO-8859-1"), "UTF-8");
                    //支付终端类型
                    String type = new String(request.getParameter("passback_params")
                            .getBytes("ISO-8859-1"), "UTF-8");

                    boolean isMergePay = isMergePayOrder(out_trade_no);
                    log.info("-------------支付回调,单号：{}，流水：{}，交易状态：{}，金额：{}，是否合并支付：{}------------",
                            out_trade_no, trade_no, trade_status, total_amount, isMergePay);
                    String lockName;
                    //非组合支付，则查出该单笔订单。
                    if (!isMergePay) {
                        TradeVO tradeVO =
                                tradeQueryProvider.getById(new TradeGetByIdRequest(out_trade_no)).getContext().getTradeVO();
                        // 锁资源：无论是否组合支付，都锁父单号，确保串行回调
                        lockName = tradeVO.getParentId();
                    } else {
                        lockName = out_trade_no;
                    }
                    Operator operator =
                            Operator.builder().ip(HttpUtil.getIpAddr()).adminId("-1").name(PayGatewayEnum.ALIPAY.name())
                                    .account(PayGatewayEnum.ALIPAY.name()).platform(Platform.THIRD).build();
                    //redis锁，防止同一订单重复回调
                    RLock rLock = redissonClient.getFairLock(lockName);
                    rLock.lock();

                    //执行
                    try {
                        List<TradeVO> trades = new ArrayList<>();
                        //查询交易记录
                        TradeRecordByOrderCodeRequest tradeRecordByOrderCodeRequest =
                                new TradeRecordByOrderCodeRequest(out_trade_no);
                        PayTradeRecordResponse recordResponse =
                                payQueryProvider.getTradeRecordByOrderCode(tradeRecordByOrderCodeRequest).getContext();
                        if (isMergePay) {
                            /*
                             * 合并支付
                             * 查询订单是否已支付或过期作废
                             */
                            trades = tradeQueryProvider.getListByParentId(
                                    new TradeListByParentIdRequest(out_trade_no)).getContext().getTradeVOList();
                            //订单合并支付场景状态采样
                            boolean paid =
                                    trades.stream().anyMatch(i -> i.getTradeState().getPayState() == PayState.PAID);

                            boolean cancel =
                                    trades.stream().anyMatch(i -> i.getTradeState().getFlowState() == FlowState.VOID);
                            //订单的支付渠道。17、18、19是我们自己对接的支付宝渠道， 表：pay_channel_item
                            if (cancel || (paid && recordResponse.getChannelItemId() != 17L && recordResponse.getChannelItemId()
                                    != 18L && recordResponse.getChannelItemId() != 19L)) {
                                //重复支付，直接退款
                                alipayRefundHandle(out_trade_no, total_amount);
                            } else {
                                alipayCallbackHandle(out_trade_no, trade_no, trade_status, total_amount, type,
                                        operator, trades, true, recordResponse);
                            }

                        } else {
                            //单笔支付
                            TradeVO tradeVO = tradeQueryProvider.getById(new TradeGetByIdRequest(out_trade_no))
                                    .getContext().getTradeVO();
                            if (tradeVO.getTradeState().getFlowState() == FlowState.VOID || (tradeVO.getTradeState()
                                    .getPayState() == PayState.PAID && recordResponse.getChannelItemId() != 17L && recordResponse.getChannelItemId()
                                    != 18L && recordResponse.getChannelItemId() != 19L)) {
                                //同一批订单重复支付或过期作废，直接退款
                                alipayRefundHandle(out_trade_no, total_amount);
                            } else {
                                trades.add(tradeVO);
                                alipayCallbackHandle(out_trade_no, trade_no, trade_status, total_amount, type,
                                        operator, trades, false, recordResponse);
                            }

                        }

                    } finally {
                        //保存支付宝支付回调
                        addPayCallBackResult(PayCallBackResultAddRequest.builder()
                                .businessId(out_trade_no)
                                .resultXml(JSONObject.toJSONString(params))
                                .resultContext(JSONObject.toJSONString(params))
                                .resultStatus(PayCallBackResultStatus.TODO)
                                .errorNum(0)
                                .payType(PayCallBackType.ALI)
                                .build());

                        //解锁
                        rLock.unlock();
                    }
                } catch (UnsupportedEncodingException e) {
                    log.error("支付宝回调字节流转码异常：", e);
                    throw e;
                } catch (Exception e) {
                    log.error("支付宝回调异常：", e);
                    throw e;
                }
                try {
                    //支付宝回调 /set/aliPayCallBack/{storeId}
                    payCallBackResultProvider.modifyResultStatusByBusinessId(PayCallBackResultModifyResultStatusRequest.builder()
                            .businessId(new String(request.getParameter("out_trade_no")
                                    .getBytes("ISO-8859-1"), "UTF-8"))
                            .resultStatus(PayCallBackResultStatus.SUCCESS)
                            .build());
                    response.getWriter().print("success");
                    response.getWriter().flush();
                    response.getWriter().close();
                    log.info("支付回调返回success");
                } catch (IOException e) {
                    log.error("支付宝回调异常", e);
                    throw e;
                }
            } else {//验证失败
                log.info("支付回调签名校验失败,单号：{}", request.getParameter("out_trade_no"));
                try {
                    response.getWriter().print("failure");
                    response.getWriter().flush();
                    response.getWriter().close();
                } catch (IOException e) {
                    log.error("支付宝回调异常", e);
                    throw e;
                }
            }
        }
    }
//    @ApiOperation(value = "支付宝回调方法")
//    @RequestMapping(value = "/set/aliPayCallBack/{storeId}", method = RequestMethod.POST)
//    @LcnTransaction
//    public void setaliPayCallBack(HttpServletRequest request, HttpServletResponse response,@PathVariable("storeId") Long storeId) throws IOException {
//        log.info("===============支付宝回调开始==============");
//        GatewayConfigByGatewayRequest gatewayConfigByGatewayRequest = new GatewayConfigByGatewayRequest();
//        gatewayConfigByGatewayRequest.setGatewayEnum(PayGatewayEnum.ALIPAY);
//        gatewayConfigByGatewayRequest.setStoreId(storeId);
//        //查询支付宝配置信息
//        PayGatewayConfigResponse payGatewayConfigResponse =
//                payQueryProvider.getGatewayConfigByGateway(gatewayConfigByGatewayRequest).getContext();
//        //支付宝公钥
//        String aliPayPublicKey = payGatewayConfigResponse.getPublicKey();
//        Map<String, String> params = new HashMap<String, String>();
//        Map<String, String[]> requestParams = request.getParameterMap();
//        //返回的参数放到params中
//        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
//            String name = iter.next();
//            String[] values = requestParams.get(name);
//            String valueStr = "";
//            for (int i = 0; i < values.length; i++) {
//                valueStr = (i == values.length - 1) ? valueStr + values[i]
//                        : valueStr + values[i] + ",";
//            }
//            params.put(name, valueStr);
//        }
//
//        //支付和退款公用一个回调，所以要判断回调的类型
//        if (params.containsKey("refund_fee")) {
//            //退款只有app支付的订单有回调，退款的逻辑在同步方法中已经处理了，这儿不再做处理
//            log.info("APP退款回调,单号：{}", params.containsKey("out_trade_no"));
//            try {
//                response.getWriter().print("success");
//                response.getWriter().flush();
//                response.getWriter().close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } else {
//
//            boolean signVerified = false;
//            try {
//                signVerified = AlipaySignature.rsaCheckV1(params, aliPayPublicKey, "UTF-8", "RSA2"); //调用SDK验证签名
//            } catch (AlipayApiException e) {
//                log.error("支付宝回调签名校验异常：", e);
//            }
//            if (signVerified) {
//                try {
//                    //商户订单号
//                    String out_trade_no = new String(request.getParameter("out_trade_no")
//                            .getBytes("ISO-8859-1"), "UTF-8");
//                    //支付宝交易号
//                    String trade_no = new String(request.getParameter("trade_no")
//                            .getBytes("ISO-8859-1"), "UTF-8");
//                    //交易状态
//                    String trade_status = new String(request.getParameter("trade_status")
//                            .getBytes("ISO-8859-1"), "UTF-8");
//                    //订单金额
//                    String total_amount = new String(request.getParameter("total_amount")
//                            .getBytes("ISO-8859-1"), "UTF-8");
//                    //支付终端类型
//                    String type = new String(request.getParameter("passback_params")
//                            .getBytes("ISO-8859-1"), "UTF-8");
//
//                    boolean isMergePay = isMergePayOrder(out_trade_no);
//                    log.info("-------------支付回调,单号：{}，流水：{}，交易状态：{}，金额：{}，是否合并支付：{}------------",
//                            out_trade_no, trade_no, trade_status, total_amount, isMergePay);
//                    String lockName;
//                    //非组合支付，则查出该单笔订单。
//                    if (!isMergePay) {
//                        TradeVO tradeVO =
//                                tradeQueryProvider.getById(new TradeGetByIdRequest(out_trade_no)).getContext().getTradeVO();
//                        // 锁资源：无论是否组合支付，都锁父单号，确保串行回调
//                        lockName = tradeVO.getParentId();
//                    } else {
//                        lockName = out_trade_no;
//                    }
//                    Operator operator =
//                            Operator.builder().ip(HttpUtil.getIpAddr()).adminId("-1").name(PayGatewayEnum.ALIPAY.name())
//                                    .account(PayGatewayEnum.ALIPAY.name()).platform(Platform.THIRD).build();
//                    //redis锁，防止同一订单重复回调
//                    RLock rLock = redissonClient.getFairLock(lockName);
//                    rLock.lock();
//
//                    //执行
//                    try {
//                        List<TradeVO> trades = new ArrayList<>();
//                        //查询交易记录
//                        TradeRecordByOrderCodeRequest tradeRecordByOrderCodeRequest =
//                                new TradeRecordByOrderCodeRequest(out_trade_no);
//                        PayTradeRecordResponse recordResponse =
//                                payQueryProvider.getTradeRecordByOrderCode(tradeRecordByOrderCodeRequest).getContext();
//                        if (isMergePay) {
//                            /*
//                             * 合并支付
//                             * 查询订单是否已支付或过期作废
//                             */
//                            trades = tradeQueryProvider.getListByParentId(
//                                    new TradeListByParentIdRequest(out_trade_no)).getContext().getTradeVOList();
//                            //订单合并支付场景状态采样
//                            boolean paid =
//                                    trades.stream().anyMatch(i -> i.getTradeState().getPayState() == PayState.PAID);
//
//                            boolean cancel =
//                                    trades.stream().anyMatch(i -> i.getTradeState().getFlowState() == FlowState.VOID);
//                            //订单的支付渠道。17、18、19是我们自己对接的支付宝渠道， 表：pay_channel_item
//                            if (cancel || (paid && recordResponse.getChannelItemId() != 17L && recordResponse.getChannelItemId()
//                                    != 18L && recordResponse.getChannelItemId() != 19L)) {
//                                //重复支付，直接退款
//                                alipayRefundHandle(out_trade_no, total_amount);
//                            } else {
//                                alipayCallbackHandle(out_trade_no, trade_no, trade_status, total_amount, type,
//                                        operator, trades, true, recordResponse);
//                            }
//
//                        } else {
//                            //单笔支付
//                            TradeVO tradeVO = tradeQueryProvider.getById(new TradeGetByIdRequest(out_trade_no))
//                                    .getContext().getTradeVO();
//                            if (tradeVO.getTradeState().getFlowState() == FlowState.VOID || (tradeVO.getTradeState()
//                                    .getPayState() == PayState.PAID && recordResponse.getChannelItemId() != 17L && recordResponse.getChannelItemId()
//                                    != 18L && recordResponse.getChannelItemId() != 19L)) {
//                                //同一批订单重复支付或过期作废，直接退款
//                                alipayRefundHandle(out_trade_no, total_amount);
//                            } else {
//                                trades.add(tradeVO);
//                                alipayCallbackHandle(out_trade_no, trade_no, trade_status, total_amount, type,
//                                        operator, trades, false, recordResponse);
//                            }
//
//                        }
//
//                    } finally {
//                        //保存支付宝支付回调
//                        addPayCallBackResult(PayCallBackResultAddRequest.builder()
//                                .businessId(out_trade_no)
//                                .resultXml(JSONObject.toJSONString(params))
//                                .resultContext(JSONObject.toJSONString(params))
//                                .resultStatus(PayCallBackResultStatus.TODO)
//                                .errorNum(0)
//                                .payType(PayCallBackType.ALI)
//                                .build());
//
//                        //解锁
//                        rLock.unlock();
//                    }
//                } catch (UnsupportedEncodingException e) {
//                    log.error("支付宝回调字节流转码异常：", e);
//                    throw e;
//                } catch (Exception e) {
//                    log.error("支付宝回调异常：", e);
//                    throw e;
//                }
//                try {
//                    payCallBackResultProvider.modifyResultStatusByBusinessId(PayCallBackResultModifyResultStatusRequest.builder()
//                            .businessId(new String(request.getParameter("out_trade_no")
//                                    .getBytes("ISO-8859-1"), "UTF-8"))
//                            .resultStatus(PayCallBackResultStatus.SUCCESS)
//                            .build());
//                    response.getWriter().print("success");
//                    response.getWriter().flush();
//                    response.getWriter().close();
//                    log.info("支付回调返回success");
//                } catch (IOException e) {
//                    log.error("支付宝回调异常", e);
//                    throw e;
//                }
//            } else {//验证失败
//                log.info("支付回调签名校验失败,单号：{}", request.getParameter("out_trade_no"));
//                try {
//                    response.getWriter().print("failure");
//                    response.getWriter().flush();
//                    response.getWriter().close();
//                } catch (IOException e) {
//                    log.error("支付宝回调异常", e);
//                    throw e;
//                }
//            }
//        }
//    }

    /**
     * @Description:  囤货支付宝支付回调
     * @Param:
     * @Author: marsjiang
     * @Date: 20211002
     */
    @ApiOperation(value = "支付宝回调方法")
    @RequestMapping(value = "/pile/aliPayCallBack/{storeId}", method = RequestMethod.POST)
    @LcnTransaction
    public void pileAliPayCallBack(HttpServletRequest request, HttpServletResponse response,@PathVariable("storeId") Long storeId) throws IOException {
        log.info("===============囤货支付宝回调开始==============");
        GatewayConfigByGatewayRequest gatewayConfigByGatewayRequest = new GatewayConfigByGatewayRequest();
        gatewayConfigByGatewayRequest.setGatewayEnum(PayGatewayEnum.ALIPAY);
        gatewayConfigByGatewayRequest.setStoreId(storeId);
        //查询支付宝配置信息
        PayGatewayConfigResponse payGatewayConfigResponse =
                payQueryProvider.getGatewayConfigByGateway(gatewayConfigByGatewayRequest).getContext();
        //支付宝公钥
        String aliPayPublicKey = payGatewayConfigResponse.getPublicKey();
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();
        //返回的参数放到params中
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = iter.next();
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }

        //支付和退款公用一个回调，所以要判断回调的类型
        if (params.containsKey("refund_fee")) {
            //退款只有app支付的订单有回调，退款的逻辑在同步方法中已经处理了，这儿不再做处理
            log.info("APP退款回调,单号：{}", params.containsKey("out_trade_no"));
            try {
                response.getWriter().print("success");
                response.getWriter().flush();
                response.getWriter().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            boolean signVerified = false;
            try {
                signVerified = AlipaySignature.rsaCheckV1(params, aliPayPublicKey, "UTF-8", "RSA2"); //调用SDK验证签名
            } catch (AlipayApiException e) {
                log.error("支付宝回调签名校验异常：", e);
            }
            if (signVerified) {
                try {
                    //商户订单号
                    String out_trade_no = new String(request.getParameter("out_trade_no")
                            .getBytes("ISO-8859-1"), "UTF-8");
                    //支付宝交易号
                    String trade_no = new String(request.getParameter("trade_no")
                            .getBytes("ISO-8859-1"), "UTF-8");
                    //交易状态
                    String trade_status = new String(request.getParameter("trade_status")
                            .getBytes("ISO-8859-1"), "UTF-8");
                    //订单金额
                    String total_amount = new String(request.getParameter("total_amount")
                            .getBytes("ISO-8859-1"), "UTF-8");
                    //支付终端类型
                    String type = new String(request.getParameter("passback_params")
                            .getBytes("ISO-8859-1"), "UTF-8");

                    boolean isMergePay = isMergePayOrder(out_trade_no);
                    log.info("-------------囤货支付宝回调,单号：{}，流水：{}，交易状态：{}，金额：{}，是否合并支付：{}------------",
                            out_trade_no, trade_no, trade_status, total_amount, isMergePay);
                    String lockName;
                    //非组合支付，则查出该单笔订单。
                    if (!isMergePay) {
                        TradeVO tradeVO =
                                pileTradeQueryProvider.getById(new TradeGetByIdRequest(out_trade_no)).getContext().getTradeVO();
                        // 锁资源：无论是否组合支付，都锁父单号，确保串行回调
                        lockName = tradeVO.getParentId();
                    } else {
                        lockName = out_trade_no;
                    }
                    Operator operator =
                            Operator.builder().ip(HttpUtil.getIpAddr()).adminId("-1").name(PayGatewayEnum.ALIPAY.name())
                                    .account(PayGatewayEnum.ALIPAY.name()).platform(Platform.THIRD).build();
                    //redis锁，防止同一订单重复回调
                    RLock rLock = redissonClient.getFairLock(lockName);
                    rLock.lock();

                    //执行
                    try {
                        List<TradeVO> trades = new ArrayList<>();
                        //查询交易记录
                        TradeRecordByOrderCodeRequest tradeRecordByOrderCodeRequest =
                                new TradeRecordByOrderCodeRequest(out_trade_no);
                        PayTradeRecordResponse recordResponse =
                                payQueryProvider.getTradeRecordByOrderCode(tradeRecordByOrderCodeRequest).getContext();
                        if (isMergePay) {
                            /*
                             * 合并支付
                             * 查询订单是否已支付或过期作废
                             */
                            trades = pileTradeQueryProvider.getListByParentId(
                                    new TradeListByParentIdRequest(out_trade_no)).getContext().getTradeVOList();
                            //订单合并支付场景状态采样
                            boolean paid =
                                    trades.stream().anyMatch(i -> i.getTradeState().getPayState() == PayState.PAID);

                            boolean cancel =
                                    trades.stream().anyMatch(i -> i.getTradeState().getFlowState() == FlowState.VOID);
                            //订单的支付渠道。17、18、19是我们自己对接的支付宝渠道， 表：pay_channel_item
                            if (cancel || (paid && recordResponse.getChannelItemId() != 17L && recordResponse.getChannelItemId()
                                    != 18L && recordResponse.getChannelItemId() != 19L)) {
                                //重复支付，直接退款
                                alipayRefundHandle(out_trade_no, total_amount);
                            } else {
                                pileAlipayCallbackHandle(out_trade_no, trade_no, trade_status, total_amount, type,
                                        operator, trades, true, recordResponse);
                            }

                        } else {
                            //单笔支付
                            TradeVO tradeVO = pileTradeQueryProvider.getById(new TradeGetByIdRequest(out_trade_no))
                                    .getContext().getTradeVO();
                            if (tradeVO.getTradeState().getFlowState() == FlowState.VOID || (tradeVO.getTradeState()
                                    .getPayState() == PayState.PAID && recordResponse.getChannelItemId() != 17L && recordResponse.getChannelItemId()
                                    != 18L && recordResponse.getChannelItemId() != 19L)) {
                                //同一批订单重复支付或过期作废，直接退款
                                alipayRefundHandle(out_trade_no, total_amount);
                            } else {
                                trades.add(tradeVO);
                                pileAlipayCallbackHandle(out_trade_no, trade_no, trade_status, total_amount, type,
                                        operator, trades, false, recordResponse);
                            }

                        }

                    } finally {
                        //保存支付宝支付回调
                        addPayCallBackResult(PayCallBackResultAddRequest.builder()
                                .businessId(out_trade_no)
                                .resultXml(JSONObject.toJSONString(params))
                                .resultContext(JSONObject.toJSONString(params))
                                .resultStatus(PayCallBackResultStatus.TODO)
                                .errorNum(0)
                                .payType(PayCallBackType.ALI)
                                .build());
                        //解锁
                        rLock.unlock();
                    }
                } catch (UnsupportedEncodingException e) {
                    log.error("支付宝回调字节流转码异常：", e);
                    throw e;
                } catch (Exception e) {
                    log.error("囤货支付宝回调异常：", e);
                    throw e;
                }
                try {
                    //囤货支付宝回调
                    payCallBackResultProvider.modifyResultStatusByBusinessId(PayCallBackResultModifyResultStatusRequest.builder()
                            .businessId(new String(request.getParameter("out_trade_no")
                                    .getBytes("ISO-8859-1"), "UTF-8"))
                            .resultStatus(PayCallBackResultStatus.SUCCESS)
                            .build());
                    response.getWriter().print("success");
                    response.getWriter().flush();
                    response.getWriter().close();
                    log.info("囤货支付回调返回success");
                } catch (IOException e) {
                    log.error("支付宝回调异常", e);
                    throw e;
                }
            } else {//验证失败
                log.info("支付回调签名校验失败,单号：{}", request.getParameter("out_trade_no"));
                try {
                    response.getWriter().print("failure");
                    response.getWriter().flush();
                    response.getWriter().close();
                } catch (IOException e) {
                    log.error("囤货支付宝回调异常", e);
                    throw e;
                }
            }
        }
    }



    /*
     * @Description:  支付宝充值回调方法
     * @Param:
     * @Author: Bob
     * @Date: 2019-02-26 12:00
     */
    @ApiOperation(value = "支付宝充值回调方法")
    @RequestMapping(value = "/aliPayRechargeCallBack/{storeId}", method = RequestMethod.POST)
    @LcnTransaction
    public void aliPayRechargeCallBack(HttpServletRequest request, HttpServletResponse response,@PathVariable("storeId") Long storeId) throws IOException {
        log.info("===============支付宝充值回调开始==============");
        GatewayConfigByGatewayRequest gatewayConfigByGatewayRequest = new GatewayConfigByGatewayRequest();
        gatewayConfigByGatewayRequest.setGatewayEnum(PayGatewayEnum.ALIPAY);
        gatewayConfigByGatewayRequest.setStoreId(storeId);
        //查询支付宝配置信息
        PayGatewayConfigResponse payGatewayConfigResponse =
                payQueryProvider.getGatewayConfigByGateway(gatewayConfigByGatewayRequest).getContext();
        //支付宝公钥
        String aliPayPublicKey = payGatewayConfigResponse.getPublicKey();
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();
        //返回的参数放到params中
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = iter.next();
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }

        //支付和退款公用一个回调，所以要判断回调的类型
        if (params.containsKey("refund_fee")) {
            //退款只有app支付的订单有回调，退款的逻辑在同步方法中已经处理了，这儿不再做处理
            log.info("APP退款回调,单号：{}", params.containsKey("out_trade_no"));
            try {
                response.getWriter().print("success");
                response.getWriter().flush();
                response.getWriter().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            boolean signVerified = false;
            try {
                signVerified = AlipaySignature.rsaCheckV1(params, aliPayPublicKey, "UTF-8", "RSA2"); //调用SDK验证签名
            } catch (AlipayApiException e) {
                log.error("支付宝回调签名校验异常：", e);
            }
            if (signVerified) {
                try {
                    //商户订单号
                    String out_trade_no = new String(request.getParameter("out_trade_no")
                            .getBytes("ISO-8859-1"), "UTF-8");
                    //支付宝交易号
                    String trade_no = new String(request.getParameter("trade_no")
                            .getBytes("ISO-8859-1"), "UTF-8");
                    //交易状态
                    String trade_status = new String(request.getParameter("trade_status")
                            .getBytes("ISO-8859-1"), "UTF-8");
                    //订单金额
                    String total_amount = new String(request.getParameter("total_amount")
                            .getBytes("ISO-8859-1"), "UTF-8");
                    //支付终端类型
                    String type = new String(request.getParameter("passback_params")
                            .getBytes("ISO-8859-1"), "UTF-8");
                    boolean isMergePay = isMergePayOrder(out_trade_no);
                    log.info("-------------支付回调,单号：{}，流水：{}，交易状态：{}，金额：{}，是否合并支付：{}------------",
                            out_trade_no, trade_no, trade_status, total_amount, isMergePay);
                    // 锁资源：无论是否组合支付，都锁父单号，确保串行回调
                    String lockName = out_trade_no;
                    QueryWalletRecordRequest recordRequest = new QueryWalletRecordRequest();
                    recordRequest.setRecordNo(out_trade_no);
                    List<WalletRecordVO> walletRecordVOs = walletRecordProvider.queryWalletRecord(recordRequest).getContext().getWalletRecordVOs();
                    if(CollectionUtils.isEmpty(walletRecordVOs)){
                        log.error("充值记录表中交易号:{}不存在",out_trade_no);
                        throw new SbcRuntimeException("充值记录不存在");
                    }
                    //非组合支付，则查出该单笔订单。
                    Operator operator =
                            Operator.builder().ip(HttpUtil.getIpAddr()).adminId("-1").name(PayGatewayEnum.ALIPAY.name())
                                    .account(PayGatewayEnum.ALIPAY.name()).platform(Platform.THIRD).build();
                    //redis锁，防止同一订单重复回调
                    RLock rLock = redissonClient.getFairLock(lockName);
                    rLock.lock();
                    //执行
                    try {
                        //单笔支付
                        //充值记录表
                        WalletRecordVO walletRecordVO = walletRecordVOs.get(0);
                        //执行逻辑
                        alipayRechargeCallbackHandle(out_trade_no, trade_no, trade_status, total_amount, type,operator, walletRecordVO);
                    } finally {
                        //解锁
                        rLock.unlock();
                    }
                } catch (UnsupportedEncodingException e) {
                    log.error("支付宝充值回调字节流转码异常：", e);
                    throw e;
                } catch (Exception e) {
                    log.error("支付宝充值回调异常：", e);
                    throw e;
                }
                try {
                    response.getWriter().print("success");
                    response.getWriter().flush();
                    response.getWriter().close();
                    log.info("支付回调返回success");
                } catch (IOException e) {
                    log.error("支付宝回调异常", e);
                    throw e;
                }
            } else {//验证失败
                log.info("支付回调签名校验失败,单号：{}", request.getParameter("out_trade_no"));
                try {
                    response.getWriter().print("failure");
                    response.getWriter().flush();
                    response.getWriter().close();
                } catch (IOException e) {
                    log.error("支付宝回调异常", e);
                    throw e;
                }
            }
        }
    }

    /*
     * @Description:  支付宝提货回调方法
     * @Param:
     * @Author: Bob
     * @Date: 2019-02-26 12:00
     */
    @ApiOperation(value = "支付宝提货回调方法")
    @RequestMapping(value = "/aliPayTakeGoodCallBack/{storeId}", method = RequestMethod.POST)
    @LcnTransaction
    public void aliPayTakeGoodCallBack(HttpServletRequest request, HttpServletResponse response,@PathVariable("storeId") Long storeId) throws IOException {
        log.info("===============支付宝回调开始==============");
        GatewayConfigByGatewayRequest gatewayConfigByGatewayRequest = new GatewayConfigByGatewayRequest();
        gatewayConfigByGatewayRequest.setGatewayEnum(PayGatewayEnum.ALIPAY);
        gatewayConfigByGatewayRequest.setStoreId(storeId);
        //查询支付宝配置信息
        PayGatewayConfigResponse payGatewayConfigResponse =
                payQueryProvider.getGatewayConfigByGateway(gatewayConfigByGatewayRequest).getContext();
        //支付宝公钥
        String aliPayPublicKey = payGatewayConfigResponse.getPublicKey();
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();
        //返回的参数放到params中
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = iter.next();
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }

        //支付和退款公用一个回调，所以要判断回调的类型
        if (params.containsKey("refund_fee")) {
            //退款只有app支付的订单有回调，退款的逻辑在同步方法中已经处理了，这儿不再做处理
            log.info("APP退款回调,单号：{}", params.containsKey("out_trade_no"));
            try {
                response.getWriter().print("success");
                response.getWriter().flush();
                response.getWriter().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            boolean signVerified = false;
            try {
                signVerified = AlipaySignature.rsaCheckV1(params, aliPayPublicKey, "UTF-8", "RSA2"); //调用SDK验证签名
            } catch (AlipayApiException e) {
                log.error("支付宝回调签名校验异常：", e);
            }
            if (signVerified) {
                try {
                    //商户订单号
                    String out_trade_no = new String(request.getParameter("out_trade_no")
                            .getBytes("ISO-8859-1"), "UTF-8");
                    //支付宝交易号
                    String trade_no = new String(request.getParameter("trade_no")
                            .getBytes("ISO-8859-1"), "UTF-8");
                    //交易状态
                    String trade_status = new String(request.getParameter("trade_status")
                            .getBytes("ISO-8859-1"), "UTF-8");
                    //订单金额
                    String total_amount = new String(request.getParameter("total_amount")
                            .getBytes("ISO-8859-1"), "UTF-8");
                    //支付终端类型
                    String type = new String(request.getParameter("passback_params")
                            .getBytes("ISO-8859-1"), "UTF-8");

                    boolean isMergePay = isMergePayOrder(out_trade_no);
                    log.info("-------------支付回调,单号：{}，流水：{}，交易状态：{}，金额：{}，是否合并支付：{}------------",
                            out_trade_no, trade_no, trade_status, total_amount, isMergePay);
                    String lockName;
                    //非组合支付，则查出该单笔订单。
                    if (!isMergePay) {
                        TradeVO tradeVO =
                                tradeQueryProvider.getById(new TradeGetByIdRequest(out_trade_no)).getContext().getTradeVO();
                        // 锁资源：无论是否组合支付，都锁父单号，确保串行回调
                        lockName = tradeVO.getParentId();
                    } else {
                        lockName = out_trade_no;
                    }
                    Operator operator =
                            Operator.builder().ip(HttpUtil.getIpAddr()).adminId("-1").name(PayGatewayEnum.ALIPAY.name())
                                    .account(PayGatewayEnum.ALIPAY.name()).platform(Platform.THIRD).build();
                    //redis锁，防止同一订单重复回调
                    RLock rLock = redissonClient.getFairLock(lockName);
                    rLock.lock();

                    //执行
                    try {
                        List<TradeVO> trades = new ArrayList<>();
                        //查询交易记录
                        TradeRecordByOrderCodeRequest tradeRecordByOrderCodeRequest =
                                new TradeRecordByOrderCodeRequest(out_trade_no);
                        PayTradeRecordResponse recordResponse =
                                payQueryProvider.getTradeRecordByOrderCode(tradeRecordByOrderCodeRequest).getContext();
                        if (isMergePay) {
                            /*
                             * 合并支付
                             * 查询订单是否已支付或过期作废
                             */
                            trades = tradeQueryProvider.getListByParentId(
                                    new TradeListByParentIdRequest(out_trade_no)).getContext().getTradeVOList();
                            //订单合并支付场景状态采样
                            boolean paid =
                                    trades.stream().anyMatch(i -> i.getTradeState().getPayState() == PayState.PAID);

                            boolean cancel =
                                    trades.stream().anyMatch(i -> i.getTradeState().getFlowState() == FlowState.VOID);
                            //订单的支付渠道。17、18、19是我们自己对接的支付宝渠道， 表：pay_channel_item
                            if (cancel || (paid && recordResponse.getChannelItemId() != 17L && recordResponse.getChannelItemId()
                                    != 18L && recordResponse.getChannelItemId() != 19L)) {
                                //重复支付，直接退款
                                alipayRefundHandle(out_trade_no, total_amount);
                            } else {
                                alipayTakeCallbackHandle(out_trade_no, trade_no, trade_status, total_amount, type,
                                        operator, trades, true, recordResponse);
                            }

                        } else {
                            //单笔支付
                            TradeVO tradeVO = tradeQueryProvider.getById(new TradeGetByIdRequest(out_trade_no))
                                    .getContext().getTradeVO();
                            if (tradeVO.getTradeState().getFlowState() == FlowState.VOID || (tradeVO.getTradeState()
                                    .getPayState() == PayState.PAID && recordResponse.getChannelItemId() != 17L && recordResponse.getChannelItemId()
                                    != 18L && recordResponse.getChannelItemId() != 19L)) {
                                //同一批订单重复支付或过期作废，直接退款
                                alipayRefundHandle(out_trade_no, total_amount);
                            } else {
                                trades.add(tradeVO);
                                alipayTakeCallbackHandle(out_trade_no, trade_no, trade_status, total_amount, type,
                                        operator, trades, false, recordResponse);
                            }

                        }

                    } finally {
                        //解锁
                        rLock.unlock();
                    }
                } catch (UnsupportedEncodingException e) {
                    log.error("支付宝回调字节流转码异常：", e);
                    throw e;
                } catch (Exception e) {
                    log.error("支付宝回调异常：", e);
                    throw e;
                }
                try {
                    response.getWriter().print("success");
                    response.getWriter().flush();
                    response.getWriter().close();
                    log.info("支付回调返回success");
                } catch (IOException e) {
                    log.error("支付宝回调异常", e);
                    throw e;
                }
            } else {//验证失败
                log.info("支付回调签名校验失败,单号：{}", request.getParameter("out_trade_no"));
                try {
                    response.getWriter().print("failure");
                    response.getWriter().flush();
                    response.getWriter().close();
                } catch (IOException e) {
                    log.error("支付宝回调异常", e);
                    throw e;
                }
            }
        }
    }



    /**
     * 微信支付--微信企业付款到零钱
     * @return
     */
    @RequestMapping(value = "/wxPayCompanyPayment", method = RequestMethod.POST)
    public BaseResponse<WxPayCompanyPaymentRsponse> wxPayCompanyPayment(@RequestBody WxPayCompanyPaymentInfoRequest request) {
        /*request.setPartner_trade_no("12312312");
        request.setOpenid("oI2m-1MF1vrtu5PIWm2v3G5ln_K0");
        request.setCheck_name("FORCE_CHECK");
        request.setRe_user_name("吕振伟");
        request.setAmount("1");
        request.setDesc("万米小店提现");*/
        request.setSpbill_create_ip(HttpUtil.getIpAddr());
        BaseResponse<WxPayCompanyPaymentRsponse> response = wxPayProvider.wxPayCompanyPayment(request);
        return response;
    }

    private boolean isMergePayOrder(String businessId) {
        return businessId.startsWith(GeneratorService._PREFIX_PARENT_TRADE_ID);
    }

    private void wxRefundHandle(WxPayResultResponse wxPayResultResponse, String businessId, Long storeId) {
        WxPayRefundInfoRequest refundInfoRequest = new WxPayRefundInfoRequest();

        refundInfoRequest.setStoreId(storeId);
        refundInfoRequest.setOut_refund_no(businessId);
        refundInfoRequest.setOut_trade_no(businessId);
        refundInfoRequest.setTotal_fee(wxPayResultResponse.getTotal_fee());
        refundInfoRequest.setRefund_fee(wxPayResultResponse.getTotal_fee());
        String tradeType = wxPayResultResponse.getTrade_type();
        if (!tradeType.equals("APP")) {
            tradeType = "PC/H5/JSAPI";
        }
        refundInfoRequest.setPay_type(tradeType);
        //重复支付进行退款处理标志
        refundInfoRequest.setRefund_type("REPEATPAY");
        BaseResponse<WxPayRefundResponse> wxPayRefund =
                wxPayProvider.wxPayRefund(refundInfoRequest);
        WxPayRefundResponse wxPayRefundResponse = wxPayRefund.getContext();
        if (wxPayRefundResponse.getResult_code().equals(WXPayConstants.SUCCESS) &&
                wxPayRefundResponse.getResult_code().equals(WXPayConstants.SUCCESS)) {
            //重复支付退款成功处理逻辑
            operateLogMQUtil.convertAndSend("微信支付", "重复支付退款成功",
                    "订单号：" + wxPayResultResponse.getOut_trade_no());
        } else {
            //重复支付退款失败处理逻辑
            operateLogMQUtil.convertAndSend("微信支付", "重复支付退款失败",
                    "订单号：" + wxPayResultResponse.getOut_trade_no());
        }
    }

    private void wxCallbackResultHandle(HttpServletResponse response, String result) throws IOException {
        if (result.equals(WXPayConstants.SUCCESS)) {
            response.getWriter().write(WXPayUtil.setXML("SUCCESS", "OK"));
            log.info("微信支付异步通知回调成功的消息回复结束");
        } else {
            response.getWriter().write(WXPayUtil.setXML("FAIL", "ERROR"));
            log.info("微信支付异步通知回调失败的消息回复结束");
        }
    }

    private void wxPayCallbackHandle(PayGatewayConfigResponse payGatewayConfig, WxPayResultResponse wxPayResultResponse,
                                     String businessId, List<TradeVO> trades, boolean isMergePay) {
        //异步回调添加交易数据
        PayTradeRecordRequest payTradeRecordRequest = new PayTradeRecordRequest();
        //微信支付订单号--及流水号
        payTradeRecordRequest.setTradeNo(wxPayResultResponse.getTransaction_id());
        //商户订单号或父单号
        payTradeRecordRequest.setBusinessId(businessId);
        payTradeRecordRequest.setResult_code(wxPayResultResponse.getResult_code());
        payTradeRecordRequest.setPracticalPrice(new BigDecimal(wxPayResultResponse.getTotal_fee()).
                divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_DOWN));
        ChannelItemByGatewayRequest channelItemByGatewayRequest = new ChannelItemByGatewayRequest();
        channelItemByGatewayRequest.setGatewayName(payGatewayConfig.getPayGateway().getName());
        PayChannelItemListResponse payChannelItemListResponse =
                payQueryProvider.listChannelItemByGatewayName(channelItemByGatewayRequest).getContext();
        List<PayChannelItemVO> payChannelItemVOList =
                payChannelItemListResponse.getPayChannelItemVOList();
        String tradeType = wxPayResultResponse.getTrade_type();
        ChannelItemSaveRequest channelItemSaveRequest = new ChannelItemSaveRequest();
        String code = "wx_qr_code";
        if (tradeType.equals("APP")) {
            code = "wx_app";
        } else if (tradeType.equals("JSAPI")) {
            code = "js_api";
        } else if (tradeType.equals("MWEB")) {
            code = "wx_mweb";
        }
        channelItemSaveRequest.setCode(code);
        payChannelItemVOList.forEach(payChannelItemVO -> {
            if (channelItemSaveRequest.getCode().equals(payChannelItemVO.getCode())) {
                //更新支付项
                payTradeRecordRequest.setChannelItemId(payChannelItemVO.getId());
            }
        });
        //微信支付异步回调添加交易数据
        payProvider.wxPayCallBack(payTradeRecordRequest);
        //订单 支付单 操作信息
        String ipAddr = "";
        try {
            ipAddr = HttpUtil.getIpAddr();
        } catch (Exception e) {
            log.error("获取ip地址异常", e);
        }
        Operator operator = Operator.builder().ip(ipAddr).adminId("-1").name(PayGatewayEnum.WECHAT.name())
                .account(PayGatewayEnum.WECHAT.name()).platform(Platform.THIRD).build();
        payCallbackOnline(trades, operator, isMergePay);
    }

    /**
     * 囤货线上订单支付回调
     * 订单 支付单 操作信息
     * @return 操作结果
     */
    private void pilePayCallbackOnline(List<TradeVO> trades, Operator operator, boolean isMergePay) {
        //封装回调参数
        /*  List<PickUpRecordAddRequest> pickUpRecordAddBatchRequest = new ArrayList<>();*/
        List<TradePayCallBackOnlineDTO> reqOnlineDTOList = trades.stream().map(i -> {
            //每笔订单做是否合并支付标识
            i.getPayInfo().setMergePay(isMergePay);
            log.info("PayCallbackController.payCallbackOnline tid:{}",i.getId());
            TradeAddMergePayRequest mergePayRequest = new TradeAddMergePayRequest();
            mergePayRequest.setMergePay(isMergePay);
            mergePayRequest.setTid(i.getId());
            pileTradeProvider.addMergePay(mergePayRequest);
            log.info("PayCallbackController.payCallbackOnline end");

            //支付单信息
            PayOrderVO payOrder = pileTradeQueryProvider.getPayOrderById(TradeGetPayOrderByIdRequest
                    .builder().payOrderId(i.getPayOrderId()).build()).getContext().getPayOrder();
            return TradePayCallBackOnlineDTO.builder()
                    .trade(KsBeanUtil.convert(i, TradeDTO.class))
                    .payOrderOld(KsBeanUtil.convert(payOrder, PayOrderDTO.class))
                    .build();
        }).collect(Collectors.toList());
        //批量回调
        pileTradeProvider.payCallBackOnlineBatch(new TradePayCallBackOnlineBatchRequest(reqOnlineDTOList,
                operator));
    }

    /**
     * 线上订单支付回调
     * 订单 支付单 操作信息
     * @return 操作结果
     */
    private void payCallbackOnline(List<TradeVO> trades, Operator operator, boolean isMergePay) {
        //封装回调参数
        /*  List<PickUpRecordAddRequest> pickUpRecordAddBatchRequest = new ArrayList<>();*/
        List<TradePayCallBackOnlineDTO> reqOnlineDTOList = trades.stream().map(i -> {
            //每笔订单做是否合并支付标识
            i.getPayInfo().setMergePay(isMergePay);
            log.info("PayCallbackController.payCallbackOnline tid:{}",i.getId());
            TradeAddMergePayRequest mergePayRequest = new TradeAddMergePayRequest();
            mergePayRequest.setMergePay(isMergePay);
            mergePayRequest.setTid(i.getId());
            tradeProvider.addMergePay(mergePayRequest);
            log.info("PayCallbackController.payCallbackOnline end");

//            tradeProvider.update(TradeUpdateRequest.builder().trade(KsBeanUtil.convert(i,TradeUpdateDTO.class)).build());

           /* TradeUpdateRequest tradeUpdateRequest = new TradeUpdateRequest(KsBeanUtil.convert(i, TradeUpdateDTO.class));
            //判断是否是先货后款
            boolean goodsFirst=Objects.equals(i.getTradeState().getAuditState() , AuditState.CHECKED) && i.getPaymentOrder() == PaymentOrder.NO_LIMIT;
            //是否是自提订单如果不是发送自提码
            if (!goodsFirst) {
                //是否是自提订单如果是组装参数，发送自提码
                if (null != i.getDeliverWay() && i.getDeliverWay().equals(DeliverWay.PICK_SELF)) {
                    PickUpRecordAddRequest pickUpRecordAddRequest = sendPickUpCode(i);
                    pickUpRecordAddBatchRequest.add(pickUpRecordAddRequest);
                    tradeUpdateRequest.getTrade().getTradeWareHouse().setPickUpCode(pickUpRecordAddRequest.getPickUpCode());
                    i.getTradeWareHouse().setPickUpCode(pickUpRecordAddRequest.getPickUpCode());
                }
            }
            tradeProvider.update(tradeUpdateRequest);*/
            //支付单信息
            PayOrderVO payOrder = tradeQueryProvider.getPayOrderById(TradeGetPayOrderByIdRequest
                    .builder().payOrderId(i.getPayOrderId()).build()).getContext().getPayOrder();
            return TradePayCallBackOnlineDTO.builder()
                    .trade(KsBeanUtil.convert(i, TradeDTO.class))
                    .payOrderOld(KsBeanUtil.convert(payOrder, PayOrderDTO.class))
                    .build();
        }).collect(Collectors.toList());
        //批量回调
        tradeProvider.payCallBackOnlineBatch(new TradePayCallBackOnlineBatchRequest(reqOnlineDTOList,
                operator));

        //自提码处理
     /*   if (CollectionUtils.isNotEmpty(pickUpRecordAddBatchRequest)) {
            //数据库新增
            pickUpRecordProvider.addBatch(PickUpRecordAddBatchRequest.builder().pickUpRecordAddRequestList(pickUpRecordAddBatchRequest).build());
            //发送自提码
            for (TradeVO inner:trades){
                if (inner.getDeliverWay().equals(DeliverWay.PICK_SELF)){
                    tradeProvider.sendPickUpMessage(SendPickUpMessageRequest.builder().trade(inner).build());
                }
            }
        }*/
    }

    /**
     * 线上订单支付回调
     * 订单 支付单 操作信息
     * @return 操作结果
     */
    private void payTakeGoodCallbackOnline(List<TradeVO> trades, Operator operator, boolean isMergePay) {
        //封装回调参数
        /*  List<PickUpRecordAddRequest> pickUpRecordAddBatchRequest = new ArrayList<>();*/
        List<TradePayCallBackOnlineDTO> reqOnlineDTOList = trades.stream().map(i -> {
            //每笔订单做是否合并支付标识
            i.getPayInfo().setMergePay(isMergePay);
            log.info("PayCallbackController.payCallbackOnline tid:{}",i.getId());
            TradeAddMergePayRequest mergePayRequest = new TradeAddMergePayRequest();
            mergePayRequest.setMergePay(isMergePay);
            mergePayRequest.setTid(i.getId());
            tradeProvider.addMergePay(mergePayRequest);
            log.info("PayCallbackController.payCallbackOnline end");
            //支付单信息
            PayOrderVO payOrder = tradeQueryProvider.getPayOrderById(TradeGetPayOrderByIdRequest
                    .builder().payOrderId(i.getPayOrderId()).build()).getContext().getPayOrder();
            return TradePayCallBackOnlineDTO.builder()
                    .trade(KsBeanUtil.convert(i, TradeDTO.class))
                    .payOrderOld(KsBeanUtil.convert(payOrder, PayOrderDTO.class))
                    .build();
        }).collect(Collectors.toList());
        //批量回调
        tradeProvider.payTakeGoodCallBackOnlineBatch(new TradePayCallBackOnlineBatchRequest(reqOnlineDTOList,
                operator));

    }


    private void payRechargeCallbackOnline(WalletRecordVO walletRecordVO){
        //用户
        String customerAccount = walletRecordVO.getCustomerAccount();
        //交易id
        String recordNo = walletRecordVO.getRecordNo();
        //虚拟商品id
        Integer virtualGoodsId = walletRecordVO.getVirtualGoodsId();
        WalletByCustomerAccountQueryRequest request = WalletByCustomerAccountQueryRequest.builder().customerAccount(customerAccount).build();
        //添加对应的充值余额到充值金额中,添加对应的充值金额到余额中,获取用户钱包表
        CustomerWalletVO walletVO = customerWalletQueryProvider.getCustomerWalletByCustomerAccount(request).getContext().getCustomerWalletVO();
        this.giveCoupon(virtualGoodsId.longValue(),walletVO.getCustomerId());
        TicketsFormModifyRequest ticketsForm = new TicketsFormModifyRequest();
        ticketsForm.setWalletId(walletVO.getWalletId());
        ticketsForm.setVirtualGoodsId(virtualGoodsId);
        ticketsForm.setApplyPrice(walletRecordVO.getDealPrice());
        ticketsForm.setApplyTime(LocalDateTime.now());
        ticketsForm.setRechargeStatus(2);
        ticketsForm.setRemark(new StringBuilder().append("用户：").append(customerAccount).append("充值").append(walletRecordVO.getDealPrice())
                .append("元,充值时间").append(LocalDateTime.now()).toString());
        ticketsForm.setRecordNo(recordNo);
        //新增交易记录
        ticketsFormProvider.saveTicketsForm(ticketsForm);
        QueryWalletRecordRequest build = new QueryWalletRecordRequest().builder().customerAccount(customerAccount).build();
        //判断是不是第一次充值,是的话赠送对应金额
        List<WalletRecordVO> walletRecordVOs = walletRecordProvider.queryWalletRecordByCustomerAccount(build).getContext().getWalletRecordVOs();
        //赠送的金额
        BigDecimal giveMoney = null;
        //充值的金额
        BigDecimal rechargeMoney = walletRecordVO.getDealPrice();
        //最终余额
        BigDecimal finalBalance;
        //最终充值金额
        BigDecimal finalRechargeMoney;
        //最终赠送金额
        BigDecimal finalGiveMoney;
        //根据虚拟商品id查询出虚拟商品对应的赠送金额进行赠送,给赠送金额添加余额,给赠送的金额添加金额
        List<Long> ids = new ArrayList<>();
        ids.add(Long.valueOf(virtualGoodsId));
        List<VirtualGoodsVO> goodsList = virtualGoodsQueryProvider.getVirtualGoodsList(VirtualGoodsRequest.builder().goodsIdList(ids).build()).getContext().getVirtualGoodsList();
        VirtualGoodsVO virtualGoods = null;
        if(CollectionUtils.isNotEmpty(goodsList)){
            virtualGoods = goodsList.get(0);
        }
        BigDecimal givePrice = virtualGoods.getGivePrice();
        //判断是否首充赠送
        if(virtualGoods.getFirstSendFlag().equals(0)){
            //是首充才送
            //首次充值,赠送礼包
            if(CollectionUtils.isEmpty(walletRecordVOs)){
                //判断是否首充赠送
                if(Objects.nonNull(givePrice)){
                    giveMoney = givePrice;
                }
            }
        }else {
            //判断是否首充赠送
            if(Objects.nonNull(givePrice)){
                giveMoney = givePrice;
            }
        }
        //钱包不为空进行添加余额,添加充值金额,添加赠送金额
        if(Objects.nonNull(walletVO)){
            //余额
            BigDecimal balanceOld = walletVO.getBalance();
            //余额添加充值金额
            BigDecimal balance = balanceOld.add(rechargeMoney);
            //赠送金额不为空,余额添加赠送金额
            if(Objects.nonNull(giveMoney)){
                finalBalance = balance.add(giveMoney);
            }else {
                //充值金额为空,余额等于充值金额加当前余额
                finalBalance = balance;
            }
            //最终余额
            walletVO.setBalance(finalBalance);
            //充值金额
            BigDecimal rechargeBalance = walletVO.getRechargeBalance();
            if(Objects.nonNull(rechargeBalance)){
                finalRechargeMoney = rechargeMoney.add(rechargeBalance);
            }else {
                finalRechargeMoney = rechargeMoney;
            }
            //最终充值金额
            walletVO.setRechargeBalance(finalRechargeMoney);

            //赠送金额
            BigDecimal giveBalance = walletVO.getGiveBalance();
            //本次赠送金额不为空
            if(Objects.nonNull(giveMoney)){
                //以往赠送金额不为空
                if(Objects.nonNull(giveBalance)){
                    finalGiveMoney = giveBalance.add(giveMoney);
                }else {
                    finalGiveMoney = giveMoney;
                }
                //赠送金额
                walletVO.setGiveBalance(finalGiveMoney);
            }
            CustomerWalletModifyRequest modifyRequest = new CustomerWalletModifyRequest().builder().customerWalletVO(walletVO).build();
            customerWalletProvider.updateCustomerWalletByWalletId(modifyRequest);
        }
    }

    /**
     * 赠送优惠券
     * @param virtualGoodsId
     */
    public List<GetCouponGroupResponse> giveCoupon(Long virtualGoodsId, String customerId) {
        VirtualGoodsVO virtualGoods = virtualGoodsQueryProvider.getVirtualGoods(VirtualGoodsRequest.builder().goodsId(virtualGoodsId).build()).getContext().getVirtualGoods();
        String activityId = virtualGoods.getActivityId();
        //当活动id不等于空的时候，赠送优惠券
        if(StringUtils.isNotEmpty(activityId)){
            List<GetCouponGroupResponse> couponList = couponActivityProvider.giveRechargeCoupon(SendCouponRechargeRequest.builder().activityId(activityId).customerId(customerId).build()).getContext().getCouponList();
            return couponList;
        }
        return null;
    }


    /**
     * 组装自提码参数
     */
    private PickUpRecordAddRequest sendPickUpCode(TradeVO trade){
        String verifyCode = RandomStringUtils.randomNumeric(6);
        return PickUpRecordAddRequest.builder().storeId(trade.getSupplier().getStoreId())
                .tradeId(trade.getId()).pickUpCode(verifyCode).pickUpFlag(DefaultFlag.NO)
                .delFlag(DeleteFlag.NO).contactPhone(trade.getConsignee().getPhone()).createTime(LocalDateTime.now()).build();
    }

    /**
     * 支付宝退款处理
     * @param out_trade_no
     * @param total_amount
     */
    private void alipayRefundHandle(String out_trade_no, String total_amount) {
        //调用退款接口。直接退款。不走退款流程，没有交易对账，只记了操作日志
        AliPayRefundResponse aliPayRefundResponse =
                aliPayProvider.aliPayRefund(AliPayRefundRequest.builder().businessId(out_trade_no)
                        .amount(new BigDecimal(total_amount)).description("重复支付退款").build()).getContext();

        if (aliPayRefundResponse.getAlipayTradeRefundResponse().isSuccess()) {
            operateLogMQUtil.convertAndSend("支付宝退款", "重复支付、超时订单退款",
                    "订单号：" + out_trade_no);
        }
        log.info("支付宝重复支付、超时订单退款,单号：{}", out_trade_no);
    }

    /**
     * 囤货支付宝回调处理方法
     * @param out_trade_no
     * @param trade_no
     * @param trade_status
     * @param total_amount
     * @param type
     * @param operator
     * @param trades
     * @param isMergePay
     * @param recordResponse
     */
    private void pileAlipayCallbackHandle(String out_trade_no, String trade_no, String trade_status, String total_amount,
                                      String type, Operator operator, List<TradeVO> trades, boolean isMergePay,
                                      PayTradeRecordResponse recordResponse) {
        if (recordResponse.getApplyPrice().compareTo(new BigDecimal(total_amount)) == 0 && trade_status.equals(
                "TRADE_SUCCESS")) {
            //异步回调添加交易数据
            PayTradeRecordRequest payTradeRecordRequest = new PayTradeRecordRequest();
            //流水号
            payTradeRecordRequest.setTradeNo(trade_no);
            //商品订单号
            payTradeRecordRequest.setBusinessId(out_trade_no);
            payTradeRecordRequest.setResult_code("SUCCESS");
            payTradeRecordRequest.setPracticalPrice(new BigDecimal(total_amount));
            payTradeRecordRequest.setChannelItemId(Long.valueOf(type));
            //添加交易数据（与微信共用）
            payProvider.wxPayCallBack(payTradeRecordRequest);
            log.info("isMergePay==== {}", isMergePay);
            pilePayCallbackOnline(trades, operator, isMergePay);
            log.info("囤货支付回调成功,单号：{}", out_trade_no);
        }
    }

    private void alipayCallbackHandle(String out_trade_no, String trade_no, String trade_status, String total_amount,
                                      String type, Operator operator, List<TradeVO> trades, boolean isMergePay,
                                      PayTradeRecordResponse recordResponse) {
        if (recordResponse.getApplyPrice().compareTo(new BigDecimal(total_amount)) == 0 && trade_status.equals(
                "TRADE_SUCCESS")) {
            //异步回调添加交易数据
            PayTradeRecordRequest payTradeRecordRequest = new PayTradeRecordRequest();
            //流水号
            payTradeRecordRequest.setTradeNo(trade_no);
            //商品订单号
            payTradeRecordRequest.setBusinessId(out_trade_no);
            payTradeRecordRequest.setResult_code("SUCCESS");
            payTradeRecordRequest.setPracticalPrice(new BigDecimal(total_amount));
            payTradeRecordRequest.setChannelItemId(Long.valueOf(type));
            //添加交易数据（与微信共用）
            payProvider.wxPayCallBack(payTradeRecordRequest);
            log.info("isMergePay==== {}", isMergePay);
            payCallbackOnline(trades, operator, isMergePay);
            log.info("支付回调成功,单号：{}", out_trade_no);
        }
    }

    private void alipayTakeCallbackHandle(String out_trade_no, String trade_no, String trade_status, String total_amount,
                                          String type, Operator operator, List<TradeVO> trades, boolean isMergePay,
                                          PayTradeRecordResponse recordResponse) {
        if (recordResponse.getApplyPrice().compareTo(new BigDecimal(total_amount)) == 0 && trade_status.equals(
                "TRADE_SUCCESS")) {
            //异步回调添加交易数据
            PayTradeRecordRequest payTradeRecordRequest = new PayTradeRecordRequest();
            //流水号
            payTradeRecordRequest.setTradeNo(trade_no);
            //商品订单号
            payTradeRecordRequest.setBusinessId(out_trade_no);
            payTradeRecordRequest.setResult_code("SUCCESS");
            payTradeRecordRequest.setPracticalPrice(new BigDecimal(total_amount));
            payTradeRecordRequest.setChannelItemId(Long.valueOf(type));
            //添加交易数据（与微信共用）
            payProvider.wxPayCallBack(payTradeRecordRequest);
            log.info("isMergePay==== {}", isMergePay);
            payTakeGoodCallbackOnline(trades, operator, isMergePay);
            log.info("支付回调成功,单号：{}", out_trade_no);
        }
    }


    private void alipayRechargeCallbackHandle(String out_trade_no, String trade_no, String trade_status, String total_amount,
                                              String type, Operator operator, WalletRecordVO walletRecordVO) {
        if (walletRecordVO.getDealPrice().compareTo(new BigDecimal(total_amount)) == 0 && walletRecordVO.getTradeState().equals(0)) {
            //异步回调添加交易数据
            PayTradeRecordRequest payTradeRecordRequest = new PayTradeRecordRequest();
            //流水号
            payTradeRecordRequest.setTradeNo(trade_no);
            //商品订单号
            payTradeRecordRequest.setBusinessId(out_trade_no);
            payTradeRecordRequest.setResult_code("SUCCESS");
            payTradeRecordRequest.setPracticalPrice(new BigDecimal(total_amount));
            payTradeRecordRequest.setChannelItemId(Long.valueOf(type));
            //添加交易数据（与微信共用）
            payProvider.wxPayCallBack(payTradeRecordRequest);
            payRechargeCallbackOnline(walletRecordVO);
            log.info("支付回调成功,单号：{}", out_trade_no);
        }
    }

    /**
     * 新银联企业支付异步回调
     *
     * @param request
     * @param response
     */
    @ApiOperation(value = "新银联企业支付异步回调")
    @RequestMapping(value = "/newUnionB2BCallBack", method = RequestMethod.POST)
    @LcnTransaction
    public void newUnionB2BCallBack(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        log.info("=========================新银联企业支付异步回调接收报文返回开始=======================");
        log.info("原始的HttpServletRequest：{}",request);
        Map<String, String> respParam = getAllRequestParamNew(request);
        log.info("处理后的参数:{}",respParam);
        log.info("JSON转换后的参数:{}",JSONObject.toJSON(respParam));
        if (StringUtil.isNotEmpty(respParam.get("Signature"))) {
            if (!verifySign(respParam)){
                log.info("验签失败");
                return;
            }
//            {"TranType":"0002","AcqDate":"20200925","AcqSeqId":"0000000068266495","TranDate":"20200925","BankInstNo":"700000000000001","CurryNo":"CNY","RemoteAddr":"172.20.111.46","PayTimeOut":"60","BusiType":"0001","OrderAmt":"1120","OrderStatus":"0000","CompleteDate":"20200925","Version":"20140728","MerOrderNo":"O202009251537430094","Signature":"oCHKAewrF8xPT+4nDKhPAd34WVCtHIbtArpOhxIiBKTzzrs5Ajw4luOCy4CPthCZZRivwZl/1509TrckS/pUNmQq+Xi0SF6pGD6yMReRb6lsKyBrIqab4FnDV3e/R/gVdoUl7oDZ2uRf96gOuOWW5Bdt3E1/JEDRqbQGKIOBTuDJ3+UDe9d9KmbmbtRaExneqhx3wwOq55qNqT4J5lWsrUUljXu6m+by4J9X+QD3K+0MAKfMeQbN8c+yMQccWxLv2NZr1NT3WLB500s8+v8mPHvTbtBnImRZsMDwatWTMpt84zoBJP4RAQJNNfH73Nwaw2HVHVJ4P7Fk2iCAlxyclg==","MerId":"594312008310001","CompleteTime":"154022","TranTime":"153755"}
            //
            //判断respCode=00 后，对涉及资金类的交易，请再发起查询接口查询，确定交易成功后更新数据库。
            String businessId= request.getParameter("MerOrderNo");
            UnionPayRequest unionPayRequest = new UnionPayRequest();
            unionPayRequest.setApiKey(request.getParameter("MerId"));
            unionPayRequest.setBusinessId(businessId);
            unionPayRequest.setTxnTime(request.getParameter("TranDate"));

//
            //交易成功，更新商户订单状态
            Operator operator = Operator.builder().ip(HttpUtil.getIpAddr()).adminId("-1").name(PayGatewayEnum.UNIONB2B.name())
                    .account(PayGatewayEnum.UNIONB2B.name()).platform(Platform.THIRD).build();
            //单笔支付与组合支付区分，获取支付订单的信息
            boolean isMergePay = isMergePayOrder(businessId);
//            boolean isCreditRepay = payServiceHelper.isCreditRepayFlag(businessId);
            payProvider.unionCallBack(respParam);
            if (isMergePay) {
                List<TradeVO> trades = new ArrayList<>();
                trades.addAll(tradeQueryProvider.getListByParentId(TradeListByParentIdRequest.builder()
                        .parentTid(businessId).build()).getContext().getTradeVOList());
                payCallbackOnline(trades, operator, true);
            }else {
                List<TradeVO> trades = new ArrayList<>();
                trades.add(tradeQueryProvider.getById(TradeGetByIdRequest
                        .builder().tid(businessId).build()).getContext().getTradeVO());
                payCallbackOnline(trades, operator, false);
            }
            response.getWriter().print("ok");
        }else {
            log.error("未获得签名");
        }
        log.info("银联企业支付异步回调接收报文返回结束");
    }

    /**
     * 数字人民币回调
     * @param request
     * @param response
     * @throws Exception
     */
    @ApiOperation(value = "数字人民币回调")
    @RequestMapping(value = "/ecnyCallBack", method = RequestMethod.POST)
    @LcnTransaction
    public void ecnyCallBack(HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("=========================新银联企业支付异步回调接收报文返回开始=======================");
        log.info("原始的HttpServletRequest：{}",request);
        Map<String, String> respParam = getAllRequestParamNew(request);
        log.info("处理后的参数:{}",respParam);
        log.info("JSON转换后的参数:{}",JSONObject.toJSON(respParam));
        if (StringUtil.isNotEmpty(respParam.get("SIGN"))) {
            if (!ecnyCallbackverifySign(respParam)){
                log.info("验签失败");
                return;
            }
            String orderid=respParam.get("ORDERID");
            String total_amount=respParam.get("PAYMENT");
            String trade_no=respParam.get("ORDERID");
            String trade_status=respParam.get("ORDERID");
            String type="H5";
            boolean isMergePay = isMergePayOrder(orderid);
            String lockName;
            //非组合支付，则查出该单笔订单。
            if (!isMergePay) {
                TradeVO tradeVO =
                        tradeQueryProvider.getById(new TradeGetByIdRequest(orderid)).getContext().getTradeVO();
                // 锁资源：无论是否组合支付，都锁父单号，确保串行回调
                lockName = tradeVO.getParentId();
            } else {
                lockName = orderid;
            }
            Operator operator =
                    Operator.builder().ip(HttpUtil.getIpAddr()).adminId("-1").name(PayGatewayEnum.ALIPAY.name())
                            .account(PayGatewayEnum.ALIPAY.name()).platform(Platform.THIRD).build();
            //redis锁，防止同一订单重复回调
            RLock rLock = redissonClient.getFairLock(lockName);
            rLock.lock();

            //执行
            try {
                List<TradeVO> trades = new ArrayList<>();
                //查询交易记录
                TradeRecordByOrderCodeRequest tradeRecordByOrderCodeRequest =
                        new TradeRecordByOrderCodeRequest(orderid);
                PayTradeRecordResponse recordResponse =
                        payQueryProvider.getTradeRecordByOrderCode(tradeRecordByOrderCodeRequest).getContext();
                if (isMergePay) {
                    /*
                     * 合并支付
                     * 查询订单是否已支付或过期作废
                     */
                    trades = tradeQueryProvider.getListByParentId(
                            new TradeListByParentIdRequest(orderid)).getContext().getTradeVOList();
                    //订单合并支付场景状态采样
                    ecnyCallbackHandle(orderid, trade_no, trade_status, total_amount, type,
                            operator, trades, true, recordResponse);

                } else {
                    //单笔支付
                    TradeVO tradeVO = tradeQueryProvider.getById(new TradeGetByIdRequest(orderid))
                            .getContext().getTradeVO();
                    trades.add(tradeVO);
                    ecnyCallbackHandle(orderid, trade_no, trade_status, total_amount, type,
                            operator, trades, false, recordResponse);

                }

            } finally {
                //解锁
                rLock.unlock();
            }
            response.getWriter().print("ok");
        }else {
            log.error("未获得签名");
        }
        log.info("数据人民币支付异步回调接收报文返回结束");
    }

    /**
     * 招商支付异步回调
     * @param request
     * @param response
     */
    @ApiOperation(value = "招商支付异步回调")
    @RequestMapping(value = "/CMBPaySuccessCallBack/{storeId}", method = {RequestMethod.POST, RequestMethod.GET})
    @LcnTransaction
    public void cmbPayBack(HttpServletRequest request, HttpServletResponse response, @PathVariable("storeId") Long storeId) throws Exception {
        log.info("===============招商回调开始==============");
//       Map<String, String> params = new HashMap<String, String>();
//        Map<String, String[]> requestParams = request.getParameterMap();
//        //返回的参数放到params中
//        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
//            String name = iter.next();
//            String[] values = requestParams.get(name);
//            String valueStr = "";
//            for (int i = 0; i < values.length; i++) {
//                valueStr = (i == values.length - 1) ? valueStr + values[i]
//                        : valueStr + values[i] + ",";
//            }
//            params.put(name, valueStr);
//        }
//        log.info("===============招商回调开始参数2222222222222：==============" +  JSONObject.toJSONString(params));
        //商户订单号
        /* String out_trade_no = new String(request.getParameter("orderNo")
                .getBytes("ISO-8859-1"), "UTF-8");
*/
        String result = request.getParameter("jsonRequestData");
        log.info("===============招商回调开始参数：==============" + result);
        String jsonModels = result.replaceAll("&quot;", "\"");

        log.info("===============招商回调开始参数11111111111：==============" + jsonModels);
        CmbCallBackRequest appRequest =
                JSONObject.parseObject(jsonModels, CmbCallBackRequest.class);

        try {
            //保存招商支付回调
            addPayCallBackResult(PayCallBackResultAddRequest.builder()
                    .businessId(appRequest.getNoticeData().getOrderNo())
                    .resultXml(jsonModels)
                    .resultContext(jsonModels)
                    .resultStatus(PayCallBackResultStatus.HANDLING)
                    .errorNum(0)
                    .payType(PayCallBackType.CMB)
                    .build());

            payCallBackTaskService.payCallBack(TradePayOnlineCallBackRequest.builder().payCallBackType(PayCallBackType.CMB)
                    .cmbPayCallBackResultStr(jsonModels)
                    .build());

        }catch (RejectedExecutionException e) {
            addPayCallBackResult(PayCallBackResultAddRequest.builder()
                    .businessId(appRequest.getNoticeData().getOrderNo())
                    .resultContext(jsonModels)
                    .resultXml(jsonModels)
                    .resultStatus(PayCallBackResultStatus.TODO)
                    .errorNum(0)
                    .payType(PayCallBackType.CMB)
                    .build());
            response.getWriter().print("failure");
            response.getWriter().flush();
            response.getWriter().close();
        }

        response.getWriter().print("HTTP Status Code 200");
        response.getWriter().flush();
        response.getWriter().close();
        log.info("支付回调返回success");
    }

    private void ecnyCallbackHandle(String out_trade_no, String trade_no, String trade_status, String total_amount,
                                    String type, Operator operator, List<TradeVO> trades, boolean isMergePay,
                                    PayTradeRecordResponse recordResponse) {
        if (recordResponse.getApplyPrice().compareTo(new BigDecimal(total_amount)) == 0 && trade_status.equals(
                "Y")) {
            //异步回调添加交易数据
            PayTradeRecordRequest payTradeRecordRequest = new PayTradeRecordRequest();
            //流水号
            payTradeRecordRequest.setTradeNo(trade_no);
            //商品订单号
            payTradeRecordRequest.setBusinessId(out_trade_no);
            payTradeRecordRequest.setResult_code("SUCCESS");
            payTradeRecordRequest.setPracticalPrice(new BigDecimal(total_amount));
            payTradeRecordRequest.setChannelItemId(Long.valueOf(type));
            //添加交易数据（与微信共用）
            payProvider.wxPayCallBack(payTradeRecordRequest);
            payCallbackOnline(trades, operator, isMergePay);
            log.info("支付回调成功,单号：{}", out_trade_no);
        }
    }
    /**
     * 数字人民币回调签名
     * @param params
     * @return
     * @throws Exception
     */
    private boolean ecnyCallbackverifySign(Map<String, String> params) throws Exception{
        String SIGN=params.get("SIGN");
        String StrSign=createVerifyStr(params);
        String newsign=WXPayUtil.MD5(StrSign);
        boolean sg=newsign.equals(SIGN.substring(SIGN.length()-32,SIGN.length()));
        return sg;
    }
    private String createVerifyStr(Map<String, String> params){
        String POSID=params.get("POSID");
        String BRANCHID=params.get("BRANCHID");
        String ORDERID=params.get("ORDERID");
        String PAYMENT=params.get("PAYMENT");
        String CURCODE=params.get("CURCODE");
        String REMARK1=params.get("REMARK1");
        String REMARK2=params.get("REMARK2");
        String ACC_TYPE=params.get("ACC_TYPE");
        String SUCCESS=params.get("SUCCESS");
        String SIGN=params.get("SIGN");

        String linkString="POSID="+POSID+"&BRANCHID="+BRANCHID+"&ORDERID="+ORDERID+"&PAYMENT="+PAYMENT+"&CURCODE="
                +CURCODE+"&REMARK1="+REMARK1+"&REMARK2="+REMARK2;
        if (ACC_TYPE!=null){
            linkString=linkString+"&ACC_TYPE="+ACC_TYPE;
        }
        linkString=linkString+"&SUCCESS=Y";
        return linkString;
    }


    /**
     * 新银联电子支付获取所有回调参数
     * @param request
     * @return
     */
    private static Map<String, String> getAllRequestParamNew(final HttpServletRequest request) {
        Map<String, String> res = new LinkedHashMap<>();
        Enumeration<String> paraNames = request.getParameterNames();
        if (null != paraNames) {
            while (paraNames.hasMoreElements()) {
                String key = paraNames.nextElement();
                // 跳过空字段
                String value = request.getParameter(key);
//                if (StringUtil.isEmpty(value)) {
//                    continue;
//                }
                // 后台通知需要解码,正式使用建议前后台接收通知地址分开
                try {
                    log.info("解码前：{}",value);
                    value = URLDecoder.decode(value, "UTF-8");
                    log.info("解码后：{}",value);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                res.put(key, value);
            }
        }
        return res;
    }

    private boolean verifySign(Map<String, String> params){
        SecssUtil secssUtil = getSecssUtil();
//            secssUtil.sign(params);
//            String sign = secssUtil.getSign();
        log.info("=====获取到的：{}",params.get("Signature"));
        //验签
        secssUtil.verify(params);
        if (!"00".equalsIgnoreCase(secssUtil.getErrCode())){
            log.info("验证签名结果[失败]:【{}】:{}",secssUtil.getErrCode(),secssUtil.getErrMsg());
            return !toggleVerifySign;
        }
        return true;
    }

    /**
     * 新银联电子支付签名工具
     * @return
     */
    private SecssUtil getSecssUtil() {
        SecssUtil secssUtil = new SecssUtil();
        secssUtil.init(url);
        return secssUtil;
    }

    /**
     * 招商支付异步回调
     * @param request
     * @param response
     */
    @ApiOperation(value = "银联支付异步回调")
    @RequestMapping(value = "/CPUSPaySuccessCallBack/{storeId}", method = {RequestMethod.POST, RequestMethod.GET})
    @LcnTransaction
    public void cpusPayBack(HttpServletRequest request, HttpServletResponse response, @PathVariable("storeId") Long storeId) throws Exception {
        log.info("===============银联回调开始==============");
        Map<String, String> result = getAllRequestParam(request);
        log.info("===============银联回调开始参数：==============" + result);

        String billFunds = StringEscapeUtils.unescapeHtml4(result.get("billFunds"));
        String billFundsDesc = StringEscapeUtils.unescapeHtml4(result.get("billFundsDesc"));
        String orderDesc = StringEscapeUtils.unescapeHtml4(result.get("orderDesc"));

        result.put("billFunds",billFunds);
        result.put("billFundsDesc",billFundsDesc);
        result.put("orderDesc",orderDesc);

        //提前做特殊字符转换
        String jsonModels = JSONObject.toJSONString(result);
        String merOrderId =  result.get("merOrderId");
        String targetSys = result.get("targetSys");
        PrintWriter writer = response.getWriter();
        if(StringUtil.isEmpty(merOrderId) || StringUtil.isEmpty(targetSys)){
            // 收到通知后记得返回SUCCESS
            writer.print("FAIL");
            writer.flush();
            writer.close();
            return;
        }

        //因为是多支付单号，所以merOrderId 需要去支付单查询出对应的订单号

        merOrderId = merOrderId.substring(4,merOrderId.length());

        // PayTradeRecordRequest recordRequest = new PayTradeRecordRequest();
        // recordRequest.setPayOrderNo(merOrderId);

        // PayTradeRecordResponse payOrderResponse =  payQueryProvider.findByPayOrderNo(recordRequest).getContext();
        String businessId = payQueryProvider.queryBusinessIdByPayOrderNo(merOrderId).getContext();
        PayCallBackType backType = targetSys.indexOf("Alipay") != -1 ? PayCallBackType.CUPSALI : PayCallBackType.CUPSWECHAT;
        log.info("===============银联回调开始参数11111111111：==============" + jsonModels);
        log.info("===============银联回调开始参数22222222222：=============={},{}",merOrderId, businessId);
        try {

            //提前验签
            GatewayConfigByGatewayRequest gatewayConfigByGatewayRequest = new GatewayConfigByGatewayRequest();
            PayGatewayEnum payGatewayEnum = backType == PayCallBackType.CUPSWECHAT ? PayGatewayEnum.CUPSWECHAT : PayGatewayEnum.CUPSALI;
            gatewayConfigByGatewayRequest.setGatewayEnum(payGatewayEnum);
            gatewayConfigByGatewayRequest.setStoreId(Constants.BOSS_DEFAULT_STORE_ID);
            //查询银联配置信息
            PayGatewayConfigResponse payGatewayConfigResponse =
                    payQueryProvider.getGatewayConfigByGateway(gatewayConfigByGatewayRequest).getContext();

            CupsPaySignRequest cupsPaySignRequest = new CupsPaySignRequest();
            cupsPaySignRequest.setCupsPayCallBackResultStr(jsonModels);
            cupsPaySignRequest.setSecret(payGatewayConfigResponse.getSecret());
            cupsPaySignRequest.setApiKey(payGatewayConfigResponse.getApiKey());
            cupsPaySignRequest.setAppId(payGatewayConfigResponse.getAppId());
            Boolean signVerified = cupsPayProvider.cupsPaySign(cupsPaySignRequest).getContext();

            if(!signVerified){
                addPayCallBackResult(PayCallBackResultAddRequest.builder()
                        .businessId(businessId)
                        .resultContext(jsonModels)
                        .resultXml(jsonModels)
                        .resultStatus(PayCallBackResultStatus.TODO)
                        .errorNum(0)
                        .payType(backType)
                        .build());
                writer.print(" FAIL");
                writer.flush();
                writer.close();
                log.info("支付回调返回FAIL");
                return;
            }

            //保存银联支付回调
            addPayCallBackResult(PayCallBackResultAddRequest.builder()
                    .businessId(businessId)
                    .resultXml(jsonModels)
                    .resultContext(jsonModels)
                    .resultStatus(PayCallBackResultStatus.HANDLING)
                    .errorNum(0)
                    .payType(backType)
                    .build());

            payCallBackTaskService.payCallBack(TradePayOnlineCallBackRequest.builder().payCallBackType(backType)
                    .cupsPayCallBackResultStr(jsonModels)
                    .businessId(businessId)
                    .build());

        }catch (RejectedExecutionException e) {
            addPayCallBackResult(PayCallBackResultAddRequest.builder()
                    .businessId(businessId)
                    .resultContext(jsonModels)
                    .resultXml(jsonModels)
                    .resultStatus(PayCallBackResultStatus.TODO)
                    .errorNum(0)
                    .payType(backType)
                    .build());
            writer.print(" FAIL");
            writer.flush();
            writer.close();
        }

        writer.print("SUCCESS");
        writer.flush();
        writer.close();
        log.info("支付回调返回success");
    }

    public static void main(String[] args) {
//        SecssUtil secssUtil = new SecssUtil();
//        secssUtil.init("D:\\workspace_li\\xiyaya\\sbc-service-pay\\service-pay-api\\src\\main\\resources\\security.properties");
////        {
////            TranType=0002,
////            AcqDate=20200927,
////            AcqSeqId=0000000143658626, TranDate=20200927, BankInstNo=700000000000004, CurryNo=CNY, RemoteAddr=192.168.201.231,
////                PayTimeOut=60, BusiType=0001, OrderAmt=13930, OrderStatus=0000, CompleteDate=20200927, Version=20140728,
////                MerOrderNo=O202009272000440815, Signature=
////                , MerId=000092009088964, CompleteTime=200136, TranTime=200048}
////        {
////            AcqDate=20200927,
////                    AcqSeqId=0000000143658690, BankInstNo=700000000000004, BusiType=0001,
////                    CompleteDate=20200927, CompleteTime=221753, CurryNo=CNY, MerId=000092009088964,
////                    MerOrderNo=O202009272217395702, OrderAmt=13930, OrderStatus=0000,
////                    PayTimeOut=60, RemoteAddr=192.168.201.231,
////                Signature=dzcUqN4Zf8zlmnLDnms2orSjpzPso1jQpA2t7RNU+HAnNGovxsys5CghpnWIJoHeHRTAxYa9TijTC9p9gnPYmjcAT6BdB+tDlRY9h3xxi8sOzRQdWu52HDaWCL+Dj8fvuQGF+k98G9pJ/9JPeeVpA1F0BHZtINzicoU0m5DohbuzoCTZszEP719cZaIQEmQkgJKhr8KH5OzVLT3YkDGdr75MVgsIyUhfNZIg+xuC0rjoR4Kw6QqBed6fpY1CJXS3fT6N/+1GQXeqHkwXWOvh3GhpwlUYliPc65cHK+sKQ4oWJ3aVaxjfntcq+KJotBBUNZAwdHm0B+5Z0OA/9+Et5Q==,
////                TranDate=20200927, TranTime=221744, TranType=0002, Version=20140728}
//        Map<String, String> params =    new TreeMap<>();
//        params.put("TranType","0002");
//        params.put("AcqDate","20200927");
//        params.put("AcqSeqId","0000000143658626");
//        params.put("TranDate","20200927");
//        params.put("BankInstNo","700000000000004");
//        params.put("CurryNo","CNY");
//        params.put("RemoteAddr","192.168.201.231");
//        params.put("PayTimeOut","60");
//        params.put("BusiType","0001");
//        params.put("OrderAmt","13930");
//        params.put("OrderStatus","0000");
//        params.put("CompleteDate","20200927");
//        params.put("Version","20140728");
//        params.put("MerOrderNo","O202009272000440815");
//        params.put("MerId","000092009088964");
//        params.put("CompleteTime","200136");
//        params.put("TranTime","200048");
////        params.put("Signature","bnl5ja4BBAK3OMHLKosH5tVMHIoykm5dH5444ymVcsM1K9dOdEUiYlEI3uljEdvzrP67K0fVoKASVdG832Q9fJbu8R+B1xPDBVUJARKaWduLZLqKVpapJeeIg5RnbDFqjApvWXK7+w3AH3BGRb4u7w654Hg+EsHKjIHUZ0l/+PWTyJb/TUDY81AAYkfefVg6YsS0J3xExtUiStKulJpNjTYewtHLrQbLB2PNuTm44X1v9jH3d2wzjqR3hIOKm6fNz8YTorMiJDqZ1vj75N05QFTED8P9vWiri8qSPRO1LX5ak59lHz3JceyiOzmnfojcmaz/xntlMWHqKoqiZ+5dYw==");
//        secssUtil.sign(params);
//        System.out.println(secssUtil.getSign());
//        params.put("Signature",secssUtil.getSign());
//        secssUtil.verify(params);
//        System.out.println(secssUtil.getErrCode());
//        System.out.println(secssUtil.getErrMsg());


        String result = "{&quot;charset&quot;:&quot;UTF-8&quot;,&quot;noticeData&quot;:{&quot;dateTime&quot;:&quot;20220315162514&quot;,&quot;date&quot;:&quot;20220315&quot;,&quot;amount&quot;:&quot;0.78&quot;,&quot;bankDate&quot;:&quot;20220315&quot;,&quot;orderNo&quot;:&quot;PO202203151625097917&quot;,&quot;cardType&quot;:&quot;02&quot;,&quot;discountAmount&quot;:&quot;0.00&quot;,&quot;noticeType&quot;:&quot;BKPAYRTN&quot;,&quot;httpMethod&quot;:&quot;POST&quot;,&quot;noticeSerialNo&quot;:&quot;20220315PO202203151625097917&quot;,&quot;merchantPara&quot;:&quot;&quot;,&quot;discountFlag&quot;:&quot;N&quot;,&quot;bankSerialNo&quot;:&quot;2169909B00011300000A&quot;,&quot;noticeUrl&quot;:&quot;https://bossbff.test.7yaya.cn/tradeCallback/CMBPaySuccessCallBack/-1&quot;,&quot;branchNo&quot;:&quot;0731&quot;,&quot;merchantNo&quot;:&quot;000131&quot;},&quot;sign&quot;:&quot;gSotgOVSubNKqOEpfta3XLGdJ9vHUHJm27zhdWJCKLpqBbTFXmPGpmu+M3Y8TqTacHNCS50N5H3eARFIw3WTHTTYJCWuEefir2BpIEF4Mp+qDX7WQ8wjWG80/MIYOxwyFRqdTn5me37Qd9kJkc6ZX0WZeSg2ZZzgrvqac2Ixrpc=&quot;,&quot;signType&quot;:&quot;RSA&quot;,&quot;version&quot;:&quot;1.0&quot;}";
        log.info("===============招商回调开始参数：==============" + result);
        String jsonModels = result.replaceAll("&quot;", "\"");
        log.info("===============招商回调开始参数：==============" + jsonModels);
        CmbCallBackRequest appRequest =
                JSONObject.parseObject(jsonModels, CmbCallBackRequest.class);

        log.info("===============招商回调开始参数：==============" + appRequest.getSign());
    }


    /**
     * 测试
     * @return
     */
//    @ApiOperation(value = "设置客户收货地址为默认")
//    @RequestMapping(value = "/synAdrress", method = RequestMethod.GET)
//    public BaseResponse synAdrress(){
//        log.info("进入到boss，准备执行更新查询");
//        return BaseResponse.success(customerDeliveryAddressProvider.synAdrress().getContext());
//    }

}
