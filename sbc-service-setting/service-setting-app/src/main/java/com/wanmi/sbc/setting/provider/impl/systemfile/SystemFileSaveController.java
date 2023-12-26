package com.wanmi.sbc.setting.provider.impl.systemfile;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.provider.systemfile.SystemFileSaveProvider;
import com.wanmi.sbc.setting.api.request.systemfile.*;
import com.wanmi.sbc.setting.api.response.systemfile.SystemFileAddResponse;
import com.wanmi.sbc.setting.api.response.systemfile.SystemFileModifyResponse;
import com.wanmi.sbc.setting.systemfile.model.root.SystemFile;
import com.wanmi.sbc.setting.systemfile.service.SystemFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * 平台文件保存服务接口实现
 * @author hudong
 * @date 2023-09-08 09:17
 */
@RestController
@Validated
public class SystemFileSaveController implements SystemFileSaveProvider {
	@Autowired
	private SystemFileService systemFileService;

	@Override
	public BaseResponse<SystemFileAddResponse> add(SystemFileAddRequest systemFileAddRequest) {
		SystemFile systemFile = new SystemFile();
		KsBeanUtil.copyPropertiesThird(systemFileAddRequest, systemFile);
		return BaseResponse.success(new SystemFileAddResponse(
				systemFileService.wrapperVo(systemFileService.add(systemFile))));
	}

	@Override
	public BaseResponse<SystemFileModifyResponse> modify(SystemFileModifyRequest systemFileModifyRequest) {
		SystemFile systemFile = new SystemFile();
		KsBeanUtil.copyPropertiesThird(systemFileModifyRequest, systemFile);
		return BaseResponse.success(new SystemFileModifyResponse(
				systemFileService.wrapperVo(systemFileService.modify(systemFile))));
	}

	@Override
	public BaseResponse deleteById(SystemFileDelByIdRequest systemFileDelByIdRequest) {
		systemFileService.deleteById(systemFileDelByIdRequest.getId());
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse move(SystemFileMoveRequest moveRequest) {
		systemFileService.updatePathByIds(moveRequest.getPath(), moveRequest.getIds());
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse deleteByIdList(SystemFileDelByIdListRequest systemFileDelByIdListRequest) {
		systemFileService.delete(systemFileDelByIdListRequest.getIds());
		return BaseResponse.SUCCESSFUL();
	}
}

