package com.wanmi.sbc.setting.provider.impl.systemcancellationpolicy;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.provider.systemcancellationplicy.SystemCancellationPolicyQueryProvider;
import com.wanmi.sbc.setting.api.response.systemcancellationpolicy.SystemCancellationPolicyResponse;
import com.wanmi.sbc.setting.systemcancellationpolicy.model.root.SystemCancellationPolicy;
import com.wanmi.sbc.setting.systemcancellationpolicy.service.SystemCancellationPolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>注销政策查询服务接口实现</p>
 *
 * @author yangzhen
 * @date 2020-09-23 14:52:35
 */
@RestController
@Validated
public class SystemCancellationPolicyQueryController implements SystemCancellationPolicyQueryProvider {
    @Autowired
    private SystemCancellationPolicyService systemCancellationPolicyService;

    @Override
    public BaseResponse<SystemCancellationPolicyResponse> querySystemCancellationPolicy() {
        SystemCancellationPolicyResponse response = new SystemCancellationPolicyResponse();
        SystemCancellationPolicy config = systemCancellationPolicyService.querySystemCancellationPolicy();
        KsBeanUtil.copyPropertiesThird(config, response);
        return BaseResponse.success(response);
    }
}

