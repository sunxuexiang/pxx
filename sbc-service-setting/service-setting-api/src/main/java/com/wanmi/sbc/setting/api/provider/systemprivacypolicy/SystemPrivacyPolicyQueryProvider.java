package com.wanmi.sbc.setting.api.provider.systemprivacypolicy;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.response.systemprivacypolicy.SystemPrivacyPolicyByIdResponse;
import com.wanmi.sbc.setting.api.response.systemprivacypolicy.SystemPrivacyPolicyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * <p>隐私政策查询服务Provider</p>
 * @author yangzhen
 * @date 2020-09-23 14:52:35
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "SystemPrivacyPolicyQueryProvider")
public interface SystemPrivacyPolicyQueryProvider {
	/**
	 * 查询隐私政策API
	 *
	 * @author yangzhen
	 * @return 隐私政策详情 {@link SystemPrivacyPolicyByIdResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/systemprivacypolicy/query-system-privacy-policy")
    BaseResponse<SystemPrivacyPolicyResponse> querySystemPrivacyPolicy();

}

