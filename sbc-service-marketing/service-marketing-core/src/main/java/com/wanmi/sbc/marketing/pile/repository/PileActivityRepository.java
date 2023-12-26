package com.wanmi.sbc.marketing.pile.repository;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.marketing.pile.model.root.PileActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: chenchang
 * @Date: 2022/09/06
 * @Description: 囤货活动Repository
 */
@Repository
public interface PileActivityRepository extends JpaRepository<PileActivity, String>,
        JpaSpecificationExecutor<PileActivity> {

    @Modifying
    @Query("update PileActivity a set a.terminationFlag = 1,a.updateTime = ?2 where a.terminationFlag = 0 and a.activityId = ?1 and a.delFlag=0")
    int close(String id, LocalDateTime now);

    @Query(value = "select * from pile_activity t  where t.store_id=?1 and (" +
            " (t.start_time>= ?2 and t.start_time<= ?3) " +
            " or (t.end_time>= ?2 and t.end_time<= ?3)  " +
            " or (t.start_time<= ?2 and t.end_time>= ?3)  " +
            " or (t.start_time>= ?2 and t.end_time<= ?3)  " +
            ") and t.activity_id != ?4 and del_flag=0 and termination_flag=0",nativeQuery = true)
    List<PileActivity> findByTimeSpan(Long storeId, LocalDateTime startTime, LocalDateTime endTime, String activityId);


    @Query(value = "select activity_id,activity_name,start_time,end_time,activity_type,public_virtual_stock,\n" +
            "store_id,force_pile_flag,termination_flag,del_flag,create_time,create_person,update_time,update_person,del_time,del_person,store_name " +
            "from pile_activity where activity_type = ?1 and termination_flag = 0 and start_time < now() and end_time > now() and del_flag = 0",nativeQuery = true)
    List<PileActivity> getStartPileActivity(Integer activityType);

    PileActivity findByActivityIdAndDelFlag(String activityId, DeleteFlag no);

    @Query(value = "select * from pile_activity where store_id=?1 and start_time > ?2 and del_flag = 0 and termination_flag=0 order by start_time asc limit 1",nativeQuery = true)
    PileActivity findRecentTimeByEndTime(Long storeId, LocalDateTime endTime);
}
