package com.wanmi.sbc.pay.api.provider;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.pay.api.request.*;
import com.wanmi.sbc.pay.api.response.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * 微信支付接口
 */
@FeignClient(value = "${application.pay.name}", url="${feign.url.pay:#{null}}", contextId = "WxPayProvider")
public interface WxPayProvider {

    @PostMapping("/pay/${application.pay.version}/wx-pay-for-native")
    BaseResponse<WxPayForNativeResponse> wxPayForNative(@RequestBody WxPayForNativeRequest request);

    @PostMapping("/pay/${application.pay.version}/wx-pay-for-mweb")
    BaseResponse<WxPayForMWebResponse> wxPayForMWeb(@RequestBody WxPayForMWebRequest mWebRequest);

    @PostMapping("/pay/${application.pay.version}/wx-pile-pay-for-mweb")
    BaseResponse<WxPayForMWebResponse> wxPilePayForMWeb(@RequestBody WxPayForMWebRequest mWebRequest);

    @PostMapping("/pay/${application.pay.version}/wx-pay-for-jsapi")
    BaseResponse<Map<String,String>> wxPayForJSApi(@RequestBody WxPayForJSApiRequest jsApiRequest);

    @PostMapping("/pay/${application.pay.version}/pile-wx-pay-for-jsapi")
    BaseResponse<Map<String,String>> pileWxPayForJSApi(@RequestBody WxPayForJSApiRequest jsApiRequest);

    @PostMapping("/pay/${application.pay.version}/wx-pay-for-little-program")
    BaseResponse<Map<String,String>> wxPayForLittleProgram(@RequestBody WxPayForJSApiRequest jsApiRequest);

    @PostMapping("/pay/${application.pay.version}/pile/wx-pay-for-little-program")
    BaseResponse<Map<String,String>> pileWxPayForLittleProgram(@RequestBody WxPayForJSApiRequest jsApiRequest);

    @PostMapping("/pay/${application.pay.version}/wx-pay-take-good-from-little-program")
    BaseResponse<Map<String,String>> wxPayTakeGoodForLittleProgram(@RequestBody WxPayForJSApiRequest jsApiRequest);

    @PostMapping("/pay/${application.pay.version}/wxPayRechargeForLittleProgram")
    BaseResponse<Map<String,String>> wxPayRechargeForLittleProgram(@RequestBody WxPayForJSApiRequest jsApiRequest);

    @PostMapping("/pay/${application.pay.version}/wx-pay-for-app")
    BaseResponse<Map<String,String>> wxPayForApp(@RequestBody WxPayForAppRequest appRequest);

    @PostMapping("/pay/${application.pay.version}/wx-pay-pile-order-for-app")
    BaseResponse<Map<String,String>> wxPayPileOrderForApp(@RequestBody WxPayForAppRequest appRequest);

    @PostMapping("/pay/${application.pay.version}/wx-pay-take-good-for-app")
    BaseResponse<Map<String,String>> wxPayTakeGoodForApp(@RequestBody WxPayForAppRequest appRequest);

    @PostMapping("/pay/${application.pay.version}/wx-pay-recharge-for-app")
    BaseResponse<Map<String,String>> wxPayRechargeForApp(@RequestBody WxPayForAppRequest appRequest);

    @PostMapping("/pay/${application.pay.version}/wx-pile-pay-refund")
    BaseResponse<WxPayRefundResponse> wxPilePayRefund(@RequestBody WxPayRefundInfoRequest refundInfoRequest);

    @PostMapping("/pay/${application.pay.version}/wx-pay-refund")
    BaseResponse<WxPayRefundResponse> wxPayRefund(@RequestBody WxPayRefundInfoRequest refundInfoRequest);

    @PostMapping("/pay/${application.pay.version}/wx-pay-company-payment")
    BaseResponse<WxPayCompanyPaymentRsponse> wxPayCompanyPayment(@RequestBody WxPayCompanyPaymentInfoRequest request);

    @PostMapping("/pay/${application.pay.version}/get-wx-pay-order-detail")
    BaseResponse<WxPayOrderDetailReponse> getWxPayOrderDetail(@RequestBody WxPayOrderDetailRequest request);

    @PostMapping("/pay/${application.pay.version}/closeWxPayOrderForJSApi")
    BaseResponse<WxPayOrderCloseForJSApiResponse> closeWxPayOrderForJSApi(@RequestBody WxPayOrderCloseForJSApiRequest weiXinPayRequest);

    @PostMapping("/pay/${application.pay.version}/wxIsPayCompleted")
    BaseResponse<Boolean> isPayCompleted(@RequestBody CmbPayOrderRequest cmbPayOrderRequest);

    @PostMapping("/pay/${application.pay.version}/wx-pay-for-native-custom-notify")
    BaseResponse<WxPayForNativeResponse> wxPayForNativeCustomNotify(@RequestBody WxPayForNativeRequest request);

    @PostMapping("/pay/${application.pay.version}/wx-pay-for-native-store-notify")
    BaseResponse<WxPayForNativeResponse> wxPayForNativeStoreNotify(@RequestBody WxPayForNativeRequest request);

}
