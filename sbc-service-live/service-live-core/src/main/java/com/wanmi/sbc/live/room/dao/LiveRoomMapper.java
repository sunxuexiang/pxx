package com.wanmi.sbc.live.room.dao;

import com.wanmi.sbc.live.api.request.room.LiveRoomPageRequest;
import com.wanmi.sbc.live.room.model.root.LiveRoom;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>直播间Mapper</p>
 * @author liudan 刘丹（ldalc.com） Automatic Generator
 * @date 2022-09-16 19:58:39
 * @version 1.0
 * @package com.wanmi.sbc.live.room.dao
 */
@Repository
public interface LiveRoomMapper {

    /**
     * 分页查询
     * @param request
     * @return
     */
    List<LiveRoom> getPage(LiveRoomPageRequest request);

    /**
     * 查询数量
     * @param request
     * @return
     */
    int getPageCount(LiveRoomPageRequest request);

    /**
     * 获取信息
     * @param roomId
     * @return
     */
    LiveRoom getInfo(Long roomId);

    /**
     * 获取平台
     * @param sysFlag
     * @return
     */
    LiveRoom getSysInfo(Integer sysFlag);

    /**
     * 新增
     * @param request
     * @return
     */
    int add(LiveRoom request);

    /**
     * 修改
     * @param request
     * @return
     */
    int modify(LiveRoom request);


    /**
     * 主播账号id获取直播间
     * @param customerId
     * @return
     */
    List<LiveRoom> getLiveRoomListByCustomerId(String customerId);

    /**
     * 通过直播间id获取品牌
     * @param liveRoomId
     * @return
     */
    List<Long> getBrandIdsByLiveRoomId(Long liveRoomId);

    List<LiveRoom> getListByStoreId(Long storeId);
}