package com.wanmi.sbc.order.trade.repository.newPileTrade;

import com.wanmi.sbc.order.trade.model.newPileTrade.NewPileOldData;
import com.wanmi.sbc.order.trade.model.newPileTrade.NewPileSendData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewPileSendDataRepository extends JpaRepository<NewPileSendData, Integer>, JpaSpecificationExecutor<NewPileSendData> {




}
