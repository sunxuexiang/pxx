package com.wanmi.sbc.setting.logisticstrail.repository;

import com.wanmi.sbc.setting.logisticstrail.root.LogisticsTrail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @desc  物流轨迹
 * @author shiy  2023/6/8 14:18
 */
@Repository
public interface LogisticsTrailRepository extends JpaRepository<com.wanmi.sbc.setting.logisticstrail.root.LogisticsTrail, Long>,
        JpaSpecificationExecutor<LogisticsTrail> {
    @Query("SELECT max(id) FROM LogisticsTrail where com = ?1 and num=?2")
    Long selectLogisticsNumNumber(String com,String num);

    @Query("FROM LogisticsTrail where com = ?1 and num=?2")
    List<LogisticsTrail> queryList(String com, String num);
}
