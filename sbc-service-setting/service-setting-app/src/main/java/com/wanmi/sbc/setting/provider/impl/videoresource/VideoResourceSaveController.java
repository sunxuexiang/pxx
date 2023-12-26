package com.wanmi.sbc.setting.provider.impl.videoresource;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.provider.videoresource.VideoResourceSaveProvider;
import com.wanmi.sbc.setting.api.request.videoresource.*;
import com.wanmi.sbc.setting.api.response.videoresource.VideoResourceAddResponse;
import com.wanmi.sbc.setting.api.response.videoresource.VideoResourceModifyResponse;
import com.wanmi.sbc.setting.systemconfig.model.root.SystemConfig;
import com.wanmi.sbc.setting.systemconfig.service.SystemConfigService;
import com.wanmi.sbc.setting.videoresource.model.root.VideoResource;
import com.wanmi.sbc.setting.videoresource.service.VideoResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 视频教程资源库保存服务接口实现
 * @author hudong
 * @date 2023-06-26 09:17
 */
@RestController
@Validated
public class VideoResourceSaveController implements VideoResourceSaveProvider {
	@Autowired
	private VideoResourceService videoResourceService;

	@Autowired
	private SystemConfigService systemConfigService;

	@Override
	public BaseResponse<VideoResourceAddResponse> add(@RequestBody @Valid VideoResourceAddRequest videoResourceAddRequest) {
		VideoResource videoResource = new VideoResource();
		KsBeanUtil.copyPropertiesThird(videoResourceAddRequest, videoResource);
		return BaseResponse.success(new VideoResourceAddResponse(
				videoResourceService.wrapperVo(videoResourceService.add(videoResource))));
	}

	@Override
	public BaseResponse<VideoResourceAddResponse> saveResources(VideoResourceAddRequest videoResourceAddRequest) {
		// 查询可用云配置
		SystemConfig availableYun = systemConfigService.getAvailableYun();
		VideoResource videoResource = new VideoResource();
		videoResource.setStoreId(0L);
		videoResource.setCompanyInfoId(0L);
		videoResource.setCateId(videoResourceAddRequest.getCateId());
		videoResource.setResourceKey(videoResourceAddRequest.getResourceKey());
		videoResource.setArtworkUrl(videoResourceAddRequest.getArtworkUrl());
		videoResource.setResourceName(videoResourceAddRequest.getResourceName());
		videoResource.setCreateTime(LocalDateTime.now());
		videoResource.setUpdateTime(videoResource.getCreateTime());
		if (Objects.nonNull(availableYun)) {
			videoResource.setServerType(availableYun.getConfigType());
		}
		videoResource.setResourceType(videoResourceAddRequest.getResourceType());
		videoResource.setDelFlag(DeleteFlag.NO);
		videoResourceService.add(videoResource);
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse<VideoResourceModifyResponse> modify(@RequestBody @Valid VideoResourceModifyRequest videoResourceModifyRequest) {
		VideoResource videoResource = new VideoResource();
		KsBeanUtil.copyPropertiesThird(videoResourceModifyRequest, videoResource);
		return BaseResponse.success(new VideoResourceModifyResponse(
				videoResourceService.wrapperVo(videoResourceService.modify(videoResource))));
	}


	@Override
	public BaseResponse move(@RequestBody @Valid VideoResourceMoveRequest
									 moveRequest) {
		videoResourceService.updateCateByIds(moveRequest.getCateId(), moveRequest.getResourceIds(),moveRequest.getStoreId());
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse deleteById(@RequestBody @Valid VideoResourceDelByIdRequest videoResourceDelByIdRequest) {
		videoResourceService.deleteById(videoResourceDelByIdRequest.getResourceId());
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse deleteByIdList(@RequestBody @Valid VideoResourceDelByIdListRequest videoResourceDelByIdListRequest) {
		videoResourceService.delete(videoResourceDelByIdListRequest.getResourceIds(),
				videoResourceDelByIdListRequest.getStoreId());
		return BaseResponse.SUCCESSFUL();
	}

}

