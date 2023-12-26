package com.wanmi.sbc.setting.api.provider.systemfile;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.systemfile.SystemFileByFileKeyRequest;
import com.wanmi.sbc.setting.api.request.systemfile.SystemFileByIdRequest;
import com.wanmi.sbc.setting.api.request.systemfile.SystemFileListRequest;
import com.wanmi.sbc.setting.api.request.systemfile.SystemFilePageRequest;
import com.wanmi.sbc.setting.api.response.systemfile.SystemFileByIdResponse;
import com.wanmi.sbc.setting.api.response.systemfile.SystemFileListResponse;
import com.wanmi.sbc.setting.api.response.systemfile.SystemFilePageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * 平台文件查询服务Provider
 * @author hudong
 * @date 2023-09-08 09:12:49
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "SystemFileQueryProvider")
public interface SystemFileQueryProvider {

	/**
	 * 分页查询平台文件API
	 *
	 * @author hudong
	 * @param systemFilePageRequest 分页请求参数和筛选对象 {@link SystemFilePageRequest}
	 * @return 平台文件分页列表信息 {@link SystemFilePageResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/systemfile/page")
	BaseResponse<SystemFilePageResponse> page(@RequestBody @Valid SystemFilePageRequest systemFilePageRequest);

	/**
	 * 列表查询平台文件API
	 *
	 * @author hudong
	 * @param systemFileListRequest 列表请求参数和筛选对象 {@link SystemFileListRequest}
	 * @return 平台文件的列表信息 {@link SystemFileListResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/systemfile/list")
	BaseResponse<SystemFileListResponse> list(@RequestBody @Valid SystemFileListRequest systemFileListRequest);

	/**
	 * 单个查询平台文件API
	 *
	 * @author hudong
	 * @param systemFileByIdRequest 单个查询平台文件请求参数 {@link SystemFileByIdRequest}
	 * @return 平台文件详情 {@link SystemFileByIdResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/systemfile/get-by-id")
	BaseResponse<SystemFileByIdResponse> getById(@RequestBody @Valid SystemFileByIdRequest
                                                            systemFileByIdRequest);

	/**
	 * 单个查询平台文件API
	 *
	 * @author hudong
	 * @param systemFileByFileKeyRequest 单个查询平台文件请求参数 {@link SystemFileByFileKeyRequest}
	 * @return 平台文件详情 {@link SystemFileByIdResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/systemfile/get-by-fileKey")
	BaseResponse<SystemFileByIdResponse> getByFileKey(@RequestBody @Valid SystemFileByFileKeyRequest
														 systemFileByFileKeyRequest);

}

