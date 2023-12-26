package com.wanmi.sbc.order.trade.repository;

import com.wanmi.sbc.order.trade.model.root.MangoTradeItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface MangoTradeItemRepository extends JpaRepository<MangoTradeItem, Long>, JpaSpecificationExecutor<MangoTradeItem> {
}
