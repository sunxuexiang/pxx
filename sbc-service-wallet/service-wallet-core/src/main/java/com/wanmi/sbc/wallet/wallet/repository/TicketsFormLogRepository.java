package com.wanmi.sbc.wallet.wallet.repository;

import com.wanmi.sbc.wallet.wallet.model.root.TicketsFormLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TicketsFormLogRepository extends JpaRepository<TicketsFormLog, Long>, JpaSpecificationExecutor<TicketsFormLog> {

    List<TicketsFormLog> findByBusinessIdIn(List<Long> businessIds);

    List<TicketsFormLog> findByAuditTimeBetween(LocalDateTime auditTimeStart, LocalDateTime auditTimeEnd);
}
