package com.wanmi.sbc.live.stream.dao;

import com.wanmi.sbc.live.api.request.room.LiveRoomPageRequest;
import com.wanmi.sbc.live.api.request.stream.LiveStreamInfoRequest;
import com.wanmi.sbc.live.api.request.stream.LiveStreamPageRequest;
import com.wanmi.sbc.live.bean.vo.LiveHaveGoodsVO;
import com.wanmi.sbc.live.stream.model.root.LiveStream;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LiveStreamMapper {
    int insertSelective(LiveStream record);

    LiveStream selectByPrimaryKey(Integer liveId);

    LiveStream selectByInfoRequest(@Param("record")LiveStreamInfoRequest infoRequest);

    int updateByPrimaryKeySelective(@Param("record")LiveStream record);

    List<LiveStream> pageListByLiveStreamReq(@Param("record") LiveStreamPageRequest record);

    int pageCountByLiveStreamReq(@Param("record") LiveStreamPageRequest record);

    /**
     * 直播广场
     * @param record
     * @return
     */
    List<LiveStream> liveBroadcastSquare(@Param("record") LiveStreamPageRequest record);

    int pageCountLiveBroadcastSquare(@Param("record") LiveStreamPageRequest record);

    /**
     * 获取平台直播最新
     * @param infoRequest
     * @return
     */
    LiveStream selectBySysRequest(@Param("record")LiveStreamInfoRequest infoRequest);

    /**
     * 商品查正在直播商品的直播间
     * @param goodsInfoId
     * @return
     */
    List<LiveHaveGoodsVO> findLiveByGoodsInfoId(@Param("goodsInfoId") String goodsInfoId);

    /**
     * 按店铺查询直播间列表
     * @param requestParam
     * @return
     */
    List<LiveStream> getStoreLiveList(@Param("record") LiveStreamPageRequest requestParam);

    Integer countStoreLiveList(@Param("record") LiveStreamPageRequest requestParam);

    List<Long> selectLiveStoreIds();

    List<LiveStream> selectLivingRomeList();

    Integer deleteByLiveId(Integer liveId);

    LiveStream getLiveStreamEditInfoByRoomId(Integer liveRoomId);

    List<Long> selectLiveRoomIdByLiveTime(@Param("startTime") String startTime, @Param("endTime") String endTime);

    List<LiveStream> selectByLiveTime(@Param("storeId") Long storeId, @Param("liveRoomIdList") List<Long> liveRoomIdList, @Param("startTime") String startTime, @Param("endTime") String endTime);

    List<LiveStream> selectDetailByLiveTime(@Param("startTime") String startTime, @Param("endTime") String endTime);

    List<LiveStream> selectLastLiveByRoomIds(@Param("roomIdList") List<Long> roomIdList);
}
