package com.wanmi.sbc.live.activity.service;

import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.live.activity.dao.LiveStreamActivityMapper;
import com.wanmi.sbc.live.activity.model.root.LiveStreamActivity;
import com.wanmi.sbc.live.api.request.activity.LiveStreamActivityAddRequest;
import com.wanmi.sbc.live.api.request.activity.LiveStreamActivityInfoRequest;
import com.wanmi.sbc.live.api.request.activity.LiveStreamActivityListRequest;
import com.wanmi.sbc.live.api.request.activity.LiveStreamActivityModifyRequest;
import com.wanmi.sbc.live.bean.vo.LiveStreamActivityVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service("LiveStreamActivityService")
public class LiveStreamActivityService {
    @Autowired
    private LiveStreamActivityMapper liveStreamActivityMapper;
    /**
     * 直播导入优惠卷活动
     */
    @Transactional
    public void addActivity(LiveStreamActivityAddRequest request) {
        List<String> activityIds = request.getActivityIds();
        Integer liveRoomId = request.getLiveRoomId();
        activityIds.forEach(activityId -> {
            LiveStreamActivity convert =new LiveStreamActivity();
            convert.setLiveRoomId(liveRoomId);
            convert.setActivityId(activityId);
            convert.setCreateTime(new Date());
            convert.setUpdateTime(new Date());
            //判断优惠劵活动是否已经添加到直播间
            if(liveStreamActivityMapper.countActivityByActivityId(liveRoomId,activityId)==0){
                liveStreamActivityMapper.insertSelective(convert);
            }
        });
    }

    /**
     * 获取优惠卷活动列表
     * @return
     */
    public List<LiveStreamActivityVO> getActivity(LiveStreamActivityListRequest request){
        List<LiveStreamActivity> liveStreamGoodsList=liveStreamActivityMapper.liveStreamActivityList(request);
        List<LiveStreamActivityVO> liveStreamActivityVOList=KsBeanUtil.copyListProperties(liveStreamGoodsList, LiveStreamActivityVO.class);
        return liveStreamActivityVOList;
    }

    /**
     * 更新直播活动
     * @param request
     */
    public void modifyActivity(LiveStreamActivityModifyRequest request){
        LiveStreamActivity convert =new LiveStreamActivity();
        convert.setLiveRoomId(request.getLiveRoomId());
        convert.setActivityId(request.getActivityId());
        convert.setDelFlag(1);
        liveStreamActivityMapper.updateByPrimaryKeySelective(convert);
    }

    /**
     * 获取优惠卷活动详情
     * @return
     */
    public LiveStreamActivityVO getActivityInfo(LiveStreamActivityInfoRequest infoRequest){
        LiveStreamActivity streamActivity=liveStreamActivityMapper.selectByPrimaryKey(infoRequest);
        LiveStreamActivityVO convert=KsBeanUtil.convert(streamActivity,LiveStreamActivityVO.class);
        return convert;
    }

}
