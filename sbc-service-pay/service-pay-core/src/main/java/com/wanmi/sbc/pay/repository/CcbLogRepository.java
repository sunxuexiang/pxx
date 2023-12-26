package com.wanmi.sbc.pay.repository;

import com.wanmi.sbc.pay.model.root.CcbLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CcbLogRepository extends JpaRepository<CcbLog, Long>, JpaSpecificationExecutor<CcbLog> {

}