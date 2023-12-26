package com.wanmi.sbc.wallet.wallet.repository;

import com.wanmi.sbc.wallet.wallet.model.root.WalletRecord;
import com.wanmi.sbc.wallet.wallet.model.root.WalletUserRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface WalletUserRecordRepository extends JpaRepository<WalletUserRecord,String>, JpaSpecificationExecutor<WalletUserRecord> {


    @Modifying
    @Query("update WalletUserRecord cw set cw.balance = cw.balance + :amount,cw.giveBalance = cw.giveBalance + :amount where cw.storeId = :storeId and cw.customerId = :customerId")
    int addAmountUser(@Param("storeId") String storeId, @Param("amount") BigDecimal amount,@Param("customerId")String customerId);

    @Modifying
    @Query("update WalletUserRecord cw set cw.balance = cw.balance - :balance,cw.consumptionBalance = cw.consumptionBalance + :balance where cw.customerId = :customerId and cw.storeId = :storeId")
    int balancePay(@Param("customerId") String customerId, @Param("balance") BigDecimal balance,@Param("storeId") String storeId);

}
