package com.wanmi.sbc.setting.api.provider.banneradmin;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.banneradmin.BannerAdminPageRequest;
import com.wanmi.sbc.setting.api.response.banneradmin.BannerAdminPageResponse;
import com.wanmi.sbc.setting.api.request.banneradmin.BannerAdminListRequest;
import com.wanmi.sbc.setting.api.response.banneradmin.BannerAdminListResponse;
import com.wanmi.sbc.setting.api.request.banneradmin.BannerAdminByIdRequest;
import com.wanmi.sbc.setting.api.response.banneradmin.BannerAdminByIdResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;

/**
 * <p>轮播管理查询服务Provider</p>
 * @author 费传奇
 * @date 2020-12-08 11:44:38
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "BannerAdminQueryProvider")
public interface BannerAdminQueryProvider {

	/**
	 * 分页查询轮播管理API
	 *
	 * @author 费传奇
	 * @param bannerAdminPageReq 分页请求参数和筛选对象 {@link BannerAdminPageRequest}
	 * @return 轮播管理分页列表信息 {@link BannerAdminPageResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/banneradmin/page")
	BaseResponse<BannerAdminPageResponse> page(@RequestBody @Valid BannerAdminPageRequest bannerAdminPageReq);

	/**
	 * 列表查询轮播管理API
	 *
	 * @author 费传奇
	 * @param bannerAdminListReq 列表请求参数和筛选对象 {@link BannerAdminListRequest}
	 * @return 轮播管理的列表信息 {@link BannerAdminListResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/banneradmin/list")
	BaseResponse<BannerAdminListResponse> list(@RequestBody @Valid BannerAdminListRequest bannerAdminListReq);

	/**
	 * 单个查询轮播管理API
	 *
	 * @author 费传奇
	 * @param bannerAdminByIdRequest 单个查询轮播管理请求参数 {@link BannerAdminByIdRequest}
	 * @return 轮播管理详情 {@link BannerAdminByIdResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/banneradmin/get-by-id")
	BaseResponse<BannerAdminByIdResponse> getById(@RequestBody @Valid BannerAdminByIdRequest bannerAdminByIdRequest);

}

