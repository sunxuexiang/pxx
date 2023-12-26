package com.wanmi.sbc.live.api.provider.stream;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.live.api.request.stream.IMAppRequest;
import com.wanmi.sbc.live.api.response.stream.IMAppResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * <p>直播聊天服务Provider</p>
 */
@FeignClient(value = "${application.live.name}", url="${feign.url.live:#{null}}",contextId = "ChatAppIMProvider")
public interface ChatAppIMProvider {
    /**
     * 前端获取签名（接口返回签名）
     * @param appRequest
     * @return
     */
    @PostMapping("/live/${application.live.version}/liveStream/getUserSig")
    BaseResponse<IMAppResponse> getUserSig(@RequestBody IMAppRequest appRequest);

}
