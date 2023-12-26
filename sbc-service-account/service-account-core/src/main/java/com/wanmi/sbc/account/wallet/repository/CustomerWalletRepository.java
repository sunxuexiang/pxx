package com.wanmi.sbc.account.wallet.repository;

import com.wanmi.sbc.account.wallet.model.root.CustomerWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Repository
public interface CustomerWalletRepository extends JpaRepository<CustomerWallet,Long>, JpaSpecificationExecutor<CustomerWallet> {

    @Query("from CustomerWallet where customerAccount = ?1")
    CustomerWallet getCustomerWalletByCustomerAccount(String customerAccount);

    @Query("from CustomerWallet where walletId = ?1")
    CustomerWallet getCustomerWalletByWalletId(Long walletId);

    @Query("select sum(balance) from CustomerWallet where delFlag = 0")
    BigDecimal getCustomerBalanceSum();

    @Query("from CustomerWallet where customerId = ?1")
    CustomerWallet getCustomerWalletByCustomerId(String customerId);

    CustomerWallet findByCustomerId(String customerId);

    /**
     * 使用余额，更新余额，可提现金额，支出金额，支出数
     *
     * @param customerId
     * @param balance
     * @return
     */
    @Modifying
    @Query("update CustomerWallet cw set cw.balance = cw.balance - :balance where cw.customerId = :customerId")
    int balancePay(@Param("customerId") String customerId, @Param("balance") BigDecimal balance);

    @Modifying
    @Query("update CustomerWallet cw set cw.balance = cw.balance + :balance where cw.storeId = :storeId")
    int balancePayOrder(@Param("storeId") String storeId, @Param("balance") BigDecimal balance);
    /**
     * 增加用户账户余额
     */
    @Modifying
    @Query("update CustomerWallet cw set cw.balance = cw.balance + :amount where cw.customerId = :customerId")
    int addAmount(@Param("customerId") String customerId, @Param("amount") BigDecimal amount);

    @Modifying
    @Query("update CustomerWallet cw set cw.balance = cw.balance + :amount,cw.blockBalance = cw.blockBalance - :amount where cw.customerId = :customerId")
    int addAmountReduceBlock(@Param("customerId") String customerId, @Param("amount") BigDecimal amount);

    @Modifying
    @Query("update CustomerWallet cw set cw.balance = cw.balance - :balance where cw.storeId = :storeId")
    int balancePayOne(@Param("storeId") Long storeId, @Param("balance") BigDecimal balance);

}
