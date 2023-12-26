package com.wanmi.sbc.setting.api.provider.iosappversionconfig;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.iosappversionconfig.IosAppVersionConfigAddRequest;
import com.wanmi.sbc.setting.api.request.iosappversionconfig.IosAppVersionConfigPageRequest;
import com.wanmi.sbc.setting.api.response.iosappversionconfig.IosAppVersionConfigPageResponse;
import com.wanmi.sbc.setting.api.response.iosappversionconfig.IosAppVersionConfigResponse;
import com.wanmi.sbc.setting.bean.vo.IosAppVersionConfigVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

/**
 * <p>IOS版本基本配置服务Provider</p>
 * @author zhou.jiang
 * @date 2021-09-15
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "IosAppVersionConfigProvider")
public interface IosAppVersionConfigProvider {
    /**
     * 新增ios版本配置管理信息
     * @param addRequest
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/ios-app-version-config/add")
    BaseResponse add(@RequestBody @Valid IosAppVersionConfigAddRequest addRequest);

    /**
     * 获取最新的版本配置信息
     * @param versionNo
     * @param buildNo
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/ios-app-version-config/get-version-info")
    BaseResponse<IosAppVersionConfigResponse> getVersionInfo(@RequestParam("versionNo") String versionNo, @RequestParam("buildNo") Long buildNo);

    /**
     * 根据主键id获取ios版本信息
     * @param id
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/ios-app-version-config/get-version-info-by-id")
    BaseResponse<IosAppVersionConfigVO> getVersionInfoById(@RequestParam("id") Long id);

    /**
     * 分页获取ios版本配置信息
     * @param request
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/ios-app-version-config/get-list-versions")
    BaseResponse<IosAppVersionConfigPageResponse> getListVersions(@RequestBody @Valid IosAppVersionConfigPageRequest request);
}
