package com.wanmi.sbc.live.api.provider.stream;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.live.api.request.stream.LiveStreamInfoRequest;
import com.wanmi.sbc.live.api.request.stream.LiveStreamPageRequest;
import com.wanmi.sbc.live.api.response.stream.*;
import com.wanmi.sbc.live.bean.vo.LiveStreamVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * <p>直播查询服务Provider</p>
 */
@FeignClient(value = "${application.live.name}", url="${feign.url.live:#{null}}",contextId = "LiveStreamQueryProvider")
public interface LiveStreamQueryProvider {

    @PostMapping("/live/${application.live.version}/liveStream/listPage")
    BaseResponse<LiveStreamPageResponse> listPage(@RequestBody @Valid LiveStreamPageRequest liveStreamListReq);


    @PostMapping("/live/${application.live.version}/liveStream/streamInfo")
    BaseResponse<LiveStreamInfoResponse> streamInfo(@RequestBody  LiveStreamInfoRequest liveStreamInfoReq);

    /**
     * 直播广场
     * @param liveStreamListReq
     * @return
     */
    @PostMapping("/live/${application.live.version}/liveStream/liveBroadcastSquare")
    BaseResponse<LiveStreamPageResponse> liveBroadcastSquare(@RequestBody @Valid LiveStreamPageRequest liveStreamListReq);

    /**
     * 直播记录商品，优惠劵活动id
     * @param liveStreamInfoReq
     * @return
     */
    @PostMapping("/live/${application.live.version}/liveStream/streamLogInfo")
    BaseResponse<LiveStreamLogInfoResponse> streamLogInfo(@RequestBody LiveStreamPageRequest liveStreamInfoReq);

    /**
     * 正在直播商品直播间
     * @param liveStreamInfoReq
     * @return
     */
    @PostMapping("/live/${application.live.version}/liveStream/goodsLiveInfo")
    BaseResponse<LiveHaveResponse> goodsLiveInfo(@RequestBody  LiveStreamInfoRequest liveStreamInfoReq);

    /**
     * 商家直播间列表
     * @param requestParam
     * @return
     */
    @PostMapping("/live/${application.live.version}/liveStream/getStoreLiveList")
    BaseResponse<LiveStreamPageResponse> getStoreLiveList(@RequestBody LiveStreamPageRequest requestParam);

    /**
     * 所有直播间
     * @param requestParam
     * @return
     */
    @PostMapping("/live/${application.live.version}/liveStream/getAllLiveList")
    BaseResponse<LiveStreamPageResponse> getAllLiveList(LiveStreamPageRequest requestParam);

    /**
     * 结束直播获取直播营销统计数据
     * @param liveStreamInfoReq
     * @return
     */
    @PostMapping("/live/${application.live.version}/liveStream/finishLive")
    BaseResponse<LiveStreamInfoResponse> finishLive(@RequestBody  LiveStreamInfoRequest liveStreamInfoReq);

    /**
     * 查询正在直播中的商家ID
     * @return
     */
    @PostMapping("/live/${application.live.version}/liveStream/getLiveStoreId")
    BaseResponse<List<Long>> getLiveStoreId();

    /**
     * 查询正在直播中的商家ID
     * @return
     */
    @PostMapping("/live/${application.live.version}/liveStream/getLiveStoreInfo")
    BaseResponse<Map<Long, List<StoreLiveStreamResponse>>> getLiveStoreInfo();

    @PostMapping("/live/${application.live.version}/liveStream/getLiveRomeEditInfo")
    BaseResponse<LiveStreamVO> getLiveRomeEditInfo(@RequestBody LiveStreamInfoRequest streamInfoRequest);
}
