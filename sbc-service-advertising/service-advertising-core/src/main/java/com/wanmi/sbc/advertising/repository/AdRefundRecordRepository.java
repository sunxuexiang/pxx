package com.wanmi.sbc.advertising.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wanmi.sbc.advertising.model.AdRefundRecord;

/**
 * @author zc
 *
 */
@Repository
public interface AdRefundRecordRepository extends JpaRepository<AdRefundRecord, Integer> {

	public AdRefundRecord queryByRefundOrderId(String refundOrderId);

}
