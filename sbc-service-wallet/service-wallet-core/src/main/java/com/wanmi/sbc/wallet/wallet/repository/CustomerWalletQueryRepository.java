package com.wanmi.sbc.wallet.wallet.repository;

import com.wanmi.sbc.wallet.wallet.model.root.CustomerWallet;
import com.wanmi.sbc.wallet.wallet.model.root.CustomerWalletQuery;
import com.wanmi.sbc.wallet.wallet.model.root.WalletRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CustomerWalletQueryRepository extends JpaRepository<CustomerWalletQuery, Long>, JpaSpecificationExecutor<CustomerWalletQuery> {

    @Query(value = "SELECT a.customer_id, a.customer_name, a.customer_account, a.balance, a.apply_time FROM " +
            "( SELECT cw.customer_id, cw.customer_name, cw.customer_account, cw.balance, tf.apply_time  FROM customer_wallet cw " +
            "LEFT JOIN ( SELECT wallet_id, apply_time FROM ( SELECT wallet_id, apply_time FROM tickets_form ORDER BY apply_time DESC ) tfo GROUP BY wallet_id ) tf " +
            "ON cw.wallet_id = tf.wallet_id ) a " +
            "WHERE 1 = 1 AND if(IFNULL((COALESCE(:cusIds, NULL)),'') != '',a.customer_id in (:cusIds),1=1)  AND if(IFNULL(:useBalance,'') != '',if(:useBalance = 1,a.balance > 0,a.balance <= 0),1=1) AND if (:customerName !=''||null , a.customer_name like concat('%', :customerName, '%') , 1=1) AND if (:customerAccount !=''||null , a.customer_account like concat('%', :customerAccount, '%') , 1=1) " +
            "AND ( if (:recentlyTicketsTimeStart !=''||null , a.apply_time >= :recentlyTicketsTimeStart , 1=1) AND if (:recentlyTicketsTimeEnd !=''||null , a.apply_time <= :recentlyTicketsTimeEnd , 1=1) )",
            countQuery = "SELECT count(1) FROM ( SELECT cw.customer_name,cw.customer_id, cw.customer_account, cw.balance, tf.apply_time  FROM customer_wallet cw LEFT JOIN ( SELECT wallet_id, apply_time FROM ( SELECT wallet_id, apply_time FROM tickets_form ORDER BY apply_time DESC ) tfo GROUP BY wallet_id ) tf ON cw.wallet_id = tf.wallet_id ) a WHERE 1 = 1 AND if(IFNULL((COALESCE(:cusIds, NULL)),'') != '',a.customer_id in (:cusIds),1=1) AND if(IFNULL(:useBalance,'') != '',if(:useBalance = 1,a.balance > 0,a.balance <= 0),1=1) AND if (:customerName !=''||null , a.customer_name like concat('%', :customerName, '%') , 1=1) AND if (:customerAccount !=''||null , a.customer_account like concat('%', :customerAccount, '%') , 1=1) AND ( if (:recentlyTicketsTimeStart !=''||null , a.apply_time >= :recentlyTicketsTimeStart , 1=1) AND if (:recentlyTicketsTimeEnd !=''||null , a.apply_time <= :recentlyTicketsTimeEnd , 1=1))"
            , nativeQuery = true)
    Page<Object> listPageCustomerWallet(@Param("customerName") String customerName,
                                        @Param("customerAccount") String customerAccount,
                                        @Param("recentlyTicketsTimeStart") LocalDateTime recentlyTicketsTimeStart,
                                        @Param("recentlyTicketsTimeEnd") LocalDateTime recentlyTicketsTimeEnd,
                                        @Param("useBalance") Integer useBalance,
                                        @Param("cusIds") List<String> cusIds,
                                        Pageable pageable);
    /**
     * 用户昨天新增，鲸币余额，昨天减少鲸币
     * */
    @Query(value = "select " +
            "\t\t\t sum(case when t2.budget_type = 0 then t2.deal_price else 0 end) as addBalance \n" +
            " from customer_wallet t1 inner join wallet_record t2 \n" +
            "on t1.customer_account = t2.customer_account and t1.store_id is NULL\n" +
            "where DATE(t2.deal_time) = CURDATE() - INTERVAL 1 DAY and trade_state = 2",nativeQuery = true)
    Object yeAddCount() ;

    @Query(value = "select sum(case when t2.budget_type = 1 then t2.deal_price else 0 end) as reduceBalance " +
            "from customer_wallet t1 inner join wallet_record t2 " +
            "on t1.customer_account = t2.customer_account and t1.store_id is NULL where DATE(t2.deal_time) = CURDATE() - INTERVAL 1 DAY and trade_state = 2 ",nativeQuery = true)
    Object yeReduceCount();

    @Query(value = "select" +
            "\t\t\t sum(case when t2.budget_type = 1 then t2.deal_price else 0 end) as reduceBalance \n" +
            " from customer_wallet t1 inner join wallet_record t2 \n" +
            "on t1.customer_account = t2.customer_account and t1.store_id is not NULL and t1.store_id != '999999'\n" +
            "where DATE(t2.deal_time) = CURDATE() - INTERVAL 1 DAY and trade_state = 2",nativeQuery = true)
    Object storeReduceCount() ;

    @Query(value = "select sum(case when t2.budget_type = 0 then t2.deal_price else 0 end)  " +
            "as addBalance from customer_wallet t1 inner join wallet_record t2 " +
            "on t1.customer_account = t2.customer_account and t1.store_id is not NULL and t1.store_id != '999999'" +
            "where DATE(t2.deal_time) = CURDATE() - INTERVAL 1 DAY and trade_state = 2 ",nativeQuery = true)
    Object storeAddCount();

    @Query(value = "select sum(t1.balance) as TotalBalance\n" +
            "  from customer_wallet t1 " +
            " where  t1.store_id is not NULL and t1.store_id != '999999' ",nativeQuery = true)
    Object storeBalanceCount();

    @Query(value = "select sum(t1.balance) as TotalBalance from customer_wallet t1\n" +
            " where  t1.store_id is NULL ",nativeQuery = true)
    Object userBalanceCount();

    @Query(value = "select sum(t1.balance) as TotalBalance from customer_wallet t1" +
            " where  t1.store_id = 123458023",nativeQuery = true)
    Object taltolBalanceCount();

    @Query(value = "select sum(case when t2.budget_type = 0 then t2.deal_price else 0 end)  " +
            "as addBalance from customer_wallet t1 inner join wallet_record t2 " +
            "on t1.customer_account = t2.customer_account and t1.store_id = 123458023 " +
            "where DATE(t2.deal_time) = CURDATE() - INTERVAL 1 DAY and trade_state = 2 ",nativeQuery = true)
    Object taltolAddCount();

    @Query(value = "select" +
            "\t\t\t sum(case when t2.budget_type = 1 then t2.deal_price else 0 end) as reduceBalance \n" +
            " from customer_wallet t1 inner join wallet_record t2 \n" +
            "on t1.customer_account = t2.customer_account and t1.store_id = 123458023 \n" +
            "where DATE(t2.deal_time) = CURDATE() - INTERVAL 1 DAY and trade_state = 2",nativeQuery = true)
    Object toltalReduceCount() ;

    /**
     * 用户鲸币列表查询
     * */

    @Query(value = "SELECT\n" +
            "\t\t\tt3.customer_name,\n" +
            "\t\t\tt1.customer_account, \n" +
            "\t\t\tt1.customer_id, \n" +
            " t4.wallet_id," +
            " t4.balance"+
            "\t\tFROM\n" +
            "`sbc-account`.customer_wallet t4 left join " +
            "\t\t\t`sbc-customer`.customer AS t1 on t1.customer_id=t4.customer_id\n" +
            "\t\t\tINNER JOIN" +
            "\t\t\t`sbc-customer`.customer_detail AS t3\n" +
            "\t\t\ton t1.customer_id = t3.customer_id\n" +
            " where  if (:customerAccount !=''||null , t1.customer_account like concat('%', :customerAccount, '%') , 1=1) " +
            "and if (:customerName !=''||null , t3.customer_name like concat('%', :customerName, '%') , 1=1) "  ,nativeQuery = true)
    Page<Object> userList(@Param("customerAccount")String customerAccount,@Param("customerName")String customerName, Pageable pageable);

    @Query(value = "SELECT\n" +
            "\t\t\tt3.customer_name,\n" +
            "\t\t\tt1.customer_account, \n" +
            "\t\t\tt1.customer_id, \n" +
            " t4.wallet_id," +
            " t4.balance"+
            "\t\tFROM\n" +
            "`sbc-account`.customer_wallet t4 left join " +
            "\t\t\t`sbc-customer`.customer AS t1 on t1.customer_id=t4.customer_id\n" +
            "\t\t\tINNER JOIN" +
            "\t\t\t`sbc-customer`.customer_detail AS t3\n" +
            "\t\t\ton t1.customer_id = t3.customer_id\n" +
            " where  if (:customerAccount !=''||null , t1.customer_account like concat('%', :customerAccount, '%') , 1=1) " +
            "and if (:customerName !=''||null , t3.customer_name like concat('%', :customerName, '%') , 1=1) " +
            "and  if(IFNULL((COALESCE(:customerIds, NULL)),'') != '',t1.customer_id in (:customerIds),1=1)" ,nativeQuery = true)
    Page<Object> userIsNull(@Param("customerAccount")String customerAccount,@Param("customerName")String customerName,@Param("customerIds") List<String> customerIds, Pageable pageable);

    @Query(value = "SELECT\n" +
            "\t\t\tt1.store_name,\n" +
            "\t\t\tt3.account_name, \n" +
            "\t\t\tt1.store_id, \n" +
            " t4.wallet_id," +
            " t4.balance"+
            "\t\tFROM\n" +
            "`sbc-account`.customer_wallet t4 left join " +
            "\t\t\t`sbc-customer`.store AS t1 on t4.customer_id=t1.store_id inner join `sbc-customer`.company_info t2 on t1.company_info_id = t2.company_info_id" +
            " inner join `sbc-customer`.employee t3 on t2.company_info_id = t3.company_info_id\n" +
            " where t1.audit_state=1 and t3.is_master_account=1 and if (:storeName !=''||null , t1.store_name like concat('%', :storeName, '%') , 1=1) " +
            "and if (:contractMobile !=''||null , t3.account_name like concat('%', :contractMobile, '%') , 1=1) " +
            "and if(IFNULL((COALESCE(:storeIds, NULL)),'') != '',t1.store_id in (:storeIds),1=1)",nativeQuery = true)
    Page<Object> storeIsNotNull(@Param("storeName")String storeName,@Param("contractMobile")String contractMobile,@Param("storeIds") List<String> storeIds, Pageable pageable);

    @Query(value = "SELECT\n" +
            "\t\t\tt1.store_name,\n" +
            "\t\t\tt3.account_name, \n" +
            "\t\t\tt1.store_id, \n" +
            " t4.wallet_id," +
            " t4.balance"+
            "\t\tFROM\n" +
            "`sbc-account`.customer_wallet t4 left join " +
            "\t\t\t`sbc-customer`.store AS t1 on t4.customer_id=t1.store_id inner join `sbc-customer`.company_info t2 on t1.company_info_id = t2.company_info_id" +
            " inner join `sbc-customer`.employee t3 on t2.company_info_id = t3.company_info_id\n" +
            " where t1.audit_state=1 and t3.is_master_account=1 and if (:storeName !=''||null , t1.store_name like concat('%', :storeName, '%') , 1=1) " +
            "and if (:contractMobile !=''||null , t3.account_name like concat('%', :contractMobile, '%') , 1=1) ",nativeQuery = true)
    Page<Object> userStore(@Param("storeName")String storeName,@Param("contractMobile")String contractMobile, Pageable pageable);

    /**
     * 查询有无鲸币账户
     * */

    @Query(value = "SELECT t1.customer_id FROM customer_wallet t1 WHERE " +
            "CASE WHEN :isMoney = 1 THEN t1.balance IS NOT NULL and t1.balance != 0.00 WHEN :isMoney = 0 THEN t1.balance IS NULL or t1.balance = 0.00 END", nativeQuery = true)
    List<String> findHaveJBAccount(@Param("isMoney")Integer isMoney);

    @Query(value = "SELECT t1.store_id FROM customer_wallet t1 WHERE t1.store_id is not null and " +
            "CASE WHEN :isMoney = 1 THEN t1.balance IS NOT NULL and t1.balance != 0.00 WHEN :isMoney = 0 THEN t1.balance IS NULL or t1.balance = 0.00 END", nativeQuery = true)
    List<String> findHaveJBStoreAccount(@Param("isMoney")Integer isMoney);

    /**
     * 查询最近一次扣除时间以及鲸币
     * */
    @Query(value = "select re.deal_time,re.deal_price from " +
            "wallet_record re where re.budget_type = :budgetType and re.customer_account = :customerAccount " +
            "ORDER BY  deal_time DESC LIMIT 1"
            ,nativeQuery = true)
    Object walletRecord(@Param("budgetType") Integer budgetType, @Param("customerAccount")String customerAccount);

    @Query(value = "select cw.customer_account,st.store_logo,st.store_name,cw.balance,cw.wallet_id " +
            "from customer_wallet cw inner join `sbc-customer`.store st on cw.store_id = st.store_id where cw.wallet_id = :walletId",nativeQuery = true)
    Object storeInfo(@Param("walletId")Long walletId);

    @Query(value = "select cw.customer_account,'no',cd.customer_name,cw.balance,cw.wallet_id from customer_wallet cw inner join `sbc-customer`.customer st on cw.customer_id = st.customer_id \n" +
            "inner join `sbc-customer`.customer_detail cd on st.customer_id = cd.customer_id where cw.wallet_id = :walletId",nativeQuery = true)
    Object customerInfo(@Param("walletId")Long walletId);

    @Query(value = "select cw.customer_account,cw.balance,cw.block_balance,s.store_name from customer_wallet cw left join `sbc-customer`.store s on cw.store_id=s.store_id where cw.balance > :balance and cw.store_id is null order by cw.balance desc",nativeQuery = true)
    List<Object> findWalletByBalanceNotStore(@Param("balance")BigDecimal balance);

    @Query(value = "select cw.customer_account,cw.balance,cw.block_balance,s.store_name from customer_wallet cw left join `sbc-customer`.store s on cw.store_id=s.store_id where cw.balance > :balance and cw.store_id is not null order by cw.balance desc",nativeQuery = true)
    List<Object> findWalletByBalanceIsStore(@Param("balance")BigDecimal balance);

}
