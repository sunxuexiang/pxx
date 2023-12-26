package com.wanmi.sbc.setting.api.provider.viewversionconfig;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.bean.vo.ViewVersionConfigVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <p>IOS版本基本配置服务Provider</p>
 *
 * @author zhou.jiang
 * @date 2021-09-15
 */
@FeignClient(value = "${application.setting.name}", contextId = "ViewVersionConfigProvider")
public interface ViewVersionConfigProvider {

    /**
     * 获取数据看板最新版本
     * @param systemType
     * @param currentVersion
     * @return
     */
    @GetMapping("/setting/${application.setting.version}/view-version-config/get-view-version")
    BaseResponse<ViewVersionConfigVO> getViewVersion(@RequestParam("systemType") String systemType , @RequestParam("currentVersion") String currentVersion);
}
