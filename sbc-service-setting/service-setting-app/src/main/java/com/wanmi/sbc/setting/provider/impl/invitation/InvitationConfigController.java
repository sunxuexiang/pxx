package com.wanmi.sbc.setting.provider.impl.invitation;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.provider.invitation.InvitationConfigProvider;
import com.wanmi.sbc.setting.api.provider.invitation.InvitationConfigRequest;
import com.wanmi.sbc.setting.api.provider.invitation.InvitationConfigResponse;
import com.wanmi.sbc.setting.api.provider.syssms.SysSmsSaveProvider;
import com.wanmi.sbc.setting.api.request.syssms.SysSmsAddRequest;
import com.wanmi.sbc.setting.api.request.syssms.SysSmsDelByIdListRequest;
import com.wanmi.sbc.setting.api.request.syssms.SysSmsDelByIdRequest;
import com.wanmi.sbc.setting.api.request.syssms.SysSmsModifyRequest;
import com.wanmi.sbc.setting.api.response.syssms.SmsSupplierRopResponse;
import com.wanmi.sbc.setting.invitation.InvitationConfigService;
import com.wanmi.sbc.setting.syssms.model.root.SysSms;
import com.wanmi.sbc.setting.syssms.service.SysSmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * <p>邀新街口实现</p>
 * @author lq
 * @date 2019-11-05 16:13:47
 */
@RestController
@Validated
public class InvitationConfigController implements InvitationConfigProvider {

	@Autowired
	private InvitationConfigService invitationConfigService;

	@Override
	public BaseResponse<InvitationConfigResponse> detail() {
		return BaseResponse.success(invitationConfigService.getDetail());
	}

	@Override
	public BaseResponse save(@RequestBody @Valid InvitationConfigRequest request) {
		invitationConfigService.save(request);
		return BaseResponse.SUCCESSFUL();
	}
}

