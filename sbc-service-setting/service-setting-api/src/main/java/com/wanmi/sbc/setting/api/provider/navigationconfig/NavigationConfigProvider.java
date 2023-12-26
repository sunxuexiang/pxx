package com.wanmi.sbc.setting.api.provider.navigationconfig;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.navigationconfig.NavigationConfigDelByIdRequest;
import com.wanmi.sbc.setting.api.request.navigationconfig.NavigationConfigModifyRequest;
import com.wanmi.sbc.setting.api.response.navigationconfig.NavigationConfigModifyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>导航配置保存服务Provider</p>
 * @author lvheng
 * @date 2021-04-12 14:46:18
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "NavigationConfigProvider")
public interface NavigationConfigProvider {

	/**
	 * 修改导航配置API
	 *
	 * @author lvheng
	 * @param navigationConfigModifyRequest 导航配置修改参数结构 {@link NavigationConfigModifyRequest}
	 * @return 修改的导航配置信息 {@link NavigationConfigModifyResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/navigationconfig/modify")
	BaseResponse<NavigationConfigModifyResponse> modify(@RequestBody @Valid NavigationConfigModifyRequest navigationConfigModifyRequest);

	/**
	 * 删除导航配置API
	 *
	 * @author lvheng
	 * @param navigationConfigDelByIdRequest 导航配置修改参数结构 {@link NavigationConfigDelByIdRequest}
	 */
	@PostMapping("/setting/${application.setting.version}/navigationconfig/delete")
	BaseResponse delete(@RequestBody @Valid NavigationConfigDelByIdRequest navigationConfigDelByIdRequest);

}

