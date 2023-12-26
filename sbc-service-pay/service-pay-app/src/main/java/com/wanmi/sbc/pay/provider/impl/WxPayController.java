package com.wanmi.sbc.pay.provider.impl;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.pay.api.provider.WxPayProvider;
import com.wanmi.sbc.pay.api.request.*;
import com.wanmi.sbc.pay.api.response.*;
import com.wanmi.sbc.pay.bean.enums.PayGatewayEnum;
import com.wanmi.sbc.pay.model.root.PayGatewayConfig;
import com.wanmi.sbc.pay.service.PayDataService;
import com.wanmi.sbc.pay.service.WxPayService;
import com.wanmi.sbc.pay.weixinpaysdk.WXPayConstants;
import com.wanmi.sbc.pay.weixinpaysdk.WXPayUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信支付接口
 */
@RestController
@Slf4j
public class WxPayController implements WxPayProvider {

    /**
     * 微信支付成功回调地址
     */
    private static final String WXPAYSUCCCALLBACK = "/tradeCallback/WXPaySuccessCallBack";

    /**
     * 鲸币支付回调地址
     * */
    private static final String WXPAYSUCCESSJINGBIBACK = "/tradeCallback/wxMerchantCallBack";

    /**
     * 囤货订单微信支付成功回调地址
     */
    private static final String WXPAYPILESUCCCALLBACK = "/tradeCallback/WXPayPileOrderSuccessCallBack";

    /**
     * 充值成功回调地址
     */
    private static final String WXPAYRECHARGESUCCCALLBACK = "/tradeCallback/WXPayRechargeSuccessCallBack";

    /**
     * 微信提货支付成功回调接口
     */
    private static final String WXTAKEGOODPAYSUCCCALLBACK = "/tradeCallback/WXPayTakeGoodSuccessCallBack";

    /**
     * 微信退款成功回调地址
     */
    private static final String WXREFUNDSUCCCALLBACK = "/tradeCallback/WXPayRefundSuccessCallBack/";

    /**
     * 囤货微信退款成功回调地址
     */
    private static final String WXPILEREFUNDSUCCCALLBACK = "/tradeCallback/WXPayPileRefundSuccessCallBack/";

    @Autowired
    private WxPayService wxPayService;

    @Autowired
    private PayDataService payDataService;

    /**
     * 统一下单接口--native扫码支付
     * @param request
     */
    @Override
    public BaseResponse<WxPayForNativeResponse> wxPayForNative(@RequestBody WxPayForNativeRequest request){
        PayGatewayConfig payGatewayConfig = payDataService.queryConfigByNameAndStoreId(PayGatewayEnum.WECHAT,request.getStoreId());
        request.setAppid(payGatewayConfig.getAppId());
        request.setMch_id(payGatewayConfig.getAccount());
        request.setNonce_str(WXPayUtil.generateNonceStr());
        request.setNotify_url(getNotifyUrl(payGatewayConfig));
        try {
            Map<String,String> nativeMap = WXPayUtil.objectToMap(request);
            nativeMap.remove("storeId");
            nativeMap.remove("pay_order_no");
            //获取签名
            String sign = WXPayUtil.generateSignature(nativeMap,payGatewayConfig.getApiKey());
            request.setSign(sign);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //调用统一下单接口
        WxPayForNativeResponse response = wxPayService.wxPayForNative(request);
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<WxPayForNativeResponse> wxPayForNativeStoreNotify(@RequestBody WxPayForNativeRequest request){
        PayGatewayConfig payGatewayConfig = payDataService.queryConfigByNameAndStoreId(PayGatewayEnum.WECHAT,request.getStoreId());
        request.setAppid(payGatewayConfig.getAppId());
        request.setMch_id(payGatewayConfig.getAccount());
        request.setNonce_str(WXPayUtil.generateNonceStr());
        request.setNotify_url(getNotifyUrlStore(payGatewayConfig));
        request.setTotal_fee(String.valueOf(new BigDecimal(request.getTotal_fee()).multiply(new BigDecimal(100)).
                setScale(0, BigDecimal.ROUND_DOWN)));
        try {
            Map<String,String> nativeMap = WXPayUtil.objectToMap(request);
            nativeMap.remove("storeId");
            nativeMap.remove("pay_order_no");
            //获取签名
            String sign = WXPayUtil.generateSignature(nativeMap,payGatewayConfig.getApiKey());
            request.setSign(sign);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //调用统一下单接口
        WxPayForNativeResponse response = wxPayService.wxPayForNative(request);
        return BaseResponse.success(response);
    }
    public static boolean isDecimal(String number) {
        return number.matches("-?\\d+\\.\\d+");
    }

    @Override
    public BaseResponse<WxPayForNativeResponse> wxPayForNativeCustomNotify(@RequestBody WxPayForNativeRequest nativeRequest){
        PayGatewayConfig payGatewayConfig = payDataService.queryConfigByNameAndStoreId(PayGatewayEnum.WECHAT, -1l);
        nativeRequest.setMch_id(payGatewayConfig.getAccount());
        nativeRequest.setAppid(payGatewayConfig.getAppId());
        nativeRequest.setMch_id(payGatewayConfig.getAccount());

        StringBuffer callbackUrl = new StringBuffer();
        callbackUrl.append(payGatewayConfig.getBossBackUrl()+nativeRequest.getNotify_url());
        nativeRequest.setNotify_url(callbackUrl.toString());

        try {
            Map<String,String> nativeMap = WXPayUtil.objectToMap(nativeRequest);
            nativeMap.remove("storeId");
            //获取签名
            String sign = WXPayUtil.generateSignature(nativeMap,payGatewayConfig.getApiKey());
            nativeRequest.setSign(sign);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //调用统一下单接口
        WxPayForNativeResponse response = wxPayService.wxPayForNative(nativeRequest);
        return BaseResponse.success(response);
    }

    private  String getNotifyUrl(PayGatewayConfig payGatewayConfig){
        StringBuilder notify_url = new StringBuilder();
        notify_url.append(payGatewayConfig.getBossBackUrl()+WXPAYSUCCCALLBACK);
        notify_url.append("/");
        notify_url.append(payGatewayConfig.getStoreId());
       return notify_url.toString();
    }

    private  String getNotifyUrlStore(PayGatewayConfig payGatewayConfig){
        StringBuilder notify_url = new StringBuilder();
        notify_url.append(payGatewayConfig.getBossBackUrl()+WXPAYSUCCESSJINGBIBACK);
        return notify_url.toString();
    }

    private  String getPileOrderNotifyUrl(PayGatewayConfig payGatewayConfig){
        StringBuilder notify_url = new StringBuilder();
        notify_url.append(payGatewayConfig.getBossBackUrl()+WXPAYPILESUCCCALLBACK);
        notify_url.append("/");
        notify_url.append(payGatewayConfig.getStoreId());
        return notify_url.toString();
    }

    private  String getRechargeNotifyUrl(PayGatewayConfig payGatewayConfig){
        StringBuilder notify_url = new StringBuilder();
        notify_url.append(payGatewayConfig.getBossBackUrl()+WXPAYRECHARGESUCCCALLBACK);
        notify_url.append("/");
        notify_url.append(payGatewayConfig.getStoreId());
        return notify_url.toString();
    }

    private String getTakeGoodNotifyUrl(PayGatewayConfig payGatewayConfig){
        StringBuilder notify_url = new StringBuilder();
        notify_url.append(payGatewayConfig.getBossBackUrl()+WXTAKEGOODPAYSUCCCALLBACK);
        notify_url.append("/");
        notify_url.append(payGatewayConfig.getStoreId());
        return notify_url.toString();
    }
    /**
     * 统一下单--非微信浏览器h5支付
     * @param mWebRequest
     * @return
     */
    @Override
    public BaseResponse<WxPayForMWebResponse> wxPayForMWeb(@RequestBody WxPayForMWebRequest mWebRequest){
        PayGatewayConfig payGatewayConfig = payDataService.queryConfigByNameAndStoreId(PayGatewayEnum.WECHAT,mWebRequest.getStoreId());
        mWebRequest.setAppid(payGatewayConfig.getAppId());
        mWebRequest.setMch_id(payGatewayConfig.getAccount());
        mWebRequest.setNonce_str(WXPayUtil.generateNonceStr());
        mWebRequest.setNotify_url(getNotifyUrl(payGatewayConfig));
        try {
            Map<String,String> mwebMap = WXPayUtil.objectToMap(mWebRequest);
            mwebMap.remove("storeId");
            //获取签名
            String sign = WXPayUtil.generateSignature(mwebMap,payGatewayConfig.getApiKey());
            mWebRequest.setSign(sign);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //调用统一下单接口
        WxPayForMWebResponse response = wxPayService.wxPayForMWeb(mWebRequest);
        return BaseResponse.success(response);
    }

    /**
     * 统一下单--非微信浏览器h5支付
     * @param mWebRequest
     * @return
     */
    @Override
    public BaseResponse<WxPayForMWebResponse> wxPilePayForMWeb(@RequestBody WxPayForMWebRequest mWebRequest){
        PayGatewayConfig payGatewayConfig = payDataService.queryConfigByNameAndStoreId(PayGatewayEnum.WECHAT,mWebRequest.getStoreId());
        mWebRequest.setAppid(payGatewayConfig.getAppId());
        mWebRequest.setMch_id(payGatewayConfig.getAccount());
        mWebRequest.setNonce_str(WXPayUtil.generateNonceStr());
        mWebRequest.setNotify_url(getPileOrderNotifyUrl(payGatewayConfig));
        try {
            Map<String,String> mwebMap = WXPayUtil.objectToMap(mWebRequest);
            mwebMap.remove("storeId");
            //获取签名
            String sign = WXPayUtil.generateSignature(mwebMap,payGatewayConfig.getApiKey());
            mWebRequest.setSign(sign);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //调用统一下单接口
        WxPayForMWebResponse response = wxPayService.wxPayForMWeb(mWebRequest);
        return BaseResponse.success(response);
    }

    /**
     * 统一下单--微信浏览器内JSApi支付
     * @param jsApiRequest
     * @return
     */
    @Override
    public BaseResponse<Map<String,String>> wxPayForJSApi(@RequestBody WxPayForJSApiRequest jsApiRequest){
        PayGatewayConfig payGatewayConfig = payDataService.queryConfigByNameAndStoreId(PayGatewayEnum.WECHAT,jsApiRequest.getStoreId());
        jsApiRequest.setAppid(payGatewayConfig.getAppId());
        wxSignCommon(jsApiRequest, payGatewayConfig);
        log.info("微信支付[JSApi]统一下单接口入参:{}", jsApiRequest);
        //调用统一下单接口
        WxPayForJSApiResponse response = wxPayService.wxPayForJSApi(jsApiRequest);
        if("SUCCESS".equals(response.getReturn_code()) && "SUCCESS".equals(response.getResult_code())){
            return getSignResultCommon(jsApiRequest.getAppid(), payGatewayConfig.getApiKey(), response.getPrepay_id());
        } else {
            log.error("微信支付[JSApi]统一下单接口调用失败,入参:{},返回结果为:{}", jsApiRequest, response);
            String errMsg = "";
            if(!response.getReturn_code().equals(WXPayConstants.SUCCESS)){
                errMsg = errMsg + response.getReturn_msg()+";";
            }
            if(!response.getResult_code().equals(WXPayConstants.SUCCESS)){
                errMsg = errMsg + response.getErr_code_des()+";";
            }
            throw new SbcRuntimeException("K-100213", new Object[]{errMsg});
        }
    }


    /**
     * 统一下单--微信浏览器内JSApi支付
     * @param jsApiRequest
     * @return
     */
    @Override
    public BaseResponse<Map<String,String>> pileWxPayForJSApi(@RequestBody WxPayForJSApiRequest jsApiRequest){
        PayGatewayConfig payGatewayConfig = payDataService.queryConfigByNameAndStoreId(PayGatewayEnum.WECHAT,jsApiRequest.getStoreId());
        jsApiRequest.setAppid(payGatewayConfig.getAppId());
        pileWxSignCommon(jsApiRequest, payGatewayConfig);
        log.info("微信支付[JSApi]统一下单接口入参:{}", jsApiRequest);
        //调用统一下单接口
        WxPayForJSApiResponse response = wxPayService.wxPayForJSApi(jsApiRequest);
        if("SUCCESS".equals(response.getReturn_code()) && "SUCCESS".equals(response.getResult_code())){
            return getSignResultCommon(jsApiRequest.getAppid(), payGatewayConfig.getApiKey(), response.getPrepay_id());
        } else {
            log.error("微信支付[JSApi]统一下单接口调用失败,入参:{},返回结果为:{}", jsApiRequest, response);
            String errMsg = "";
            if(!response.getReturn_code().equals(WXPayConstants.SUCCESS)){
                errMsg = errMsg + response.getReturn_msg()+";";
            }
            if(!response.getResult_code().equals(WXPayConstants.SUCCESS)){
                errMsg = errMsg + response.getErr_code_des()+";";
            }
            throw new SbcRuntimeException("K-100213", new Object[]{errMsg});
        }
    }

    /**
     * 统一下单--小程序内JSApi支付
     * @param jsApiRequest
     * @return
     */
    @Override
    public BaseResponse<Map<String,String>> wxPayForLittleProgram(@RequestBody WxPayForJSApiRequest jsApiRequest){
        PayGatewayConfig payGatewayConfig = payDataService.queryConfigByNameAndStoreId(PayGatewayEnum.WECHAT,jsApiRequest.getStoreId());
        wxSignCommon(jsApiRequest, payGatewayConfig);
        log.info("小程序支付[JSApi]统一下单接口入参:{}", jsApiRequest);
        //调用统一下单接口
        WxPayForJSApiResponse response = wxPayService.wxPayForJSApi(jsApiRequest);
        if("SUCCESS".equals(response.getReturn_code()) && "SUCCESS".equals(response.getResult_code())){
            return getSignResultCommon(jsApiRequest.getAppid(), payGatewayConfig.getApiKey(), response.getPrepay_id());
        } else {
            log.error("小程序支付[JSApi]统一下单接口调用失败,入参:{},返回结果为:{}", jsApiRequest, response);
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
    }

    /**
     * 囤货统一下单--小程序内JSApi支付
     * @param jsApiRequest
     * @return
     */
    @Override
    public BaseResponse<Map<String,String>> pileWxPayForLittleProgram(@RequestBody WxPayForJSApiRequest jsApiRequest){
        PayGatewayConfig payGatewayConfig = payDataService.queryConfigByNameAndStoreId(PayGatewayEnum.WECHAT,jsApiRequest.getStoreId());
        pileWxSignCommon(jsApiRequest, payGatewayConfig);
        log.info("小程序支付[JSApi]统一下单接口入参:{}", jsApiRequest);
        //调用统一下单接口
        WxPayForJSApiResponse response = wxPayService.wxPayForJSApi(jsApiRequest);
        if("SUCCESS".equals(response.getReturn_code()) && "SUCCESS".equals(response.getResult_code())){
            return getSignResultCommon(jsApiRequest.getAppid(), payGatewayConfig.getApiKey(), response.getPrepay_id());
        } else {
            log.error("小程序支付[JSApi]统一下单接口调用失败,入参:{},返回结果为:{}", jsApiRequest, response);
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
    }

    /**
     * 统一提货下单--小程序内JSApi支付
     * @param jsApiRequest
     * @return
     */
    @Override
    public BaseResponse<Map<String, String>> wxPayTakeGoodForLittleProgram(WxPayForJSApiRequest jsApiRequest) {
        PayGatewayConfig payGatewayConfig = payDataService.queryConfigByNameAndStoreId(PayGatewayEnum.WECHAT,jsApiRequest.getStoreId());
        wxTakeGoodSignCommon(jsApiRequest, payGatewayConfig);
        log.info("小程序支付[JSApi]统一下单接口入参:{}", jsApiRequest);
        //调用统一下单接口
        WxPayForJSApiResponse response = wxPayService.wxPayForJSApi(jsApiRequest);
        if("SUCCESS".equals(response.getReturn_code()) && "SUCCESS".equals(response.getResult_code())){
            return getSignResultCommon(jsApiRequest.getAppid(), payGatewayConfig.getApiKey(), response.getPrepay_id());
        } else {
            log.error("小程序支付[JSApi]统一下单接口调用失败,入参:{},返回结果为:{}", jsApiRequest, response);
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
    }

    /**
     * 统一充值下单--小程序内JSApi支付
     * @param jsApiRequest
     * @return
     */
    @Override
    public BaseResponse<Map<String,String>> wxPayRechargeForLittleProgram(@RequestBody WxPayForJSApiRequest jsApiRequest){
        PayGatewayConfig payGatewayConfig = payDataService.queryConfigByNameAndStoreId(PayGatewayEnum.WECHAT,jsApiRequest.getStoreId());
        wxRechargeSignCommon(jsApiRequest, payGatewayConfig);
        log.info("小程序支付[JSApi]统一下单接口入参:{}", jsApiRequest);
        //调用统一下单接口
        WxPayForJSApiResponse response = wxPayService.wxPayForJSApi(jsApiRequest);
        if("SUCCESS".equals(response.getReturn_code()) && "SUCCESS".equals(response.getResult_code())){
            return getSignResultCommon(jsApiRequest.getAppid(), payGatewayConfig.getApiKey(), response.getPrepay_id());
        } else {
            log.error("小程序支付[JSApi]统一下单接口调用失败,入参:{},返回结果为:{}", jsApiRequest, response);
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
    }

    /**
     * 囤货微信浏览器内,小程序内签名参数公共方法
     * @param jsApiRequest
     * @param payGatewayConfig
     */
    private void pileWxSignCommon(@RequestBody WxPayForJSApiRequest jsApiRequest, PayGatewayConfig payGatewayConfig) {
        jsApiRequest.setMch_id(payGatewayConfig.getAccount());
        jsApiRequest.setNonce_str(WXPayUtil.generateNonceStr());
        jsApiRequest.setNotify_url(getPileOrderNotifyUrl(payGatewayConfig));
        try {
            Map<String, String> mwebMap = WXPayUtil.objectToMap(jsApiRequest);
            mwebMap.remove("storeId");
            mwebMap.remove("payOrderNo");
            mwebMap.put("out_trade_no",jsApiRequest.getPayOrderNo());
            //获取签名
            String sign = WXPayUtil.generateSignature(mwebMap, payGatewayConfig.getApiKey());
            jsApiRequest.setSign(sign);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
    }

    /**
     * 微信浏览器内,小程序内签名参数公共方法
     * @param jsApiRequest
     * @param payGatewayConfig
     */
    private void wxSignCommon(@RequestBody WxPayForJSApiRequest jsApiRequest, PayGatewayConfig payGatewayConfig) {
        jsApiRequest.setMch_id(payGatewayConfig.getAccount());
        jsApiRequest.setNonce_str(WXPayUtil.generateNonceStr());
        jsApiRequest.setNotify_url(getNotifyUrl(payGatewayConfig));
        try {
            Map<String, String> mwebMap = WXPayUtil.objectToMap(jsApiRequest);
            mwebMap.remove("storeId");
            mwebMap.remove("payOrderNo");
            mwebMap.put("out_trade_no",jsApiRequest.getPayOrderNo());
            //获取签名
            String sign = WXPayUtil.generateSignature(mwebMap, payGatewayConfig.getApiKey());
            jsApiRequest.setSign(sign);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
    }


    /**
     * 微信浏览器内,小程序内签名参数公共方法(充值)
     * @param jsApiRequest
     * @param payGatewayConfig
     */
    private void wxTakeGoodSignCommon(@RequestBody WxPayForJSApiRequest jsApiRequest, PayGatewayConfig payGatewayConfig) {
        jsApiRequest.setMch_id(payGatewayConfig.getAccount());
        jsApiRequest.setNonce_str(WXPayUtil.generateNonceStr());
        jsApiRequest.setNotify_url(getNotifyUrl(payGatewayConfig));
        try {
            Map<String, String> mwebMap = WXPayUtil.objectToMap(jsApiRequest);
            mwebMap.remove("storeId");
            //获取签名
            String sign = WXPayUtil.generateSignature(mwebMap, payGatewayConfig.getApiKey());
            jsApiRequest.setSign(sign);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
    }

    /**
     * 微信浏览器内,小程序内签名参数公共方法(充值)
     * @param jsApiRequest
     * @param payGatewayConfig
     */
    private void wxRechargeSignCommon(@RequestBody WxPayForJSApiRequest jsApiRequest, PayGatewayConfig payGatewayConfig) {
        jsApiRequest.setMch_id(payGatewayConfig.getAccount());
        jsApiRequest.setNonce_str(WXPayUtil.generateNonceStr());
        jsApiRequest.setNotify_url(getRechargeNotifyUrl(payGatewayConfig));
        try {
            Map<String, String> mwebMap = WXPayUtil.objectToMap(jsApiRequest);
            mwebMap.remove("storeId");
            //获取签名
            String sign = WXPayUtil.generateSignature(mwebMap, payGatewayConfig.getApiKey());
            jsApiRequest.setSign(sign);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
    }

    /**
     * 微信浏览器内,小程序内支付,获取返回对象公共方法
     * @param appId
     * @param apiKey
     * @param prepayId
     * @return
     */
    private BaseResponse<Map<String, String>> getSignResultCommon(String appId, String apiKey, String prepayId) {
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("appId", appId);
        resultMap.put("timeStamp", String.valueOf(WXPayUtil.getCurrentTimestamp()));
        resultMap.put("nonceStr", WXPayUtil.generateNonceStr());
        resultMap.put("package", "prepay_id=" + prepayId);
        resultMap.put("signType", "MD5");
        try {
            resultMap.put("paySign", WXPayUtil.generateSignature(resultMap, apiKey));
        } catch (Exception e) {
            e.printStackTrace();
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
        return BaseResponse.success(resultMap);
    }

    /**
     * 统一下单--微信app支付
     * @param appRequest
     * @return
     */
    @Override
    public BaseResponse<Map<String, String>> wxPayForApp(@RequestBody WxPayForAppRequest appRequest) {
        PayGatewayConfig payGatewayConfig = payDataService.queryConfigByNameAndStoreId(PayGatewayEnum.WECHAT,appRequest.getStoreId());
        appRequest.setAppid(payGatewayConfig.getOpenPlatformAppId());
        appRequest.setMch_id(payGatewayConfig.getOpenPlatformAccount());
        appRequest.setNonce_str(WXPayUtil.generateNonceStr());
        appRequest.setNotify_url(getNotifyUrl(payGatewayConfig));
        try {
            Map<String,String> appMap = WXPayUtil.objectToMap(appRequest);
            appMap.remove("storeId");
            //获取签名
            String sign = WXPayUtil.generateSignature(appMap,payGatewayConfig.getOpenPlatformApiKey());
            appRequest.setSign(sign);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
        log.info("微信支付[App]统一下单接口入参:{}", appRequest);
        //调用统一下单接口
        WxPayForAppResponse response = wxPayService.wxPayForApp(appRequest);
        log.info("调用微信接口返回对象：" + response.toString());
        if("SUCCESS".equals(response.getReturn_code()) && "SUCCESS".equals(response.getResult_code())){
            Map<String,String> resultMap = new HashMap<>();
            resultMap.put("appid", payGatewayConfig.getOpenPlatformAppId());
            resultMap.put("partnerid",payGatewayConfig.getOpenPlatformAccount());
            resultMap.put("prepayid",response.getPrepay_id());
            resultMap.put("package", "Sign=WXPay");
            resultMap.put("noncestr", appRequest.getNonce_str());
            resultMap.put("timestamp", String.valueOf(WXPayUtil.getCurrentTimestamp()));
            try {
                resultMap.put("sign", WXPayUtil.generateSignature(resultMap,payGatewayConfig.getOpenPlatformApiKey()));
            } catch (Exception e) {
                e.printStackTrace();
                throw new SbcRuntimeException(CommonErrorCode.FAILED);
            }
            return BaseResponse.success(resultMap);
        } else if(("ORDERPAID").equals(response.getErr_code())) {
            //订单已支付
            throw new SbcRuntimeException("K-100210");
        } else {
            log.error("微信支付[app]统一下单接口调用失败,入参:{},返回结果为:{}", appRequest, response);
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
    }

    @Override
    public BaseResponse<Map<String, String>> wxPayPileOrderForApp(WxPayForAppRequest appRequest) {
        PayGatewayConfig payGatewayConfig = payDataService.queryConfigByNameAndStoreId(PayGatewayEnum.WECHAT,appRequest.getStoreId());
        appRequest.setAppid(payGatewayConfig.getOpenPlatformAppId());
        appRequest.setMch_id(payGatewayConfig.getOpenPlatformAccount());
        appRequest.setNonce_str(WXPayUtil.generateNonceStr());
        appRequest.setNotify_url(getPileOrderNotifyUrl(payGatewayConfig));
        try {
            Map<String,String> appMap = WXPayUtil.objectToMap(appRequest);
            appMap.remove("storeId");
            //获取签名
            String sign = WXPayUtil.generateSignature(appMap,payGatewayConfig.getOpenPlatformApiKey());
            appRequest.setSign(sign);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
        log.info("微信支付[App]统一下单接口入参:{}", appRequest);
        //调用统一下单接口
        WxPayForAppResponse response = wxPayService.wxPayForApp(appRequest);
        log.info("调用微信接口返回对象：" + response.toString());
        if("SUCCESS".equals(response.getReturn_code()) && "SUCCESS".equals(response.getResult_code())){
            Map<String,String> resultMap = new HashMap<>();
            resultMap.put("appid", payGatewayConfig.getOpenPlatformAppId());
            resultMap.put("partnerid",payGatewayConfig.getOpenPlatformAccount());
            resultMap.put("prepayid",response.getPrepay_id());
            resultMap.put("package", "Sign=WXPay");
            resultMap.put("noncestr", appRequest.getNonce_str());
            resultMap.put("timestamp", String.valueOf(WXPayUtil.getCurrentTimestamp()));
            try {
                resultMap.put("sign", WXPayUtil.generateSignature(resultMap,payGatewayConfig.getOpenPlatformApiKey()));
            } catch (Exception e) {
                e.printStackTrace();
                throw new SbcRuntimeException(CommonErrorCode.FAILED);
            }
            return BaseResponse.success(resultMap);
        } else if(("ORDERPAID").equals(response.getErr_code())) {
            //订单已支付
            throw new SbcRuntimeException("K-100210");
        } else {
            log.error("微信支付[app]统一下单接口调用失败,入参:{},返回结果为:{}", appRequest, response);
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
    }

    /**
     * 提货
     * @param appRequest
     * @return
     */
    @Override
    public BaseResponse<Map<String, String>> wxPayTakeGoodForApp(WxPayForAppRequest appRequest) {
        PayGatewayConfig payGatewayConfig = payDataService.queryConfigByNameAndStoreId(PayGatewayEnum.WECHAT,appRequest.getStoreId());
        appRequest.setAppid(payGatewayConfig.getOpenPlatformAppId());
        appRequest.setMch_id(payGatewayConfig.getOpenPlatformAccount());
        appRequest.setNonce_str(WXPayUtil.generateNonceStr());
        appRequest.setNotify_url(getNotifyUrl(payGatewayConfig));
        try {
            Map<String,String> appMap = WXPayUtil.objectToMap(appRequest);
            appMap.remove("storeId");
            //获取签名
            String sign = WXPayUtil.generateSignature(appMap,payGatewayConfig.getOpenPlatformApiKey());
            appRequest.setSign(sign);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
        log.info("微信支付[App]统一下单接口入参:{}", appRequest);
        //调用统一下单接口
        WxPayForAppResponse response = wxPayService.wxPayForApp(appRequest);
        log.info("调用微信接口返回对象：" + response.toString());
        if("SUCCESS".equals(response.getReturn_code()) && "SUCCESS".equals(response.getResult_code())){
            Map<String,String> resultMap = new HashMap<>();
            resultMap.put("appid", payGatewayConfig.getOpenPlatformAppId());
            resultMap.put("partnerid",payGatewayConfig.getOpenPlatformAccount());
            resultMap.put("prepayid",response.getPrepay_id());
            resultMap.put("package", "Sign=WXPay");
            resultMap.put("noncestr", appRequest.getNonce_str());
            resultMap.put("timestamp", String.valueOf(WXPayUtil.getCurrentTimestamp()));
            try {
                resultMap.put("sign", WXPayUtil.generateSignature(resultMap,payGatewayConfig.getOpenPlatformApiKey()));
            } catch (Exception e) {
                e.printStackTrace();
                throw new SbcRuntimeException(CommonErrorCode.FAILED);
            }
            return BaseResponse.success(resultMap);
        } else if(("ORDERPAID").equals(response.getErr_code())) {
            //订单已支付
            throw new SbcRuntimeException("K-100210");
        } else {
            log.error("微信支付[app]统一下单接口调用失败,入参:{},返回结果为:{}", appRequest, response);
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
    }

    /**
     * app充值钱包统一下单接口
     * @param appRequest
     * @return
     */
    @Override
    public BaseResponse<Map<String, String>> wxPayRechargeForApp(WxPayForAppRequest appRequest) {
        //获取微信支付网关基本配置信息
        PayGatewayConfig payGatewayConfig = payDataService.queryConfigByNameAndStoreId(PayGatewayEnum.WECHAT,appRequest.getStoreId());
        appRequest.setAppid(payGatewayConfig.getOpenPlatformAppId());
        appRequest.setMch_id(payGatewayConfig.getOpenPlatformAccount());
        appRequest.setNonce_str(WXPayUtil.generateNonceStr());
        //获取回调地址
        appRequest.setNotify_url(getRechargeNotifyUrl(payGatewayConfig));
        try {
            Map<String,String> appMap = WXPayUtil.objectToMap(appRequest);
            appMap.remove("storeId");
            //获取签名
            String sign = WXPayUtil.generateSignature(appMap,payGatewayConfig.getOpenPlatformApiKey());
            appRequest.setSign(sign);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
        log.info("微信支付[App]统一下单接口入参:{}", appRequest);
        //调用统一下单接口
        WxPayForAppResponse response = wxPayService.wxPayForApp(appRequest);
        log.info("调用微信接口返回对象：" + response.toString());
        if("SUCCESS".equals(response.getReturn_code()) && "SUCCESS".equals(response.getResult_code())){
            Map<String,String> resultMap = new HashMap<>();
            resultMap.put("appid", payGatewayConfig.getOpenPlatformAppId());
            resultMap.put("partnerid",payGatewayConfig.getOpenPlatformAccount());
            resultMap.put("prepayid",response.getPrepay_id());
            resultMap.put("package", "Sign=WXPay");
            resultMap.put("noncestr", appRequest.getNonce_str());
            resultMap.put("timestamp", String.valueOf(WXPayUtil.getCurrentTimestamp()));
            try {
                resultMap.put("sign", WXPayUtil.generateSignature(resultMap,payGatewayConfig.getOpenPlatformApiKey()));
            } catch (Exception e) {
                e.printStackTrace();
                throw new SbcRuntimeException(CommonErrorCode.FAILED);
            }
            return BaseResponse.success(resultMap);
        } else if(("ORDERPAID").equals(response.getErr_code())) {
            //订单已支付
            throw new SbcRuntimeException("K-100210");
        } else {
            log.error("微信支付[app]统一下单接口调用失败,入参:{},返回结果为:{}", appRequest, response);
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
    }

    /**
     * 囤货微信退款
     * @param refundInfoRequest
     * @return
     */
    @Override
    public BaseResponse<WxPayRefundResponse> wxPilePayRefund(@RequestBody WxPayRefundInfoRequest refundInfoRequest){
        PayGatewayConfig payGatewayConfig = payDataService.queryConfigByNameAndStoreId(PayGatewayEnum.WECHAT,refundInfoRequest.getStoreId());
        String appId = payGatewayConfig.getAppId();
        String account = payGatewayConfig.getAccount();
        String apiKey = payGatewayConfig.getApiKey();
        if(refundInfoRequest.getPay_type().equals("APP")){
            appId = payGatewayConfig.getOpenPlatformAppId();
            account = payGatewayConfig.getOpenPlatformAccount();
            apiKey = payGatewayConfig.getOpenPlatformApiKey();
        }
        WxPayRefundRequest refundRequest = new WxPayRefundRequest();
        refundRequest.setAppid(appId);
        refundRequest.setMch_id(account);
        refundRequest.setNonce_str(WXPayUtil.generateNonceStr());
        refundRequest.setOut_refund_no(refundInfoRequest.getOut_refund_no());
        refundRequest.setOut_trade_no(refundInfoRequest.getOut_trade_no());
        refundRequest.setTotal_fee(refundInfoRequest.getTotal_fee());
        refundRequest.setRefund_fee(refundInfoRequest.getRefund_fee());
        //重复支付退款不需要异步回调地址
        if(StringUtils.isNotBlank(refundInfoRequest.getRefund_type()) && !refundInfoRequest.getRefund_type().equals("REPEATPAY")){
            refundRequest.setNotify_url(payGatewayConfig.getBossBackUrl()+WXPILEREFUNDSUCCCALLBACK+refundInfoRequest.getStoreId());
        }
        try {
            Map<String,String> refundMap = WXPayUtil.objectToMap(refundRequest);
            //获取签名
            String sign = WXPayUtil.generateSignature(refundMap,apiKey);
            refundRequest.setSign(sign);
        } catch (Exception e) {
            e.printStackTrace();
        }
        WxPayRefundResponse wxPayRefundResponse = wxPayService.wxPayRefund(refundRequest,
                refundInfoRequest.getPay_type(), refundInfoRequest.getStoreId());
        return BaseResponse.success(wxPayRefundResponse);
    }

    /**
     * 微信退款
     * @param refundInfoRequest
     * @return
     */
    @Override
    public BaseResponse<WxPayRefundResponse> wxPayRefund(@RequestBody WxPayRefundInfoRequest refundInfoRequest){
        PayGatewayConfig payGatewayConfig = payDataService.queryConfigByNameAndStoreId(PayGatewayEnum.WECHAT,refundInfoRequest.getStoreId());
        String appId = payGatewayConfig.getAppId();
        String account = payGatewayConfig.getAccount();
        String apiKey = payGatewayConfig.getApiKey();
        if(refundInfoRequest.getPay_type().equals("APP")){
            appId = payGatewayConfig.getOpenPlatformAppId();
            account = payGatewayConfig.getOpenPlatformAccount();
            apiKey = payGatewayConfig.getOpenPlatformApiKey();
        }
        WxPayRefundRequest refundRequest = new WxPayRefundRequest();
        refundRequest.setAppid(appId);
        refundRequest.setMch_id(account);
        refundRequest.setNonce_str(WXPayUtil.generateNonceStr());
        refundRequest.setOut_refund_no(refundInfoRequest.getOut_refund_no());
        refundRequest.setOut_trade_no(refundInfoRequest.getOut_trade_no());
        refundRequest.setTotal_fee(refundInfoRequest.getTotal_fee());
        refundRequest.setRefund_fee(refundInfoRequest.getRefund_fee());
        //重复支付退款不需要异步回调地址
        if(StringUtils.isNotBlank(refundInfoRequest.getRefund_type()) && !refundInfoRequest.getRefund_type().equals("REPEATPAY")){
            refundRequest.setNotify_url(payGatewayConfig.getBossBackUrl()+WXREFUNDSUCCCALLBACK+refundInfoRequest.getStoreId());
        }
        try {
            Map<String,String> refundMap = WXPayUtil.objectToMap(refundRequest);
            //获取签名
            String sign = WXPayUtil.generateSignature(refundMap,apiKey);
            refundRequest.setSign(sign);
        } catch (Exception e) {
            e.printStackTrace();
        }
        WxPayRefundResponse wxPayRefundResponse = wxPayService.wxPayRefund(refundRequest,
                refundInfoRequest.getPay_type(), refundInfoRequest.getStoreId());
        return BaseResponse.success(wxPayRefundResponse);
    }

    /**
     * 微信支付--微信企业付款到零钱
     * @param request
     * @return
     */
    @Override
    public BaseResponse<WxPayCompanyPaymentRsponse> wxPayCompanyPayment(@RequestBody WxPayCompanyPaymentInfoRequest request){
        WxPayCompanyPaymentRsponse wxPayCompanyPaymentRsponse = wxPayService.wxPayCompanyPayment(request);
        return BaseResponse.success(wxPayCompanyPaymentRsponse);
    }

    /**
     * @Author lvzhenwei
     * @Description 查询微信支付单详情
     * @Date 14:30 2020/9/17
     * @Param [request]
     * @return com.wanmi.sbc.common.base.BaseResponse<com.wanmi.sbc.pay.api.response.WxPayOrderDetailReponse>
     **/
    @Override
    public BaseResponse<WxPayOrderDetailReponse> getWxPayOrderDetail(@RequestBody WxPayOrderDetailRequest request){
        WxPayOrderDetailReponse wxPayOrderDetailReponse = wxPayService.getWxPayOrderDetail(request);
        return BaseResponse.success(wxPayOrderDetailReponse);

    }

    @Override
    public BaseResponse<WxPayOrderCloseForJSApiResponse> closeWxPayOrderForJSApi(WxPayOrderCloseForJSApiRequest request) {
        WxPayOrderCloseForJSApiResponse wxPayOrderCloseForJSApiResponse = wxPayService.closeWxPayOrderForJSApi(request);
        return BaseResponse.success(wxPayOrderCloseForJSApiResponse);
    }

    @Override
    public BaseResponse<Boolean> isPayCompleted(CmbPayOrderRequest cmbPayOrderRequest) {
        return BaseResponse.success(wxPayService.isPayCompleted(cmbPayOrderRequest));
    }
}
