package com.wanmi.sbc.wallet.paycallbackresult.repository;

import com.wanmi.sbc.wallet.paycallbackresult.model.root.PayCallBackResult;
import com.wanmi.sbc.walletorder.bean.enums.PayWalletCallBackResultStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * <p>支付回调结果DAO</p>
 * @author lvzhenwei
 * @date 2020-07-01 17:34:23
 */
@Repository
public interface PayCallBackResultRepository extends JpaRepository<PayCallBackResult, Long>,
        JpaSpecificationExecutor<PayCallBackResult> {

    @Modifying
    @Query("update PayCallBackResult set resultStatus = ?2,errorNum = errorNum+1 where businessId = ?1 and errorNum < 6")
    int updateStatusFailedByBusinessId(String businessId, PayWalletCallBackResultStatus resultStatus);

    @Modifying
    @Query("update PayCallBackResult set resultStatus = ?2 where businessId = ?1")
    int updateStatusSuccessByBusinessId(String businessId, PayWalletCallBackResultStatus resultStatus);

}
