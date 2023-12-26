package com.wanmi.sbc.customer.company.repository;

import com.wanmi.sbc.customer.company.model.root.AutoAuditCompanyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AutoAuditCompanyRecordRepository extends JpaRepository<AutoAuditCompanyRecord, Long>, JpaSpecificationExecutor<AutoAuditCompanyRecord> {

}