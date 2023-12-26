package com.wanmi.sbc.advertisingnew;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.wanmi.sbc.advertising.api.provider.AdActivityProvider;
import com.wanmi.sbc.advertising.api.request.activity.ActGetByIdRequest;
import com.wanmi.sbc.advertising.api.request.activity.ActQueryListPageRequest;
import com.wanmi.sbc.advertising.bean.constant.AdConstants;
import com.wanmi.sbc.advertising.bean.dto.AdActivityDTO;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.GeneratorService;
import com.wanmi.sbc.setting.api.provider.yunservice.YunServiceProvider;
import com.wanmi.sbc.setting.api.request.yunservice.YunUploadResourceRequest;
import com.wanmi.sbc.util.CommonUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zc
 *
 */
@Slf4j
@Api(description = "广告相关功能API", tags = "AdvertisingNewBaseController")
@RestController
@RequestMapping(value = "advertising")
public class AdvertisingNewBaseController {
	
	@Autowired
	private YunServiceProvider yunServiceProvider;

	@Autowired
	private GeneratorService generatorService;
	
	@Autowired
	private CommonUtil commonUtil;
	
	@Autowired
	private AdActivityProvider adActivityProvider;

	
	@ApiOperation(value = "上传文件")
	@PostMapping("/uploadFile")
	BaseResponse<String> uploadFile(MultipartFile file) throws IOException {
		if (file != null && file.getSize() > 0) {
			String fileKey = getFileKey(file);
			BaseResponse<String> justUploadFile = yunServiceProvider
					.justUploadFile(YunUploadResourceRequest.builder().resourceKey(fileKey).content(file.getBytes()).build());
			log.info("广告相关文件上传oss成功[{}]", justUploadFile.getContext());
			return BaseResponse.success(justUploadFile.getContext());
		}
		throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "上传的文件不正确");
	}

	private String getFileKey(MultipartFile file) {
		String originalFilename = file.getOriginalFilename();
		String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
		String fileKey = generatorService.generate(AdConstants.AD_FILE_PREFIX);
		return fileKey + fileExtension;
	}
	
	@ApiOperation(value = "分页查询广告活动")
	@PostMapping("/adActivity/queryListPage")
	BaseResponse<MicroServicePage<AdActivityDTO>> querySupplierActivityListPage(@RequestBody ActQueryListPageRequest request) {
		request.setStoreId(commonUtil.getStoreId());
		return adActivityProvider.queryListPage(request);
	}
	
	@ApiOperation(value = "查询单个广告活动")
	@GetMapping("/adActivity/getById")
	BaseResponse<AdActivityDTO> getActivityById(ActGetByIdRequest request) {
		return adActivityProvider.queryOne(request);
	}


}
