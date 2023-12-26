package com.wanmi.sbc.account.wallet.repository;

import com.wanmi.sbc.account.wallet.model.root.CustomerWalletQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
    @Query(value = "SELECT\n" +
            " cw.*\n" +
            "FROM\n" +
            " customer_wallet cw\n" +
            " LEFT JOIN (\n" +
            " SELECT\n" +
            "  tf1.wallet_id,\n" +
            "  SUM( tf1.apply_price ) AS apply_price \n" +
            " FROM\n" +
            "  tickets_form tf1 \n" +
            " WHERE\n" +
            "  1 = 1 \n" +
            "  AND ( tf1.extract_status = 1 OR tf1.extract_status = 2 ) \n" +
            " GROUP BY\n" +
            "  tf1.wallet_id \n" +
            " ) tf ON cw.wallet_id = tf.wallet_id \n" +
            "WHERE\n" +
            " 1 = 1 \n" +
            " AND ( cw.balance > 0 OR tf.apply_price > 0 ) and cw.store_id is null" , nativeQuery = true)
    List<CustomerWalletQuery> queryAutoWalletWithdrawaAccountBalance();
}