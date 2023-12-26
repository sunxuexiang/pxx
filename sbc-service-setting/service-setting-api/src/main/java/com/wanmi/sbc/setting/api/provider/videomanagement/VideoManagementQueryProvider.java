package com.wanmi.sbc.setting.api.provider.videomanagement;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.videomanagement.*;
import com.wanmi.sbc.setting.api.response.videomanagement.VideoManagementPageResponse;
import com.wanmi.sbc.setting.api.response.videomanagement.VideoManagementListResponse;
import com.wanmi.sbc.setting.api.response.videomanagement.VideoManagementByIdResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;

/**
 * <p>视频管理查询服务Provider</p>
 * @author zhaowei
 * @date 2021-04-17 17:47:22
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "VideoManagementQueryProvider")
public interface VideoManagementQueryProvider {

	/**
	 * 分页查询视频管理API
	 *
	 * @author zhaowei
	 * @param videoManagementPageReq 分页请求参数和筛选对象 {@link VideoManagementPageRequest}
	 * @return 视频管理分页列表信息 {@link VideoManagementPageResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/videomanagement/page")
	BaseResponse<VideoManagementPageResponse> page(@RequestBody @Valid VideoManagementPageRequest videoManagementPageReq);

	/**
	 * 分页查询视频管理API
	 *
	 * @author zhaowei
	 * @param videoManagementPageReq 分页请求参数和筛选对象 {@link VideoManagementPageRequest}
	 * @return 视频管理分页列表信息 {@link VideoManagementPageResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/videomanagement/like-page")
	BaseResponse<VideoManagementPageResponse> likePage(@RequestBody @Valid VideoManagementPageRequest videoManagementPageReq);

	@PostMapping("/setting/${application.setting.version}/videomanagement/follow-page")
	BaseResponse<VideoManagementPageResponse> followPage(@RequestBody @Valid VideoManagementPageRequest videoManagementPageReq);


	@PostMapping("/setting/${application.setting.version}/videomanagement/follow")
	BaseResponse follow(@RequestBody @Valid VideoFollowAddRequest request);

	@PostMapping("/setting/${application.setting.version}/videomanagement/cancel_follow")
	BaseResponse cancelFollow(@RequestBody @Valid VideoFollowCancelRequest request);

	/**
	 * 列表查询视频管理API
	 *
	 * @author zhaowei
	 * @param videoManagementListReq 列表请求参数和筛选对象 {@link VideoManagementListRequest}
	 * @return 视频管理的列表信息 {@link VideoManagementListResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/videomanagement/list")
	BaseResponse<VideoManagementListResponse> list(@RequestBody @Valid VideoManagementListRequest videoManagementListReq);

	/**
	 * c端点击视频播放
	 *
	 * @author zhaowei
	 * @param quest c端点击视频播放 {@link VideoByIdOrCustomerIdRequest}
	 * @return 视频管理详情 {@link VideoManagementByIdResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/videomanagement/get-by-id-or-customerId")
	BaseResponse getByIdOrCustomerId(@RequestBody @Valid VideoByIdOrCustomerIdRequest quest);

	/**
	 * boss根据id获取视频详情
	 *
	 * @author zhaowei
	 * @param videoManagementByIdRequest boss根据id获取详情 {@link VideoManagementByIdRequest}
	 * @return 视频管理详情 {@link VideoManagementByIdResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/videomanagement/get-details-by-id")
	BaseResponse<VideoManagementByIdResponse> getDetailsById(@RequestBody @Valid VideoManagementByIdRequest videoManagementByIdRequest);

	/**
	 * 小程序端分页查询视频管理API
	 *
	 * @author zhaowei
	 * @param videoManagementPageReq 小程序端 {@link VideoManagementPageRequest}
	 * @return 视频管理分页列表信息 {@link VideoManagementPageResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/videomanagement/get-applets-page")
	BaseResponse<VideoManagementPageResponse> getAppletsPage(@RequestBody @Valid VideoManagementPageRequest videoManagementPageReq);



	/**
	 * 小程序端分页查询视频管理API
	 *
	 * @author zhaowei
	 * @param videoManagementPageReq 小程序端 {@link VideoManagementPageRequest}
	 * @return 视频管理分页列表信息 {@link VideoManagementPageResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/videomanagement/get-applets-list")
	BaseResponse<VideoManagementListResponse> getAppletsList(@RequestBody @Valid VideoManagementPageRequest videoManagementPageReq);


	/**
	 * 小程序端分页查询视频管理API
	 *
	 * @author zhaowei
	 * @param videoManagementPageReq 小程序端 {@link VideoManagementPageRequest}
	 * @return 视频管理分页列表信息 {@link VideoManagementPageResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/videomanagement/get-applets-list-android")
	BaseResponse<VideoManagementPageResponse> getAppletsListAndroid(@RequestBody @Valid VideoManagementPageRequest videoManagementPageReq);



}

