package com.wanmi.sbc.live.api.provider.stream;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.live.api.request.stream.BagAppRequest;
import com.wanmi.sbc.live.api.request.stream.IMAppRequest;
import com.wanmi.sbc.live.api.request.stream.LiveStreamAddRequest;
import com.wanmi.sbc.live.api.request.stream.LiveStreamSetRequest;
import com.wanmi.sbc.live.api.response.stream.BagAppResponse;
import com.wanmi.sbc.live.api.response.stream.IMAppResponse;
import com.wanmi.sbc.live.bean.vo.LiveStreamVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * <p>直播保存服务Provider</p>
 */
@FeignClient(value = "${application.live.name}", url="${feign.url.live:#{null}}",contextId = "LiveStreamProvider")
public interface LiveStreamProvider {
    /**
     * 创建直播间
     * @param mobileAddReq
     * @return
     */
    @PostMapping("/live/${application.live.version}/liveStream/createRoom")
    BaseResponse createRoom(@RequestBody LiveStreamAddRequest mobileAddReq);


    /**
     * 更新直播间
     * @param mobileAddReq
     * @return
     */
    @PostMapping("/live/${application.live.version}/liveStream/updateRoom")
    BaseResponse updateRoom(@RequestBody LiveStreamAddRequest mobileAddReq);

    /**
     * im前端获取签名
     * @param imAppRequest
     * @return
     */
    @PostMapping("/live/${application.live.version}/liveStream/getTxCloudUserSig")
    BaseResponse<IMAppResponse> getTxCloudUserSig(@RequestBody IMAppRequest imAppRequest);

    /**
     * im发送群系统消息
     * @param imAppRequest
     * @return
     */
    @PostMapping("/live/${application.live.version}/liveStream/sendGroupSystemNotification")
    BaseResponse sendGroupSystemNotification(@RequestBody IMAppRequest imAppRequest);


    /**
     * 设置加购、立购、领取优惠卷人数
     * @param streamSetRequest
     * @return
     */
    @PostMapping("/live/${application.live.version}/liveStream/set")
    BaseResponse set(@RequestBody LiveStreamSetRequest streamSetRequest);

    /**
     * 断流通知
     * @param streamSetRequest
     * @return
     */
    @PostMapping("/live/${application.live.version}/liveStream/cutoffStream")
    BaseResponse cutoffStream(@RequestBody LiveStreamSetRequest streamSetRequest);

    /**
     * 更新直播间在线人数
     * @param live
     * @return
     */
    @PostMapping("/live/${application.live.version}/liveStream/updateLiveStreamPeopleNum")
    BaseResponse updateLiveStreamPeopleNum(@RequestBody LiveStreamVO live);

    @PostMapping("/live/${application.live.version}/liveStream/deleteLiveStream")
    BaseResponse deleteLiveStream(@RequestBody Integer liveId);
}
