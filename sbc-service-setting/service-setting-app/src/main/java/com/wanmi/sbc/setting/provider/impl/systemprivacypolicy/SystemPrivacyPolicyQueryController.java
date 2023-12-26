package com.wanmi.sbc.setting.provider.impl.systemprivacypolicy;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.provider.systemprivacypolicy.SystemPrivacyPolicyQueryProvider;
import com.wanmi.sbc.setting.api.response.systemprivacypolicy.SystemPrivacyPolicyResponse;
import com.wanmi.sbc.setting.systemprivacypolicy.model.root.SystemPrivacyPolicy;
import com.wanmi.sbc.setting.systemprivacypolicy.service.SystemPrivacyPolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>隐私政策查询服务接口实现</p>
 *
 * @author yangzhen
 * @date 2020-09-23 14:52:35
 */
@RestController
@Validated
public class SystemPrivacyPolicyQueryController implements SystemPrivacyPolicyQueryProvider {
    @Autowired
    private SystemPrivacyPolicyService systemPrivacyPolicyService;

    @Override
    public BaseResponse<SystemPrivacyPolicyResponse> querySystemPrivacyPolicy() {
        SystemPrivacyPolicyResponse response = new SystemPrivacyPolicyResponse();
        SystemPrivacyPolicy config = systemPrivacyPolicyService.querySystemPrivacyPolicy();
        KsBeanUtil.copyPropertiesThird(config, response);
        return BaseResponse.success(response);
    }
}

