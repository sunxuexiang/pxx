package com.wanmi.sbc.setting.api.provider.videoresource;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.videoresource.*;
import com.wanmi.sbc.setting.api.response.storeresource.StoreResourceModifyResponse;
import com.wanmi.sbc.setting.api.response.videoresource.VideoResourceAddResponse;
import com.wanmi.sbc.setting.api.response.videoresource.VideoResourceModifyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * 视频教程资源库保存服务Provider
 * @author hudong
 * @date 2023-06-26 09:12:49
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "VideoResourceSaveProvider")
public interface VideoResourceSaveProvider {

	/**
	 * 新增视频教程资源库API
	 *
	 * @author hudong
	 * @param videoResourceAddRequest 视频教程资源库新增参数结构 {@link VideoResourceAddRequest}
	 * @return 新增的视频教程资源库信息 {@link VideoResourceAddResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/videoresource/add")
	BaseResponse<VideoResourceAddResponse> add(@RequestBody @Valid VideoResourceAddRequest videoResourceAddRequest);

	/**
	 * 新增视频教程资源库API
	 *
	 * @author hudong
	 * @param videoResourceAddRequest 视频教程资源库新增参数结构 {@link VideoResourceAddRequest}
	 * @return 新增的视频教程资源库信息 {@link VideoResourceAddResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/videoresource/saveResources")
	BaseResponse<VideoResourceAddResponse> saveResources(@RequestBody @Valid VideoResourceAddRequest videoResourceAddRequest);

	/**
	 * 修改视频教程资源库API
	 *
	 * @author hudong
	 * @param videoResourceModifyRequest 视频教程资源库修改参数结构 {@link VideoResourceModifyRequest}
	 * @return 修改的视频教程资源库信息 {@link StoreResourceModifyResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/videoresource/modify")
	BaseResponse<VideoResourceModifyResponse> modify(@RequestBody @Valid VideoResourceModifyRequest
															 videoResourceModifyRequest);

	/**
	 * 单个删除视频教程资源库API
	 *
	 * @author hudong
	 * @param videoResourceDelByIdRequest 单个删除参数结构 {@link VideoResourceDelByIdRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/videoresource/delete-by-id")
	BaseResponse deleteById(@RequestBody @Valid VideoResourceDelByIdRequest videoResourceDelByIdRequest);

	/**
	 * 移动视频教程素材资源API
	 *
	 * @author hudong
	 * @param moveRequest 视频教程素材资源修改参数结构  {@link VideoResourceMoveRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/videoresource/move")
	BaseResponse move(@RequestBody @Valid VideoResourceMoveRequest
									moveRequest);

	/**
	 * 批量删除视频教程资源库API
	 *
	 * @author hudong
	 * @param videoResourceDelByIdListRequest 批量删除参数结构 {@link VideoResourceDelByIdListRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/videoresource/delete-by-id-list")
	BaseResponse deleteByIdList(@RequestBody @Valid VideoResourceDelByIdListRequest videoResourceDelByIdListRequest);

}

