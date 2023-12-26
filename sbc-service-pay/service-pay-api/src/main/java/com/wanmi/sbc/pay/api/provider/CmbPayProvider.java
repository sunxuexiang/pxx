package com.wanmi.sbc.pay.api.provider;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.pay.api.request.CmbPayOrderRequest;
import com.wanmi.sbc.pay.api.request.CmbPayRefundDataRequest;
import com.wanmi.sbc.pay.api.response.CmbPayRefundResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "${application.pay.name}", url="${feign.url.pay:#{null}}", contextId = "CmbPayProvider")
public interface CmbPayProvider {


//    /*
//     * @Description: 退款接口，直接退款，不涉及业务(app)
//     * @Param:  request 支付请求对象
//     * @Author: Bob
//     * @Date: 2019-01-28 16:25
//     */
//    @PostMapping("/pay/${application.pay.version}/cmb-app-pay-refund")
//    BaseResponse cmbPayRefund();
//
//
//    /*
//     * @Description: cmb异步回调订单是否交易成功(app成功支付结果通知)
//     * @Param:  request cmb异步回调请求对象
//     * @Author: Bob
//     * @Date: 2019-01-28 16:25
//     */
//    @PostMapping("/pay/${application.pay.version}/cmb-app-call-back")
//    BaseResponse cmbCallback(CmbCallBackRequest appRequest);
//
//    /*
//     * @Description: 创建支付订单
//     * @Param:  request 支付请求对象
//     * @Author: Bob
//     * @Date: 2019-01-28 16:25
//     */
//    @PostMapping("/pay/${application.pay.version}/cmb-app-pay-order")
//    BaseResponse cmbPayOrder(CmbPayOrderRequest cmbPayOrderRequest);
//
//
//    /*
//     * @Description: cmb异步回调签约是否成功
//     * @Param:  request cmb异步回调请求对象
//     * @Author: Bob
//     * @Date: 2019-01-28 16:25
//     */
//    @PostMapping("/pay/${application.pay.version}/cmb-call-back-sign")
//    BaseResponse cmbCallbackSign(CmbCallbackSignRequest appRequest);

      /*
     * @Description: 创建支付订单
     * @Param:  request 支付请求对象
     * @Author: Bob
     * @Date: 2019-01-28 16:25
     */
    @PostMapping("/pay/${application.pay.version}/cmb-app-pay-order")
    BaseResponse cmbPayOrder(@RequestBody CmbPayOrderRequest cmbPayOrderRequest);

    @PostMapping("/pay/${application.pay.version}/cmb-app-pay-refund")
    BaseResponse<CmbPayRefundResponse> cmbPayRefund(@RequestBody CmbPayRefundDataRequest cmbPayRefundDataRequest);

    @PostMapping("/pay/${application.pay.version}/cmb-app-pay-cmbDoBusiness")
    BaseResponse<CmbPayRefundResponse> cmbDoBusiness();

}
