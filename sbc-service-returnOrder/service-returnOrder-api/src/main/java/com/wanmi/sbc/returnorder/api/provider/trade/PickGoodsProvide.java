package com.wanmi.sbc.returnorder.api.provider.trade;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.returnorder.api.request.trade.PickGoodsRequest;
import com.wanmi.sbc.returnorder.api.response.trade.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "${application.returnOrder.name}", url="${feign.url.returnOrder:#{null}}", contextId = "ReturnPickGoodsProvide")
public interface PickGoodsProvide {

    @PostMapping("/returnOrder/${application.order.version}/pickGoods/pileAndTradeStatistics")
    BaseResponse<PileAndTradeStatisticsResponse> pileAndTradeStatistics(@RequestBody PickGoodsRequest request);

    @PostMapping("/returnOrder/${application.order.version}/pickGoods/statisticPickUpLog")
    BaseResponse<List<StatisticPickUpLogResponse>> statisticPickUpLog(@RequestBody PickGoodsRequest request);

    @PostMapping("/returnOrder/${application.order.version}/pickGoods/statisticRecordItemPriceNumNoPile")
    BaseResponse<List<StatisticRecordItemPriceNumNoPileResponse>> statisticRecordItemPriceNumNoPile();

    @PostMapping("/returnOrder/${application.order.version}/pickGoods/statisticRecordItemPriceNumNoPileUser")
    BaseResponse<List<StatisticRecordItemPriceNumNoPileUserResponse>> statisticRecordItemPriceNumNoPileUser();

    /**
     * 新囤货未提货客户数据
     * @param request
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/pickGoods/statisticNewPileTradeNoPileUser")
    BaseResponse<List<StatisticRecordItemPriceNumNoPileUserResponse>> statisticNewPileTradeNoPileUser(@RequestBody PickGoodsRequest request);

    /**
     * 新囤货未提商品数据
     * @param request
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/pickGoods/statisticNewPileTradeNoPile")
    BaseResponse<List<NewPileTradeNoPileExcel>> statisticNewPileTradeNoPile(@RequestBody PickGoodsRequest request);

    /**
     * 新囤货总囤货商品数据
     * @param request
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/pickGoods/statisticNewPileTradePileTotal")
    BaseResponse<List<NewPileTradeTotalPileExcel>> statisticNewPileTradePileTotal(@RequestBody PickGoodsRequest request);
}
