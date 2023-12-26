package com.wanmi.sbc.returnorder.api.provider.receivables;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.returnorder.api.request.receivables.ReceivablesDeleteRequests;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p> 收款单服务</p>
 * author: sunpeng
 * Date: 2018-12-5
 */
@FeignClient(value = "${application.returnOrder.name}", url="${feign.url.returnOrder:#{null}}", contextId = "ReturnReceivableProvider")
public interface ReceivableProvider {

    /**
     * 删除收款单
     * @param request  {@link ReceivablesDeleteRequests}
     * @return   {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/receivables/delete-receivables")
    BaseResponse deleteReceivables(@RequestBody @Valid ReceivablesDeleteRequests request);
}
