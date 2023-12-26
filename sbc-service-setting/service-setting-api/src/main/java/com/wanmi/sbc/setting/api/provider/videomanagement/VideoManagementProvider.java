package com.wanmi.sbc.setting.api.provider.videomanagement;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.videomanagement.*;
import com.wanmi.sbc.setting.api.response.videomanagement.VideoManagementAddResponse;
import com.wanmi.sbc.setting.api.response.videomanagement.VideoManagementModifyResponse;
import com.wanmi.sbc.setting.api.response.videomanagement.VideoManagementPageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;

/**
 * <p>视频管理保存服务Provider</p>
 * @author zhaowei
 * @date 2021-04-17 17:47:22
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "VideoManagementProvider")
public interface VideoManagementProvider {

	/**
	 * 新增视频管理API
	 *
	 * @author zhaowei
	 * @param videoManagementAddRequest 视频管理新增参数结构 {@link VideoManagementAddRequest}
	 * @return 新增的视频管理信息 {@link VideoManagementAddResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/videomanagement/add")
	BaseResponse<VideoManagementAddResponse> add(@RequestBody @Valid VideoManagementAddRequest videoManagementAddRequest);

	@PostMapping("/setting/${application.setting.version}/videomanagement/initCoverImg")
	BaseResponse initCoverImg();

	@PostMapping("/setting/${application.setting.version}/videomanagement/getCoverImg")
	BaseResponse<String> getCoverImg(@RequestBody @Valid GetCoverImgRequest getCoverImgRequest);

	/**
	 * 修改视频管理API
	 *
	 * @author zhaowei
	 * @param videoManagementModifyRequest 视频管理修改参数结构 {@link VideoManagementModifyRequest}
	 * @return 修改的视频管理信息 {@link VideoManagementModifyResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/videomanagement/modify")
	BaseResponse modify(@RequestBody @Valid VideoManagementModifyRequest videoManagementModifyRequest);

	/**
	 * 修改上下架状态
	 *
	 * @author zhaowei
	 * @param request 视频管理修改参数结构 {@link VideoManagementUpdateStateRequest}
	 * @return 修改的视频管理信息 {@link VideoManagementModifyResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/videomanagement/update-state-by-id")
	BaseResponse updateStateById(@RequestBody @Valid VideoManagementUpdateStateRequest request);



	/**
	 * 单个删除视频管理API
	 *
	 * @author zhaowei
	 * @param videoManagementDelByIdRequest 单个删除参数结构 {@link VideoManagementDelByIdRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/videomanagement/delete-by-id")
	BaseResponse deleteById(@RequestBody @Valid VideoManagementDelByIdRequest videoManagementDelByIdRequest);

	/**
	 * 视频点赞
	 *
	 * @author zhaowei
	 * @param addReq 视频点赞 {@link VideoLikeAddRequest}
	 * @return  {@link BaseResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/videomanagement/add-video-like")
	BaseResponse addVideoLike(@RequestBody @Valid VideoLikeAddRequest addReq);

	/**
	 * 视频点赞
	 *
	 * @author zhaowei
	 * @param cancelReq 视频点赞 {@link VideoLikeCancelRequest}
	 * @return  {@link BaseResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/videomanagement/cancel-video-like")
	BaseResponse cancelVideoLike(@RequestBody @Valid VideoLikeCancelRequest cancelReq);

	/**
	 * 批量删除视频管理API
	 *
	 * @author zhaowei
	 * @param videoManagementDelByIdListRequest 批量删除参数结构 {@link VideoManagementDelByIdListRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/videomanagement/delete-by-id-list")
	BaseResponse deleteByIdList(@RequestBody @Valid VideoManagementDelByIdListRequest videoManagementDelByIdListRequest);


}

