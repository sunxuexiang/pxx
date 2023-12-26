package com.wanmi.sbc.live.rule.dao;

import com.wanmi.sbc.live.rule.model.root.LiveRule;
import com.wanmi.sbc.live.stream.model.root.LiveStreamLogInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author tools.49db.cn
 * @version 1.0
 * @date 2022-09-22
 */
@Repository
public interface LiveRuleMapper {

    int insertSelective(LiveRule record);

    int updateSelective(LiveRule record);

    LiveRule selectByType(@Param("type") Integer type,@Param("liveRoomId") Integer liveRoomId);
}