package com.wanmi.sbc.marketing.coupon.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.wanmi.sbc.marketing.coupon.model.root.CoinActivityStoreRecordDetail;


@Repository
public interface CoinActivityStoreRecordDetailRepository extends JpaRepository<CoinActivityStoreRecordDetail, Long>, JpaSpecificationExecutor<CoinActivityStoreRecordDetail> {
  
    @Query(value = "SELECT * FROM `coin_activity_store_record_detail` WHERE order_no=?1 AND record_type=1 AND goods_info_id=?2", nativeQuery = true)
    CoinActivityStoreRecordDetail querySkuSendRecord(String orderNo, String skuId);
    
    @Query(value = "SELECT * FROM `coin_activity_store_record_detail` WHERE order_no=?1 AND record_type=1", nativeQuery = true)
    List<CoinActivityStoreRecordDetail> querySendRecord(String orderNo);
}
