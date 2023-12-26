package com.wanmi.sbc.setting.provider.impl;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.provider.SwitchProvider;
import com.wanmi.sbc.setting.api.request.sysswitch.SwitchModifyRequest;
import com.wanmi.sbc.setting.api.request.sysswitch.SwitchRequest;
import com.wanmi.sbc.setting.api.response.SwitchModifyResponse;
import com.wanmi.sbc.setting.sysswitch.SwitchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SwitchController implements SwitchProvider {
    @Autowired
    private SwitchService switchService;

    @Override
    public BaseResponse<SwitchModifyResponse> modify(@RequestBody SwitchModifyRequest request) {
        SwitchModifyResponse response = new SwitchModifyResponse();
        response.setCount(switchService.updateSwitch(request.getId(), request.getStatus()));
        return BaseResponse.success(response);
    }


    @Override
    public BaseResponse save(SwitchRequest switchRequest) {
        switchService.save(switchRequest);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<Boolean> switchIsOpen(String switchCode) {
        Boolean isOpend=  switchService.isOpen(switchCode);
        return BaseResponse.success(isOpend);
    }
}
