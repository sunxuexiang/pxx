package com.wanmi.sbc.order.trade.repository.newPileTrade;


import com.wanmi.sbc.order.bean.enums.PayState;
import com.wanmi.sbc.order.trade.model.newPileTrade.NewPileTrade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 订单repository
 * Created by jinwei on 15/3/2017.
 */
public interface NewPileTradeRepository extends MongoRepository<NewPileTrade, String> {

//    Trade findById(String id);

    List<NewPileTrade> findListByParentId(String parentId);

    Optional<NewPileTrade> findTopByIdAndTradeDelivers_Logistics_LogisticNo(String id, String logisticNo);

    Optional<NewPileTrade> findTopByTradeDelivers_Logistics_LogisticNoAndTradeDelivers_Logistics_logisticStandardCode
            (String id, String logisticNo);

    Page<NewPileTrade> findBySupplier_StoreIdAndTradeState_PayTimeBetweenAndTradeState_PayState(Long storeId, Date
            startTime, Date endTime, Pageable pageRequest, PayState payState);

    List<NewPileTrade> findListByWMSPushFlag(Boolean wmsPushFlag);

    List<NewPileTrade> findListByIdIn(List<String> pileNos);
}

