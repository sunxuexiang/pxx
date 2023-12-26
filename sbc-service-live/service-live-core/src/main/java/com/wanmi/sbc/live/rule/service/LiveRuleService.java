package com.wanmi.sbc.live.rule.service;

import com.wanmi.sbc.live.api.request.rule.LiveRuleAddRequest;
import com.wanmi.sbc.live.api.request.rule.LiveRuleInfoRequest;
import com.wanmi.sbc.live.redis.CacheKeyConstant;
import com.wanmi.sbc.live.redis.RedisService;
import com.wanmi.sbc.live.rule.dao.LiveRuleMapper;
import com.wanmi.sbc.live.rule.model.root.LiveRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
public class LiveRuleService {
    @Autowired
    private LiveRuleMapper liveRuleMapper;

    @Autowired
    private RedisService redisService;
    /**
     * 创建规则
     * @param addRequest
     */
    public void createRule(LiveRuleAddRequest addRequest){
        LiveRule liveRule=liveRuleMapper.selectByType(addRequest.getType(),addRequest.getLiveRoomId());
        if(Objects.nonNull(liveRule)){
            liveRule.setBeginNum(addRequest.getBeginNum());
            liveRule.setCoefficient(addRequest.getCoefficient());
            liveRule.setFixed(addRequest.getFixed());
            liveRule.setType(addRequest.getType());
            liveRule.setLiveRoomId(addRequest.getLiveRoomId());
            liveRuleMapper.updateSelective(liveRule);
        }else{
            liveRule=new LiveRule();
            liveRule.setBeginNum(addRequest.getBeginNum());
            liveRule.setCoefficient(addRequest.getCoefficient());
            liveRule.setFixed(addRequest.getFixed());
            liveRule.setType(addRequest.getType());
            liveRule.setLiveRoomId(addRequest.getLiveRoomId());
            liveRuleMapper.insertSelective(liveRule);
        }
        redisService.setString(CacheKeyConstant.LIVE_RULE_COE+addRequest.getLiveRoomId()+":"+addRequest.getType(),addRequest.getCoefficient(),0);
        redisService.setString(CacheKeyConstant.LIVE_RULE_FIX+addRequest.getLiveRoomId()+":"+addRequest.getType(),addRequest.getFixed()+"",0);
    }

    /**
     * 获取规则信息
     * @param infoRequest
     * @return
     */
    public LiveRule getRuleInfo(LiveRuleInfoRequest infoRequest){
        return liveRuleMapper.selectByType(infoRequest.getType(),infoRequest.getLiveRoomId());
    }
}
