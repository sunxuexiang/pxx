package com.wanmi.sbc.returnorder.api.provider.refundfreight;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.returnorder.api.request.refundfreight.RefundFreightCallbackRequest;
import com.wanmi.sbc.returnorder.api.request.refundfreight.RefundFreightRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/11/30 14:27
 */
@FeignClient(value = "${application.returnOrder.name}", url="${feign.url.returnOrder:#{null}}", contextId = "RefundFreightProvider")
public interface RefundFreightProvider {


    @PostMapping("/returnOrder/${application.returnOrder.version}/refund/freight/add")
    BaseResponse add(@RequestBody @Valid RefundFreightRequest request);

    @PostMapping("/returnOrder/${application.returnOrder.version}/refund/freight/callback")
    BaseResponse callback(@RequestBody @Valid RefundFreightCallbackRequest request);

    @PostMapping("/returnOrder/${application.returnOrder.version}/refund/extra/callback")
    BaseResponse extraCallback(@RequestBody @Valid RefundFreightCallbackRequest build);
}
