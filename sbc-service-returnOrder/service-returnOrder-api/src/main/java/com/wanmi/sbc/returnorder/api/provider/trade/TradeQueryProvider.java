package com.wanmi.sbc.returnorder.api.provider.trade;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.returnorder.api.response.historylogisticscompany.HistoryLogisticsCompanyByCustomerIdResponse;
import com.wanmi.sbc.returnorder.bean.vo.HistoryLogisticsCompanyVO;
import com.wanmi.sbc.returnorder.bean.vo.TradeVO;
import com.wanmi.sbc.returnorder.api.request.trade.*;
import com.wanmi.sbc.returnorder.api.response.trade.*;
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
@FeignClient(value = "${application.returnOrder.name}", url="${feign.url.returnOrder:#{null}}", contextId = "ReturnTradeQueryProvider")
public interface TradeQueryProvider {

//    /**
//     * 分页
//     *
//     * @param tradePageRequest 分页参数 {@link TradePageRequest}
//     * @return
//     */
//    @PostMapping("/returnOrder/${application.order.version}/trade/page")
//    BaseResponse<TradePageResponse> page(@RequestBody @Valid TradePageRequest tradePageRequest);

    /**
     * 条件分页
     *
     * @param tradePageCriteriaRequest 带参分页参数 {@link TradePageCriteriaRequest}
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/page-criteria")
    BaseResponse<TradePageCriteriaResponse> pageCriteria(@RequestBody @Valid TradePageCriteriaRequest tradePageCriteriaRequest);

    /**
     * 条件分页
     *
     * @param tradePageCriteriaRequest 带参分页参数 {@link TradePageCriteriaRequest}
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/supplier-page-criteria")
    BaseResponse<TradePageCriteriaResponse> supplierPageCriteria(@RequestBody @Valid TradePageCriteriaRequest tradePageCriteriaRequest);

    /**
     * boss 订单条件分页
     * @param tradePageCriteriaRequest
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/page-boss-criteria")
    BaseResponse<TradePageCriteriaResponse> pageBossCriteria(@RequestBody @Valid TradePageCriteriaRequest tradePageCriteriaRequest);
    /**
     * 条件分页
     *
     * @param tradeCountCriteriaRequest 带参分页参数 {@link TradeCountCriteriaRequest}
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/count-criteria")
    BaseResponse<TradeCountCriteriaResponse> countCriteria(@RequestBody @Valid TradeCountCriteriaRequest tradeCountCriteriaRequest);

    /**
     * 调用校验与封装单个订单信息
     *
     * @param tradeWrapperBackendCommitRequest 包装信息参数 {@link TradeWrapperBackendCommitRequest}
     * @return 订单信息 {@link TradeWrapperBackendCommitResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/wrapper-backend-commit")
    BaseResponse<TradeWrapperBackendCommitResponse> wrapperBackendCommit(@RequestBody @Valid TradeWrapperBackendCommitRequest tradeWrapperBackendCommitRequest);

    /**
     * 查询店铺订单应付的运费
     *
     * @param tradeParamsRequest 包装信息参数 {@link TradeParamsRequest}
     * @return 店铺运费信息 {@link TradeGetFreightResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/get-freight")
    BaseResponse<TradeGetFreightResponse> getFreight(@RequestBody @Valid TradeParamsRequest tradeParamsRequest);

    @PostMapping("/returnOrder/${application.order.version}/trade/get-freight-delivery-store")
    BaseResponse<List<TradeGetFreightResponse>> getFreightForDeliveryToStore(@RequestBody @Valid TradeParamsRequestForApp tradeParamsRequestForApp);

    @PostMapping("/returnOrder/${application.order.version}/trade/get-trade-freight-and-bluk")
    BaseResponse<TradeGetFreightResponse> getTradeFreightAndBluk(@RequestBody @Valid TradeParamsRequest tradeParamsRequest);
    /**
     * 查询店铺订单应付的运费
     *
     * @param tradeParams 包装信息参数 {@link TradeParamsRequest}
     * @return 店铺运费信息 {@link TradeGetFreightResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/get-boss-freight")
    BaseResponse<List<TradeGetFreightResponse>> getBossFreight(@RequestBody @Valid List<TradeParamsRequest> tradeParams);
    /**
     * 获取订单商品详情,不包含区间价，会员级别价信息
     *
     * @param tradeGetGoodsRequest 商品skuid列表 {@link TradeGetGoodsRequest}
     * @return 商品信息列表 {@link TradeGetGoodsResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/get-goods")
    BaseResponse<TradeGetGoodsResponse> getGoods(@RequestBody @Valid TradeGetGoodsRequest tradeGetGoodsRequest);

    /**
     * 获取拆箱订单商品详情,不包含区间价，会员级别价信息
     *
     * @param tradeGetGoodsRequest 商品skuid列表 {@link TradeGetGoodsRequest}
     * @return 商品信息列表 {@link TradeGetGoodsResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/devanning/get-goods")
    BaseResponse<TradeGetGoodsResponse> getDevanningGoods(@RequestBody @Valid TradeGetGoodsRequest tradeGetGoodsRequest);

    /**
     * 获取订单商品详情,不包含区间价，会员级别价信息
     *
     * @param tradeGetGoodsRequest 商品skuid列表 {@link TradeGetGoodsRequest}
     * @return 商品信息列表 {@link TradeGetGoodsResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/get-retail-goods")
    BaseResponse<TradeGetGoodsResponse> getRetailGoods(@RequestBody @Valid TradeGetGoodsRequest tradeGetGoodsRequest);


    /**
     * 获取订单商品详情,不包含区间价，会员级别价信息
     *
     * @param tradeGetGoodsRequest 商品skuid列表 {@link TradeGetGoodsRequest}
     * @return 商品信息列表 {@link TradeGetGoodsResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/get-bulk-goods")
    BaseResponse<TradeGetGoodsResponse> getBulkGoods(@RequestBody @Valid TradeGetGoodsRequest tradeGetGoodsRequest);


    /**
     * 发货校验,检查请求发货商品数量是否符合应发货数量
     *
     * @param tradeDeliveryCheckRequest 订单号 物流信息 {@link TradeDeliveryCheckRequest}
     * @return 处理结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/delivery-check")
    BaseResponse deliveryCheck(@RequestBody @Valid TradeDeliveryCheckRequest tradeDeliveryCheckRequest);

    /**
     * 通过id获取交易单信息并将buyer.account加密
     *
     * @param tradeGetByIdRequest 交易单id {@link TradeGetByIdRequest}
     * @return 交易单信息 {@link TradeGetByIdResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/get-by-id")
    BaseResponse<TradeGetByIdResponse> getById(@RequestBody @Valid TradeGetByIdRequest tradeGetByIdRequest);

    /**
     * 管理后台交易订单信息查询
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/getByIdManager")
    BaseResponse<TradeGetByIdResponse> getByIdManager(@RequestBody @Valid TradeGetByIdManagerRequest request);


    /**
     * 通过parentId获取交易单信息并将buyer.account加密
     *
     * @param tradeGetByParentRequest 交易单id {@link TradeGetByIdRequest}
     * @return 交易单信息 {@link TradeGetByIdResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/get-by-parent")
    BaseResponse<TradeGetByIdResponse> getByParent(@RequestBody @Valid TradeGetByParentRequest tradeGetByParentRequest);

    /**
     * 通过id获取交易单信息
     *
     * @param tradeGetByIdRequest 交易单id {@link TradeGetByIdRequest}
     * @return 交易单信息 {@link TradeGetByIdResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/get-order-by-id")
    BaseResponse<TradeGetByIdResponse> getOrderById(@RequestBody @Valid TradeGetByIdRequest tradeGetByIdRequest);

    /**
     * 通过父订单号获取交易单集合并将buyer.account加密
     *
     * @param tradeListByParentIdRequest 父交易单id {@link TradeListByParentIdRequest}
     * @return 交易单信息 {@link TradeListByParentIdResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/get-list-by-parent-id")
    BaseResponse<TradeListByParentIdResponse> getListByParentId(@RequestBody @Valid TradeListByParentIdRequest tradeListByParentIdRequest);

    /**
     * 通过父订单号获取交易单集合
     *
     * @param tradeListByParentIdRequest 父交易单id {@link TradeListByParentIdRequest}
     * @return 交易单信息 {@link TradeListByParentIdResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/get-order-list-by-parent-id")
    BaseResponse<TradeListByParentIdResponse> getOrderListByParentId(@RequestBody @Valid TradeListByParentIdRequest tradeListByParentIdRequest);

    /**
     * 验证订单是否存在售后申请
     *
     * @param tradeVerifyAfterProcessingRequest 交易单id {@link TradeVerifyAfterProcessingRequest}
     * @return 验证结果 {@link TradeVerifyAfterProcessingResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/verify-after-processing")
    BaseResponse<TradeVerifyAfterProcessingResponse> verifyAfterProcessing(@RequestBody @Valid TradeVerifyAfterProcessingRequest tradeVerifyAfterProcessingRequest);

    /**
     * 条件查询所有订单
     *
     * @param tradeListAllRequest 查询条件 {@link TradeListAllRequest}
     * @return 验证结果 {@link TradeListAllResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/list-all")
    BaseResponse<TradeListAllResponse> listAll(@RequestBody @Valid TradeListAllRequest tradeListAllRequest);

    /**
     * 获取支付单
     *
     * @param tradeGetPayOrderByIdRequest 支付单号 {@link TradeGetPayOrderByIdRequest}
     * @return 支付单 {@link TradeGetPayOrderByIdResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/get-payOrder-by-id")
    BaseResponse<TradeGetPayOrderByIdResponse> getPayOrderById(@RequestBody @Valid TradeGetPayOrderByIdRequest tradeGetPayOrderByIdRequest);


    /**
     * 查询订单信息作为结算原始数据
     *
     * @param tradePageForSettlementRequest 查询分页参数 {@link TradePageForSettlementRequest}
     * @return 支付单集合 {@link TradePageForSettlementResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/page-for-settlement")
    BaseResponse<TradePageForSettlementResponse> pageForSettlement(@RequestBody @Valid TradePageForSettlementRequest tradePageForSettlementRequest);

    /**
     * 根据快照封装订单确认页信息
     *
     * @param tradeQueryPurchaseInfoRequest 交易单快照信息 {@link TradeQueryPurchaseInfoRequest}
     * @return 交易单确认项 {@link TradeQueryPurchaseInfoResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/query-purchaseInfo")
    BaseResponse<TradeQueryPurchaseInfoResponse> queryPurchaseInfo(@RequestBody @Valid TradeQueryPurchaseInfoRequest tradeQueryPurchaseInfoRequest);

    /**
     * 根据快照封装提货订单确认页信息
     *
     * @param tradeQueryPurchaseInfoRequest 交易单快照信息 {@link TradeQueryPurchaseInfoRequest}
     * @return 交易单确认项 {@link TradeQueryPurchaseInfoResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/query-take-purchaseInfo")
    BaseResponse<TradeQueryPurchaseInfoResponse> queryTakePurchaseInfo(@RequestBody @Valid TradeQueryPurchaseInfoRequest tradeQueryPurchaseInfoRequest);

    /**
     * 根据订单状态统计订单
     *
     * @param tradeCountByFlowStateRequest 店铺id {@link TradeCountByFlowStateRequest}
     * @return 支付单集合 {@link TradeCountByFlowStateResponse}
//     */
//    @PostMapping("/returnOrder/${application.order.version}/trade/count-by-flowState")
//    BaseResponse<TradeCountByFlowStateResponse> countByFlowState(@RequestBody @Valid TradeCountByFlowStateRequest tradeCountByFlowStateRequest);
//
//    /**
//     * 根据支付状态统计订单
//     *
//     * @param tradeCountByPayStateRequest 店铺id {@link TradeCountByPayStateRequest}
//     * @return 支付单集合 {@link TradeCountByPayStateResponse}
//     */
//    @PostMapping("/returnOrder/${application.order.version}/trade/count-by-payState")
//    BaseResponse<TradeCountByPayStateResponse> countByPayState(@RequestBody @Valid TradeCountByPayStateRequest tradeCountByPayStateRequest);

    /**
     * 用于编辑订单前的展示信息，包含了原订单信息和最新关联的订单商品价格（计算了会员价和级别价后的商品单价）
     *
     * @param tradeGetRemedyByTidRequest 交易单id {@link TradeGetRemedyByTidRequest}
     * @return 废弃单 {@link TradeGetRemedyByTidResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/get-remedy-by-tid")
    BaseResponse<TradeGetRemedyByTidResponse> getRemedyByTid(@RequestBody @Valid TradeGetRemedyByTidRequest tradeGetRemedyByTidRequest);

    /**
     * 查询客户首笔完成的交易号
     *
     * @param request
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/query-first-complete-trade")
    BaseResponse<TradeQueryFirstCompleteResponse> queryFirstCompleteTrade(@RequestBody @Valid TradeQueryFirstCompleteRequest request);

    /**
     * 订单选择银联企业支付通知财务
     *
     * @param tradeSendEmailToFinanceRequest 邮箱信息 {@link TradeSendEmailToFinanceRequest}
     * @return 发送结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/send-email-to-finance")
    BaseResponse sendEmailToFinance(@RequestBody @Valid TradeSendEmailToFinanceRequest tradeSendEmailToFinanceRequest);

    /**
     * 发送上月订单信息给运营管理员
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/send-email-translate")
    BaseResponse sendEmailTranslate();

    /**
     * 查询导出数据
     *
     * @param tradeListExportRequest 查询条件 {@link TradeListExportRequest}
     * @return 验证结果 {@link TradeListExportResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/list-export")
    BaseResponse<TradeListExportResponse> listTradeExport(@RequestBody @Valid TradeListExportRequest tradeListExportRequest);

    /**
     * 查询导出数据
     *
     * @param tradeListExportRequest 查询条件 {@link TradeListExportRequest}
     * @return 验证结果 {@link TradeListExportResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/list-trade-expor-montht")
    BaseResponse<TradeListExportResponse> listTradeExportMonth(@RequestBody @Valid TradeListExportRequest tradeListExportRequest);

    /**
     * 通过支付单获取交易单
     *
     * @param tradeByPayOrderIdRequest 父交易单id {@link TradeByPayOrderIdRequest}
     * @return 交易单信息 {@link TradeByPayOrderIdResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/get-order-list-by-pay-order-id")
    BaseResponse<TradeByPayOrderIdResponse> getOrderByPayOrderId(@RequestBody @Valid TradeByPayOrderIdRequest tradeByPayOrderIdRequest);


    /**
     * 获取确认订单失败的订单
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/query-all-comfirm-failed")
    BaseResponse<TradeListAllResponse> queryAllConfirmFailed();



    /**
     * 根据物流公司查询订单
     * @param tradeListByParentIdRequest
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/get-order-list-by-logisticscompany-id")
    BaseResponse<TradeListAllResponse> getListByLogisticsCompanyId(@RequestBody @Valid TradeGetByLogisticsCompanyIdRequest tradeListByParentIdRequest);


    /**
     * 根据会员id查询订单
     * @param request
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/get-order-list-by-customer-id")
    BaseResponse<HistoryLogisticsCompanyByCustomerIdResponse> getByCustomerId(@RequestBody @Valid TradeByCustomerIdRequest request);

    /**
     * 根据会员id查询订单
     * @param request
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/get-order-list-by-customer-id-marketid")
    BaseResponse<Long> getByCustomerIdAndMarketId(@RequestBody @Valid TradeByCustomerIdRequest request);

    @PostMapping("/returnOrder/${application.order.version}/trade/get-History-Vo-list-by-customer-id-marketid")
    BaseResponse<HistoryLogisticsCompanyVO> getHistoryVoByCustomerIdAndMarketId(@RequestBody @Valid TradeByCustomerIdRequest request);

    /**
     * 查询采购件数
     * @param request
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/query-Purchase-Count")
    BaseResponse<Long> queryPurchaseCount(@RequestBody @Valid PurchaseQueryCountRequest request);

    /**
     * 近30天销量排行
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/query-salesRanking")
    BaseResponse salesRanking();

    /**
     * 获取客户最近下单支付时间
     * @param request
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/get-order-time-by-customer-id")
    BaseResponse<Map<String,String>> getOrderTimeByCustomerIds(@RequestBody @Valid TradeByCustomerIdRequest request);

    /**
     * 获取客户最近下单支付时间
     * @param request
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/logistics")
    BaseResponse getOrderLogisticsId(@RequestBody @Valid TradeLogisticsRequest request);


    /**
     * 通过订单号查询有效的订单
     * @param ids
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/getbyids")
    BaseResponse<List<TradeVO>> getOrderByIds(@RequestBody @Valid List<String> ids);

    /**
     * 通过订单号查询有效的订单
     * @param ids
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/getbyidssimplify")
    BaseResponse<List<TradeVO>> getOrderByIdsSimplify(@RequestBody @Valid List<String> ids);

    @PostMapping("/returnOrder/${application.order.version}/trade/checkTrade")
    BaseResponse<TradeCheckResponse> checkTrade(@RequestBody @Valid TradeCheckRequest request);
    /**
     * 通过多条件查询有效的订单（聚合函数）
     *
     * */
    @PostMapping("/returnOrder/${application.order.version}/trade/recommendTypeByCustomerIdAndCompanyInfo")
    BaseResponse<List<OrderRecommendCount>> recommendTypeByCustomerIdAndCompanyInfo(@RequestBody @Valid TradeListAllRequest tradeListAllRequest);
    /**
     *查询订单用户数据分页
     *
     * */
    @PostMapping("/returnOrder/${application.order.version}/trade/recommendTypeGetCustomerId")
    BaseResponse<List<OrderRecommendCount>> recommendTypeGetCustomerId();

    /**
     * 近30天销量排行
     */
    @PostMapping("/returnOrder/${application.order.version}/trade/query-orderSort")
    BaseResponse<List<OrderSalesRankingSort>> orderSort();
    /**
     *查询收款单状态
     *
     * */
    @PostMapping("/returnOrder/${application.order.version}/trade/queryKingdeePushPayStatus")
    BaseResponse<Integer> queryKingdeePushPayStatus(@RequestParam(value="tid")String tid);
    /**
     *根据sku 排序
     *
     * */
    @PostMapping("/returnOrder/${application.order.version}/trade/recommendByUserIdAndSku")
    BaseResponse<List<OrderRecommendCount>> recommendByUserIdAndSku();

    /**
     * 通过多条件查询有效的订单（聚合函数）
     *
     * */
    @PostMapping("/returnOrder/${application.order.version}/trade/recommendTypeByCustomerId")
    BaseResponse<List<OrderRecommendSkuCount>> recommendTypeByCustomerId(@RequestBody @Valid TradeListAllRequest tradeListAllRequest);


    @PostMapping("/returnOrder/${application.order.version}/trade/sortByCustomerId")
    BaseResponse<List<OrderRecommendSkuCount>> sortByCustomerId(@RequestBody @Valid TradeListAllRequest build);

    @PostMapping("/returnOrder/${application.order.version}/trade/sortByUserIdAndSku")
    BaseResponse<List<OrderRecommendCount>> sortByUserIdAndSku();



}
