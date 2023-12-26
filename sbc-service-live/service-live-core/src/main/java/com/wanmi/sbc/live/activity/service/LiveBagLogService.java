package com.wanmi.sbc.live.activity.service;

import com.wanmi.sbc.live.activity.dao.LiveBagLogMapper;
import com.wanmi.sbc.live.activity.model.root.LiveBagLog;
import com.wanmi.sbc.live.api.request.stream.BagAppRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("LiveBagLogService")
public class LiveBagLogService {

    @Autowired
    private LiveBagLogMapper liveBagLogMapper;

    /**
     * 获取福袋抽奖记录
     * @param bagId
     * @return
     */
    public LiveBagLog getLiveBagInfo(Integer bagId){
        return liveBagLogMapper.selectByPrimaryKey(bagId);
    }

    /**
     * 更新福袋抽奖记录
     * @param liveBagLog
     * @return
     */
    public long updateLiveBagLog(LiveBagLog liveBagLog){
        return liveBagLogMapper.updateByPrimaryKeySelective(liveBagLog);
    }


    /**
     * 获取发送福袋最新记录
     * @param bagAppRequest
     * @return
     */
    public LiveBagLog selectByBagAndTicketStatus(BagAppRequest bagAppRequest){
        return liveBagLogMapper.selectByBagAndTicketStatus(bagAppRequest);
    }

}
