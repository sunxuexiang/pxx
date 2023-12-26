package com.wanmi.sbc.wallet.wallet.repository;

import com.wanmi.sbc.wallet.wallet.model.root.TicketsForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketsFormRepository extends JpaRepository<TicketsForm, Long>, JpaSpecificationExecutor<TicketsForm> {

    @Query("from TicketsForm where formId = ?1")
    TicketsForm getByFromId(Long formId);

    @Query("from TicketsForm where walletId = ?1")
    List<TicketsForm> getByWalletId(Long walletId);

    @Query("from TicketsForm where recordNo = ?1")
    TicketsForm findTicketsFormByRecordNo(String recordNo);

    @Modifying
    @Query("update TicketsForm t1 set t1.rechargeStatus = :rechargeStatus where t1.recordNo = :recordNo")
    void updateTicketsStatus(@Param("recordNo") String recordNo,@Param("rechargeStatus")Integer rechargeStatus);

}
