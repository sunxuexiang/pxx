package com.wanmi.sbc.setting.api.provider.banneradmin;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.banneradmin.BannerAdminAddRequest;
import com.wanmi.sbc.setting.api.response.banneradmin.BannerAdminAddResponse;
import com.wanmi.sbc.setting.api.request.banneradmin.BannerAdminModifyRequest;
import com.wanmi.sbc.setting.api.response.banneradmin.BannerAdminModifyResponse;
import com.wanmi.sbc.setting.api.request.banneradmin.BannerAdminDelByIdRequest;
import com.wanmi.sbc.setting.api.request.banneradmin.BannerAdminDelByIdListRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;

/**
 * <p>轮播管理保存服务Provider</p>
 * @author 费传奇
 * @date 2020-12-08 11:44:38
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "BannerAdminProvider")
public interface BannerAdminProvider {

	/**
	 * 新增轮播管理API
	 *
	 * @author 费传奇
	 * @param bannerAdminAddRequest 轮播管理新增参数结构 {@link BannerAdminAddRequest}
	 * @return 新增的轮播管理信息 {@link BannerAdminAddResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/banneradmin/add")
	BaseResponse<BannerAdminAddResponse> add(@RequestBody @Valid BannerAdminAddRequest bannerAdminAddRequest);

	/**
	 * 修改轮播管理API
	 *
	 * @author 费传奇
	 * @param bannerAdminModifyRequest 轮播管理修改参数结构 {@link BannerAdminModifyRequest}
	 * @return 修改的轮播管理信息 {@link BannerAdminModifyResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/banneradmin/modify")
	BaseResponse<BannerAdminModifyResponse> modify(@RequestBody @Valid BannerAdminModifyRequest bannerAdminModifyRequest);


	@PostMapping("/setting/${application.setting.version}/banneradmin/modifyStatus")
	BaseResponse<BannerAdminModifyResponse> modifyStatus(@RequestBody @Valid BannerAdminModifyRequest bannerAdminModifyRequest);
	/**
	 * 单个删除轮播管理API
	 *
	 * @author 费传奇
	 * @param bannerAdminDelByIdRequest 单个删除参数结构 {@link BannerAdminDelByIdRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/banneradmin/delete-by-id")
	BaseResponse deleteById(@RequestBody @Valid BannerAdminDelByIdRequest bannerAdminDelByIdRequest);

	/**
	 * 批量删除轮播管理API
	 *
	 * @author 费传奇
	 * @param bannerAdminDelByIdListRequest 批量删除参数结构 {@link BannerAdminDelByIdListRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/banneradmin/delete-by-id-list")
	BaseResponse deleteByIdList(@RequestBody @Valid BannerAdminDelByIdListRequest bannerAdminDelByIdListRequest);

}

