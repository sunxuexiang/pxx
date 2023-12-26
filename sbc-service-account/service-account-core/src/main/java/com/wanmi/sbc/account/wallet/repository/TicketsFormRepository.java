package com.wanmi.sbc.account.wallet.repository;

import com.wanmi.sbc.account.wallet.model.root.TicketsForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TicketsFormRepository extends JpaRepository<TicketsForm, Long>, JpaSpecificationExecutor<TicketsForm> {

    @Query("from TicketsForm where formId = ?1")
    TicketsForm getByFromId(Long formId);

    @Query("from TicketsForm where walletId = ?1")
    List<TicketsForm> getByWalletId(Long walletId);

}
