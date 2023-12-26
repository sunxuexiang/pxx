package com.wanmi.sbc.order.provider.impl.returnorder;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.bean.vo.CustomerAccountVO;
import com.wanmi.sbc.order.api.provider.returnorder.ReturnPileOrderProvider;
import com.wanmi.sbc.order.api.request.returnorder.*;
import com.wanmi.sbc.order.api.response.returnorder.ReturnOrderAddResponse;
import com.wanmi.sbc.order.refund.model.root.RefundBill;
import com.wanmi.sbc.order.refund.model.root.RefundOrder;
import com.wanmi.sbc.order.returnorder.model.root.ReturnPileOrder;
import com.wanmi.sbc.order.returnorder.model.value.ReturnLogistics;
import com.wanmi.sbc.order.returnorder.service.ReturnPileOrderService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

/**
 * @Description: 囤货退单服务操作接口
 * @author: jiangxin
 * @create: 2021-09-28 14:28
 */
@Validated
@RestController
public class ReturnPileOrderController implements ReturnPileOrderProvider {

    @Autowired
    private ReturnPileOrderService returnPileOrderService;

    /**
     * 退单创建
     *
     * @param request 退单创建请求结构 {@link ReturnOrderAddRequest}
     * @return 退单id {@link ReturnOrderAddResponse}
     */
    @Override
    public BaseResponse<ReturnOrderAddResponse> add(@RequestBody @Valid ReturnOrderAddRequest request) {
        String rid = returnPileOrderService.create(KsBeanUtil.convert(request.getReturnOrder(), ReturnPileOrder.class),
                request.getOperator(), request.getForceRefund());
        return BaseResponse.success(ReturnOrderAddResponse.builder().returnOrderId(rid).build());
    }

    /**
     * 退单快照创建
     *
     * @param request 退单快照创建请求结构 {@link ReturnOrderTransferAddRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse addTransfer(@RequestBody @Valid ReturnOrderTransferAddRequest request) {
        returnPileOrderService.transfer(KsBeanUtil.convert(request.getReturnOrder(), ReturnPileOrder.class),
                request.getOperator());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 退单快照删除
     *
     * @param request 退单快照删除请求结构 {@link ReturnOrderTransferDeleteRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse deleteTransfer(@RequestBody @Valid ReturnOrderTransferDeleteRequest request) {
        returnPileOrderService.delTransfer(request.getUserId());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 退单审核
     *
     * @param request 退单审核请求结构 {@link ReturnOrderAuditRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse audit(@RequestBody @Valid ReturnOrderAuditRequest request) {
        returnPileOrderService.audit(request.getRid(), request.getOperator());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 退单发出
     *
     * @param request 退货发出请求结构 {@link ReturnOrderDeliverRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse deliver(@RequestBody @Valid ReturnOrderDeliverRequest request) {
        returnPileOrderService.deliver(request.getRid(), KsBeanUtil.convert(request.getLogistics(), ReturnLogistics.class),
                request.getOperator());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 退单收货
     *
     * @param request 退单收货请求结构 {@link ReturnOrderReceiveRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse receive(@RequestBody @Valid ReturnOrderReceiveRequest request) {
        returnPileOrderService.receive(request.getRid(), request.getOperator());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 退单拒绝收货
     *
     * @param request 退单拒绝收货请求结构 {@link ReturnOrderRejectReceiveRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse rejectReceive(@RequestBody @Valid ReturnOrderRejectReceiveRequest request) {
        returnPileOrderService.rejectReceive(request.getRid(), request.getReason(), request.getOperator());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 退单修改退单价格
     *
     * @param request 退单修改退单价格请求结构 {@link ReturnOrderOnlineModifyPriceRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse onlineModifyPrice(@RequestBody @Valid ReturnOrderOnlineModifyPriceRequest request) {
        returnPileOrderService.onlineEditPrice(KsBeanUtil.convert(request.getReturnOrder(), ReturnPileOrder.class),
                request.getRefundComment(), request.getActualReturnPrice(), request.getActualReturnPoints(),
                request.getOperator());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 退单退款
     *
     * @param request 退单退款请求结构 {@link ReturnOrderRefundRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse refund(@RequestBody @Valid ReturnOrderRefundRequest request) {
        returnPileOrderService.refund(request.getRid(), request.getOperator(), request.getPrice());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 退单在线退款
     *
     * @param request 退单在线退款请求结构 {@link ReturnOrderOnlineRefundRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse onlineRefund(@RequestBody @Valid ReturnOrderOnlineRefundRequest request) {
        returnPileOrderService.refundOnline(KsBeanUtil.convert(request.getReturnOrder(), ReturnPileOrder.class),
                KsBeanUtil.convert(request.getRefundOrder(), RefundOrder.class), request.getOperator());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 退单b2b线下退款
     *
     * @param request 退单线下退款请求结构 {@link ReturnOrderOfflineRefundRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse offlineRefund(@RequestBody @Valid ReturnOrderOfflineRefundRequest request) {
        returnPileOrderService.refundOffline(request.getRid(), request.getCustomerAccount(),
                KsBeanUtil.convert(request.getRefundBill(), RefundBill.class), request.getOperator());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 商家退单线下退款
     *
     * @param request 商家退单线下退款请求结构 {@link ReturnOrderOfflineRefundForSupplierRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse offlineRefundForSupplier(@RequestBody @Valid ReturnOrderOfflineRefundForSupplierRequest
                                                         request) {
        CustomerAccountVO customerAccount = null;
        if (Objects.nonNull(request.getCustomerAccount())) {
            customerAccount = KsBeanUtil.convert(request.getCustomerAccount(), CustomerAccountVO.class);
        }
        returnPileOrderService.supplierRefundOffline(request.getRid(), customerAccount, KsBeanUtil.convert(request
                .getRefundBill(), RefundBill.class), request.getOperator());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 退单s2b退单线下退款
     *
     * @param request 平台退单线下退款请求结构 {@link ReturnOrderOfflineRefundForBossRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse offlineRefundForBoss(@RequestBody @Valid ReturnOrderOfflineRefundForBossRequest request) {
        returnPileOrderService.s2bBoosRefundOffline(request.getRid(), KsBeanUtil.convert(request.getRefundBill(),
                RefundBill.class), request.getOperator(), request.getTid());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 退单拒绝退款
     *
     * @param request 退单拒绝退款请求结构 {@link ReturnOrderRejectRefundRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse rejectRefund(@RequestBody @Valid ReturnOrderRejectRefundRequest request) {
        returnPileOrderService.refundReject(request.getRid(), request.getReason(), request.getOperator());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 退单拒绝退款
     *
     * @param request 退单拒绝退款请求结构 {@link ReturnOrderRejectRefundRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse rejectRefundAndRefuse(@RequestBody @Valid ReturnOrderRejectRefundRequest request) {
        returnPileOrderService.refundRejectAndRefuse(request.getRid(), request.getReason(), request.getOperator());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 退单驳回
     *
     * @param request 退单驳回请求结构 {@link ReturnOrderCancelRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse cancel(@RequestBody @Valid ReturnOrderCancelRequest request) {
        returnPileOrderService.cancel(request.getRid(), request.getOperator(), request.getRemark());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 退款状态扭转
     *
     * @param request 退款状态扭转请求结构 {@link ReturnOrderReverseRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse reverse(@RequestBody @Valid ReturnOrderReverseRequest request) {
        returnPileOrderService.reverse(request.getRid(), request.getOperator());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 退单修改
     *
     * @param request 退单修改请求结构 {@link ReturnOrderRemedyRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse remedy(@RequestBody @Valid ReturnOrderRemedyRequest request) {
        returnPileOrderService.remedy(KsBeanUtil.convert(request.getNewReturnOrder(), ReturnPileOrder.class),
                request.getOperator());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 根据客户id更新退单中所有业务员
     *
     * @param request 根据客户id更新退单中所有业务员请求结构 {@link ReturnOrderModifyEmployeeIdRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse modifyEmployeeId(@RequestBody @Valid ReturnOrderModifyEmployeeIdRequest request) {
        returnPileOrderService.updateEmployeeId(request.getEmployeeId(), request.getCustomerId());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 完善没有业务员的退单
     *
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse fillEmployeeId() {
        returnPileOrderService.fillEmployeeId();
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 统一更新退单方法
     *
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse modify(@RequestBody @Valid ReturnOrderModifyRequest returnOrderModifyRequest) {
        returnPileOrderService.updateReturnOrder(KsBeanUtil.convert(returnOrderModifyRequest.getReturnOrderDTO(), ReturnPileOrder.class));
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 关闭退款
     *
     * @param request 关闭退单请求结构 {@link ReturnOrderOnlineRefundRequest}
     * @return
     */
    @Override
    public BaseResponse closeRefund(@RequestBody @Valid ReturnOrderCloseRequest request) {
        returnPileOrderService.closeRefund(request.getRid(), request.getOperator());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<Object> refundOnlineByTid(@RequestBody @Valid ReturnOrderOnlineRefundByTidRequest request){

        List<Object> result = returnPileOrderService.refundOnlineByTid(request.getReturnOrderCode(),request.getOperator(),request.getWmsStats());

        if (CollectionUtils.isEmpty(result)){
            return BaseResponse.success(null);
        }
        return BaseResponse.success(result.get(0));
    }
}
