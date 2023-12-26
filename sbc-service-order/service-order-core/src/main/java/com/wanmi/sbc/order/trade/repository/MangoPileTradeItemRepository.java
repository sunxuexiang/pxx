package com.wanmi.sbc.order.trade.repository;

import com.wanmi.sbc.order.trade.model.root.MangoPileTradeItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MangoPileTradeItemRepository extends JpaRepository<MangoPileTradeItem, Long>, JpaSpecificationExecutor<MangoPileTradeItem> {
}
