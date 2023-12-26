package com.wanmi.sbc.setting.api.provider;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.sysswitch.SwitchModifyRequest;
import com.wanmi.sbc.setting.api.request.sysswitch.SwitchRequest;
import com.wanmi.sbc.setting.api.response.SwitchModifyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "SwitchProvider")
public interface SwitchProvider {
    /**
     * 开关开启关闭
     *
     * @return 修改的记录数 {@link SwitchModifyResponse}
     */
    @PostMapping("/setting/${application.setting.version}/sysswitch/modify")
    BaseResponse<SwitchModifyResponse> modify(@RequestBody SwitchModifyRequest request);


    @PostMapping("/setting/${application.setting.version}/sysswitch/save")
    BaseResponse save(@RequestBody SwitchRequest switchRequest);

    @GetMapping("/setting/${application.setting.version}/sysswitch/switchIsOpen")
    BaseResponse<Boolean> switchIsOpen(@RequestParam(value="switchCode")String switchCode);
}
