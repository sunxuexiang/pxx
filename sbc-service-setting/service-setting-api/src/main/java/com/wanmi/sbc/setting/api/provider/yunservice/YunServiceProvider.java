package com.wanmi.sbc.setting.api.provider.yunservice;

import javax.validation.Valid;

import com.wanmi.sbc.setting.api.request.yunservice.*;
import com.wanmi.sbc.setting.api.response.yunservice.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.wanmi.sbc.common.base.BaseResponse;

/**
 * <p>
 * 系统配置表查询服务Provider
 * </p>
 *
 * @author yang
 * @date 2019-11-05 18:33:04
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "YunServiceProvider")
public interface YunServiceProvider {

	/**
	 * 列表查询云配置信息
	 *
	 * @param yunConfigListRequest 列表请求参数和筛选对象 {@link YunConfigListRequest}
	 * @return 云系统配置表的列表信息 {@link YunConfigListResponse}
	 * @author yang
	 */
	@PostMapping("/setting/${application.setting.version}/yunconfig/list")
	BaseResponse<YunConfigListResponse> list(@RequestBody @Valid YunConfigListRequest yunConfigListRequest);

	/**
	 * 根据id查询云配置信息
	 *
	 * @param yunConfigByIdRequest 请求参数和筛选对象 {@link YunConfigByIdRequest}
	 * @return 云系统配置表的列表信息 {@link YunConfigResponse}
	 * @author yang
	 */
	@PostMapping("/setting/${application.setting.version}/yunconfig/get-by-id")
	BaseResponse<YunConfigResponse> getById(@RequestBody @Valid YunConfigByIdRequest yunConfigByIdRequest);

	/**
	 * 查询可用云配置信息
	 *
	 * @return 云系统配置表的列表信息 {@link YunConfigResponse}
	 * @author yang
	 */
	@PostMapping("/setting/${application.setting.version}/yunconfig/get-available-yun")
	BaseResponse<YunAvailableConfigResponse> getAvailableYun();

	/**
	 * 修改云配置信息
	 *
	 * @param yunConfigModifyRequest 修改云配置信息参数结构 {@link YunConfigModifyRequest}
	 * @return 修改云配置信息 {@link YunConfigResponse}
	 * @author yang
	 */
	@PostMapping("/setting/${application.setting.version}/yunconfig/modify")
	BaseResponse<YunConfigResponse> modify(@RequestBody @Valid YunConfigModifyRequest yunConfigModifyRequest);

	/**
	 * 上传文件
	 * @param yunUploadResourceRequest
	 * @return
	 */
	@PostMapping("/setting/${application.setting.version}/yunconfig/just-upload-resource")
	BaseResponse<String> justUploadFile(@RequestBody @Valid YunUploadResourceRequest yunUploadResourceRequest);

	/**
	 * 上传文件
	 *
	 * @param yunUploadResourceRequest 上传文件 {@link YunUploadResourceRequest}
	 * @return 上传文件信息 {@link String}
	 * @author yang
	 */
	@PostMapping("/setting/${application.setting.version}/yunconfig/upload-resource")
	BaseResponse<String> uploadFile(@RequestBody @Valid YunUploadResourceRequest yunUploadResourceRequest);

	/**
	 * 上传视频教程文件
	 *
	 * @param yunUploadVideoResourceRequest 上传文件 {@link YunUploadVideoResourceRequest}
	 * @return 上传视频教程文件信息 {@link String}
	 * @author hudong
	 */
	@PostMapping("/setting/${application.setting.version}/yunconfig/upload-video-resource")
	BaseResponse<String> uploadVideoFile(@RequestBody @Valid YunUploadVideoResourceRequest yunUploadVideoResourceRequest);
	/**
	 * 上传Excl文件
	 *
	 * @param yunUploadVideoResourceRequest 上传文件 {@link YunUploadVideoResourceRequest}
	 * @return 上传视频教程文件信息 {@link String}
	 * @author hudong
	 */
	@PostMapping("/setting/${application.setting.version}/yunconfig/upload-excl-resource")
	BaseResponse<String> uploadExclFile(@RequestBody @Valid YunUploadVideoResourceRequest yunUploadVideoResourceRequest);
	/**
	 * 获取文件
	 *
	 * @param yunGetResourceRequest 获取文件 {@link YunGetResourceRequest}
	 * @return 获取文件 {@link YunGetResourceResponse}
	 * @author yang
	 */
	@PostMapping("/setting/${application.setting.version}/yunconfig/get-resource")
	BaseResponse<YunGetResourceResponse> getFile(@RequestBody @Valid YunGetResourceRequest yunGetResourceRequest);

	/**
	 * 获取地址
	 * @return 获取地址 {@link String}
	 * @author hudong
	 */
	@GetMapping("/setting/${application.setting.version}/yunconfig/get-oss-token")
	BaseResponse<YunParamResponse> getOssToken();

}
