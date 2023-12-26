package com.wanmi.sbc.setting.api.provider.systemcancellationplicy;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.response.systemcancellationpolicy.SystemCancellationPolicyResponse;
import com.wanmi.sbc.setting.api.response.systemprivacypolicy.SystemPrivacyPolicyByIdResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @Description: 注销政策查询服务
 * @Author: XinJiang
 * @Date: 2021/12/7 11:55
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "SystemCancellationPolicyQueryProvider")
public interface SystemCancellationPolicyQueryProvider {

    /**
     * 注销隐私政策API
     *
     * @author jiangxin
     * @return 隐私政策详情 {@link SystemPrivacyPolicyByIdResponse}
     */
    @PostMapping("/setting/${application.setting.version}/systemcancellationpolicy/query-system-cancellation-policy")
    BaseResponse<SystemCancellationPolicyResponse> querySystemCancellationPolicy();
}
