package com.wanmi.sbc.order.historydeliver;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HistoryOrderDeliverWayRepository extends JpaRepository<HistoryOrderDeliverWay, Long>, JpaSpecificationExecutor<HistoryOrderDeliverWay> {

    @Query(value = "select a.store_id,a.deliver_way from history_order_deliver_way a inner join (select customer_id,store_id,max(id)id from history_order_deliver_way where customer_id=?1 and store_id in (?2) GROUP BY customer_id,store_id) b on a.customer_id=b.customer_id and a.store_id=b.store_id and a.id=b.id", nativeQuery = true)
    List<Object[]> queryDeliverWayByStoreIdAndCustomerId(String customerId, List<Long> storeIdList);
}