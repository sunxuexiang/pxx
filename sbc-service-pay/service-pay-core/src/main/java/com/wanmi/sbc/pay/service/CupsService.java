package com.wanmi.sbc.pay.service;

import com.alibaba.fastjson.JSONObject;
import com.esotericsoftware.minlog.Log;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.GeneratorService;
import com.wanmi.sbc.pay.api.request.CmbPayOrderRequest;
import com.wanmi.sbc.pay.api.request.CupsPayRefundDataRequest;
import com.wanmi.sbc.pay.api.request.CupsPaySignRequest;
import com.wanmi.sbc.pay.api.response.CpusPayOrderResponse;
import com.wanmi.sbc.pay.api.response.CupsPayRefundResponse;
import com.wanmi.sbc.pay.bean.enums.PayGatewayEnum;
import com.wanmi.sbc.pay.bean.enums.TradeStatus;
import com.wanmi.sbc.pay.bean.enums.TradeType;
import com.wanmi.sbc.pay.model.root.PayGatewayConfig;
import com.wanmi.sbc.pay.model.root.PayRecord;
import com.wanmi.sbc.pay.model.root.PayTradeRecord;
import com.wanmi.sbc.pay.repository.PayRecordRepository;
import com.wanmi.sbc.pay.repository.TradeRecordRepository;
import com.wanmi.sbc.pay.unionpay.acp.sdk.HttpClient;
import com.wanmi.sbc.pay.utils.GeneratorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 银联支付
 */
@Slf4j
@Service
public class CupsService {

    private static int socketTimeout = 10000;// 连接超时时间,默认10秒
    private static int connectTimeout = 30000;// 传输超时时间,默认30秒



    /**
     * 招商支付成功回调地址
     */
    private static final String CUPSPAYSUCCCALLBACK = "/tradeCallback/CPUSPaySuccessCallBack";

    @Resource
    private TradeRecordRepository recordRepository;

    @Autowired
    private PayDataService payDataService;


    @Autowired
    private GeneratorService generatorService;

    @Autowired
    private PayRecordRepository payRecordRepository;






    //来源编号
    private  static String sourceCode = "31YT";

    private  static String nonce = UUID.randomUUID().toString().replace("-", "");

    private static String INSTMID  = "APPDEFAULT";

    private static String INSTMID_WX = "MINIDEFAULT";

    private static String TRADETYPE = "APP";
    private static String TRADETYPE_WX = "MINI";

    /**
     * 商户号
     */
    //private static String OLD_MID = "898130453112912";
    private static String OLD_MID = "898130453112913";
    //private static String MID = "898130453112913";
    private static String MID = "898130453112912";

    /**
     * 终端号
     */
    //private static String OLD_TID = "04961674";
    private static String OLD_TID = "04961675";
    //private static String TID = "04961675";
    private static String TID = "04961674";

    private static String WXAPPID = "wxd2ce35d00195e377";

    private static String WX_SECRET= "940d03371d025da98beb5e89dd744a52";

    /**
     * 银联微信支付地址
     */
    private static String CPUS_WX_PAY_URL = "https://api-mop.chinaums.com/v1/netpay/wx/unified-order";

    /**
     * 银联支付宝支付地址
     */
    private static String CPUS_ALI_PAY_URL = "https://api-mop.chinaums.com/v1/netpay/trade/precreate";

    /**
     * 银联单笔订单数据查询
     */
    private static String CPUS_QUERY_ORDER_URL = "https://api-mop.chinaums.com/v1/netpay/query";

    /**
     * 银联单笔订单数据关闭
     */
    private static String CPUS_CLOSE_ORDER_URL = "https://api-mop.chinaums.com/v1/netpay/close";


    /**
     * 支付宝退款地址
     */
    private static String CPUS_PAY_REFUND_URL = "https://api-mop.chinaums.com/v1/netpay/refund";

    /**
     * 微信退款地址
     */
    private static String WX_CPUS_PAY_REFUND_URL = "https://api-mop.chinaums.com/v1/netpay/refund";




    private static String WX_OPENID_URL = "https://api.weixin.qq.com/sns/jscode2session";


    public String getWxOpenId(String jsCode){
        String  result = "";
        try{
            //发送请求
            HttpClient httpClient = new HttpClient(WX_OPENID_URL,socketTimeout,connectTimeout);
            Map<String, String> jsonRequestData = new HashMap<>();
            jsonRequestData.put("appid", WXAPPID);
            jsonRequestData.put("secret", WX_SECRET);
            jsonRequestData.put("js_code", jsCode);
            jsonRequestData.put("grant_type", "authorization_code");
            String resultStr =  httpClient.sendSring(jsonRequestData,"UTF-8");
            JSONObject jsonObject  = JSONObject.parseObject(resultStr);
            result = jsonObject.getString("openid");
        }catch (Exception e){
            Log.error("银联支付失败：原因:",e);
            throw new SbcRuntimeException("K-000001");
        }finally {
            return result;
        }
    }



    /**
     * 添加订单支付--交易记录
     *
     * @param request
     */
    public CpusPayOrderResponse cupsPayOrder(CmbPayOrderRequest  request) {
        try{
            log.info("收到的银联支付订单参数：" + JSONObject.toJSONString(request));
            PayTradeRecord record = recordRepository.findByBusinessId(request.getOut_trade_no());
            if (!Objects.isNull(record) && record.getStatus() == TradeStatus.SUCCEED) {
                //如果重复支付，判断状态，已成功状态则做异常提示
                throw new SbcRuntimeException("K-100203");
            } else {
                if (record == null) {
                    record = new PayTradeRecord();
                    record.setId(GeneratorUtils.generatePT());
                }

                record.setApplyPrice(new BigDecimal(request.getTotal_fee()).divide(new BigDecimal(100)).
                        setScale(2, BigDecimal.ROUND_DOWN));
                record.setBusinessId(request.getOut_trade_no());
                record.setClientIp(request.getSpbill_create_ip());
                record.setChannelItemId(request.getChannelId());
                record.setTradeType(TradeType.PAY);
                record.setCreateTime(LocalDateTime.now());
                record.setStatus(TradeStatus.PROCESSING);
                record.setPayOrderNo(request.getPayOrderNo());
                recordRepository.saveAndFlush(record);
            }

            PayGatewayEnum payGatewayEnum = request.getChannelId().longValue() == 29L ? PayGatewayEnum.CUPSALI : PayGatewayEnum.CUPSWECHAT;
            PayGatewayConfig payGatewayConfig = payDataService.queryConfigByNameAndStoreId(payGatewayEnum,request.getStoreId());

            String timestamp = DateUtil.format(new Date(),"yyyyMMddHHmmss");

            JSONObject json = new JSONObject();
            json.put("tradeType",request.getChannelId().longValue() == 30L ? TRADETYPE_WX : TRADETYPE);
            json.put("instMid", request.getChannelId().longValue() == 30L ? INSTMID_WX : INSTMID );
            json.put("mid",MID);
            json.put("notifyUrl",getNotifyUrl(payGatewayConfig));
            json.put("tid", TID);
            json.put("totalAmount", request.getTotal_fee());
            json.put("requestTimestamp",timestamp);
            json.put("merOrderId",sourceCode + request.getPayOrderNo());
            json.put("subAppId", request.getChannelId().longValue() == 30L ? WXAPPID : "");
            json.put("subOpenId", request.getChannelId().longValue() == 30L ? getWxOpenId(request.getSubOpenId()) : "");
            json.put("orderDesc",request.getBody());

            String authorization = postOpenBodySigForNetpay(payGatewayConfig.getAppId(), payGatewayConfig.getApiKey(), timestamp, nonce, json.toString());
            log.info("======cupsSend:{} authorization:{}",json,authorization);
            String result =  postJson(json,request.getChannelId().longValue() == 30L ? CPUS_WX_PAY_URL : CPUS_ALI_PAY_URL,authorization);
            log.info("======cupsresult:{}",result);
            if(result.indexOf("SUCCESS") == -1){
                throw new SbcRuntimeException("K-000001");
            }

            // 保存拉起支付记录
            PayRecord payRecord = new PayRecord();
            payRecord.setBusinessId(record.getBusinessId());
            payRecord.setApplyPrice(record.getApplyPrice());
            payRecord.setChannelItemId(record.getChannelItemId());
            payRecord.setCreateTime(record.getCreateTime());
            payRecord.setPayOrderNo(record.getPayOrderNo());
            payRecord.setStatus(0);
            payRecordRepository.saveAndFlush(payRecord);

            CpusPayOrderResponse response = JSONObject.parseObject(result, CpusPayOrderResponse.class);
            return response;
        }catch (Exception e){
            Log.error("银联支付失败：原因{},{}",String.valueOf(request.getChannelId()),e);
            throw new SbcRuntimeException("K-000001");
        }
    }

    /**
     * 支付退款
     * @param request
     * @return
     */
    public CupsPayRefundResponse cupsPayRefund(CupsPayRefundDataRequest request){

        String timestamp = DateUtil.format(new Date(),"yyyyMMddHHmmss");

        JSONObject json = new JSONObject();
        json.put("requestTimestamp",timestamp);
        json.put("mid",MID);
        json.put("tid", TID);
        json.put("instMid", request.getChannelId() == 30L ? INSTMID_WX : INSTMID );
        json.put("platformAmount",0);
        json.put("refundAmount",new BigDecimal(request.getAmount()).multiply(new BigDecimal(100)).
                setScale(0, BigDecimal.ROUND_DOWN).toString());
        json.put("refundOrderId", sourceCode + request.getRefundOrderId());
        json.put("merOrderId",sourceCode + request.getPayOrderNo());
        //去掉特殊字符

        String desc = "";
//        String regEx = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？–]|\n|\r|\t//–";
        if (request.getDesc()!=null) {
//            Pattern p = Pattern.compile(regEx);
//            Matcher m = p.matcher(request.getDesc());
//            desc = m.replaceAll("");
            desc = request.getDesc().replaceAll("[^a-zA-Z0-9\u4E00-\u9FA5]", "");
        }
        json.put("refundDesc", desc);
        try{
            log.info("====银联退款JSON:{}", json);
            String authorization = postOpenBodySigForNetpay(request.getAppId(), request.getApiKey(), timestamp, nonce, json.toString());
            String result = postJson(json, request.getChannelId() == 30L ? WX_CPUS_PAY_REFUND_URL : CPUS_PAY_REFUND_URL, authorization);
            log.info("===========银联退款===============退单号={}，返回结果={}", request.getRefundOrderId(), result);
            if (!result.contains("SUCCESS") && result.contains("NO_ORDER")) {
                // 兼容老商户号退款
                json.put("mid",OLD_MID);
                json.put("tid", OLD_TID);

                log.info("====银联退款JSON===老商户号:{}", json);
                String oldAuthorization = postOpenBodySigForNetpay(request.getAppId(), request.getApiKey(), timestamp, nonce, json.toString());
                String oldResult = postJson(json, request.getChannelId() == 30L ? WX_CPUS_PAY_REFUND_URL : CPUS_PAY_REFUND_URL, oldAuthorization);
                log.info("===========银联退款===========老商户号====退单号={}，返回结果={}", request.getRefundOrderId(), oldResult);
                if(!oldResult.contains("SUCCESS")){
                    throw new SbcRuntimeException("K-000001");
                }

                return JSONObject.parseObject(oldResult, CupsPayRefundResponse.class);

            }

            if(!result.contains("SUCCESS")){
                throw new SbcRuntimeException("K-000001");
            }
            return JSONObject.parseObject(result, CupsPayRefundResponse.class);

        }catch (Exception e){
            Log.error("银联退款失败：原因{},{}",json.toJSONString(),e);
            throw new SbcRuntimeException("K-000001");
        }
    }


    private  String getNotifyUrl(PayGatewayConfig payGatewayConfig){
        StringBuilder notify_url = new StringBuilder();
        notify_url.append(payGatewayConfig.getBossBackUrl()+CUPSPAYSUCCCALLBACK);
        notify_url.append("/");
        notify_url.append(payGatewayConfig.getStoreId());
        return notify_url.toString();
    }

    public Boolean cupsPaySign(CupsPaySignRequest cupsPaySignRequest) {
        try{
            log.info("=============银联回调验签===================={}", cupsPaySignRequest.getCupsPayCallBackResultStr());
            Map<String, String> params =
                    JSONObject.parseObject(cupsPaySignRequest.getCupsPayCallBackResultStr(), Map.class);
            // 验签
            log.info("=============银联回调验签签名===================={}", params.get("sign"));

            String timestamp = DateUtil.format(new Date(),"yyyyMMddHHmmss");
            //验签通过，查询当前订单详情数据
            if (checkSign2(cupsPaySignRequest.getSecret(),params)) {
                JSONObject json = new JSONObject();
                json.put("requestTimestamp",timestamp);
                json.put("mid",MID);
                json.put("tid", TID);
                json.put("instMid", INSTMID);
                json.put("merOrderId",params.get("merOrderId"));
                json.put("targetOrderId", params.get("targetOrderId"));

                String authorization = postOpenBodySigForNetpay(cupsPaySignRequest.getAppId(), cupsPaySignRequest.getApiKey(), timestamp, nonce, json.toString());
                String result = postJson(json,CPUS_QUERY_ORDER_URL,authorization);
                log.info("=============银联回调验签参数===================={}", result);
                if(result.indexOf("SUCCESS") == -1){
                   return false;
                }
                return true;
            }
        }catch (Exception e){
            throw new RuntimeException("签名过程中出现错误");
        }
        return false;
    }

    public boolean  isPayCompleted(CmbPayOrderRequest request){

        PayGatewayEnum payGatewayEnum = request.getChannelId().longValue() == 29L ? PayGatewayEnum.CUPSALI : PayGatewayEnum.CUPSWECHAT;
        PayGatewayConfig payGatewayConfig = payDataService.queryConfigByNameAndStoreId(payGatewayEnum,request.getStoreId());
        //这里可以加验证，请求银联是否已经交易成功了
        return isPayCompleted(request.getPayOrderNo(),payGatewayConfig.getAppId(),payGatewayConfig.getApiKey());
    }

    private boolean  isPayCompleted(String merOrderId, String appId, String appKey){
        try{
            String timestamp = DateUtil.format(new Date(),"yyyyMMddHHmmss");
            JSONObject json = new JSONObject();
            json.put("requestTimestamp",timestamp);
            json.put("mid",MID);
            json.put("tid", TID);
            json.put("instMid", INSTMID);
            json.put("merOrderId", sourceCode + merOrderId);

            String authorization = postOpenBodySigForNetpay(appId, appKey, timestamp, nonce, json.toString());
            JSONObject jsonObject = JSONObject.parseObject(postJson(json,CPUS_QUERY_ORDER_URL,authorization));
            log.info("====银联查询单笔订单：{}", jsonObject);
            String status = jsonObject.getString("status");
            if(StringUtils.isEmpty(status)){
                return true;
            }
            //已经支付完成
            String result = jsonObject.toJSONString();
            if(result.indexOf("SUCCESS") == -1 || status.equals("TRADE_SUCCESS")){
                return false;
            }
            //没有支付，但是有订单，则关闭订单
            closeOrder(merOrderId, appId, appKey);
            return true;
        }catch (Exception e){
            throw new RuntimeException("签名过程中出现错误");
        }
    }

    public void closePayOrder(String payOrderNo, Long channelId, Long storeId) {
        PayGatewayEnum payGatewayEnum = channelId == 29L ? PayGatewayEnum.CUPSALI : PayGatewayEnum.CUPSWECHAT;
        PayGatewayConfig payGatewayConfig = payDataService.queryConfigByNameAndStoreId(payGatewayEnum, storeId);
        closeOrder(payOrderNo, payGatewayConfig.getAppId(), payGatewayConfig.getApiKey());
    }

    /**
     * 关闭订单
     * @param merOrderId
     * @param appId
     * @param appKey
     * @return
     */
    public static boolean closeOrder(String merOrderId, String appId, String appKey){
        try{
            String timestamp = DateUtil.format(new Date(),"yyyyMMddHHmmss");
            JSONObject json = new JSONObject();
            json.put("requestTimestamp",timestamp);
            json.put("merOrderId", sourceCode + merOrderId);
            json.put("instMid", INSTMID);
            json.put("mid",MID);
            json.put("tid", TID);

            String authorization = postOpenBodySigForNetpay(appId, appKey, timestamp, nonce, json.toString());
            JSONObject jsonObject = JSONObject.parseObject(postJson(json,CPUS_CLOSE_ORDER_URL,authorization));
            log.info("====银联关闭单笔订单：{}", jsonObject);
            String status = jsonObject.getString("status");
            if(StringUtils.isEmpty(status)){
                return true;
            }
            String result = jsonObject.toJSONString();
            if(result.indexOf("SUCCESS") == -1 || status.equals("TRADE_SUCCESS")){
                return false;
            }
            return true;
        }catch (Exception e){
            throw new RuntimeException("签名过程中出现错误");
        }
    }




    public static void main(String [] args) throws Exception {

//        System.out.println(closeOrder("OD202206301702100272","8a81c1bf7f93c86b018098b09eb10ab4",
//                "90261839da79423d8a066085716aa4be"));

//        JSONObject json = new JSONObject();
//        json.put("tradeType","APP");
//        json.put("instMid","APPDEFAULT");
//        json.put("mid","898130453112912");
//        json.put("notifyUrl",CMBPAYSUCCCALLBACK);
//        json.put("tid","04961674");
//        json.put("totalAmount","1");
//        json.put("requestTimestamp",DateUtil.format(new Date(),"yyyyMMddHHmmss"));
//        json.put("merOrderId",sourceCode + "O202205091542291396");
//        json.put("subAppId","wxcffdff7b448f6444");
//        String authorization = postOpenBodySigForNetpay("8a81c1bf7f93c86b018098b09eb10ab4", "90261839da79423d8a066085716aa4be", timestamp, nonce, json.toString());
//        JSONObject jsonObject = doPost(CPUS_ALI_PAY_URL,json,authorization);

//        String result = jsonObject.toJSONString();

//        System.out.println(result);

//        if(result.indexOf("SUCCESS") == -1){
//            return;
//        }
//
//        JSONObject json1 = new JSONObject();
//        json1.put("connectSys","UNIONPAY");
//        json1.put("delegatedFlag","N");
//        json1.put("merName","湖南喜吖吖商业服务有限公司");
//        json1.put("mid","898130453112912");
//        JSONObject json2 = new JSONObject();
//        json2.put("msgType","trade.precreate");
//        json2.put("qrCode","https://qr.alipay.com/bax00711un4ynxlpnuv530a7");
//        json1.put("appPayRequest",json2.toJSONString());
//        json1.put("settleRefId","27204884896N");
//        json1.put("tid","04961674");
//        json1.put("totalAmount","1");
//        json1.put("qrCode","https://qr.alipay.com/bax00711un4ynxlpnuv530a7");
//        json1.put("targetMid","2088510789833244");
//        json1.put("responseTimestamp","2022-05-10 10:34:00");
//        json1.put("errCode","SUCCESS");
//        json1.put("targetStatus","10000");
//        json1.put("seqId","27204884896N");
//        json1.put("merOrderId","31YTO202205091542291397");
//        json1.put("status","NEW_ORDER");
//        json1.put("targetSys","Alipay 2.0");
//
//        System.out.println(json1.toJSONString());
//
//        CpusPayOrderResponse response = JSONObject.parseObject(json1.toJSONString(), CpusPayOrderResponse.class);
//        System.out.println(response.getAppPayRequest());
//
//
//        Map<String, String> paramMap = new HashMap<>();
//        paramMap.put("111","111");
//        paramMap.put("222","222");
//        paramMap.put("333","333");


//        String str = "{\"msgType\":\"wx.notify\",\"payTime\":\"2022-06-29 17:41:46\",\"buyerCashPayAmt\":\"6\",\"qW\":\"qHgZ\",\"connectSys\":\"UNIONPAY\",\"sign\":\"C59D64B15EC7BCA2F64069C261853382FDDA5EF9857AE206F47D3D5B3C158D34\",\"merName\":\"湖南喜吖吖商业服务有限公司\",\"mid\":\"898130453112912\",\"invoiceAmount\":\"6\",\"settleDate\":\"2022-06-29\",\"billFunds\":\"现金:6\",\"buyerId\":\"otdJ_uBa2ub_Lnh_uIzGxh6nxcBg\",\"mchntUuid\":\"2d9081bd802dacb801803aa221c63fec\",\"tid\":\"04961674\",\"instMid\":\"MINIDEFAULT\",\"receiptAmount\":\"6\",\"couponAmount\":\"0\",\"cardAttr\":\"BALANCE\",\"targetOrderId\":\"4200001509202206297076707940\",\"signType\":\"SHA256\",\"billFundsDesc\":\"现金支付0.06元。\",\"subBuyerId\":\"oNWOw5IFHiysb_bmS5O3GbkQqTp4\",\"orderDesc\":\"湖南喜吖吖商业服务有限公司\",\"seqId\":\"28106894007N\",\"merOrderId\":\"31YTOD202206291741387001\",\"targetSys\":\"WXPay\",\"bankInfo\":\"OTHERS\",\"totalAmount\":\"6\",\"createTime\":\"2022-06-29 17:41:38\",\"buyerPayAmount\":\"6\",\"notifyId\":\"f78bb937-5835-4f5e-9342-21ac6071611e\",\"subInst\":\"102800\",\"status\":\"TRADE_SUCCESS\"}";
//        Map<String, String> params =
//                JSONObject.parseObject(str, Map.class);
//        System.out.println(params.get("sign"));
//        checkSign2("QFRCtbBJBD8mDFTD7bdhT2dMrmSk6SMy",params);

//        String uid = UUID.randomUUID().toString().replace("-", "");
//        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
//        JSONObject json = new JSONObject();
//        json.put("requestTimestamp",timestamp);
//        json.put("mid",MID);
//        json.put("tid", TID);
//        json.put("instMid", 29L == 30L ? INSTMID_WX : INSTMID );
//        json.put("platformAmount",0);
//        json.put("refundAmount",103400);
//        json.put("refundOrderId", sourceCode + "R202207091533327841");
//        String desc="阿尔卑斯·2.5kg散装焦香源味牛奶硬糖440085（新）–可拆半箱  等多件商品";
//        String regEx = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？–]|\n|\r|\t//–";
//            Pattern p = Pattern.compile(regEx);
//            Matcher m = p.matcher(desc);
//            desc = m.replaceAll("");
//        json.put("refundDesc", desc);
//        //商户需遵循商户订单号生成规范，即以银商分配的4位来源编号作为账单号的前4位，且在商户系统中此
//        //账单号保证唯一。总长度需大于6位，小于28位。银商的推荐规则为（无特殊情况下，建议遵守此规
//        //则）
//        //{来源编号(4位)}{时间(yyyyMMddmmHHssSSS)(17位)}{7位随机数}
//        json.put("merOrderId","31YTOD202207091533327841");// 订单号
//        json.put("requestTimestamp", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
//
//        String authorization = postOpenBodySigForNetpay("8a81c1bf7f93c86b018098b09eb10ab4", "90261839da79423d8a066085716aa4be",
//                timestamp, uid, json.toString());
//
//        String result = postJson( json,CPUS_PAY_REFUND_URL,authorization);
//        System.out.println(json);
//        System.out.println(authorization);
//        System.out.println(result);

//        String str = "{\"msgType\":\"wx.notify\",\"payTime\":\"2022-07-21 03:15:00\",\"buyerCashPayAmt\":\"124400\",\"connectSys\":\"UNIONPAY\",\"sign\":\"3C97F057525F4F73A92FD18229E04014B1B59584E5DE7C1456BE0222AEE10CCF\",\"merName\":\"湖南喜吖吖商业服务有限公司\",\"mid\":\"898130453112912\",\"invoiceAmount\":\"124400\",\"settleDate\":\"2022-07-21\",\"billFunds\":\"现金:124400\",\"buyerId\":\"otdJ_uLsDy72JlioNfqA865HgLME\",\"mchntUuid\":\"2d9081bd802dacb801803aa221c63fec\",\"tid\":\"04961674\",\"instMid\":\"MINIDEFAULT\",\"Eu\":\"drXd\",\"receiptAmount\":\"124400\",\"couponAmount\":\"0\",\"cardAttr\":\"DEBIT_CARD\",\"targetOrderId\":\"4200001538202207215899506809\",\"signType\":\"SHA256\",\"billFundsDesc\":\"现金支付1244.00元。\",\"subBuyerId\":\"oNWOw5GguBO3POg44O-NvflA5lJ0\",\"orderDesc\":\"M&amp;M’s 30.6g牛奶巧克力豆（12  等多件商品订单\",\"seqId\":\"28530783954N\",\"merOrderId\":\"31YTOD202207210314525865\",\"targetSys\":\"WXPay\",\"bankInfo\":\"建设银行(借记卡)\",\"totalAmount\":\"124400\",\"createTime\":\"2022-07-21 03:14:53\",\"buyerPayAmount\":\"124400\",\"notifyId\":\"4bdf92d2-abd2-494d-bd02-7cf4c46c13af\",\"subInst\":\"102800\",\"status\":\"TRADE_SUCCESS\"}";
//        //招商公钥
//        Map<String, String> params =
//                JSONObject.parseObject(str, Map.class);
//
//        String billFunds = StringEscapeUtils.unescapeHtml4(params.get("billFunds"));
//        String billFundsDesc = StringEscapeUtils.unescapeHtml4(params.get("billFundsDesc"));
//        String orderDesc = StringEscapeUtils.unescapeHtml4(params.get("orderDesc"));
//
//        params.put("billFunds",billFunds);
//        params.put("billFundsDesc",billFundsDesc);
//        params.put("orderDesc",orderDesc);
//
//        //提前做特殊字符转换
//        String jsonModels = JSONObject.toJSONString(params);
//
//        CupsPaySignRequest cupsPaySignRequest = new CupsPaySignRequest();
//        cupsPaySignRequest.setCupsPayCallBackResultStr(jsonModels);
//        cupsPaySignRequest.setSecret("QFRCtbBJBD8mDFTD7bdhT2dMrmSk6SMy");
//        cupsPaySignRequest.setApiKey("90261839da79423d8a066085716aa4be");
//        cupsPaySignRequest.setAppId("8a81c1bf7f93c86b018098b09eb10ab4");
//        CupsService service = new CupsService();
//        service.cupsPaySign(cupsPaySignRequest);
//
//
//        String desc = "红星二锅头·56°100ml小白扁等多件商品[ _`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？–]|\\n|\\r|\\t//–";
////            Pattern p = Pattern.compile(regEx);
////            Matcher m = p.matcher(request.getDesc());
////            desc = m.replaceAll("");
//            desc = desc.replaceAll("[^a-zA-Z0-9\u4E00-\u9FA5]", "");
//        System.out.println(desc);

//        String timestamp = DateUtil.format(new Date(),"yyyyMMddHHmmss");
//        JSONObject json = new JSONObject();
//        json.put("requestTimestamp",timestamp);
//        json.put("mid",MID);
//        json.put("tid", TID);
//        json.put("instMid", INSTMID);
//        json.put("merOrderId", "31YTOD202209211947561089");
//
//        String authorization = postOpenBodySigForNetpay("8a81c1bf7f93c86b018098b09eb10ab4", "90261839da79423d8a066085716aa4be", timestamp, nonce, json.toString());
//        JSONObject jsonObject = JSONObject.parseObject(postJson(json,CPUS_QUERY_ORDER_URL,authorization));
//        log.info("====银联查询单笔订单：{}", jsonObject);

        //退款查询
        String url = "https://api-mop.chinaums.com/v1/netpay/refund-query";
        String timestamp = DateUtil.format(new Date(),"yyyyMMddHHmmss");
        JSONObject json = new JSONObject();
        json.put("requestTimestamp",timestamp);
        json.put("mid",MID);
        json.put("tid", TID);
        json.put("instMid", INSTMID);
        json.put("merOrderId", "31YTOD202209300819420583");

        String authorization = postOpenBodySigForNetpay("8a81c1bf7f93c86b018098b09eb10ab4", "90261839da79423d8a066085716aa4be", timestamp, nonce, json.toString());
        JSONObject jsonObject = JSONObject.parseObject(postJson(json,url,authorization));
        log.info("====银联查询单笔订单：{}", jsonObject);
    }

    /**
     * 验签
     */
    public static boolean checkSign(String md5Key, Map<String, String> params) {
        String sign = params.get("sign");
        if (StringUtils.isBlank(sign)) {
            return false;
        }
        String signV = makeSign(md5Key, params);
        System.out.println(signV);
        return StringUtils.equalsIgnoreCase(sign, signV);
    }

//    /*发送post请求得到返回的数据*/
//    public static JSONObject doPost(String url,JSONObject json, String authorization){
//
//        CloseableHttpClient httpclient = HttpClientBuilder.create().build();
//        HttpPost post = new HttpPost(url);
//        post.setHeader("Authorization",authorization);
//
//        JSONObject response = null;
//        try {
//            StringEntity s = new StringEntity(json.toString());
//            s.setContentEncoding("UTF-8");
//            s.setContentType("application/json");//发送json数据需要设置contentType
//            post.setEntity(s);
//            CloseableHttpResponse res = httpclient.execute(post);
//            HttpEntity entity = res.getEntity();
//            String result = EntityUtils.toString(entity);// 返回json格式：
//            response = JSONObject.parseObject(result);
//            res.close();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        return response;
//    }

    /**
     *
     * @param json
     *            JSON
     * @param URL
     *            地址
     * @param Authorization
     *            签名信息
     * @return
     */
    public static String postJson(JSONObject json, String URL, String Authorization) {

        org.apache.http.client.HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(URL);
        post.setHeader("Content-Type", "application/json");
        post.addHeader("Authorization", Authorization);
        String result = "";
        try {
            StringEntity s = new StringEntity(json.toString(), "utf-8");
            s.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            post.setEntity(s);
            // 发送请求
            HttpResponse httpResponse = client.execute(post);
            // 获取响应输入流
            InputStream inStream = httpResponse.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "utf-8"));
            StringBuilder strber = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null)
                strber.append(line + "\n");
            inStream.close();
            result = strber.toString();
            System.out.println(result);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                System.out.println("请求服务器成功，做相应处理");
            } else {
                System.out.println("请求服务端失败");
            }

        } catch (Exception e) {
            System.out.println("请求异常");
            throw new RuntimeException(e);
        }

        return result;
    }


    /**
     * 第二种调用接口签名
     *
     * @param appId
     * @param appKey
     * @param timestamp
     * @param nonce
     * @param body
     * @return
     * @throws Exception
     */
    public static String postOpenBodySigForNetpay(String appId, String appKey, String timestamp, String nonce, String body)
            throws Exception {
        InputStream is = new ByteArrayInputStream(body.getBytes("UTF-8"));
        String bodyDigest = DigestUtils.sha256Hex(is);
        System.out.println(bodyDigest);
//       log.info("银联头部加密：{}" ,bodyDigest);
        String str1_C = appId + timestamp + nonce + bodyDigest;
        byte[] localSignature = hmacSHA256(str1_C.getBytes(), appKey.getBytes());
        String localSignatureStr = Base64.encodeBase64String(localSignature);
        log.info("银联头部加密1：{}" ,localSignatureStr);
        return ("OPEN-BODY-SIG AppId=" + "\"" + appId + "\"" + ", Timestamp=" + "\"" + timestamp + "\"" + ", Nonce="
                + "\"" + nonce + "\"" + ", Signature=" + "\"" + localSignatureStr + "\"");
    }

    public static byte[] hmacSHA256(byte[] data, byte[] key) throws NoSuchAlgorithmException, InvalidKeyException {
        String algorithm = "HmacSHA256";
        Mac mac = Mac.getInstance(algorithm);
        mac.init(new SecretKeySpec(key, algorithm));
        return mac.doFinal(data);
    }

    /**
     * 验签2
     */
    public static boolean checkSign2(String md5Key, Map<String, String> params) {
        // 获取params中的sign
        String originalSign = params.get("sign");
        // 生成待签字串 和 sign
        String preStrNew = buildSignString(params); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
        String preStrNew_md5Key = preStrNew + md5Key;
        String sign = "";


        if(params.get("signType").equals("SHA256")){
            sign = DigestUtils.sha256Hex(getContentBytes(preStrNew_md5Key)).toUpperCase();
        }else{
            sign = DigestUtils.md5Hex(getContentBytes(preStrNew_md5Key)).toUpperCase();
        }
        log.info("=============银联回调验签签名生成===================={}", sign);
        // 返回结果
        return originalSign.equals(sign);
    }

    public static String makeSign(String md5Key, Map<String, String> params) {
        String preStr = buildSignString(params); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
        String text = preStr + md5Key;
        if(params.get("signType").equals("SHA256")){
            return DigestUtils.sha256Hex(getContentBytes(text)).toUpperCase();
        }else{
            return DigestUtils.md5Hex(getContentBytes(text)).toUpperCase();
        }

    }

    // 构建签名字符串
    private static String buildSignString(Map<String, String> params) {
        if (params == null || params.size() == 0) {
            return "";
        }
        List<String> keys = new ArrayList<>(params.size());
        for (String key : params.keySet()) {
            if ("sign".equals(key))
                continue;
            if (StringUtils.isEmpty(params.get(key)))
                continue;
            keys.add(key);
        }
        Collections.sort(keys);
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);

            if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
                buf.append(key + "=" + value);
            } else {
                buf.append(key + "=" + value + "&");
            }
        }

        return buf.toString();
    }


    // 根据编码类型获得签名内容byte[]
    private static byte[] getContentBytes(String content) {
        try {
            return content.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("签名过程中出现错误");
        }
    }

}