package com.wanmi.sbc.live.activity.dao;

import com.wanmi.sbc.live.activity.model.root.LiveStreamActivity;
import com.wanmi.sbc.live.api.request.activity.LiveStreamActivityInfoRequest;
import com.wanmi.sbc.live.api.request.activity.LiveStreamActivityListRequest;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LiveStreamActivityMapper {

    int insertSelective(LiveStreamActivity record);

    List<LiveStreamActivity> liveStreamActivityList(@Param("para") LiveStreamActivityListRequest request);

    LiveStreamActivity selectByPrimaryKey(LiveStreamActivityInfoRequest infoRequest);

    int updateByPrimaryKeySelective(LiveStreamActivity record);

    int updateAllByActivityId(String activityId);

    long countActivityByActivityId(@Param("liveRoomId")Integer liveRoomId,@Param("activityId")String activityId);

    int updateAllByLiveRoomId(Integer liveRoomId);
}
