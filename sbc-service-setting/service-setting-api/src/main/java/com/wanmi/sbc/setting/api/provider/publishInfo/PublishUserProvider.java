package com.wanmi.sbc.setting.api.provider.publishInfo;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.publishInfo.PublishUserRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 信息发布用户API
 * @Author: lwp
 * @Version: 1.0
 */
@FeignClient(value = "${application.setting.name}",url="${feign.url.setting:#{null}}", contextId = "PublishUserProvider")
public interface PublishUserProvider {


    /**
     * 新增用户
     */
    @PostMapping("/setting/${application.setting.version}/publishuser/add")
    BaseResponse addPublishUser(@RequestBody PublishUserRequest request);


}
