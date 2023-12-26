package com.wanmi.sbc.order.api.provider.trade;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.order.api.request.purchase.PilePurchaseRequest;
import com.wanmi.sbc.order.api.request.trade.*;
import com.wanmi.sbc.order.api.response.historylogisticscompany.HistoryLogisticsCompanyByCustomerIdResponse;
import com.wanmi.sbc.order.api.response.purchase.CustomerPilePurchaseListResponse;
import com.wanmi.sbc.order.api.response.purchase.PilePurchaseGoodsNumsResponse;
import com.wanmi.sbc.order.api.response.trade.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2018-12-03 17:24
 */
@FeignClient(value = "${application.order.name}", url="${feign.url.order:#{null}}", contextId = "PileTradeQueryProvider")
public interface PileTradeQueryProvider {

//    /**
//     * 分页
//     *
//     * @param tradePageRequest 分页参数 {@link TradePageRequest}
//     * @return
//     */
//    @PostMapping("/order/${application.order.version}/pile/trade/page")
//    BaseResponse<TradePageResponse> page(@RequestBody @Valid TradePageRequest tradePageRequest);

    /**
     * 条件分页
     *
     * @param tradePageCriteriaRequest 带参分页参数 {@link TradePageCriteriaRequest}
     * @return
     */
    @PostMapping("/order/${application.order.version}/pile/trade/page-criteria")
    BaseResponse<TradePageCriteriaResponse> pageCriteria(@RequestBody @Valid TradePageCriteriaRequest tradePageCriteriaRequest);

    /**
     * 条件分页
     *
     * @param tradePageCriteriaRequest 带参分页参数 {@link TradePageCriteriaRequest}
     * @return
     */
    @PostMapping("/order/${application.order.version}/pile/trade/supplier-page-criteria")
    BaseResponse<TradePageCriteriaResponse> supplierPageCriteria(@RequestBody @Valid TradePageCriteriaRequest tradePageCriteriaRequest);

    /**
     * boss 订单条件分页
     * @param tradePageCriteriaRequest
     * @return
     */
    @PostMapping("/order/${application.order.version}/pile/trade/page-boss-criteria")
    BaseResponse<TradePageCriteriaResponse> pageBossCriteria(@RequestBody @Valid TradePageCriteriaRequest tradePageCriteriaRequest);
    /**
     * 条件分页
     *
     * @param tradeCountCriteriaRequest 带参分页参数 {@link TradeCountCriteriaRequest}
     * @return
     */
    @PostMapping("/order/${application.order.version}/pile/trade/count-criteria")
    BaseResponse<TradeCountCriteriaResponse> countCriteria(@RequestBody @Valid TradeCountCriteriaRequest tradeCountCriteriaRequest);

    /**
     * 调用校验与封装单个订单信息
     *
     * @param tradeWrapperBackendCommitRequest 包装信息参数 {@link TradeWrapperBackendCommitRequest}
     * @return 订单信息 {@link TradeWrapperBackendCommitResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/wrapper-backend-commit")
    BaseResponse<TradeWrapperBackendCommitResponse> wrapperBackendCommit(@RequestBody @Valid TradeWrapperBackendCommitRequest tradeWrapperBackendCommitRequest);

    /**
     * 查询店铺订单应付的运费
     *
     * @param tradeParamsRequest 包装信息参数 {@link TradeParamsRequest}
     * @return 店铺运费信息 {@link TradeGetFreightResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/get-freight")
    BaseResponse<TradeGetFreightResponse> getFreight(@RequestBody @Valid TradeParamsRequest tradeParamsRequest);

    /**
     * 查询店铺订单应付的运费
     *
     * @param tradeParams 包装信息参数 {@link TradeParamsRequest}
     * @return 店铺运费信息 {@link TradeGetFreightResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/get-boss-freight")
    BaseResponse<List<TradeGetFreightResponse>> getBossFreight(@RequestBody @Valid List<TradeParamsRequest> tradeParams);
    /**
     * 获取订单商品详情,不包含区间价，会员级别价信息
     *
     * @param tradeGetGoodsRequest 商品skuid列表 {@link TradeGetGoodsRequest}
     * @return 商品信息列表 {@link TradeGetGoodsResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/get-goods")
    BaseResponse<TradeGetGoodsResponse> getGoods(@RequestBody @Valid TradeGetGoodsRequest tradeGetGoodsRequest);


    /**
     * 发货校验,检查请求发货商品数量是否符合应发货数量
     *
     * @param tradeDeliveryCheckRequest 订单号 物流信息 {@link TradeDeliveryCheckRequest}
     * @return 处理结果 {@link BaseResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/delivery-check")
    BaseResponse deliveryCheck(@RequestBody @Valid TradeDeliveryCheckRequest tradeDeliveryCheckRequest);

    /**
     * 通过id获取交易单信息并将buyer.account加密
     *
     * @param tradeGetByIdRequest 交易单id {@link TradeGetByIdRequest}
     * @return 交易单信息 {@link TradeGetByIdResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/get-by-id")
    BaseResponse<TradeGetByIdResponse> getById(@RequestBody @Valid TradeGetByIdRequest tradeGetByIdRequest);

    /**
     * 管理后台交易订单信息查询
     */
    @PostMapping("/order/${application.order.version}/pile/trade/getByIdManager")
    BaseResponse<TradeGetByIdResponse> getByIdManager(@RequestBody @Valid TradeGetByIdManagerRequest request);


    /**
     * 通过parentId获取交易单信息并将buyer.account加密
     *
     * @param tradeGetByParentRequest 交易单id {@link TradeGetByIdRequest}
     * @return 交易单信息 {@link TradeGetByIdResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/get-by-parent")
    BaseResponse<TradeGetByIdResponse> getByParent(@RequestBody @Valid TradeGetByParentRequest tradeGetByParentRequest);

    /**
     * 通过id获取交易单信息
     *
     * @param tradeGetByIdRequest 交易单id {@link TradeGetByIdRequest}
     * @return 交易单信息 {@link TradeGetByIdResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/get-order-by-id")
    BaseResponse<TradeGetByIdResponse> getOrderById(@RequestBody @Valid TradeGetByIdRequest tradeGetByIdRequest);

    /**
     * 通过父订单号获取交易单集合并将buyer.account加密
     *
     * @param tradeListByParentIdRequest 父交易单id {@link TradeListByParentIdRequest}
     * @return 交易单信息 {@link TradeListByParentIdResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/get-list-by-parent-id")
    BaseResponse<TradeListByParentIdResponse> getListByParentId(@RequestBody @Valid TradeListByParentIdRequest tradeListByParentIdRequest);

    /**
     * 通过父订单号获取交易单集合
     *
     * @param tradeListByParentIdRequest 父交易单id {@link TradeListByParentIdRequest}
     * @return 交易单信息 {@link TradeListByParentIdResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/get-order-list-by-parent-id")
    BaseResponse<TradeListByParentIdResponse> getOrderListByParentId(@RequestBody @Valid TradeListByParentIdRequest tradeListByParentIdRequest);

    /**
     * 验证订单是否存在售后申请
     *
     * @param tradeVerifyAfterProcessingRequest 交易单id {@link TradeVerifyAfterProcessingRequest}
     * @return 验证结果 {@link TradeVerifyAfterProcessingResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/verify-after-processing")
    BaseResponse<TradeVerifyAfterProcessingResponse> verifyAfterProcessing(@RequestBody @Valid TradeVerifyAfterProcessingRequest tradeVerifyAfterProcessingRequest);

    /**
     * 条件查询所有订单
     *
     * @param tradeListAllRequest 查询条件 {@link TradeListAllRequest}
     * @return 验证结果 {@link TradeListAllResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/list-all")
    BaseResponse<TradeListAllResponse> listAll(@RequestBody @Valid TradeListAllRequest tradeListAllRequest);

    /**
     * 获取支付单
     *
     * @param tradeGetPayOrderByIdRequest 支付单号 {@link TradeGetPayOrderByIdRequest}
     * @return 支付单 {@link TradeGetPayOrderByIdResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/get-payOrder-by-id")
    BaseResponse<TradeGetPayOrderByIdResponse> getPayOrderById(@RequestBody @Valid TradeGetPayOrderByIdRequest tradeGetPayOrderByIdRequest);


    /**
     * 查询订单信息作为结算原始数据
     *
     * @param tradePageForSettlementRequest 查询分页参数 {@link TradePageForSettlementRequest}
     * @return 支付单集合 {@link TradePageForSettlementResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/page-for-settlement")
    BaseResponse<TradePageForSettlementResponse> pageForSettlement(@RequestBody @Valid TradePageForSettlementRequest tradePageForSettlementRequest);

    /**
     * 根据快照封装订单确认页信息
     *
     * @param tradeQueryPurchaseInfoRequest 交易单快照信息 {@link TradeQueryPurchaseInfoRequest}
     * @return 交易单确认项 {@link TradeQueryPurchaseInfoResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/query-purchaseInfo")
    BaseResponse<TradeQueryPurchaseInfoResponse> queryPurchaseInfo(@RequestBody @Valid TradeQueryPurchaseInfoRequest tradeQueryPurchaseInfoRequest);

    /**
     * 根据订单状态统计订单
     *
     * @param tradeCountByFlowStateRequest 店铺id {@link TradeCountByFlowStateRequest}
     * @return 支付单集合 {@link TradeCountByFlowStateResponse}
//     */

    /**
     * 查询客户首笔完成的交易号
     *
     * @param request
     * @return
     */
    @PostMapping("/order/${application.order.version}/pile/trade/query-first-complete-trade")
    BaseResponse<TradeQueryFirstCompleteResponse> queryFirstCompleteTrade(@RequestBody @Valid TradeQueryFirstCompleteRequest request);

    /**
     * 订单选择银联企业支付通知财务
     *
     * @param tradeSendEmailToFinanceRequest 邮箱信息 {@link TradeSendEmailToFinanceRequest}
     * @return 发送结果 {@link BaseResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/send-email-to-finance")
    BaseResponse sendEmailToFinance(@RequestBody @Valid TradeSendEmailToFinanceRequest tradeSendEmailToFinanceRequest);

    /**
     * 发送上月订单信息给运营管理员
     * @return
     */
    @PostMapping("/order/${application.order.version}/pile/trade/send-email-translate")
    BaseResponse sendEmailTranslate();

    /**
     * 查询导出数据
     *
     * @param tradeListExportRequest 查询条件 {@link TradeListExportRequest}
     * @return 验证结果 {@link TradeListExportResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/list-export")
    BaseResponse<TradeListExportResponse> listTradeExport(@RequestBody @Valid TradeListExportRequest tradeListExportRequest);

    /**
     * 查询导出数据
     *
     * @param tradeListExportRequest 查询条件 {@link TradeListExportRequest}
     * @return 验证结果 {@link TradeListExportResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/list-trade-expor-montht")
    BaseResponse<TradeListExportResponse> listTradeExportMonth(@RequestBody @Valid TradeListExportRequest tradeListExportRequest);

    /**
     * 通过支付单获取交易单
     *
     * @param tradeByPayOrderIdRequest 父交易单id {@link TradeByPayOrderIdRequest}
     * @return 交易单信息 {@link TradeByPayOrderIdResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/trade/get-order-list-by-pay-order-id")
    BaseResponse<TradeByPayOrderIdResponse> getOrderByPayOrderId(@RequestBody @Valid TradeByPayOrderIdRequest tradeByPayOrderIdRequest);


    /**
     * 获取确认订单失败的订单
     * @return
     */
    @PostMapping("/order/${application.order.version}/pile/trade/query-all-comfirm-failed")
    BaseResponse<TradeListAllResponse> queryAllConfirmFailed();



    /**
     * 根据物流公司查询订单
     * @param tradeListByParentIdRequest
     * @return
     */
    @PostMapping("/order/${application.order.version}/pile/trade/get-order-list-by-logisticscompany-id")
    BaseResponse<TradeListAllResponse> getListByLogisticsCompanyId(@RequestBody @Valid TradeGetByLogisticsCompanyIdRequest tradeListByParentIdRequest);


    /**
     * 根据会员id查询订单
     * @param request
     * @return
     */
    @PostMapping("/order/${application.order.version}/pile/trade/get-order-list-by-customer-id")
    BaseResponse<HistoryLogisticsCompanyByCustomerIdResponse> getByCustomerId(@RequestBody @Valid TradeByCustomerIdRequest request);

    /**
     * 查询采购件数
     * @param request
     * @return
     */
    @PostMapping("/order/${application.order.version}/pile/trade/query-Purchase-Count")
    BaseResponse<Long> queryPurchaseCount(@RequestBody @Valid PurchaseQueryCountRequest request);

    /**
     * 近30天销量排行
     */
    @PostMapping("/order/${application.order.version}/pile/trade/query-salesRanking")
    BaseResponse salesRanking();

    /**
     * 获取客户最近下单支付时间
     * @param request
     * @return
     */
    @PostMapping("/order/${application.order.version}/pile/trade/get-order-time-by-customer-id")
    BaseResponse<Map<String,String>> getOrderTimeByCustomerIds(@RequestBody @Valid TradeByCustomerIdRequest request);

    /**
     * 获取某个商品囤货总数
     * @param skuId
     * @return
     */
    @PostMapping("/order/${application.order.version}/pile/trade/get-goods-pile-num")
    BaseResponse<Long> getGoodsPileNum(@RequestParam("skuId") String skuId);

    /**
     * 根据skuIds获取囤货商品数量
     * @param request
     * @return
     */
    @PostMapping("/order/${application.order.version}/pile/trade/get-goods-pile-num-by-skuIds")
    BaseResponse<PilePurchaseGoodsNumsResponse> getGoodsPileNumBySkuIds(@RequestBody @Valid PilePurchaseRequest request);

    /**
     * 查询用户当前可提货商品
     * @return
     */
    @PostMapping("/order/${application.order.version}/pile/trade/getPilePurchaseByCustomerId")
    BaseResponse<CustomerPilePurchaseListResponse> getPilePurchaseByCustomerId(@RequestBody PilePurchaseRequest request);

    /**
     * 查询昨今日订单
     * @return
     */
    @PostMapping("/order/${application.order.version}/pile/trade/getPileTradesByDate")
    BaseResponse<TradeSaleStatisticResponse> getPileTradesByDate(@RequestParam("dateType") Integer dateType);

    /**
     * 查询昨今日囤货金额
     * @return
     */
    @PostMapping("/order/${application.order.version}/pile/trade/getPileAndTradesPriceByDate")
    BaseResponse<TradeSaleStatisticResponse> getPileAndTradesPriceByDate();

    /**
     * 查询昨今日囤货金额
     * @return
     */
    @PostMapping("/order/${application.order.version}/pile/trade/recentSevenDaySaleStatistic")
    BaseResponse<TradeSaleStatisticResponse> recentSevenDaySaleStatistic() throws ParseException;
}
