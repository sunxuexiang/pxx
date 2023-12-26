package com.wanmi.sbc.returnorder.returnextra.repository;

import com.wanmi.sbc.returnorder.returnextra.model.root.RefundExtraRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RefundExtraRecordRepository extends JpaRepository<RefundExtraRecord, Long>, JpaSpecificationExecutor<RefundExtraRecord> {
    RefundExtraRecord findByExtraId(String extraId);
    RefundExtraRecord findByExtraIdAndRefundStatus(String extraId, Integer refundStatus);

}