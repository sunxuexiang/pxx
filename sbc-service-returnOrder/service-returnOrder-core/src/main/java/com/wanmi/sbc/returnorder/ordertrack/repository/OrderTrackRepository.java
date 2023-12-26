package com.wanmi.sbc.returnorder.ordertrack.repository;

import com.wanmi.sbc.returnorder.ordertrack.root.OrderTrack;
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
public interface OrderTrackRepository extends JpaRepository<OrderTrack, Long>,
        JpaSpecificationExecutor<OrderTrack> {
    @Query("SELECT max(id) FROM OrderTrack where com = ?1 and num=?2")
    Long selectLogisticsNumNumber(String com,String num);

    @Query("FROM OrderTrack where com = ?1 and num=?2")
    List<OrderTrack> queryList(String com, String num);
}
