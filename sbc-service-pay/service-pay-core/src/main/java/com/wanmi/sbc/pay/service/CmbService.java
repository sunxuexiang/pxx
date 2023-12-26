package com.wanmi.sbc.pay.service;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.pay.api.request.*;
import com.wanmi.sbc.pay.api.response.CmbPayOrderReqDataResponse;
import com.wanmi.sbc.pay.api.response.CmbPayOrderResponse;
import com.wanmi.sbc.pay.api.response.CmbPayRefundResponse;
import com.wanmi.sbc.pay.bean.enums.PayGatewayEnum;
import com.wanmi.sbc.pay.bean.enums.TradeStatus;
import com.wanmi.sbc.pay.bean.enums.TradeType;
import com.wanmi.sbc.pay.model.root.PayGatewayConfig;
import com.wanmi.sbc.pay.model.root.PayTradeRecord;
import com.wanmi.sbc.pay.repository.TradeRecordRepository;
import com.wanmi.sbc.pay.unionpay.acp.sdk.HttpClient;
import com.wanmi.sbc.pay.utils.GeneratorUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 招商支付
 */
@Slf4j
@Service
public class CmbService {

    private static int socketTimeout = 10000;// 连接超时时间,默认10秒
    private static int connectTimeout = 30000;// 传输超时时间,默认30秒

    private static final String sMerchantKey = "C914d2818371d660";

    private static final String branchNo = "0731";

    private static final String merchantNo = "630425";

    private static final  String charset = "UTF-8";

    private static final String signTypeSHA = "SHA-256";

//    private static String QUERY_ORDER_SINGLE_URL = "http://121.15.180.66:801/netpayment_directlink_nosession/BaseHttp.dll?QuerySingleOrder";

    /**
     * 获取公钥
     */
    private static String QUERY_DO_BUSINESS_URL = "https://b2b.cmbchina.com/CmbBank_B2B/UI/NetPay/DoBusiness.ashx";

//    private static String QUERY_DO_BUSINESS_URL_TEST = "http://121.15.180.66:801/netpayment_directlink_nosession/BaseHttp.dll?DoRefundV2";

    /**
     * 退款
     */

    private static String REFUND_URL = "https://payment.ebank.cmbchina.com/NetPayment/BaseHttp.dll?DoRefund";



    /**
     * 招商支付成功回调地址
     */
    private static final String CMBPAYSUCCCALLBACK = "/tradeCallback/CMBPaySuccessCallBack";

    @Resource
    private TradeRecordRepository recordRepository;

    @Autowired
    private PayDataService payDataService;





    /**
     * 添加订单支付--交易记录
     *
     * @param request
     */
    public CmbPayOrderResponse cmbPayOrder(CmbPayOrderRequest  request) {

        log.info("收到的招商支付订单参数：" + JSONObject.toJSONString(request));
        //是否重复支付
        PayTradeRecord record = recordRepository.findByBusinessId(request.getOut_trade_no());
        String html = "";
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
            record.setChannelItemId(28L);
            record.setTradeType(TradeType.PAY);
            record.setCreateTime(LocalDateTime.now());
            record.setStatus(TradeStatus.PROCESSING);
            recordRepository.saveAndFlush(record);
        }
        PayGatewayConfig payGatewayConfig = payDataService.queryConfigByNameAndStoreId(PayGatewayEnum.CMB,request.getStoreId());


        CmbPayOrderReqDataResponse reqData =  CmbPayOrderReqDataResponse.builder()
                .dateTime(DateUtil.format(LocalDateTime.now(),DateUtil.FMT_TIME_3))
                .branchNo(branchNo).merchantNo(merchantNo).date(DateUtil.format(record.getCreateTime(), DateUtil.FMT_TIME_5))
                .orderNo(record.getBusinessId()).amount(String.valueOf(record.getApplyPrice())).expireTimeSpan(request.getOutTime())
                .payNoticeUrl(getNotifyUrl(payGatewayConfig)).build();

        return CmbPayOrderResponse.builder()
                .body(request.getBody())
                .fbPubKey(payGatewayConfig.getPublicKey())
                .charset(charset)
                .version("1.0").sign(convertSHA256(createCrmLinkString(reqData).append("&" + sMerchantKey))).signType(signTypeSHA)
                .reqData(JSONObject.toJSONString(reqData)).build();
    }

    /**
     * 招行支付退款
     * @param request
     * @return
     */
    public CmbPayRefundResponse cmbPayRefund(CmbPayRefundDataRequest request){

        try {
            request.setBranchNo(branchNo);
            request.setMerchantNo(merchantNo);
            request.setDateTime(DateUtil.format(LocalDateTime.now(), DateUtil.FMT_TIME_3));

            String desc = "";
            if (request.getDesc()!=null) {
                desc = request.getDesc().replaceAll("[^a-zA-Z0-9\u4E00-\u9FA5]", "");
            }
            request.setDesc(desc);

            CmbPayRefundRequest refundRequest = CmbPayRefundRequest.builder().build();
            refundRequest.setVersion("2.0");
            refundRequest.setCharset(charset);
            refundRequest.setSignType(signTypeSHA);

            StringBuilder sb = createCrmLinkString(request);
            sb.append("&" + sMerchantKey);
            String sign = convertSHA256(sb);
            refundRequest.setSign(sign);

            refundRequest.setReqData(request);

            //发送请求
            HttpClient httpClient = new HttpClient(REFUND_URL,socketTimeout,connectTimeout);
            Map<String, String> jsonRequestData = new HashMap<>();
            jsonRequestData.put("jsonRequestData", JSONObject.toJSONString(refundRequest));
            System.out.println(jsonRequestData.get("jsonRequestData"));
            jsonRequestData.put("charset", charset);
            String   result  =  httpClient.sendSring(jsonRequestData,charset);
            CmbPayRefundResponse response  = JSONObject.parseObject(result, CmbPayRefundResponse.class);
            return response;
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }


//    /**
//     * 根据订单查询当前订单的数据（向招商银行查询，看和客户端联调的时候，客户端需要什么在做返回）
//     * @param request
//     * @return
//     */
//    @Transactional(noRollbackFor = SbcRuntimeException.class)
//    public CmbDoBusinessResponse cmbPayOrder(@Valid CmbPayOrderRequest request) {
//        String result = "";
//       try{
//           //查询单笔订单API
//           CmbQuerySingleOrderRequest cmbQuerySingleOrderRequest = new CmbQuerySingleOrderRequest();
//           cmbQuerySingleOrderRequest.setVersion("2.0");
//           cmbQuerySingleOrderRequest.setCharset(charset);
//           cmbQuerySingleOrderRequest.setSignType(signTypeSHA);
//
//           CmbSingleOrderDataRequest cmbSingleOrderDataRequest = new CmbSingleOrderDataRequest();
//           cmbSingleOrderDataRequest.setDateTime(DateUtil.format(new Date(), DateUtil.FMT_TIME_3));
//           cmbSingleOrderDataRequest.setBranchNo(branchNo);
//           cmbSingleOrderDataRequest.setMerchantNo(merchantNo);
//           cmbSingleOrderDataRequest.setType("B");
//           //获取订单数据
//           cmbSingleOrderDataRequest.setDate(request.getDateTime());//订单生成日期
//           cmbSingleOrderDataRequest.setOrderNo(request.getOut_trade_no());
//           cmbSingleOrderDataRequest.setOperatorNo("9999");
//           cmbSingleOrderDataRequest.setBankSerialNo("");
//           cmbQuerySingleOrderRequest.setReqData(cmbSingleOrderDataRequest);
//
//           StringBuilder dbStrToSign = createCrmLinkString(cmbQuerySingleOrderRequest.getReqData());
//           log.info(dbStrToSign.toString());
//           dbStrToSign.append("&").append(sMerchantKey);
//           //生成签名
//           String dbSign =convertSHA256(dbStrToSign);
//           cmbQuerySingleOrderRequest.setSign(dbSign);
//           addTradeRecord(request);
//           HttpClient httpClient = new HttpClient(QUERY_ORDER_SINGLE_URL,socketTimeout,connectTimeout);
//           Map<String, String> jsonRequestData = new HashMap<>();
//           jsonRequestData.put("jsonRequestData", JSONObject.toJSONString(cmbQuerySingleOrderRequest));
//           jsonRequestData.put("charset", "UTF-8");
//           result  =  httpClient.sendSring(jsonRequestData,charset);
//       }catch (Exception e){
//           e.printStackTrace();
//           throw new SbcRuntimeException(CommonErrorCode.FAILED);
//       }
//        return JSONObject.parseObject(result, CmbDoBusinessResponse.class);
//    }
//
//    /**
//     * 招商回调支付接口
//     * @param appRequest
//     * @return
//     */
//    @Transactional(noRollbackFor = SbcRuntimeException.class)
//    public BaseResponse cmbCallback(CmbCallBackRequest appRequest){
//        try{
//            Long storeId = -1L;
//            PayGatewayConfig payGatewayConfig = payDataService.queryConfigByNameAndStoreId(PayGatewayEnum.CMBPAY, storeId);
//            //添加商户密钥(公式)需要去查询数据库（chain_pay_gateway_config，每晚2点15分左右定时更新公式）
//
//            // 如果公式没有则直接掉公式接口（并更新到数据库）
//            //签名验证
//            StringBuilder strToSign = createCrmLinkString(appRequest.getNoticeData());
//            String publicKey = payGatewayConfig == null ? "" : payGatewayConfig.getPublicKey();
//            if(StringUtils.isEmpty(publicKey)){
//                publicKey = cmbDoBusiness();
//            }
//            if(StringUtils.isEmpty(publicKey)){
//                return BaseResponse.FAILED();
//            }
//            //银行给过来的签名内容
//            String signStr = appRequest.getSign();
//            Boolean bool =  isValidSignature(strToSign.toString(),signStr, publicKey);
//            if(!bool){
//                log.info("签名验证不通过:" + signStr + " self:" + strToSign);
//                return BaseResponse.FAILED();
//            }
//            //更新订单信息
//            CmbNoticeDataRequest request = appRequest.getNoticeData();
//
//            //查询单笔信息，看是否交易成功
////            CmbDoBusinessResponse response = queryOrderSingle(request.getOrderNo(),request.getDateTime());
////            if(response.getRspData().getRspCode()){}
//
//            //获取订单
//            PayTradeRecord record = recordRepository.findByBusinessId(request.getOrderNo());
//            record.setPracticalPrice(new BigDecimal(request.getAmount()));
//            record.setCallbackTime(LocalDateTime.now());
//            record.setFinishTime(LocalDateTime.parse(request.getBankDate()));
//            record.setTradeNo(request.getBankSerialNo());
//            record.setStatus(TradeStatus.SUCCEED);
//            recordRepository.updateTradeStatusAndPracticalPriceAndFinishTime()
//            //更新mogonDb券的问题
//            //以及mysql数据库券的问题
//            return new BaseResponse("HTTP Status Code 200");
//        }catch (Exception e){
//            throw new RuntimeException();
//        }
//    }
//
//    /**
//     * 招商回调签约接口
//     * @param appRequest
//     * @return
//     */
//    @Transactional(noRollbackFor = SbcRuntimeException.class)
//    public BaseResponse cmbCallbackSign(CmbCallbackSignRequest appRequest){
//        try{
//            //签名验证
//            StringBuilder strToSign = createCrmLinkString(appRequest.getNoticeData());
//            //添加商户密钥(公式)需要去查询数据库（chain_pay_gateway_config，每晚2点15分左右定时更新公式）
//            // 如果公式没有则直接掉公式接口（并更新到数据库）
//            String publicKey = "";
//            if(StringUtils.isEmpty(publicKey)){
//                publicKey = cmbDoBusiness();
//            }
//            if(StringUtils.isEmpty(publicKey)){
//                return BaseResponse.FAILED();
//            }
//            //银行给过来的签名内容
//            String signStr = appRequest.getSign();
//            Boolean bool =  isValidSignature(strToSign.toString(),signStr, publicKey);
//            if(!bool){
//                log.info("签名验证不通过:" + signStr + " self:" + strToSign);
//                return BaseResponse.FAILED();
//            }
//            //验证是否有数据，有就更新，或者验证已经签约，没有就存储
//            return new BaseResponse("HTTP Status Code 200");
//        }catch (Exception e){
//            throw new RuntimeException();
//        }
//    }
//
//    public BaseResponse cmbPayRefund() {
//        return null;
//    }
//
//
//    public CmbDoBusinessResponse queryOrderSingle(String orderNo, String dateTime){
//        //查询单笔订单API
//        String result = "";
//        try{
//            CmbQuerySingleOrderRequest cmbQuerySingleOrderRequest = new CmbQuerySingleOrderRequest();
//            cmbQuerySingleOrderRequest.setVersion("2.0");
//            cmbQuerySingleOrderRequest.setCharset(charset);
//            cmbQuerySingleOrderRequest.setSignType(signTypeSHA);
//
//            CmbSingleOrderDataRequest cmbSingleOrderDataRequest = new CmbSingleOrderDataRequest();
//            cmbSingleOrderDataRequest.setDateTime(DateUtil.format(new Date(), DateUtil.FMT_TIME_3));
//            cmbSingleOrderDataRequest.setBranchNo(branchNo);
//            cmbSingleOrderDataRequest.setMerchantNo(merchantNo);
//            cmbSingleOrderDataRequest.setType("B");
//            //获取订单数据
//            cmbSingleOrderDataRequest.setDate(dateTime);//订单生成日期
//            cmbSingleOrderDataRequest.setOrderNo(orderNo);
//            cmbSingleOrderDataRequest.setOperatorNo("9999");
//            cmbSingleOrderDataRequest.setBankSerialNo("");
//            cmbQuerySingleOrderRequest.setReqData(cmbSingleOrderDataRequest);
//
//            StringBuilder dbStrToSign = createCrmLinkString(cmbQuerySingleOrderRequest.getReqData());
//            log.info(dbStrToSign.toString());
//            dbStrToSign.append("&").append(sMerchantKey);
//            //生成签名
//            String dbSign =convertSHA256(dbStrToSign);
//            cmbQuerySingleOrderRequest.setSign(dbSign);
//            HttpClient httpClient = new HttpClient(QUERY_ORDER_SINGLE_URL,socketTimeout,connectTimeout);
//            Map<String, String> jsonRequestData = new HashMap<>();
//            jsonRequestData.put("jsonRequestData", JSONObject.toJSONString(cmbQuerySingleOrderRequest));
//            jsonRequestData.put("charset", "UTF-8");
//            result  =  httpClient.sendSring(jsonRequestData,charset);
//        }catch (Exception e){
//            e.printStackTrace();
//            throw new SbcRuntimeException(CommonErrorCode.FAILED);
//        }
//        return JSONObject.parseObject(result, CmbDoBusinessResponse.class);
//    }
//
    /**
     * 获取公式
     * @return
     * @throws Exception
     */
    public BaseResponse cmbDoBusiness() {
        try{

            CmbDoBusinessRequest doBusinessRequest = new CmbDoBusinessRequest();
            doBusinessRequest.setVersion("1.0");
            doBusinessRequest.setCharset(charset);
            doBusinessRequest.setSignType(signTypeSHA);

            CmbReqDataRequest reqDataRequest = new CmbReqDataRequest();
            reqDataRequest.setDateTime(DateUtil.format(new Date(), DateUtil.FMT_TIME_3));
            reqDataRequest.setTxCode("FBPK");
            reqDataRequest.setBranchNo(branchNo);
            reqDataRequest.setMerchantNo(merchantNo);

            doBusinessRequest.setReqData(reqDataRequest);
            StringBuilder dbStrToSign = createCrmLinkString(doBusinessRequest.getReqData());
            log.info(dbStrToSign.toString());
            dbStrToSign.append("&").append(sMerchantKey);
            String dbSign =convertSHA256(dbStrToSign);
            doBusinessRequest.setSign(dbSign);

            HttpClient httpClient = new HttpClient(QUERY_DO_BUSINESS_URL,socketTimeout,connectTimeout);
            Map<String, String> jsonRequestData = new HashMap<>();
            jsonRequestData.put("jsonRequestData", JSONObject.toJSONString(doBusinessRequest));
            jsonRequestData.put("charset", charset);
            String result =  httpClient.sendSring(jsonRequestData,charset);
            CmbDoBusinessResponse cmbDoBusinessResponse = JSONObject.parseObject(result, CmbDoBusinessResponse.class);
            if(cmbDoBusinessResponse.getRspData() == null || StringUtils.isEmpty(cmbDoBusinessResponse.getRspData().getRspCode())
                    || !cmbDoBusinessResponse.getRspData().getRspCode().equals("SUC0000")){
                return null;
            }
            PayGatewayConfig payGatewayConfig = payDataService.queryConfigByNameAndStoreId(PayGatewayEnum.CMB,-1L);
            if(Objects.nonNull(payGatewayConfig)){
                log.info("修改前招商支付publicKey:" + payGatewayConfig.getPublicKey());
                payGatewayConfig.setPublicKey(cmbDoBusinessResponse.getRspData().getFbPubKey());
                payDataService.saveConfig(payGatewayConfig);
                log.info("修改了招商支付publicKey:" + cmbDoBusinessResponse.getRspData().getFbPubKey());
            }else{
                log.info("定时修改招商支付publicKey错误....payGatewayConfig is null");
            }
            return BaseResponse.SUCCESSFUL();
        }catch (Exception e){
            throw new SbcRuntimeException("执行同步招商publicKey异常错误",e);
        }
    }



    private  String getNotifyUrl(PayGatewayConfig payGatewayConfig){
        StringBuilder notify_url = new StringBuilder();
        notify_url.append(payGatewayConfig.getBossBackUrl()+CMBPAYSUCCCALLBACK);
        notify_url.append("/");
        notify_url.append(payGatewayConfig.getStoreId());
        return notify_url.toString();
    }




    public static void main(String [] args) throws Exception {
//        CmbPayRefundDataRequest refundRequest = CmbPayRefundDataRequest.builder().build();
//        refundRequest.setDate("20220318");
//        refundRequest.setOrderNo("PO202203180850109630");
//        refundRequest.setRefundSerialNo("1169C09A0003EB00000A");
//        refundRequest.setAmount("652.50");
//        refundRequest.setDesc("测试退款");
//      wxPayRefund(refundRequest);
//        cmbDoBusiness();

        BigDecimal f = new BigDecimal(0.5d).setScale(2,BigDecimal.ROUND_DOWN);
        BigDecimal f1 = new BigDecimal(String.valueOf("66.6")).setScale(2,BigDecimal.ROUND_DOWN);
        System.out.println(f + "==" + f1);
        if(f.doubleValue() % 1D != 0){
            System.out.println(f1.multiply(new BigDecimal(2D)));
        }
    }

    //    /**
//     *
//     * @param strToSign 待验证签名字符串strToSign
//     * @param strSign 签名内容为strSign
//     * @param publicKey 招行通知公钥为publicKey
//     * @return
//     */
//    private boolean isValidSignature(String strToSign, String strSign, String publicKey)
//    {
//        try
//        {
//            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//            byte[] encodedKey = Base64.getDecoder().decode(publicKey);
//            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
//
//            Signature signature = Signature
//                    .getInstance("SHA1WithRSA");
//
//            signature.initVerify(pubKey);
//            signature.update(strToSign.getBytes("UTF-8") );
//
//            boolean bverify = signature.verify( Base64.getDecoder().decode(strSign) );
//            return bverify;
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//
//        return false;
//    }
//
    private static String HexString(byte[] baSrc)
    {
        if (baSrc == null)
        {
            return "";
        }

        int nByteNum = baSrc.length;
        StringBuilder sbResult = new StringBuilder(nByteNum * 2);

        for (int i = 0; i < nByteNum; i++)
        {
            char chHex;

            byte btHigh = (byte)((baSrc[i] & 0xF0) >> 4);
            if (btHigh < 10)
            {
                chHex = (char)('0' + btHigh);
            }
            else
            {
                chHex = (char)('A' + (btHigh - 10));
            }
            sbResult.append(chHex);

            byte btLow = (byte)(baSrc[i] & 0x0F);
            if (btLow < 10)
            {
                chHex = (char)('0' + btLow);
            }
            else
            {
                chHex = (char)('A' + (btLow - 10));
            }
            sbResult.append(chHex);
        }

        return sbResult.toString();
    }

    private static String convertSHA256(StringBuilder strToSign) {

        try{
            // 创建加密对象
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            // 传入要加密的字符串,按指定的字符集将字符串转换为字节流
            messageDigest.update(strToSign.toString().getBytes("UTF-8"));
            byte byteBuffer[] = messageDigest.digest();
            // 將 byte数组转换为16进制string
            String signStr = HexString(byteBuffer);
            return signStr;
        }catch (Exception e){
            return "";
        }
    }
    //
//
    private static StringBuilder createCrmLinkString(Object obj){
        JSONObject joNoticeData = (JSONObject) JSONObject.toJSON(obj);

        //按字典顺序排序，即字母顺序与大小写无关
        List<String> keys = new ArrayList<String>(joNoticeData.keySet());
        Collections.sort(keys, new Comparator<String>() {
            public int compare(String s1, String s2) {
                int i = s1.toLowerCase().compareTo(s2.toLowerCase());
                if (i != 0) {
                    return i;
                }
                return s2.compareTo(s1);
            }
        });

        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            if(joNoticeData.getString(key) == null || joNoticeData.getString(key).equals("")){
                sb.append(key).append("=").append("&");
            }else{
                sb.append(key).append("=").append(joNoticeData.getString(key)).append("&");
            }
        }
        return  sb.deleteCharAt(sb.length() - 1);
    }
}