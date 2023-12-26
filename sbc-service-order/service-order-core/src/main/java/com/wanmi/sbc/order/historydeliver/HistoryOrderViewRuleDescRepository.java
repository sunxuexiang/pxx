package com.wanmi.sbc.order.historydeliver;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface HistoryOrderViewRuleDescRepository extends JpaRepository<HistoryOrderViewRuleDesc, Long>, JpaSpecificationExecutor<HistoryOrderViewRuleDesc> {

    @Query(value = "SELECT COUNT(id) FROM history_order_view_rule_desc  WHERE consignee_id=?1 AND deliver_way=?2 AND api_id=?3",nativeQuery = true)
    public Integer getCountByConsigneeIdAndApiId(String consigneeId,Integer deliverWay,Integer apiId);
}