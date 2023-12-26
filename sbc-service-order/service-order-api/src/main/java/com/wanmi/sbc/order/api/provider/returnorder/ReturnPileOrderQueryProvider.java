package com.wanmi.sbc.order.api.provider.returnorder;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.order.api.request.returnorder.*;
import com.wanmi.sbc.order.api.request.trade.TradeCanReturnNumRequest;
import com.wanmi.sbc.order.api.response.returnorder.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @Description: 退单服务查询接口
 * @author: jiangxin
 * @create: 2021-09-28 20:05
 */
@FeignClient(value = "${application.order.name}", url="${feign.url.order:#{null}}", contextId = "ReturnPileOrderQueryProvider")
public interface ReturnPileOrderQueryProvider {

    /**
     * 根据userId获取退单快照
     *
     * @param request 根据userId获取退单快照请求结构 {@link ReturnOrderTransferByUserIdRequest}
     * @return 退单快照 {@link ReturnOrderTransferByUserIdResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/return/get-transfer-by-user-id")
    BaseResponse<ReturnOrderTransferByUserIdResponse> getTransferByUserId(@RequestBody @Valid
                                                                                  ReturnOrderTransferByUserIdRequest
                                                                                  request);

    /**
     * 根据动态条件统计退单
     *
     * @param request 根据动态条件统计退单请求结构 {@link ReturnCountByConditionRequest}
     * @return 退单数 {@link ReturnCountByConditionResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/return/count-by-condition")
    BaseResponse<ReturnCountByConditionResponse> countByCondition(@RequestBody @Valid
                                                                          ReturnCountByConditionRequest
                                                                          request);

    /**
     * 根据动态条件查询退单分页列表
     *
     * @param request 根据动态条件查询退单分页列表请求结构 {@link ReturnOrderPageRequest}
     * @return 退单分页列表 {@link ReturnOrderPageResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/return/page")
    BaseResponse<ReturnOrderPageResponse> page(@RequestBody @Valid ReturnOrderPageRequest request);

    /**
     * 根据动态条件查询退单分页列表(重写)
     *
     * @param request 根据动态条件查询退单分页列表请求结构 {@link ReturnOrderPageRequest}
     * @return 退单分页列表 {@link ReturnOrderPageResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/return/newPage")
    BaseResponse<ReturnOrderPageNewResponse> newPage(@RequestBody @Valid ReturnOrderPageRequest request);

    /**
     * 根据动态条件查询退单列表
     *
     * @param request 根据动态条件查询退单列表请求结构 {@link ReturnOrderByConditionRequest}
     * @return 退单列表 {@link ReturnOrderByConditionResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/return/list-by-condition")
    BaseResponse<ReturnOrderByConditionResponse> listByCondition(@RequestBody @Valid ReturnOrderByConditionRequest
                                                                         request);

    /**
     * 根据id查询退单
     *
     * @param request 根据id查询退单请求结构 {@link ReturnOrderByIdRequest}
     * @return 退单信息 {@link ReturnOrderByIdResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/return/get-by-id")
    BaseResponse<ReturnOrderByIdResponse> getById(@RequestBody @Valid ReturnOrderByIdRequest
                                                          request);

    /**
     * 查询所有退货方式
     *
     * @return 退货方式列表 {@link ReturnWayListResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/return/list-return-way")
    BaseResponse<ReturnWayListResponse> listReturnWay();

    /**
     * 查询所有退货原因
     *
     * @return 退货原因列表 {@link ReturnReasonListResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/return/list-return-reason")
    BaseResponse<ReturnReasonListResponse> listReturnReason();

    /**
     * 查询可退金额
     *
     * @param request 查询可退金额请求结构 {@link ReturnOrderQueryRefundPriceRequest}
     * @return 可退金额 {@link ReturnOrderQueryRefundPriceResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/return/query-refund-price")
    BaseResponse<ReturnOrderQueryRefundPriceResponse> queryRefundPrice(@RequestBody @Valid
                                                                               ReturnOrderQueryRefundPriceRequest
                                                                               request);

    /**
     * 根据订单id查询含可退商品的交易信息
     *
     * @param request 根据订单id查询可退商品数请求结构 {@link CanReturnItemNumByTidRequest}
     * @return 含可退商品的交易信息 {@link CanReturnItemNumByTidResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/return/query-can-return-item-num-by-tid")
    BaseResponse<CanReturnItemNumByTidResponse> queryCanReturnItemNumByTid(@RequestBody @Valid
                                                                                   CanReturnItemNumByTidRequest
                                                                                   request);

    /**
     * 根据订单id查询过滤出可退商品的信息
     *
     * @param request 根据订单id查询过滤出可退商品的信息 {@link CanReturnItemNumByTidRequest}
     * @return 含可退商品的交易信息 {@link CanReturnItemNumByTidResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/return/query-filter-can-return-items-by-tid")
    BaseResponse<CanReturnItemNumByTidResponse> queryFilterCanReturnItemsByTid(@RequestBody @Valid
                                                                                   CanReturnItemNumByTidRequest
                                                                                   request);

    /**
     * 根据订单id查询含可退商品的交易信息
     *
     * @param request 根据订单id查询可退商品数请求结构 {@link CanReturnItemNumByTidRequest}
     * @return 含可退商品的交易信息 {@link CanReturnItemNumByTidResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/return/query-can-return-num")
    BaseResponse<CanReturnItemResponse> canReturnFlag(@RequestBody @Valid TradeCanReturnNumRequest request);

    /**
     * 根据退单id查询含可退商品的退单信息
     *
     * @param request 根据退单id查询可退商品数请求结构 {@link CanReturnItemNumByIdRequest}
     * @return 含可退商品的退单信息 {@link CanReturnItemNumByIdResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/return/query-can-return-item-num-by-id")
    BaseResponse<CanReturnItemNumByIdResponse> queryCanReturnItemNumById(@RequestBody @Valid
                                                                                 CanReturnItemNumByIdRequest
                                                                                 request);

    /**
     * 根据订单id查询退单列表(不包含已作废状态以及拒绝收货的退货单与拒绝退款的退款单)
     *
     * @param request 查询退单列表请求结构 {@link ReturnOrderNotVoidByTidRequest}
     * @return 退单列表 {@link ReturnOrderNotVoidByTidResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/return/list-not-void-by-tid")
    BaseResponse<ReturnOrderNotVoidByTidResponse> listNotVoidByTid(@RequestBody @Valid
                                                                           ReturnOrderNotVoidByTidRequest
                                                                           request);

    /**
     * 根据订单id查询所有退单
     *
     * @param request 根据订单id查询请求结构 {@link ReturnOrderListByTidRequest}
     * @return 退单列表 {@link ReturnOrderListByTidResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/return/list-by-tid")
    BaseResponse<ReturnOrderListByTidResponse> listByTid(@RequestBody @Valid
                                                                 ReturnOrderListByTidRequest
                                                                 request);

    /**
     * 根据结束时间统计退单
     *
     * @param request 根据结束时间统计退单请求结构 {@link ReturnOrderCountByEndDateRequest}
     * @return 退单数 {@link ReturnOrderCountByEndDateResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/return/count-by-end-date")
    BaseResponse<ReturnOrderCountByEndDateResponse> countByEndDate(@RequestBody @Valid
                                                                           ReturnOrderCountByEndDateRequest
                                                                           request);

    /**
     * 根据结束时间查询退单列表
     *
     * @param request 根据结束时间查询退单列表请求结构 {@link ReturnOrderListByEndDateRequest}
     * @return 退单列表 {@link ReturnOrderListByEndDateResponse}
     */
    @PostMapping("/order/${application.order.version}/pile/return/list-by-end-date")
    BaseResponse<ReturnOrderListByEndDateResponse> listByEndDate(@RequestBody @Valid
                                                                         ReturnOrderListByEndDateRequest
                                                                         request);
}
