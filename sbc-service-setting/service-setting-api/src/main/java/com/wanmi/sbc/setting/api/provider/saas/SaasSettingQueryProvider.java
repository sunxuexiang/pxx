package com.wanmi.sbc.setting.api.provider.saas;

import com.wanmi.sbc.common.base.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @Author: songhanlin
 * @Date: Created In 19:29 2020/1/15
 * @Description: Saas配置Provider
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "SaasSettingQueryProvider")
public interface SaasSettingQueryProvider {

    /**
     * 查询Saas开启状态
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/saas/setting/status")
    BaseResponse<Boolean> queryStatus();

}
