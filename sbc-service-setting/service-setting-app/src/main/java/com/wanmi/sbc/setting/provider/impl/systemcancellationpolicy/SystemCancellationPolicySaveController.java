package com.wanmi.sbc.setting.provider.impl.systemcancellationpolicy;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.provider.systemcancellationplicy.SystemCancellationPolicySaveProvider;
import com.wanmi.sbc.setting.api.request.systemcancellationpolicy.*;
import com.wanmi.sbc.setting.api.request.systemprivacypolicy.*;
import com.wanmi.sbc.setting.api.response.systemcancellationpolicy.SystemCancellationPolicyAddResponse;
import com.wanmi.sbc.setting.api.response.systemcancellationpolicy.SystemCancellationPolicyModifyResponse;
import com.wanmi.sbc.setting.api.response.systemprivacypolicy.SystemPrivacyPolicyModifyResponse;
import com.wanmi.sbc.setting.systemcancellationpolicy.model.root.SystemCancellationPolicy;
import com.wanmi.sbc.setting.systemcancellationpolicy.service.SystemCancellationPolicyService;
import com.wanmi.sbc.setting.systemprivacypolicy.model.root.SystemPrivacyPolicy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;

/**
 * <p>隐私政策保存服务接口实现</p>
 *
 * @author yangzhen
 * @date 2020-09-23 14:52:35
 */
@RestController
@Validated
public class SystemCancellationPolicySaveController implements SystemCancellationPolicySaveProvider {
    @Autowired
    private SystemCancellationPolicyService systemCancellationPolicyService;


    @Override
    public BaseResponse<SystemCancellationPolicyAddResponse> add(@RequestBody @Valid SystemCancellationPolicyAddRequest systemCancellationPolicyAddRequest) {
        SystemCancellationPolicy systemCancellationPolicy = new SystemCancellationPolicy();
        KsBeanUtil.copyPropertiesThird(systemCancellationPolicyAddRequest, systemCancellationPolicy);
        return BaseResponse.success(new SystemCancellationPolicyAddResponse(
                systemCancellationPolicyService.wrapperVo(systemCancellationPolicyService.add(systemCancellationPolicy))));
    }

    @Override
    public BaseResponse<SystemCancellationPolicyModifyResponse> modify(@RequestBody @Valid SystemCancellationPolicyModifyRequest systemCancellationPolicyModifyRequest) {
        SystemCancellationPolicy systemCancellationPolicy = new SystemCancellationPolicy();
        KsBeanUtil.copyPropertiesThird(systemCancellationPolicyModifyRequest, systemCancellationPolicy);
        return BaseResponse.success(new SystemCancellationPolicyModifyResponse(
                systemCancellationPolicyService.wrapperVo(systemCancellationPolicyService.modify(systemCancellationPolicy))));
    }

    @Override
    public BaseResponse deleteById(@RequestBody @Valid SystemCancellationPolicyDelByIdRequest request) {
        systemCancellationPolicyService.deleteById(request.getCancellationPolicyId());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse deleteByIdList(@RequestBody @Valid SystemCancellationPolicyDelByIdListRequest request) {
        systemCancellationPolicyService.deleteByIdList(request.getCancellationPolicyIdList());
        return BaseResponse.SUCCESSFUL();
    }


    @Override
    public BaseResponse modifyOrAdd(@RequestBody @Valid SystemCancellationPolicyRequest request) {
        SystemCancellationPolicy systemCancellationPolicy = new SystemCancellationPolicy();
        if (StringUtils.isNotEmpty(request.getCancellationPolicyId())) {
            systemCancellationPolicy = systemCancellationPolicyService.getById(request.getCancellationPolicyId());
        }
        KsBeanUtil.copyProperties(request, systemCancellationPolicy);
        systemCancellationPolicy.setDelFlag(DeleteFlag.NO);
        if (StringUtils.isBlank(request.getCancellationPolicyId())) {
            systemCancellationPolicy.setCreatePerson(request.getOperator().getUserId());
            systemCancellationPolicy.setCreateTime(LocalDateTime.now());
        } else {
            systemCancellationPolicy.setUpdatePerson(request.getOperator().getUserId());
            systemCancellationPolicy.setUpdateTime(LocalDateTime.now());
        }
        systemCancellationPolicyService.modify(systemCancellationPolicy);
        return BaseResponse.SUCCESSFUL();
    }

}

