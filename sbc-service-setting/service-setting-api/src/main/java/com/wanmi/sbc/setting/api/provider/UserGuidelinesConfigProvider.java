package com.wanmi.sbc.setting.api.provider;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.EmailConfigModifyRequest;
import com.wanmi.sbc.setting.api.request.UserGuidelinesConfigModifyRequest;
import com.wanmi.sbc.setting.api.response.EmailConfigQueryResponse;
import com.wanmi.sbc.setting.api.response.UserGuidelinesConfigQueryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "UserGuidelinesConfigProvider")
public interface UserGuidelinesConfigProvider {

    /**
     * 查询BOSS管理后台邮箱接口配置
     */
    @PostMapping("/setting/${application.setting.version}/userGuidelines/query-user-guidelines-config")
    BaseResponse<UserGuidelinesConfigQueryResponse> queryUserGuidelinesConfig();

    /**
     * 更新邮箱接口配置
     *
     * @param request {@link EmailConfigModifyRequest}
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/userGuidelines/modify-user-guidelines-config")
    BaseResponse modifyUserGuidelinesConfig(@RequestBody UserGuidelinesConfigModifyRequest request);

}
