package com.wanmi.sbc.pay.repository;

import com.wanmi.sbc.pay.model.root.PayRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PayRecordRepository extends JpaRepository<PayRecord, Long>, JpaSpecificationExecutor<PayRecord> {
    PayRecord findByPayOrderNo(String payOrderNo);

}