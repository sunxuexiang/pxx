package com.wanmi.sbc.live.activity.dao;
import com.wanmi.sbc.live.activity.model.root.LiveBagLog;
import com.wanmi.sbc.live.api.request.stream.BagAppRequest;
import org.springframework.stereotype.Repository;

/**
 * @author tools.49db.cn
 * @version 1.0
 * @date 2022-09-20
 */
@Repository
public interface LiveBagLogMapper {

    int insertSelective(LiveBagLog record);

    LiveBagLog selectByPrimaryKey(Integer bagId);

    LiveBagLog selectByBagAndTicketStatus(BagAppRequest bagAppRequest);

    long countByExample(Integer bagId);

    int updateByPrimaryKeySelective(LiveBagLog record);

}