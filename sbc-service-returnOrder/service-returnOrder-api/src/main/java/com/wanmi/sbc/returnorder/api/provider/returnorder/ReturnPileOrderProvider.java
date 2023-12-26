package com.wanmi.sbc.returnorder.api.provider.returnorder;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.returnorder.api.response.returnorder.ReturnOrderAddResponse;
import com.wanmi.sbc.returnorder.api.request.returnorder.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @Description: 囤货退单服务操作接口
 * @author: jiangxin
 * @create: 2021-09-28 14:25
 */
@FeignClient(value = "${application.returnOrder.name}", url="${feign.url.returnOrder:#{null}}", contextId = "ReturnReturnPileOrderProvider")
public interface ReturnPileOrderProvider {

    /**
     * 退单创建
     *
     * @param request 退单创建请求结构 {@link ReturnOrderAddRequest}
     * @return 退单id {@link ReturnOrderAddResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/pile/return/add")
    BaseResponse<ReturnOrderAddResponse> add(@RequestBody @Valid ReturnOrderAddRequest request);

    /**
     * 退单快照创建
     *
     * @param request 退单快照创建请求结构 {@link ReturnOrderTransferAddRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/pile/return/add-transfer")
    BaseResponse addTransfer(@RequestBody @Valid ReturnOrderTransferAddRequest request);

    /**
     * 退单快照删除
     *
     * @param request 退单快照删除请求结构 {@link ReturnOrderTransferDeleteRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/pile/return/delete-transfer")
    BaseResponse deleteTransfer(@RequestBody @Valid ReturnOrderTransferDeleteRequest request);

    /**
     * 退单审核
     *
     * @param request 退单审核请求结构 {@link ReturnOrderAuditRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/pile/return/audit")
    BaseResponse audit(@RequestBody @Valid ReturnOrderAuditRequest request);

    /**
     * 退单发出
     *
     * @param request 退货发出请求结构 {@link ReturnOrderDeliverRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/pile/return/deliver")
    BaseResponse deliver(@RequestBody @Valid ReturnOrderDeliverRequest request);

    /**
     * 退单收货
     *
     * @param request 退单收货请求结构 {@link ReturnOrderReceiveRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/pile/return/receive")
    BaseResponse receive(@RequestBody @Valid ReturnOrderReceiveRequest request);

    /**
     * 退单拒绝收货
     *
     * @param request 退单拒绝收货请求结构 {@link ReturnOrderRejectReceiveRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/pile/return/reject-receive")
    BaseResponse rejectReceive(@RequestBody @Valid ReturnOrderRejectReceiveRequest request);

    /**
     * 退单修改退单价格
     *
     * @param request 退单修改退单价格请求结构 {@link ReturnOrderOnlineModifyPriceRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/pile/return/online-modify-price")
    BaseResponse onlineModifyPrice(@RequestBody @Valid ReturnOrderOnlineModifyPriceRequest request);

    /**
     * 退单退款
     *
     * @param request 退单退款请求结构 {@link ReturnOrderRefundRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/pile/return/refund")
    BaseResponse refund(@RequestBody @Valid ReturnOrderRefundRequest request);

    /**
     * 退单在线退款
     *
     * @param request 退单在线退款请求结构 {@link ReturnOrderOnlineRefundRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/pile/return/online-refund")
    BaseResponse onlineRefund(@RequestBody @Valid ReturnOrderOnlineRefundRequest request);

    /**
     * 退单b2b线下退款
     *
     * @param request 退单线下退款请求结构 {@link ReturnOrderOfflineRefundRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/pile/return/offline-refund")
    BaseResponse offlineRefund(@RequestBody @Valid ReturnOrderOfflineRefundRequest request);

    /**
     * 商家退单线下退款
     *
     * @param request 商家退单线下退款请求结构 {@link ReturnOrderOfflineRefundForSupplierRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/pile/return/offline-refund-for-supplier")
    BaseResponse offlineRefundForSupplier(@RequestBody @Valid ReturnOrderOfflineRefundForSupplierRequest request);

    /**
     * 退单s2b退单线下退款
     *
     * @param request 平台退单线下退款请求结构 {@link ReturnOrderOfflineRefundForBossRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/pile/return/offline-refund-for-boss")
    BaseResponse offlineRefundForBoss(@RequestBody @Valid ReturnOrderOfflineRefundForBossRequest request);

    /**
     * 退单拒绝退款
     *
     * @param request 退单拒绝退款请求结构 {@link ReturnOrderRejectRefundRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/pile/return/reject-refund")
    BaseResponse rejectRefund(@RequestBody @Valid ReturnOrderRejectRefundRequest request);

    /**
     * 退单拒绝退款
     *
     * @param request 退单拒绝退款请求结构 {@link ReturnOrderRejectRefundRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/pile/return/reject-refund-refuse")
    BaseResponse rejectRefundAndRefuse(@RequestBody @Valid ReturnOrderRejectRefundRequest request);

    /**
     * 退单驳回
     *
     * @param request 退单驳回请求结构 {@link ReturnOrderCancelRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/pile/return/cancel")
    BaseResponse cancel(@RequestBody @Valid ReturnOrderCancelRequest request);

    /**
     * 退款状态扭转
     *
     * @param request 退款状态扭转请求结构 {@link ReturnOrderReverseRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/pile/return/reverse")
    BaseResponse reverse(@RequestBody @Valid ReturnOrderReverseRequest request);

    /**
     * 退单修改
     *
     * @param request 退单修改请求结构 {@link ReturnOrderRemedyRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/pile/return/remedy")
    BaseResponse remedy(@RequestBody @Valid ReturnOrderRemedyRequest request);

    /**
     * 根据客户id更新退单中所有业务员
     *
     * @param request 根据客户id更新退单中所有业务员请求结构 {@link ReturnOrderModifyEmployeeIdRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/pile/return/modify-employee-id")
    BaseResponse modifyEmployeeId(@RequestBody @Valid ReturnOrderModifyEmployeeIdRequest request);

    /**
     * 完善没有业务员的退单
     *
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/pile/return/fill-employee-id")
    BaseResponse fillEmployeeId();

    /**
     * 统一更新退单方法
     *
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/pile/return/modify")
    BaseResponse modify(@RequestBody @Valid ReturnOrderModifyRequest returnOrderModifyRequest);

    /**
     * 关闭退单
     *
     * @param request 关闭退单请求结构 {@link ReturnOrderOnlineRefundRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/pile/return/close-refund")
    BaseResponse closeRefund(@RequestBody @Valid ReturnOrderCloseRequest request);

    /**
     * 根据退单ID在线退款
     * @param request
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/pile/return/refund-online-by-tid")
    BaseResponse<Object> refundOnlineByTid(@RequestBody @Valid ReturnOrderOnlineRefundByTidRequest request);
}
