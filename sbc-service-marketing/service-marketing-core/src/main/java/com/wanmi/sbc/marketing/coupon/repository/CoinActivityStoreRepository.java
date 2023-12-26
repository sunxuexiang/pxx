package com.wanmi.sbc.marketing.coupon.repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.marketing.coupon.model.root.CoinActivityStore;


@Repository
public interface CoinActivityStoreRepository extends JpaRepository<CoinActivityStore, Long>, JpaSpecificationExecutor<CoinActivityStore> {

    @Query(value = "SELECT\r\n"
    		+ "	cag.store_id \r\n"
    		+ "FROM\r\n"
    		+ "	coin_activity_store cag\r\n"
    		+ "	LEFT JOIN coin_activity ca ON cag.activity_id = ca.activity_id \r\n"
    		+ "WHERE\r\n"
    		+ "	ca.del_flag = 0 \r\n"
    		+ "	AND ca.termination_flag = 0 \r\n"
    		+ " AND ca.start_time <= ?1 AND ca.end_time >= ?2"
    		+ "	AND ca.end_time > NOW() \r\n"
    		+ "	AND cag.termination_flag = 0 AND cag.store_id IN (?3)", nativeQuery = true)
    List<Long> getConflictStoreIds(LocalDateTime start, LocalDateTime end, List<Long> configStoreIds);


	List<CoinActivityStore> findByActivityId(String activityId);

    @Modifying
    @Query("update CoinActivityStore c set c.terminationFlag = ?1, c.terminationTime = ?2 where c.activityId = ?3")
	void updateTerminationInfo(BoolFlag yes, LocalDateTime now, String activityId);
    
    @Query(value = "SELECT count(*) storeCount FROM `coin_activity_store` WHERE activity_id=?1", nativeQuery = true)
    Integer queryStoreCount(String actId);

    List<CoinActivityStore> findByStoreNameLike(String storeName);
}
