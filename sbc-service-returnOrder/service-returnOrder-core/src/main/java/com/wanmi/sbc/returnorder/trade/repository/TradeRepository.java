package com.wanmi.sbc.returnorder.trade.repository;


import com.wanmi.sbc.returnorder.bean.enums.PayState;
import com.wanmi.sbc.returnorder.trade.model.root.Trade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 订单repository
 * Created by jinwei on 15/3/2017.
 */
public interface TradeRepository extends MongoRepository<Trade, String> {

//    Trade findById(String id);

    List<Trade> findListByIdIn(List<String> ids);

    List<Trade> findListByParentId(String parentId);

    Optional<Trade> findTopByIdAndTradeDelivers_Logistics_LogisticNo(String id, String logisticNo);

    Optional<Trade> findTopByTradeDelivers_Logistics_LogisticNoAndTradeDelivers_Logistics_logisticStandardCode
            (String id, String logisticNo);

    Page<Trade> findBySupplier_StoreIdAndTradeState_PayTimeBetweenAndTradeState_PayState(Long storeId, Date
            startTime, Date endTime, Pageable pageRequest, PayState payState);

    List<Trade> findListByWMSPushFlag(Boolean wmsPushFlag);

    @Query("{ 'activityType' : ?0, 'tradeItems.pickGoodsList.newPileOrderNo' : ?1 }")
    List<Trade> findListByActivityTypeAndPileOrderNo(String activityType,String pileOrderNo);


}

