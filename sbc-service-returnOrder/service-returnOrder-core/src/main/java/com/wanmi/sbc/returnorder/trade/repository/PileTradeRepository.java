package com.wanmi.sbc.returnorder.trade.repository;


import com.wanmi.sbc.returnorder.bean.enums.PayState;
import com.wanmi.sbc.returnorder.trade.model.root.PileTrade;
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
public interface PileTradeRepository extends MongoRepository<PileTrade, String> {

//    Trade findById(String id);

    List<PileTrade> findListByParentId(String parentId);

    Optional<PileTrade> findTopByIdAndTradeDelivers_Logistics_LogisticNo(String id, String logisticNo);

    Optional<PileTrade> findTopByTradeDelivers_Logistics_LogisticNoAndTradeDelivers_Logistics_logisticStandardCode
            (String id, String logisticNo);

    Page<PileTrade> findBySupplier_StoreIdAndTradeState_PayTimeBetweenAndTradeState_PayState(Long storeId, Date
            startTime, Date endTime, Pageable pageRequest, PayState payState);

    List<PileTrade> findListByWMSPushFlag(Boolean wmsPushFlag);

}

