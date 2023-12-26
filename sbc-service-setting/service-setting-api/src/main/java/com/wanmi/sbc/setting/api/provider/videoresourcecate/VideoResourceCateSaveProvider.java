package com.wanmi.sbc.setting.api.provider.videoresourcecate;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.AllRoleMenuInfoListRequest;
import com.wanmi.sbc.setting.api.request.videoresourcecate.VideoResourceCateAddRequest;
import com.wanmi.sbc.setting.api.request.videoresourcecate.VideoResourceCateDelByIdRequest;
import com.wanmi.sbc.setting.api.request.videoresourcecate.VideoResourceCateInitRequest;
import com.wanmi.sbc.setting.api.request.videoresourcecate.VideoResourceCateModifyRequest;
import com.wanmi.sbc.setting.api.response.TwoAndThreeMenuInfoListResponse;
import com.wanmi.sbc.setting.api.response.videoresourcecate.VideoResourceCateAddResponse;
import com.wanmi.sbc.setting.api.response.videoresourcecate.VideoResourceCateModifyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * 视频教程资源资源分类表保存服务Provider
 * @author hudong
 * @date 2023-06-26 09:23:22
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "VideoResourceCateSaveProvider")
public interface VideoResourceCateSaveProvider {

	/**
	 * 新增视频教程资源资源分类表API
	 *
	 * @author hudong
	 * @param videoResourceCateAddRequest 视频教程资源资源分类表新增参数结构 {@link VideoResourceCateAddRequest}
	 * @return 新增的店铺资源资源分类表信息 {@link VideoResourceCateAddResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/videoresourcecate/add")
	BaseResponse<VideoResourceCateAddResponse> add(@RequestBody @Valid VideoResourceCateAddRequest
                                                           videoResourceCateAddRequest);

	/**
	 * 修改视频教程资源资源分类表API
	 *
	 * @author hudong
	 * @param videoResourceCateModifyRequest 视频教程资源资源分类表修改参数结构 {@link VideoResourceCateModifyRequest}
	 * @return 修改的视频教程资源资源分类表信息 {@link VideoResourceCateModifyResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/videoresourcecate/modify")
	BaseResponse<VideoResourceCateModifyResponse> modify(@RequestBody @Valid VideoResourceCateModifyRequest videoResourceCateModifyRequest);

	/**
	 * 单个删除视频教程资源资源分类表API
	 *
	 * @author hudong
	 * @param videoResourceCateDelByIdRequest 单个删除参数结构 {@link VideoResourceCateDelByIdRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/videoresourcecate/delete-by-id")
	BaseResponse delete(@RequestBody @Valid VideoResourceCateDelByIdRequest videoResourceCateDelByIdRequest);



	/**
	 * 初始化视频教程默认分类
	 *
	 * @author hudong
	 * @param videoResourceCate 批量删除参数结构 {@link VideoResourceCateInitRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/videoresourcecate/init")
	BaseResponse init(@RequestBody @Valid VideoResourceCateInitRequest videoResourceCate);

	/**
	 * 根据目录信息初始化视频教程默认分类
	 *
	 * @author hudong
	 * @param request 批量删除参数结构 {@link AllRoleMenuInfoListRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/videoresourcecate/initMenuInfoListToCate")
	BaseResponse initMenuInfoListToCate(@RequestBody AllRoleMenuInfoListRequest request);

}

