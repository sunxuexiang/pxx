package com.wanmi.sbc.pay.api.provider;

import com.alipay.api.response.AlipayTradeQueryResponse;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.pay.api.request.AliPayPaymentSlipRequest;
import com.wanmi.sbc.pay.api.request.AliPayRefundRequest;
import com.wanmi.sbc.pay.api.request.PayExtraRequest;
import com.wanmi.sbc.pay.api.request.PayRequest;
import com.wanmi.sbc.pay.api.response.AliPayFormResponse;
import com.wanmi.sbc.pay.api.response.AliPayRefundResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @program: service-pay
 * @description: 支付宝相关接口
 * @create: 2019-01-28 16:15
 **/
@FeignClient(value = "${application.pay.name}", url="${feign.url.pay:#{null}}", contextId = "AliPayProvider")
public interface AliPayProvider {

    /*
     * @Description: 支付接口，返回支付宝支付表单前端自动提交重定向到支付宝收银台
     * @Param:  request 支付请求对象
     * @Author: Bob
     * @Date: 2019-01-28 16:25
    */
    @PostMapping("/pay/${application.pay.version}/get-payForm")
    BaseResponse<AliPayFormResponse> getPayForm(@RequestBody @Valid PayExtraRequest request);

    /**
     * 囤货支付接口
     * @param request
     * @return
     */
    @PostMapping("/pay/${application.pay.version}/get-pile-payForm")
    BaseResponse<AliPayFormResponse> getPilePayForm(@RequestBody @Valid PayExtraRequest request);

    @PostMapping("/pay/${application.pay.version}/getPayRechargeForm")
    BaseResponse<AliPayFormResponse> getPayRechargeForm(@RequestBody @Valid PayExtraRequest request);

    @PostMapping("/pay/${application.pay.version}/getPayTakeGoodForm")
    BaseResponse<AliPayFormResponse> getPayTakeGoodForm(@RequestBody @Valid PayExtraRequest request);

    /*
     * @Description: 退款接口，直接退款，不涉及业务
     * @Param:  request 支付请求对象
     * @Author: Bob
     * @Date: 2019-01-28 16:25
     */
    @PostMapping("/pay/${application.pay.version}/aliPayRefund")
    BaseResponse<AliPayRefundResponse> aliPayRefund(@RequestBody @Valid AliPayRefundRequest refundRequest);

    /**
     * alipay.trade.query(统一收单线下交易查询)
     * @param request
     * @return
     */
    @PostMapping("/pay/${application.pay.version}/queryAlipayPaymentSlip")
    BaseResponse<AlipayTradeQueryResponse> queryAlipayPaymentSlip(@RequestBody @Valid AliPayPaymentSlipRequest request);

}
