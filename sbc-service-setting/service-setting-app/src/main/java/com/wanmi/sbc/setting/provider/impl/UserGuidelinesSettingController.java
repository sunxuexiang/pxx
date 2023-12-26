package com.wanmi.sbc.setting.provider.impl;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.provider.MobileSettingProvider;
import com.wanmi.sbc.setting.api.provider.UserGuidelinesConfigProvider;
import com.wanmi.sbc.setting.api.request.AboutUsModifyRequest;
import com.wanmi.sbc.setting.api.request.AppShareSettingModifyRequest;
import com.wanmi.sbc.setting.api.request.AppUpgradeModifyRequest;
import com.wanmi.sbc.setting.api.request.UserGuidelinesConfigModifyRequest;
import com.wanmi.sbc.setting.api.response.AppShareSettingGetResponse;
import com.wanmi.sbc.setting.api.response.AppUpgradeGetResponse;
import com.wanmi.sbc.setting.api.response.UserGuidelinesConfigQueryResponse;
import com.wanmi.sbc.setting.mobile.MobileSettingService;
import com.wanmi.sbc.setting.userguidelines.UserGuidelinesSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @Author: gaomuwei
 * @Date: Created In 上午10:41 2018/11/19
 * @Description: 移动端设置controller
 */
@RestController
public class UserGuidelinesSettingController implements UserGuidelinesConfigProvider {

    @Autowired
    private UserGuidelinesSettingService userGuidelinesSettingService;

    @Override
    public BaseResponse<UserGuidelinesConfigQueryResponse> queryUserGuidelinesConfig() {
        return BaseResponse.success(userGuidelinesSettingService.getUserGuidelines());
    }

    @Override
    public BaseResponse modifyUserGuidelinesConfig(UserGuidelinesConfigModifyRequest request) {
        userGuidelinesSettingService.modifyUserGuidelines(request);
        return BaseResponse.SUCCESSFUL();
    }
}
