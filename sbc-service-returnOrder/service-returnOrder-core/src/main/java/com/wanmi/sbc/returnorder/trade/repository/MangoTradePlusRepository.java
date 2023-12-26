package com.wanmi.sbc.returnorder.trade.repository;

import com.wanmi.sbc.returnorder.trade.model.root.MangoTradePlus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MangoTradePlusRepository extends JpaRepository<MangoTradePlus, Long>, JpaSpecificationExecutor<MangoTradePlus> {
}
