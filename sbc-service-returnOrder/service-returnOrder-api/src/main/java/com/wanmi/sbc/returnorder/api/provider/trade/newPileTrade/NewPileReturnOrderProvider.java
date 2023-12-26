package com.wanmi.sbc.returnorder.api.provider.trade.newPileTrade;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.returnorder.api.request.refund.RefundOrderByReturnOrderNoRequest;
import com.wanmi.sbc.returnorder.api.request.refund.RefundOrderResponseByReturnOrderCodeRequest;
import com.wanmi.sbc.returnorder.api.request.trade.newpile.RefreshReturnedOrderRequest;
import com.wanmi.sbc.returnorder.api.response.refund.RefundOrderByReturnOrderNoResponse;
import com.wanmi.sbc.returnorder.api.response.refund.RefundOrderResponse;
import com.wanmi.sbc.returnorder.api.request.returnorder.*;
import com.wanmi.sbc.returnorder.api.response.returnorder.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(value = "${application.returnOrder.name}", url="${feign.url.returnOrder:#{null}}", contextId = "ReturnNewPileReturnOrderProvider")
public interface NewPileReturnOrderProvider {


    /**
     * 根据退单编号查询退款单
     * @param refundOrderResponseByReturnOrderCodeRequest {@link RefundOrderResponseByReturnOrderCodeRequest }
     * @return {@link RefundOrderResponse }
     */
    @PostMapping("/returnOrder/${application.order.version}/newPileReturn/get-refund-order-by-return-code-new-pile")
    BaseResponse<RefundOrderResponse> getRefundOrderReturnOrderCodeNewPile(@RequestBody @Valid RefundOrderResponseByReturnOrderCodeRequest refundOrderResponseByReturnOrderCodeRequest);

    /**
     * 根据动态条件查询退单分页列表
     *
     * @param request 根据动态条件查询退单分页列表请求结构 {@link ReturnOrderPageRequest}
     * @return 退单分页列表 {@link ReturnOrderPageResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/newPileReturn/page")
    BaseResponse<ReturnOrderPageResponse> page(@RequestBody @Valid NewPileReturnOrderPageRequest request);

    /**
     * 根据退单编号查询退款单（查不到不抛异常）
     * @param refundOrderByReturnOrderNoRequest 包含：退单编号 {@link RefundOrderByReturnOrderNoRequest }
     * @return  {@link RefundOrderByReturnOrderNoResponse }
     */
    @PostMapping("/returnOrder/${application.order.version}/newPileRefund/get-by-return-order-no")
    BaseResponse<RefundOrderByReturnOrderNoResponse> getByReturnOrderNo(@RequestBody @Valid RefundOrderByReturnOrderNoRequest refundOrderByReturnOrderNoRequest);

    /**
     * 根据退单编号查询囤货退款单
     * @param refundOrderResponseByReturnOrderCodeRequest {@link RefundOrderResponseByReturnOrderCodeRequest }
     * @return {@link RefundOrderResponse }
     */
    @PostMapping("/returnOrder/${application.order.version}/newPileRefund/get-pile-refund-order-resp-by-return-code")
    BaseResponse<RefundOrderResponse> getPileRefundOrderRespByReturnOrderCode(@RequestBody @Valid RefundOrderResponseByReturnOrderCodeRequest refundOrderResponseByReturnOrderCodeRequest);

    /**
     * 根据id查询退单
     *
     * @param request 根据id查询退单请求结构 {@link ReturnOrderByIdRequest}
     * @return 退单信息 {@link ReturnOrderByIdResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/newPileReturn/get-by-id")
    BaseResponse<NewPileReturnOrderByIdResponse> getById(@RequestBody @Valid ReturnOrderByIdRequest
                                                          request);

    /**
     * 囤货退单审核
     *
     * @param request 退单审核请求结构 {@link ReturnOrderAuditRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/newPileReturn/audit")
    BaseResponse audit(@RequestBody @Valid ReturnOrderAuditRequest request);

    /**
     * 根据动态条件查询退单列表
     *
     * @param request 根据动态条件查询退单列表请求结构 {@link ReturnOrderByConditionRequest}
     * @return 退单列表 {@link ReturnOrderByConditionResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/newPileReturn/list-by-condition")
    BaseResponse<ReturnOrderByConditionResponse> listByCondition(@RequestBody @Valid ReturnOrderByConditionRequest
                                                                         request);

    /**
     * 退单s2b退单线下退款
     *
     * @param request 平台退单线下退款请求结构 {@link ReturnOrderOfflineRefundForBossRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/newPileReturn/offline-refund-for-boss")
    BaseResponse offlineRefundForBoss(@RequestBody @Valid ReturnOrderOfflineRefundForBossRequest request);

    /**
     * 退单创建
     *
     * @param request 退单创建请求结构 {@link ReturnOrderAddRequest}
     * @return 退单id {@link ReturnOrderAddResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/newPileReturn/add")
    BaseResponse<ReturnOrderAddResponse> add(@RequestBody @Valid ReturnOrderAddRequest request);

    /**
     * 根据订单id查询所有退单
     *
     * @param request 根据订单id查询请求结构 {@link ReturnOrderListByTidRequest}
     * @return 退单列表 {@link ReturnOrderListByTidResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/return/new-pile-list-by-tid")
    BaseResponse<ReturnOrderListByTidResponse> newPilelistByTid(@RequestBody @Valid
                                                                        ReturnOrderListByTidRequest
                                                                        request);

    /**
     * 根据订单id查询含可退商品的交易信息
     *
     * @param request 根据订单id查询可退商品数请求结构 {@link CanReturnItemNumByTidRequest}
     * @return 含可退商品的交易信息 {@link CanReturnItemNumByTidResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/return/new-pile-query-can-return-item-num-by-tid")
    BaseResponse<CanReturnItemNumByTidNewPileResponse> queryCanReturnItemNumByTidNewPile(@RequestBody @Valid
                                                                                                 CanReturnItemNumByTidRequest
                                                                                                 request);
    /**
     * 退单s2b退单线下退款(客服)
     *
     * @param request 平台退单线下退款请求结构 {@link ReturnOrderOfflineRefundForSupplierRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/newPileReturn/offlineRefundForSupplier")
    BaseResponse offlineRefundForSupplier(@RequestBody @Valid ReturnOrderOfflineRefundForSupplierRequest request);

    @PostMapping("/returnOrder/${application.order.version}/newPileTrade/doRefreshReturnedOrder")
    BaseResponse doRefreshReturnOrder(@RequestBody @Valid RefreshReturnedOrderRequest request);

    @PostMapping("/returnOrder/${application.order.version}/newPileReturn/refundOnlineByTid")
    BaseResponse<Object> refundOnlineByTid(ReturnOrderOnlineRefundByTidRequest request);
}
