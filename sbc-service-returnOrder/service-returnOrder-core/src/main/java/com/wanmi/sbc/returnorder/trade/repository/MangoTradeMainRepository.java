package com.wanmi.sbc.returnorder.trade.repository;

import com.wanmi.sbc.returnorder.trade.model.root.MangoTradeMain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MangoTradeMainRepository extends JpaRepository<MangoTradeMain, Long>, JpaSpecificationExecutor<MangoTradeMain> {

    MangoTradeMain findByOrderNo(String orderNo);
}
