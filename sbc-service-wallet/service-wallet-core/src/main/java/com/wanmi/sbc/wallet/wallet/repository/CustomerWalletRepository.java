package com.wanmi.sbc.wallet.wallet.repository;

import com.wanmi.sbc.wallet.wallet.model.root.CustomerWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CustomerWalletRepository extends JpaRepository<CustomerWallet,Long>, JpaSpecificationExecutor<CustomerWallet> {

    @Query("from CustomerWallet where customerAccount = ?1")
    CustomerWallet getCustomerWalletByCustomerAccount(String customerAccount);

    @Query("from CustomerWallet  where customerAccount = ?1")
    List<CustomerWallet> getCustomerWalletByCustomerList(String customerAccount);

    @Query("from CustomerWallet where walletId = ?1")
    CustomerWallet getCustomerWalletByWalletId(Long walletId);

    @Query("select sum(balance) from CustomerWallet where delFlag = 0")
    BigDecimal getCustomerBalanceSum();

    @Query("from CustomerWallet where customerId = ?1")
    CustomerWallet getCustomerWalletByCustomerId(String customerId);

    CustomerWallet findByCustomerId(String customerId);


    @Modifying
    @Query("update CustomerWallet cw set cw.isEnable =  :isEnable where cw.storeId = :storeId")
    void updateIsEnableByStoreId(@Param("isEnable") Integer isEnable, @Param("storeId") String storeId);
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
    @Query("update CustomerWallet cw set cw.balance = cw.balance - :balance where cw.storeId = :storeId")
    int balancePayOne(@Param("storeId") String storeId, @Param("balance") BigDecimal balance);


    /**
     * 用户下单商家增加鲸币
     * */
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
    @Query("update CustomerWallet cw set cw.balance = cw.balance + :amount,cw.rechargeBalance = cw.rechargeBalance + :amount where cw.storeId = :storeId")
    int addAmountOne(@Param("storeId") String storeId, @Param("amount") BigDecimal amount);

    @Modifying
    @Query("update CustomerWallet cw set cw.balance = cw.balance + :amount,cw.giveBalance = cw.giveBalance + :amount where cw.customerId = :customerId")
    int addAmountUser(@Param("customerId") String customerId, @Param("amount") BigDecimal amount);

    @Modifying
    @Query("update CustomerWallet cw set cw.balance = cw.balance - :amount,cw.blockBalance = cw.blockBalance + :amount where cw.storeId = :storeId")
    int withdrawal(@Param("storeId") String storeId, @Param("amount") BigDecimal amount);

    @Transactional
    @Modifying
    @Query("update CustomerWallet cw set cw.delFlag=1 where cw.customerId = :customerId")
    int updateByCustomerId(@Param("customerId") String customerId);
}
