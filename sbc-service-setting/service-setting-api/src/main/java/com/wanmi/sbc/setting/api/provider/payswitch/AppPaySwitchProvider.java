package com.wanmi.sbc.setting.api.provider.payswitch;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.payswitch.AppPaySwitchParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "AppPaySwitchProvider")
public interface AppPaySwitchProvider {

    /**
     * 修改开关状态
     */
    @PostMapping("/setting/${application.setting.version}/updateAppPaySwitch")
    BaseResponse updateAppPaySwitch(@RequestBody List<AppPaySwitchParam> requestList);

    @GetMapping("/setting/${application.setting.version}/getAppPaySwitch")
    BaseResponse<List<AppPaySwitchParam>> getAppPaySwitch();
}
