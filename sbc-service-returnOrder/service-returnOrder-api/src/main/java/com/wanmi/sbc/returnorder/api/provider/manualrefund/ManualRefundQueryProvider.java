package com.wanmi.sbc.returnorder.api.provider.manualrefund;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.returnorder.api.request.manualrefund.ManualRefundResponseByOrderCodeRequest;
import com.wanmi.sbc.returnorder.api.response.manualrefund.ManualRefundResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

@FeignClient(value = "${application.returnOrder.name}", url="${feign.url.returnOrder:#{null}}", contextId = "ReturnManualRefundQueryProvider")
public interface ManualRefundQueryProvider {
    /**
     * 根据订单号查询退款列表
     * @param manualRefundResponseByOrderCodeRequest {@link ManualRefundResponseByOrderCodeRequest }
     * @return {@link List<ManualRefundResponse> }
     */
    @PostMapping("/returnOrder/${application.order.version}/manualrefund/get-manual-refund-order-resp-by-order-code")
    BaseResponse<List<ManualRefundResponse>> getManualRefundRespByOrderCode(@RequestBody @Valid ManualRefundResponseByOrderCodeRequest manualRefundResponseByOrderCodeRequest);

    /**
     * 根据订单号查询退款列表
     * @param manualRefundResponseByOrderCodeRequest {@link ManualRefundResponseByOrderCodeRequest }
     * @return {@link List<ManualRefundResponse> }
     */
    @PostMapping("/returnOrder/${application.order.version}/manualrefund/manual-refund-by-order-code")
    BaseResponse<List<ManualRefundResponse>> manualRefundByOrderCode(@RequestBody @Valid ManualRefundResponseByOrderCodeRequest manualRefundResponseByOrderCodeRequest);

    /**
     * 根据订单号查询退款列表
     * @param request
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/manualrefund/newPileTradeRefundByOrderCode")
    BaseResponse newPileTradeRefundByOrderCode(@RequestBody @Valid ManualRefundResponseByOrderCodeRequest request);
}
