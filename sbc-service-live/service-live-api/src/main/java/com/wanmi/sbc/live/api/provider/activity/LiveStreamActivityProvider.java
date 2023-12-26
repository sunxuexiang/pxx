package com.wanmi.sbc.live.api.provider.activity;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.live.api.request.activity.LiveStreamActivityAddRequest;
import com.wanmi.sbc.live.api.request.activity.LiveStreamActivityModifyRequest;
import com.wanmi.sbc.live.api.request.goods.LiveStreamGoodsAddRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * <p>直播活动保存服务Provider</p>
 */
@FeignClient(value = "${application.live.name}", url="${feign.url.live:#{null}}",contextId = "LiveStreamActivityProvider")
public interface LiveStreamActivityProvider {

    /**
     * supplier端保存直播活动
     * @param supplierAddReq
     * @return
     */
    @PostMapping("/live/${application.live.version}/liveStreamActivity/supplier")
    BaseResponse supplier(@RequestBody LiveStreamActivityAddRequest supplierAddReq);



    /**
     * supplier端更新直播活动
     * @param supplierModifyReq
     * @return
     */
    @PostMapping("/live/${application.live.version}/liveStreamActivity/modify")
    BaseResponse modify(@RequestBody LiveStreamActivityModifyRequest supplierModifyReq);
}
