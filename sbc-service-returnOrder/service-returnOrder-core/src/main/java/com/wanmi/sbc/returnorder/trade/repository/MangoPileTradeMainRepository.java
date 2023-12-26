package com.wanmi.sbc.returnorder.trade.repository;

import com.wanmi.sbc.returnorder.trade.model.root.MangoPileTradeMain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MangoPileTradeMainRepository extends JpaRepository<MangoPileTradeMain, Long>, JpaSpecificationExecutor<MangoPileTradeMain> {

    MangoPileTradeMain findByOrderNo(String orderNo);
}
