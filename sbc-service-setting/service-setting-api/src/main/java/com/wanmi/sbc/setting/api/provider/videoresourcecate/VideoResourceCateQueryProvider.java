package com.wanmi.sbc.setting.api.provider.videoresourcecate;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.videoresourcecate.*;
import com.wanmi.sbc.setting.api.response.videoresourcecate.VideoResourceCateByIdResponse;
import com.wanmi.sbc.setting.api.response.videoresourcecate.VideoResourceCateListResponse;
import com.wanmi.sbc.setting.api.response.videoresourcecate.VideoResourceCatePageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * 视频教程资源资源分类表查询服务Provider
 * @author hudong
 * @date 2023-06-26 09:23:22
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "VideoResourceCateQueryProvider")
public interface VideoResourceCateQueryProvider {

	/**
	 * 分页查询视频教程资源资源分类表API
	 *
	 * @author hudong
	 * @param videoResourceCatePageReq 分页请求参数和筛选对象 {@link VideoResourceCatePageRequest}
	 * @return 视频教程资源资源分类表分页列表信息 {@link VideoResourceCatePageResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/videoresourcecate/page")
	BaseResponse<VideoResourceCatePageResponse> page(@RequestBody @Valid VideoResourceCatePageRequest
                                                             videoResourceCatePageReq);

	/**
	 * 列表查询视频教程资源资源分类表API
	 *
	 * @author hudong
	 * @param videoResourceCateListReq 列表请求参数和筛选对象 {@link VideoResourceCateListRequest}
	 * @return 视频教程资源资源分类表的列表信息 {@link VideoResourceCateListResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/videoresourcecate/list")
	BaseResponse<VideoResourceCateListResponse> list(@RequestBody @Valid VideoResourceCateListRequest videoResourceCateListReq);

	/**
	 * 列表查询视频教程资源后台视频教程默认资源分类表API
	 *
	 * @author hudong
	 * @param videoResourceCateListReq 列表请求参数和筛选对象 {@link VideoResourceCateListRequest}
	 * @return 视频教程资源后台视频教程默认资源分类信息 {@link VideoResourceCateListResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/videoresourcecate/defaultList")
	BaseResponse<VideoResourceCateListResponse> defaultList(@RequestBody @Valid VideoResourceCateListRequest videoResourceCateListReq);

	/**
	 * 单个查询视频教程资源资源分类表API
	 *
	 * @author hudong
	 * @param videoResourceCateByIdRequest 单个查询视频教程资源资源分类表请求参数 {@link VideoResourceCateByIdRequest}
	 * @return 视频教程资源资源分类表详情 {@link VideoResourceCateByIdResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/videoresourcecate/get-by-id")
	BaseResponse<VideoResourceCateByIdResponse> getById(@RequestBody @Valid VideoResourceCateByIdRequest videoResourceCateByIdRequest);

	/**
	 * 验证是否有子类
	 *
	 * @param videoResourceCateCheckChildRequest 验证是否有子类 {@link VideoResourceCateCheckChildRequest}
	 * @return 验证是否有子类 {@link VideoResourceCateListResponse}
	 * @author hudong
	 */
	@PostMapping("/setting/${application.setting.version}/videoresourcecate/check-child")
	BaseResponse<Integer> checkChild(@RequestBody @Valid VideoResourceCateCheckChildRequest
											 videoResourceCateCheckChildRequest);

	/**
	 * 验证是否有素材
	 *
	 * @param videoResourceCateCheckResourceRequest 验证是否有素材 {@link VideoResourceCateCheckResourceRequest}
	 * @return 验证是否有素材 {@link VideoResourceCateListResponse}
	 * @author hudong
	 */
	@PostMapping("/setting/${application.setting.version}/videoresourcecate/check-resource")
	BaseResponse<Integer> checkResource(@RequestBody @Valid VideoResourceCateCheckResourceRequest videoResourceCateCheckResourceRequest);


}

