package com.wanmi.sbc.account.wallet.repository;

import com.wanmi.sbc.account.wallet.model.root.WalletConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WalletConfigRepository extends JpaRepository<WalletConfig,Long> , JpaSpecificationExecutor<WalletConfig> {

    @Query("from WalletConfig where id = ?1")
    WalletConfig getWalletConfigById(Long id);

    @Query("from WalletConfig where delFlag = 0")
    List<WalletConfig> getWalletConfigAll();
}
