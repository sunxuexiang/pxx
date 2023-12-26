package com.wanmi.sbc.live.api.provider.room;

import javax.validation.Valid;

import com.wanmi.sbc.live.api.response.room.*;
import com.wanmi.sbc.live.bean.vo.LiveRoomVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.live.api.request.room.LiveRoomAddRequest;
import com.wanmi.sbc.live.api.request.room.LiveRoomModifyRequest;
import com.wanmi.sbc.live.api.request.room.LiveRoomPageRequest;
import com.wanmi.sbc.live.api.request.room.LiveStreamRoomListRequest;

import java.util.List;

/**
 * <p>直播间服务Provider</p>
 */
@FeignClient(value = "${application.live.name}", url="${feign.url.live:#{null}}",contextId = "LiveStreamRoomProvider")
public interface LiveStreamRoomProvider {

    /**
     * 保存直播间
     * @param addRequest
     * @return
     */
    @PostMapping("/room/${application.live.version}/liveRoom/supplier")
    BaseResponse add(@Valid @RequestBody LiveRoomAddRequest addRequest);

    /**
     * 更新直播间
     * @param modifyRequest
     * @return
     */
    @PostMapping("/room/${application.live.version}/liveRoom/modify")
    BaseResponse<List<LiveAccountNumResponse>> modify(@Valid @RequestBody LiveRoomModifyRequest modifyRequest);


    /**
     * 根据ID查询直播间
     * @param liveRoomId
     * @return
     */
    @PostMapping("/room/${application.live.version}/liveRoom/getInfo")
    BaseResponse<LiveRoomInfoResponse> getInfo(@Valid @RequestBody Long liveRoomId);

    /**
     * 查询直播间分页
     * @param pageRequest
     * @return
     */
    @PostMapping("/room/${application.live.version}/liveRoom/getPage")
    BaseResponse getPage(@Valid @RequestBody LiveRoomPageRequest pageRequest);

    /**
     * 根据主播账号id获取直播间列表
     * @param roomListRequest
     * @return
     */
    @PostMapping("/room/${application.live.version}/liveRoom/getLiveRoom")
    BaseResponse<LiveStreamRoomListResponse> getLiveRoomListByCustomerId(@Valid @RequestBody LiveStreamRoomListRequest roomListRequest);

    /**
     * 根据商家查询直播间列表
     * @param storeId
     * @return
     */
    @PostMapping("/room/${application.live.version}/liveRoom/getLiveRoomByStore")
    BaseResponse<List<LiveRoomVO>> getLiveRoomByStore(@RequestBody Long storeId);

    @PostMapping("/room/${application.live.version}/liveRoom/getExportData")
    BaseResponse<List<LiveDetailExportVo>> getExportData(@RequestBody LiveRoomPageRequest request);
}