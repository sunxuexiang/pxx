package com.wanmi.sbc.live.stream.dao;

import com.wanmi.sbc.live.stream.model.root.LiveStreamLogInfo;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author tools.49db.cn
 * @version 1.0
 * @date 2022-09-22
 */
@Repository
public interface LiveStreamLogInfoMapper {

    int insertSelective(LiveStreamLogInfo record);

    LiveStreamLogInfo selectByLiveId(Integer liveId);
}