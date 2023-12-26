package com.wanmi.sbc.returnorder.api.provider.returnorder;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.returnorder.api.request.returnorder.ReturnLogisticsRequest;
import com.wanmi.sbc.returnorder.api.response.returnorder.ReturnLogisticsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/12/7 11:35
 */
@FeignClient(value = "${application.returnOrder.name}", url="${feign.url.returnOrder:#{null}}", contextId = "ReturnOrderLogisticsProvider")
public interface ReturnOrderLogisticsProvider {
    @PostMapping("/returnOrder/${application.order.version}/return/logistics/findReturnLogisticsByHistory")
    BaseResponse<ReturnLogisticsResponse> findReturnLogisticsByHistory(@RequestBody @Valid ReturnLogisticsRequest request);
}
