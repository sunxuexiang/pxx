package com.wanmi.sbc.account.wallet.repository;

import com.wanmi.sbc.account.wallet.model.root.WalletRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WalletRecordRepository extends JpaRepository<WalletRecord,String>, JpaSpecificationExecutor<WalletRecord> {

    WalletRecord getWalletRecordByRecordNo(String recordNo);

    List<WalletRecord> findByCustomerAccount(String customerAccount);

    @Query("from WalletRecord where customerAccount = ?1 and tradeState = 1")
    List<WalletRecord> getWalletRecordByCustomerAccount(String customerAccount);

    Page<WalletRecord> findByTradeTypeAndCustomerAccount(Integer tradeType,String customerAccount,Pageable pageable);

    WalletRecord findByRecordNo(String recordNo);

    Page<WalletRecord> findByCustomerAccount(String customerAccount, PageRequest pageRequest);

    @Query(value ="select wr.customer_account as customerAccount,wr.deal_price as dealPrice ,wr.deal_time as dealTime,tf.extract_status as extractStatus, wr.trade_remark as remark, wr.extract_type as extractType,wr.record_no as recordNo,tf.apply_time as applyTime,wr.charge_price as chargePrice from wallet_record as wr ,tickets_form as tf where wr.record_no=tf.record_no and wr.customer_account =:customerAccount and wr.trade_type=:tradeType",
    countQuery = "select count(*) from wallet_record as wr ,tickets_form as tf where wr.record_no=tf.record_no and wr.customer_account =:customerAccount and wr.trade_type=:tradeType",nativeQuery = true)
    Page<Object> queryExtractInfo(@Param("tradeType") Integer tradeType, @Param("customerAccount") String customerAccount, Pageable pageable);
    @Query("from WalletRecord where relationOrderId = ?1  order by dealTime desc")
    List<WalletRecord> getWalletRecordByRelationOrderId(String relationOrderId);

    @Query(value = "select wr.trade_type as tradeType,wr.deal_price as dealPrice,wr.deal_time as dealTime,wr.current_balance as currentBalance from wallet_record as wr ,tickets_form as tf where wr.record_no=tf.record_no and wr.customer_account = :customerAccount and wr.trade_state=:tradeState and tf.recharge_status=:rechargeStatus and tf.extract_status=:extractStatus",
    countQuery = "select count(*) from wallet_record as wr ,tickets_form as tf where wr.record_no=tf.record_no and wr.customer_account = :customerAccount and wr.trade_state=:tradeState and tf.recharge_status=:rechargeStatus and tf.extract_status=:extractStatus",nativeQuery = true)
    Page<Object> getRemainingMoneyInfo(@Param("customerAccount") String customerAccount ,@Param("tradeState") Integer tradeState,@Param("rechargeStatus") Integer rechargeStatus,@Param("extractStatus") Integer extractStatus, Pageable pageable);

    @Query("from WalletRecord where relationOrderId = ?1 and tradeRemark like CONCAT('%',?2,'%') order by dealTime desc")
    List<WalletRecord> getWalletRecordByRelationOrderIdAndTradeRemark(String relationOrderId, String tradeRemark);

}
