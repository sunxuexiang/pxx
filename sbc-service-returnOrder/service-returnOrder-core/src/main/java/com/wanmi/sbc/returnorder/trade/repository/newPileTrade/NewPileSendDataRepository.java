package com.wanmi.sbc.returnorder.trade.repository.newPileTrade;

import com.wanmi.sbc.returnorder.trade.model.newPileTrade.NewPileSendData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface NewPileSendDataRepository extends JpaRepository<NewPileSendData, Integer>, JpaSpecificationExecutor<NewPileSendData> {




}
