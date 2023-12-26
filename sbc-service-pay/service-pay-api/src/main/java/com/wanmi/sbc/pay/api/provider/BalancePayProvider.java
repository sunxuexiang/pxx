package com.wanmi.sbc.pay.api.provider;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.pay.api.request.CmbPayOrderRequest;
import com.wanmi.sbc.pay.api.request.CmbPayRefundDataRequest;
import com.wanmi.sbc.pay.api.response.CmbPayRefundResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "${application.pay.name}", url="${feign.url.pay:#{null}}", contextId = "BalancePayProvider")
public interface BalancePayProvider {


      /*
     * @Description: 创建支付订单
     * @Param:  request 支付请求对象
     * @Author: Bob
     * @Date: 2019-01-28 16:25
     */
    @PostMapping("/pay/${application.pay.version}/balance-app-pay-order")
    BaseResponse balancePayOrder(@RequestBody CmbPayOrderRequest cmbPayOrderRequest);

    @PostMapping("/pay/${application.pay.version}/balance-app-pay-refund")
    BaseResponse<CmbPayRefundResponse> balancePayRefund(@RequestBody CmbPayRefundDataRequest cmbPayRefundDataRequest);

}
