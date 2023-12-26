package com.wanmi.sbc.setting.api.provider.onlineservice;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.imonlineservice.CustomerServiceLimitWordRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "CustomerServiceLimitWordProvider")
public interface CustomerServiceLimitWordProvider {

    @PostMapping("/setting/${application.setting.version}/customerWord/add")
    BaseResponse add(@RequestBody CustomerServiceLimitWordRequest request);

    @PostMapping("/setting/${application.setting.version}/customerWord/update")
    BaseResponse update(@RequestBody CustomerServiceLimitWordRequest request);

    @PostMapping("/setting/${application.setting.version}/customerWord/delete")
    BaseResponse delete(@RequestBody CustomerServiceLimitWordRequest request);

    @PostMapping("/setting/${application.setting.version}/customerWord/getAll")
    BaseResponse getAll();

    @PostMapping("/setting/${application.setting.version}/customerWord/getByType")
    BaseResponse getByType(@RequestBody CustomerServiceLimitWordRequest request);

    @PostMapping("/setting/${application.setting.version}/customerWord/getRegex")
    BaseResponse getRegex();
}
