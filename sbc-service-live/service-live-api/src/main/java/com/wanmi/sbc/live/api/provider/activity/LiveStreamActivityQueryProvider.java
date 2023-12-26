package com.wanmi.sbc.live.api.provider.activity;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.live.api.request.activity.LiveStreamActivityInfoRequest;
import com.wanmi.sbc.live.api.request.activity.LiveStreamActivityListRequest;
import com.wanmi.sbc.live.api.response.activity.LiveStreamActivityInfoResponse;
import com.wanmi.sbc.live.api.response.activity.LiveStreamActivityListResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * <p>直播活动查询服务Provider</p>
 */
@FeignClient(value = "${application.live.name}", url="${feign.url.live:#{null}}",contextId = "LiveStreamActivityQueryProvider")
public interface LiveStreamActivityQueryProvider {

    /**
     * 列表查询直播活动API
     *
     * @return 直播商品的列表信息 {@link LiveStreamActivityListResponse}
     * @author zwb
     */
    @PostMapping("/live/${application.live.version}/liveStreamActivity/list")
     BaseResponse<LiveStreamActivityListResponse> list(@RequestBody LiveStreamActivityListRequest liveStreamActivityListRequest);

    @PostMapping("/live/${application.live.version}/liveStreamActivity/info")
    BaseResponse<LiveStreamActivityInfoResponse> activityInfo(@RequestBody LiveStreamActivityInfoRequest infoRequest);
}
