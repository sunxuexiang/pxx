package com.wanmi.sbc.order.api.provider.trade.newPileTrade;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.order.api.request.trade.*;
import com.wanmi.sbc.order.api.request.trade.newpile.*;
import com.wanmi.sbc.order.api.response.trade.*;
import com.wanmi.sbc.order.bean.vo.NewPileTradeVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2018-12-03 17:24
 */
@FeignClient(value = "${application.order.name}", url="${feign.url.order:#{null}}", contextId = "NewPileTradeProvider")
public interface NewPileTradeProvider {


    /**
     * 通过parentId获取交易单信息并将buyer.account加密
     *
     * @param tradeGetByParentRequest 交易单id {@link TradeGetByIdRequest}
     * @return 交易单信息 {@link TradeGetByIdResponse}
     */
    @PostMapping("/order/${application.order.version}/newPileTrade/get-by-parent")
    BaseResponse<NewPileTradeGetByIdResponse> getByParent(@RequestBody @Valid TradeGetByParentRequest tradeGetByParentRequest);

    /**
     * C端提交订单(囤货单)
     *
     * @param tradeCommitRequest 提交订单请求对象  {@link TradeCommitRequest}
     * @return
     */
    @PostMapping("/order/${application.order.version}/newPileTrade/commit-all")
    BaseResponse<TradeCommitResponse> newPileCommitAll(@RequestBody @Valid TradeCommitRequest tradeCommitRequest);


    /**
     * 取消订单
     *
     * @param tradeCancelRequest 店铺信息 交易单信息 操作信息 {@link TradeCancelRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/order/${application.order.version}/newPileTrade/cancel")
    BaseResponse cancel(@RequestBody @Valid TradeCancelRequest tradeCancelRequest);

    /**
     * C端提交订单(囤货单)
     *
     * @param tradeCommitRequest 提交订单请求对象  {@link TradeCommitRequest}
     * @return
     */
    @PostMapping("/order/${application.order.version}/newPilePickTrade/commit-all")
    BaseResponse<TradeCommitResponse> newPilePickCommitAll(@RequestBody @Valid TradeCommitRequest tradeCommitRequest);


    /**
     * 通过id获取交易单信息并将buyer.account加密
     *
     * @param tradeGetByIdRequest 交易单id {@link TradeGetByIdRequest}
     * @return 交易单信息 {@link TradeGetByIdResponse}
     */
    @PostMapping("/order/${application.order.version}/newPileTrade/get-by-id")
    BaseResponse<NewPileTradeGetByIdResponse> getById(@RequestBody @Valid TradeGetByIdRequest tradeGetByIdRequest);

    /**
     * 根据快照封装订单确认页信息
     *
     * @param tradeQueryPurchaseInfoRequest 交易单快照信息 {@link TradeQueryPurchaseInfoRequest}
     * @return 交易单确认项 {@link TradeQueryPurchaseInfoResponse}
     */
    @PostMapping("/order/${application.order.version}/newPileTrade/query-purchaseInfo")
    BaseResponse<TradeQueryPurchaseInfoResponse> queryPurchaseInfo(@RequestBody @Valid TradeQueryPurchaseInfoRequest tradeQueryPurchaseInfoRequest);

    /**
     * 获取订单商品详情,不包含区间价，会员级别价信息
     *
     * @param tradeGetGoodsRequest 商品skuid列表 {@link TradeGetGoodsRequest}
     * @return 商品信息列表 {@link TradeGetGoodsResponse}
     */
    @PostMapping("/order/${application.order.version}/newPileTrade/get-goods")
    BaseResponse<TradeGetGoodsResponse> getGoods(@RequestBody @Valid TradeGetGoodsRequest tradeGetGoodsRequest);

    /**
     * 条件分页
     *
     * @param tradePageCriteriaRequest 带参分页参数 {@link TradePageCriteriaRequest}
     * @return
     */
    @PostMapping("/order/${application.order.version}/newPileTrade/supplier-page-criteria")
    BaseResponse<NewPileTradePageCriteriaResponse> supplierPageCriteria(@RequestBody @Valid NewPileTradePageCriteriaRequest tradePageCriteriaRequest);

    /**
     * 管理后台交易订单信息查询
     */
    @PostMapping("/order/${application.order.version}/newPileTrade/getByIdManager")
    BaseResponse<NewPileTradeGetByIdResponse> getByIdManager(@RequestBody @Valid TradeGetByIdManagerRequest request);

    /**
     * 新增线下收款单(包含线上线下的收款单)
     *
     * @param tradeAddReceivableRequest 收款单平台信息{@link TradeAddReceivableRequest}
     * @return 支付单 {@link TradeDefaultPayResponse}
     */
    @PostMapping("/order/${application.order.version}/newPileTrade/add-receivable")
    BaseResponse addReceivable(@RequestBody @Valid TradeAddReceivableRequest tradeAddReceivableRequest);


    /**
     * 条件分页
     *
     * @param tradePageCriteriaRequest 带参分页参数 {@link TradePageCriteriaRequest}
     * @return
     */
    @PostMapping("/order/${application.order.version}/newPileTrade/page-criteria")
    BaseResponse<NewPileTradePageCriteriaResponse> pageCriteria(@RequestBody @Valid NewPileTradePageCriteriaRequest tradePageCriteriaRequest);


    /**
     * 通过父订单号获取交易单集合
     *
     * @param tradeListByParentIdRequest 父交易单id {@link TradeListByParentIdRequest}
     * @return 交易单信息 {@link TradeListByParentIdResponse}
     */
    @PostMapping("/order/${application.order.version}/newPileTrade/get-order-list-by-parent-id")
    BaseResponse<NewPileTradeListByParentIdResponse> getOrderListByParentId(@RequestBody @Valid TradeListByParentIdRequest tradeListByParentIdRequest);


    /**
     * 我的囤货数据
     * @param request
     * @return
     */
    @PostMapping("/order/${application.order.version}/newPileTrade/get-new-pile-goods-num-customer")
    BaseResponse<List<GoodsPickStockResponse>> getNewPileGoodsNumByCustomer(@RequestBody @Valid NewPileGoodsNumByCustomerRequest request);


    /**
     * 我的囤货数据
     * @param request
     * @return
     */
    @PostMapping("/order/${application.order.version}/newPileTrade/get-new-pile-trade-wareHose-customer")
    BaseResponse<List<Long>> getNewPileTradeWareHoseCustomer(@RequestBody @Valid NewPileGoodsNumByCustomerRequest request);


    /**
     * 更新订单
     *
     * @param tradeUpdateRequest 订单信息 {@link TradeUpdateRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/order/${application.order.version}/newPileTrade/update")
    BaseResponse update(@RequestBody @Valid NewPileTradeUpdateRequest tradeUpdateRequest);

    /**
     * 订单审核
     *
     * @param tradeAuditRequest 订单审核相关必要信息 {@link TradeAuditRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/order/${application.order.version}/newPileTrade/audit")
    BaseResponse audit(@RequestBody @Valid TradeAuditRequest tradeAuditRequest);

    /**
     * 订单批量审核
     *
     * @param tradeAuditBatchRequest 订单审核相关必要信息 {@link TradeAuditBatchRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/order/${application.order.version}/newPileTrade/audit-batch")
    BaseResponse auditBatch(@RequestBody @Valid TradeAuditBatchRequest tradeAuditBatchRequest);

    @PostMapping("/order/${application.order.version}/newPileTrade/listByPileNos")
    BaseResponse<List<NewPileTradeVO>> listByPileNos(@RequestBody @Valid List<String> pileNos);

    /**
     * 新囤货订单导出列表查询
     * @param newPileTradeListExportRequest
     * @return
     */
    @PostMapping("/order/${application.order.version}/newPileTrade/listTradeExport")
    BaseResponse<NewPileTradeListExportResponse> listTradeExport(@RequestBody @Valid NewPileTradeListExportRequest newPileTradeListExportRequest);

    /**
     * 通过父订单号查询子订单
     * @param parentTid
     * @return
     */
    @PostMapping("/order/${application.order.version}/newPileTrade/getOrderListByParentIds")
    BaseResponse<NewPileTradeListByParentIdResponse> getOrderListByParentIds(@RequestBody @Valid NewPileTradeListByParentIdRequest parentTid);

    @PostMapping("/order/${application.order.version}/newPileTrade/findByNewPileTradeNos")
    BaseResponse<List<GoodsPickStockResponse>> findByNewPileTradeNos(@RequestBody @Valid GoodsPickStockIdsRequest newPileTradeIds);


    /**
     * 仅仅用户查询统计去年的囤货数据,以及加入数据
     */
    @PostMapping("/order/${application.order.version}/newPileTrade/newPileOldDataHandler")
    BaseResponse newPileOldDataHandler();



    /**
     * 补偿去年的囤货数据，修改payorder表和mongo文档
     */
    @PostMapping("/order/${application.order.version}/newPileTrade/updateOldDataMongoAndPayOrder")
    BaseResponse updateOldDataMongoAndPayOrder(@RequestBody @Valid String tid);

    @PostMapping("/order/${application.order.version}/newPileTrade/returnCoupon")
    BaseResponse returnCoupon(@RequestParam(value = "tid") String tid, @RequestParam(value = "rid") String rid);

    @PostMapping("/order/${application.order.version}/handlePileData")
    BaseResponse<String> handleNanChangPileData(@RequestBody @Valid Map<String, String> map);

    @PostMapping("/order/${application.order.version}/handleNanChangTakeData")
    BaseResponse<String> handleNanChangTakeData(@RequestBody @Valid Map<String, List<String>> map);


    /**
     * 通过parentId获取交易单列表信息.account加密
     *
     */
    @PostMapping("/order/${application.order.version}/newPileTrade/getListByParentId")
    BaseResponse<List<NewPileTradeVO>> getListByParentId(@RequestBody @Valid TradeGetByParentRequest tradeGetByParentRequest);
}
