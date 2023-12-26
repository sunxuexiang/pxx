package com.wanmi.sbc.setting.api.provider.onlineservice;


import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.imonlineservice.TencentImLogRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "TencentImLogProvider")
public interface TencentImLogProvider {

    @PostMapping("/setting/${application.setting.version}/tencentImLog/add")
    BaseResponse add(@RequestBody TencentImLogRequest request);
}
