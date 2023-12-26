package com.wanmi.sbc.setting.api.provider.publishInfo;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.publishInfo.PublishInfoRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 信息发布API
 * @Author: lwp
 * @Version: 1.0
 */
@FeignClient(value = "${application.setting.name}",url="${feign.url.setting:#{null}}", contextId = "PublishInfoProvider")
public interface PublishInfoProvider {


    /**
     * 新增信息发布
     *
     */
    @PostMapping("/setting/${application.setting.version}/publishinfo/add")
    BaseResponse addPublishInfo(@RequestBody PublishInfoRequest request);


}
