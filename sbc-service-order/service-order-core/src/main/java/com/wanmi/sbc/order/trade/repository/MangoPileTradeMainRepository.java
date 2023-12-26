package com.wanmi.sbc.order.trade.repository;

import com.wanmi.sbc.order.trade.model.root.MangoPileTradeMain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MangoPileTradeMainRepository extends JpaRepository<MangoPileTradeMain, Long>, JpaSpecificationExecutor<MangoPileTradeMain> {

    MangoPileTradeMain findByOrderNo(String orderNo);
}
