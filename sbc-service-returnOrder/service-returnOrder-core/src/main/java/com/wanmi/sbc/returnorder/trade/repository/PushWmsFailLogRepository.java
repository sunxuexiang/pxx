package com.wanmi.sbc.returnorder.trade.repository;

import com.wanmi.sbc.returnorder.trade.model.root.PushFailLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


/**
 * @author lm
 * @date 2022/11/19 16:13
 */
@Repository
public interface PushWmsFailLogRepository  extends JpaRepository<PushFailLog,String> {

    @Query(value = "select date_format(max(create_time),'%Y-%m-%d %H:%i:%S') as 'last' from push_fail_log",nativeQuery = true)
    String findLastPushTime();
}
