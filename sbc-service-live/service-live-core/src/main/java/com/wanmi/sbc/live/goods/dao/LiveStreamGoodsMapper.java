package com.wanmi.sbc.live.goods.dao;

import com.wanmi.sbc.live.api.request.goods.LiveStreamGoodsListRequest;
import com.wanmi.sbc.live.api.request.goods.LiveStreamGoodsModifyRequest;
import com.wanmi.sbc.live.goods.model.root.LiveStreamGoods;
import com.wanmi.sbc.live.stream.model.root.LiveStream;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LiveStreamGoodsMapper {
    int insertSelective(LiveStreamGoods record);

    int updateByPrimaryKeySelective(LiveStreamGoods record);

    int updateAllByGoodsInfoId(LiveStreamGoodsModifyRequest liveStreamGoodsModifyRequest);

    Long countGoodsByGoodsInfoId(@Param("goodsInfoId") String goodsInfoId,@Param("liveRoomId")Integer liveRoomId);

    List<LiveStreamGoods> findListByReq(LiveStreamGoodsListRequest liveStreamGoodsListRequest);

    int updateAllByLiveRoomId(Integer liveRoomId);

    int updateBatchByLiveIdAndGoodsInfoIds(LiveStreamGoodsModifyRequest liveStreamGoodsModifyRequest);

    List<LiveStreamGoods> findByLiveRoomAndGoodsInfoId(@Param("liveRoomId") Long liveRoomId, @Param("goodsInfoId") String goodsInfoId);
}
