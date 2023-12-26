package com.wanmi.sbc.setting.api.provider.publishInfo;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.publishInfo.PublishUserRequest;
import com.wanmi.sbc.setting.api.response.publishInfo.PublishUserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 信息发布用户查询API
 * @Author: lwp
 * @Version: 1.0
 */
@FeignClient(value = "${application.setting.name}",url="${feign.url.setting:#{null}}", contextId = "PublishUserQueryProvider")
public interface PublishUserQueryProvider {


    /**
     * 查询用户信息
     */
    @PostMapping("/setting/${application.setting.version}/publishuser/getPublishUser")
    BaseResponse<PublishUserResponse> getPublishUser(@RequestBody PublishUserRequest request);

}
