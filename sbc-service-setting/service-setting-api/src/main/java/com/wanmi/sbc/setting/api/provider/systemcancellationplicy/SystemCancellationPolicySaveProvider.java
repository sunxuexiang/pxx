package com.wanmi.sbc.setting.api.provider.systemcancellationplicy;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.systemcancellationpolicy.*;
import com.wanmi.sbc.setting.api.request.systemprivacypolicy.*;
import com.wanmi.sbc.setting.api.response.systemcancellationpolicy.SystemCancellationPolicyAddResponse;
import com.wanmi.sbc.setting.api.response.systemcancellationpolicy.SystemCancellationPolicyModifyResponse;
import com.wanmi.sbc.setting.api.response.systemprivacypolicy.SystemPrivacyPolicyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>隐私政策保存服务Provider</p>
 * @author yangzhen
 * @date 2020-09-23 14:52:35
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "SystemCancellationPolicySaveProvider")
public interface SystemCancellationPolicySaveProvider {

	/**
	 * 新增注销政策API
	 *
	 * @author yangzhen
	 * @param systemCancellationPolicyAddRequest 注销政策新增参数结构 {@link SystemCancellationPolicyAddRequest}
	 * @return 新增的注销政策信息 {@link SystemCancellationPolicyAddRequest}
	 */
	@PostMapping("/setting/${application.setting.version}/systemcancellationpolicy/add")
    BaseResponse<SystemCancellationPolicyAddResponse> add(@RequestBody @Valid SystemCancellationPolicyAddRequest systemCancellationPolicyAddRequest);

	/**
	 * 修改隐私政策API
	 *
	 * @author yangzhen
	 * @param modifyRequest 隐私政策修改参数结构 {@link SystemCancellationPolicyModifyRequest}
	 * @return 修改的隐私政策信息 {@link SystemCancellationPolicyModifyResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/systemcancellationpolicy/modify")
    BaseResponse<SystemCancellationPolicyModifyResponse> modify(@RequestBody @Valid SystemCancellationPolicyModifyRequest modifyRequest);

	/**
	 * 单个删除隐私政策API
	 *
	 * @author yangzhen
	 * @param request 单个删除参数结构 {@link SystemCancellationPolicyDelByIdRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/systemcancellationpolicy/delete-by-id")
    BaseResponse deleteById(@RequestBody @Valid SystemCancellationPolicyDelByIdRequest request);

	/**
	 * 批量删除隐私政策API
	 *
	 * @author yangzhen
	 * @param request 批量删除参数结构 {@link SystemCancellationPolicyDelByIdListRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/systemcancellationpolicy/delete-by-id-list")
    BaseResponse deleteByIdList(@RequestBody @Valid SystemCancellationPolicyDelByIdListRequest request);



	/**
	 * 修改/保存隐私政策API
	 *
	 * @author yangzhen
	 * @param request 隐私政策修改参数结构 {@link SystemCancellationPolicyRequest}
	 * @return 修改的隐私政策信息 {@link SystemPrivacyPolicyResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/systemcancellationpolicy/modify-or-add")
    BaseResponse modifyOrAdd(@RequestBody @Valid SystemCancellationPolicyRequest request);

}

