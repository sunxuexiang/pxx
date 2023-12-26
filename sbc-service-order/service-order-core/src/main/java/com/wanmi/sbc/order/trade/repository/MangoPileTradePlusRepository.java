package com.wanmi.sbc.order.trade.repository;

import com.wanmi.sbc.order.trade.model.root.MangoPileTradePlus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MangoPileTradePlusRepository extends JpaRepository<MangoPileTradePlus, Long>, JpaSpecificationExecutor<MangoPileTradePlus> {
}
