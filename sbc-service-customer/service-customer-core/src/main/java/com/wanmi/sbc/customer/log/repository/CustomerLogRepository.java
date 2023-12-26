package com.wanmi.sbc.customer.log.repository;

import com.wanmi.sbc.customer.log.model.root.CustomerLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description  
 * @author  shiy
 * @date    2023/4/7 11:00
 * @params  
 * @return  
*/
@Repository
public interface CustomerLogRepository extends JpaRepository<CustomerLog, Long>,
        JpaSpecificationExecutor<com.wanmi.sbc.customer.log.model.root.CustomerLog> {

    /**
     * 全量版本更新记录
     *
     */
    @Query("from CustomerLog c where c.logType = 2 and c.createTime>=?1 and c.createTime<=?2")
    List<CustomerLog> findAllVersionUpdateLog(LocalDateTime createTimeBegin,LocalDateTime createTimeEnd);

    @Query(value = "select * from customer_log t where t.user_no=?1 and t.app_type=?2 and t.create_time>'2023-04-01' order by t.id desc limit 1",nativeQuery = true)
    CustomerLog findLatestLoginRecord(String userNo,Integer appType);

    /**
     * 全量版本更新记录
     *
     */
    @Query("from CustomerLog c where c.logType = 2 and c.appType=?1 and c.userNo=?2")
    List<CustomerLog> findUpdateLogByUserInfo(Integer logType,String userNo);

    /**
     * 全量版本更新记录
     *
     */
    @Query("from CustomerLog c where c.logType = 2 and c.userNo = ?1")
    List<CustomerLog> findAllVersionUpdateLogByUserNo(String userNo);
}
