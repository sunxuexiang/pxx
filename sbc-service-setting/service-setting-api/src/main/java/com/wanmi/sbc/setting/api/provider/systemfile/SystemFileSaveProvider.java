package com.wanmi.sbc.setting.api.provider.systemfile;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.systemfile.*;
import com.wanmi.sbc.setting.api.response.systemfile.SystemFileAddResponse;
import com.wanmi.sbc.setting.api.response.systemfile.SystemFileModifyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * 平台文件保存服务Provider
 * @author hudong
 * @date 2023-09-08 09:12:49
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "SystemFileSaveProvider")
public interface SystemFileSaveProvider {

	/**
	 * 新增平台文件API
	 *
	 * @author hudong
	 * @param systemFileAddRequest 平台文件新增参数结构 {@link SystemFileAddRequest}
	 * @return 新增的平台文件信息 {@link SystemFileAddResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/systemfile/add")
	BaseResponse<SystemFileAddResponse> add(@RequestBody @Valid SystemFileAddRequest systemFileAddRequest);

	/**
	 * 修改平台文件API
	 *
	 * @author hudong
	 * @param systemFileModifyRequest 平台文件修改参数结构 {@link SystemFileModifyRequest}
	 * @return 修改的平台文件信息 {@link SystemFileModifyResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/systemfile/modify")
	BaseResponse<SystemFileModifyResponse> modify(@RequestBody @Valid SystemFileModifyRequest
															 systemFileModifyRequest);

	/**
	 * 单个删除平台文件API
	 *
	 * @author hudong
	 * @param systemFileDelByIdRequest 单个删除参数结构 {@link SystemFileDelByIdRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/systemfile/delete-by-id")
	BaseResponse deleteById(@RequestBody @Valid SystemFileDelByIdRequest systemFileDelByIdRequest);

	/**
	 * 移动平台文件API
	 *
	 * @author hudong
	 * @param moveRequest 平台文件修改参数结构  {@link SystemFileMoveRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/systemfile/move")
	BaseResponse move(@RequestBody @Valid SystemFileMoveRequest
									moveRequest);

	/**
	 * 批量删除平台文件API
	 *
	 * @author hudong
	 * @param systemFileDelByIdListRequest 批量删除参数结构 {@link SystemFileDelByIdListRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/systemfile/delete-by-id-list")
	BaseResponse deleteByIdList(@RequestBody @Valid SystemFileDelByIdListRequest systemFileDelByIdListRequest);

}

