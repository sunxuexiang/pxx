package com.wanmi.sbc.setting.api.provider.navigationconfig;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.navigationconfig.NavigationConfigPageRequest;
import com.wanmi.sbc.setting.api.response.navigationconfig.NavigationConfigPageResponse;
import com.wanmi.sbc.setting.api.request.navigationconfig.NavigationConfigListRequest;
import com.wanmi.sbc.setting.api.response.navigationconfig.NavigationConfigListResponse;
import com.wanmi.sbc.setting.api.request.navigationconfig.NavigationConfigByIdRequest;
import com.wanmi.sbc.setting.api.response.navigationconfig.NavigationConfigByIdResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;

/**
 * <p>导航配置查询服务Provider</p>
 * @author lvheng
 * @date 2021-04-12 14:46:18
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "NavigationConfigQueryProvider")
public interface NavigationConfigQueryProvider {

	/**
	 * 分页查询导航配置API
	 *
	 * @author lvheng
	 * @param navigationConfigPageReq 分页请求参数和筛选对象 {@link NavigationConfigPageRequest}
	 * @return 导航配置分页列表信息 {@link NavigationConfigPageResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/navigationconfig/page")
	BaseResponse<NavigationConfigPageResponse> page(@RequestBody @Valid NavigationConfigPageRequest navigationConfigPageReq);

	/**
	 * 列表查询导航配置API
	 *
	 * @author lvheng
	 * @param navigationConfigListReq 列表请求参数和筛选对象 {@link NavigationConfigListRequest}
	 * @return 导航配置的列表信息 {@link NavigationConfigListResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/navigationconfig/list")
	BaseResponse<NavigationConfigListResponse> list(@RequestBody @Valid NavigationConfigListRequest navigationConfigListReq);

	/**
	 * 单个查询导航配置API
	 *
	 * @author lvheng
	 * @param navigationConfigByIdRequest 单个查询导航配置请求参数 {@link NavigationConfigByIdRequest}
	 * @return 导航配置详情 {@link NavigationConfigByIdResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/navigationconfig/get-by-id")
	BaseResponse<NavigationConfigByIdResponse> getById(@RequestBody @Valid NavigationConfigByIdRequest navigationConfigByIdRequest);

}

