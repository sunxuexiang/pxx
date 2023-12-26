package com.wanmi.sbc.marketing.coupon.repository;

import com.wanmi.sbc.marketing.coupon.model.root.CoinActivityRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * 指定商品赠金币
 *
 * @Author : Like
 * @create 2023/5/22 9:27
 */

@Repository
public interface CoinActivityRecordRepository extends JpaRepository<CoinActivityRecord, String>, JpaSpecificationExecutor<CoinActivityRecord> {
    boolean existsByOrderNo(String orderNo);
    CoinActivityRecord findByOrderNoAndActivityId(String orderNo, String activityId);
    List<CoinActivityRecord> findBySendNo(String sendNo);
    List<CoinActivityRecord> findByOrderNo(String orderNo);
    List<CoinActivityRecord> findByRecordIdIn(Collection<Long> recordIds);

}
