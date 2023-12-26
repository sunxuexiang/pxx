package com.wanmi.sbc.order.trade.repository;



import com.wanmi.sbc.order.trade.model.root.OrderCount;
import com.wanmi.sbc.order.trade.model.root.OrderLogistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 查询订单参数
 * Created by sgy
 */
@Repository
@Transactional
public interface OrderCountRepository extends JpaRepository<OrderCount, String>,
        JpaSpecificationExecutor<OrderCount> {



    @Query(value = " SELECT   sku_id,COUNT(*) AS count  FROM s2b_statistics.replay_trade_item WHERE  tid in (SELECT  tid  FROM s2b_statistics.replay_trade\n" +
            "WHERE date(create_time) >= DATE_SUB(CURDATE(),INTERVAL 15 DAY)\n" +
            "AND pay_state=2) GROUP BY sku_id\n" +
            "ORDER BY count DESC LIMIT 100\n",nativeQuery = true)
    List<Object> orderSort();

@Query(value = " SELECT  sku_id,COUNT(*)  FROM s2b_statistics.replay_trade  str  LEFT JOIN " +
        "  s2b_statistics.replay_trade_item  tm ON str.tid=tm.tid where  str.customer_id = ?1  AND str.pay_state=2  GROUP BY tm.sku_id ORDER BY str.id DESC  limit 100",nativeQuery = true)
    List<Object> queryAllByBuyerId( String buyerId);
}

