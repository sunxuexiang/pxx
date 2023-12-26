package com.wanmi.sbc.marketing.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.wanmi.sbc.marketing.coupon.model.root.CoinActivityStoreRecord;



@Repository
public interface CoinActivityStoreRecordRepository extends JpaRepository<CoinActivityStoreRecord, Long>, JpaSpecificationExecutor<CoinActivityStoreRecord> {

	boolean existsByOrderNo(String tid);
	
	CoinActivityStoreRecord findByOrderNo(String orderNo);
  
	

}
