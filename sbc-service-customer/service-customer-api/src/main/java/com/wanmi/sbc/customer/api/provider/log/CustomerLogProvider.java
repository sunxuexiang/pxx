package com.wanmi.sbc.customer.api.provider.log;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.customer.api.request.log.CustomerLogAddRequest;
import com.wanmi.sbc.customer.api.request.log.CustomerLogPageRequest;
import com.wanmi.sbc.customer.api.response.log.CustomerLogPageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(value = "${application.customer.name}", url="${feign.url.customer:#{null}}", contextId = "CustomerLogProvider")
public interface CustomerLogProvider {

    @PostMapping("/customer/${application.customer.version}/log/page")
    BaseResponse<CustomerLogPageResponse> page(@RequestBody @Valid CustomerLogPageRequest customerLogPageRequest);

    @PostMapping("/customer/${application.customer.version}/log/find-update-record-by-userno")
    BaseResponse<CustomerLogPageResponse> findUpdateRecordByUserNo(@RequestBody @Valid CustomerLogPageRequest customerLogPageRequest);

    @PostMapping("/customer/${application.customer.version}/log/add-customer-log")
    BaseResponse add(@RequestBody @Valid CustomerLogAddRequest request);
}
