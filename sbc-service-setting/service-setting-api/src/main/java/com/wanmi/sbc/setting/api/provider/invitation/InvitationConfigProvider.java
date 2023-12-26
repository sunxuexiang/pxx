package com.wanmi.sbc.setting.api.provider.invitation;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.ConfigQueryRequest;
import com.wanmi.sbc.setting.api.request.systemconfig.SystemConfigQueryRequest;
import com.wanmi.sbc.setting.api.response.systemconfig.LogisticsRopResponse;
import com.wanmi.sbc.setting.api.response.systemconfig.SystemConfigResponse;
import com.wanmi.sbc.setting.api.response.systemconfig.SystemConfigTypeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 *
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "InvitationConfigProvider")
public interface InvitationConfigProvider {

    @PostMapping("/setting/${application.setting.version}/invitationConfig/detail")
    BaseResponse<InvitationConfigResponse> detail();

    @PostMapping("/setting/${application.setting.version}/invitationConfig/save")
    BaseResponse save(@RequestBody @Valid InvitationConfigRequest request);


}
