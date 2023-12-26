package com.wanmi.sbc.pay.api.provider;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.pay.api.request.CmbPayOrderRequest;
import com.wanmi.sbc.pay.api.request.CmbPayRefundDataRequest;
import com.wanmi.sbc.pay.api.request.CupsPayRefundDataRequest;
import com.wanmi.sbc.pay.api.request.CupsPaySignRequest;
import com.wanmi.sbc.pay.api.response.CmbPayRefundResponse;
import com.wanmi.sbc.pay.api.response.CupsPayRefundResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(value = "${application.pay.name}", url="${feign.url.pay:#{null}}", contextId = "CupsPayProvider")
public interface CupsPayProvider {



      /*
     * @Description: 创建支付订单
     * @Param:  request 支付请求对象
     * @Author: Bob
     * @Date: 2019-01-28 16:25
     */
    @PostMapping("/pay/${application.pay.version}/cups-app-pay-order")
    BaseResponse cupsPayOrder(@RequestBody CmbPayOrderRequest cmbPayOrderRequest);

    @PostMapping("/pay/${application.pay.version}/cups-app-pay-sign")
    BaseResponse<Boolean> cupsPaySign(@RequestBody CupsPaySignRequest cmbPaySignRequest);

    @PostMapping("/pay/${application.pay.version}/cups-app-pay-refund")
    BaseResponse<CupsPayRefundResponse> cupsPayRefund(@RequestBody CupsPayRefundDataRequest cupsPayRefundDataRequest);

    @PostMapping("/pay/${application.pay.version}/cups-app-pay-completed")
    BaseResponse<Boolean> isPayCompleted(@RequestBody CmbPayOrderRequest cmbPayOrderRequest);

    @PostMapping("/pay/${application.pay.version}/closePayOrder")
    BaseResponse<String> closePayOrder(@RequestBody Map<String,String> params);
}
