package com.wanmi.sbc.pay.repository;

import com.wanmi.sbc.pay.model.root.CcbRefundRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CcbRefundRecordRepository extends JpaRepository<CcbRefundRecord, Long>, JpaSpecificationExecutor<CcbRefundRecord> {
    List<CcbRefundRecord> findByTidAndRefundStatus(String tid, Integer refundStatus);
    CcbRefundRecord findByRidAndRefundStatus(String rid, Integer refundStatus);
    @Transactional
    @Modifying
    @Query("update CcbRefundRecord c set c.refundStatus = ?1 where c.rid = ?2")
    int updateRefundStatusByRid(Integer refundStatus, String rid);
    CcbRefundRecord findByRid(String rid);

}