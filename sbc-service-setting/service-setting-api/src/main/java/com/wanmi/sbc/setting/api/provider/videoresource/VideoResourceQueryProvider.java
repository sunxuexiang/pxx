package com.wanmi.sbc.setting.api.provider.videoresource;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.storeresource.StoreResourceByIdRequest;
import com.wanmi.sbc.setting.api.request.storeresource.StoreResourceListRequest;
import com.wanmi.sbc.setting.api.request.storeresource.StoreResourcePageRequest;
import com.wanmi.sbc.setting.api.request.videoresource.VideoResourceByIdRequest;
import com.wanmi.sbc.setting.api.request.videoresource.VideoResourceListRequest;
import com.wanmi.sbc.setting.api.request.videoresource.VideoResourcePageRequest;
import com.wanmi.sbc.setting.api.response.storeresource.StoreResourceByIdResponse;
import com.wanmi.sbc.setting.api.response.storeresource.StoreResourceListResponse;
import com.wanmi.sbc.setting.api.response.storeresource.StoreResourcePageResponse;
import com.wanmi.sbc.setting.api.response.videoresource.VideoResourceByIdResponse;
import com.wanmi.sbc.setting.api.response.videoresource.VideoResourceListResponse;
import com.wanmi.sbc.setting.api.response.videoresource.VideoResourceMapResponse;
import com.wanmi.sbc.setting.api.response.videoresource.VideoResourcePageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * 视频教程资源库查询服务Provider
 * @author hudong
 * @date 2023-06-26 09:12:49
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "VideoResourceQueryProvider")
public interface VideoResourceQueryProvider {

	/**
	 * 分页查询视频教程资源库API
	 *
	 * @author hudong
	 * @param videoResourcePageRequest 分页请求参数和筛选对象 {@link VideoResourcePageRequest}
	 * @return 视频教程资源库分页列表信息 {@link VideoResourcePageResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/videoresource/page")
	BaseResponse<VideoResourcePageResponse> page(@RequestBody @Valid VideoResourcePageRequest videoResourcePageRequest);

	/**
	 * 分页查询视频教程资源库API
	 *
	 * @author hudong
	 * @param videoResourcePageRequest 分页请求参数和筛选对象 {@link VideoResourcePageRequest}
	 * @return 视频教程资源库分页列表信息 {@link VideoResourcePageResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/videoresource/defaultList")
	BaseResponse<VideoResourcePageResponse> defaultList(@RequestBody @Valid VideoResourcePageRequest videoResourcePageRequest);

	/**
	 * 查询视频教程资源库API,返回map形式
	 *
	 * @author hudong
	 * @param videoResourcePageRequest 分页请求参数和筛选对象 {@link VideoResourcePageRequest}
	 * @return 视频教程资源库分页列表信息 {@link VideoResourcePageResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/videoresource/mapInfo")
	BaseResponse<VideoResourceMapResponse> mapInfo(@RequestBody @Valid VideoResourcePageRequest videoResourcePageRequest);

	/**
	 * 列表查询视频教程资源库API
	 *
	 * @author hudong
	 * @param videoResourceListReq 列表请求参数和筛选对象 {@link VideoResourceListRequest}
	 * @return 视频教程资源库的列表信息 {@link VideoResourceListResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/videoresource/list")
	BaseResponse<List<VideoResourceListResponse>> list(@RequestBody @Valid VideoResourceListRequest videoResourceListReq);


	/**
	 * 单个查询视频教程资源库API
	 *
	 * @author hudong
	 * @param videoResourceByIdRequest 单个查询视频教程资源库请求参数 {@link VideoResourceByIdRequest}
	 * @return 视频教程资源库详情 {@link VideoResourceByIdResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/videoresource/get-by-id")
	BaseResponse<VideoResourceByIdResponse> getById(@RequestBody @Valid VideoResourceByIdRequest
                                                            videoResourceByIdRequest);

}

