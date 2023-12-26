package com.wanmi.sbc.setting.api.provider.systemstoreconfig;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.systemstoreconfig.SystemStoreConfigModifyRequest;
import com.wanmi.sbc.setting.api.request.systemstoreconfig.SystemStoreConfigQueryRequest;
import com.wanmi.sbc.setting.api.response.systemstoreconfig.SystemStoreConfigListResponse;
import com.wanmi.sbc.setting.api.response.systemstoreconfig.SystemStoreConfigResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @desc  
 * @author shiy  2023/7/4 8:23
*/
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "SystemStoreConfigProvider")
public interface SystemStoreConfigProvider {
    @PostMapping("/setting/${application.setting.version}/sysStoreConfig/list")
    BaseResponse<SystemStoreConfigListResponse> findByStoreIdAndConfigKey(@RequestBody @Valid SystemStoreConfigQueryRequest request);

    @PostMapping("/setting/${application.setting.version}/sysStoreConfig/find-by-type")
    BaseResponse<SystemStoreConfigResponse> findByStoreIdAndConfigKeyAndConfigTypeAndDelFlag(@RequestBody @Valid SystemStoreConfigQueryRequest request);

    @PostMapping("/setting/${application.setting.version}/sysStoreConfig/find-list-by-storeId")
    BaseResponse<SystemStoreConfigListResponse> listByStoreId(@RequestBody @Valid SystemStoreConfigQueryRequest request);

    @PostMapping("/setting/${application.setting.version}/sysStoreConfig/add")
    BaseResponse add(@RequestBody @Valid SystemStoreConfigModifyRequest request);

    @PostMapping("/setting/${application.setting.version}/sysStoreConfig/update")
    BaseResponse update(@RequestBody @Valid SystemStoreConfigModifyRequest request);
}
