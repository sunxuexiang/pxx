package com.wanmi.sbc.order.provider.impl.returnorder;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.bean.vo.CustomerAccountVO;
import com.wanmi.sbc.order.api.provider.returnorder.ReturnOrderProvider;
import com.wanmi.sbc.order.api.request.refund.RefundOrderNotAuditProducerRequest;
import com.wanmi.sbc.order.api.request.returnorder.*;
import com.wanmi.sbc.order.api.response.returnorder.ReturnOrderAddResponse;
import com.wanmi.sbc.order.bean.enums.TradeActivityTypeEnum;
import com.wanmi.sbc.order.bean.vo.ReturnOrderVO;
import com.wanmi.sbc.order.constant.OrderErrorCode;
import com.wanmi.sbc.order.refund.model.root.RefundBill;
import com.wanmi.sbc.order.refund.model.root.RefundOrder;
import com.wanmi.sbc.order.returnorder.model.root.NewPileReturnOrder;
import com.wanmi.sbc.order.returnorder.model.root.ReturnOrder;
import com.wanmi.sbc.order.returnorder.model.value.ReturnLogistics;
import com.wanmi.sbc.order.returnorder.mq.ReturnOrderProducerService;
import com.wanmi.sbc.order.returnorder.service.newPileOrder.NewPileReturnOrderService;
import com.wanmi.sbc.order.returnorder.service.ReturnOrderService;
import com.wanmi.sbc.order.trade.service.TradeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

/**
 * <p>退单服务操作接口</p>
 *
 * @Author: daiyitian
 * @Description: 退单服务操作接口
 * @Date: 2018-12-03 15:40
 */
@Validated
@RestController
@Slf4j
public class ReturnOrderController implements ReturnOrderProvider {
    @Value("${wms.api.flag}")
    private Boolean wmsAPIFlag;

    @Autowired
    private ReturnOrderService returnOrderService;

    @Autowired
    private NewPileReturnOrderService newPileReturnOrderService;

    @Autowired
    private ReturnOrderProducerService returnOrderProducerService;

    @Autowired
    private TradeService tradeService;

    @Override
    public BaseResponse refundOrderNotAuditProducer(@RequestBody RefundOrderNotAuditProducerRequest request) {
        log.info("发送消息---------------------------------------------------"+ JSON.toJSONString(request));
        returnOrderProducerService.refundOrderNotAuditProducer(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse refundOrderNotAuditNewPileProducer(@RequestBody RefundOrderNotAuditProducerRequest request) {
        log.info("发送消息---------------------------------------------------"+ JSON.toJSONString(request));
        returnOrderProducerService.refundOrderNotAuditNewPileProducer(request);
        return BaseResponse.SUCCESSFUL();
    }




    /**
     * 退单创建
     *
     * @param request 退单创建请求结构 {@link ReturnOrderAddRequest}
     * @return 退单id {@link ReturnOrderAddResponse}
     */
    @Override
    public BaseResponse<ReturnOrderAddResponse> add(@RequestBody @Valid ReturnOrderAddRequest request) {
        if (!wmsAPIFlag
                && (Objects.isNull(request.getReturnOrder().getWmsStats()) || !request.getReturnOrder().getWmsStats())
        ) {
            //wms不可用且不为wms缺货退款时，仓库手工点击拣货，记录拣货状态
            // 生成退单时，检查订单如果在拣货中，提示拣货中，不可退款
            Assert.notNull(request.getReturnOrder(), "request.getReturnOrder() must not be null");
            Assert.notNull(request.getReturnOrder().getTid(), "request.getReturnOrder().getTid() must not be null");
            boolean picking = checkIsPicking(request.getReturnOrder().getTid());
            if (picking) {
                throw new SbcRuntimeException(OrderErrorCode.ORDER_HAS_BEEN_PICK_IN_WMS, "申请退款失败，商品已拣货");
            }
        }

        String rid = null;
        if(TradeActivityTypeEnum.STOCKUP.toActivityType().equals(request.getReturnOrder().getActivityType())){
            //原囤货退单
            rid = returnOrderService.createPile(KsBeanUtil.convert(request.getReturnOrder(), ReturnOrder.class),
                    request.getOperator(), request.getForceRefund());
        }else if(TradeActivityTypeEnum.NEWPILETRADE.toActivityType().equals(request.getReturnOrder().getActivityType())){
            //新版本囤货退单 1
            rid = newPileReturnOrderService.createNewPileReturnOrder(KsBeanUtil.convert(request.getReturnOrder(), NewPileReturnOrder.class),
                    request.getOperator());
        }else if(TradeActivityTypeEnum.NEWPICKTRADE.toActivityType().equals(request.getReturnOrder().getActivityType())){
            //新版本提货退单 1
            rid = newPileReturnOrderService.createNewPickReturnOrder(KsBeanUtil.convert(request.getReturnOrder(), ReturnOrder.class),
                    request.getOperator(), request.getForceRefund());
        }else{
            //普通退单 1
            rid = returnOrderService.create(KsBeanUtil.convert(request.getReturnOrder(), ReturnOrder.class),
                    request.getOperator(), request.getForceRefund());
        }
        return BaseResponse.success(ReturnOrderAddResponse.builder().returnOrderId(rid).build());
    }

    private boolean checkIsPicking(String tid) {
        return tradeService.checkIsPicking(tid);
    }

    /**
     * 退单快照创建
     *
     * @param request 退单快照创建请求结构 {@link ReturnOrderTransferAddRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse addTransfer(@RequestBody @Valid ReturnOrderTransferAddRequest request) {
        returnOrderService.transfer(KsBeanUtil.convert(request.getReturnOrder(), ReturnOrder.class),
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
        returnOrderService.delTransfer(request.getUserId());
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
        returnOrderService.audit(request.getRid(), request.getOperator());
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
        returnOrderService.deliver(request.getRid(), KsBeanUtil.convert(request.getLogistics(), ReturnLogistics.class),
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
        returnOrderService.receive(request.getRid(), request.getOperator());
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
        returnOrderService.rejectReceive(request.getRid(), request.getReason(), request.getOperator());
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
        returnOrderService.onlineEditPrice(KsBeanUtil.convert(request.getReturnOrder(), ReturnOrder.class),
                request.getRefundComment(), request.getActualReturnPrice(), request.getActualReturnPoints(),
                request.getOperator());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 退单修改退单价格
     *
     * @param request 退单修改退单价格请求结构 {@link ReturnOrderOnlineModifyPriceRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse onlineEditPriceNewPile(@RequestBody @Valid ReturnOrderOnlineModifyPriceRequest request) {
        returnOrderService.onlineEditPriceNewPile(KsBeanUtil.convert(request.getReturnOrder(), NewPileReturnOrder.class),
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
        returnOrderService.refund(request.getRid(), request.getOperator(), request.getPrice());
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
        returnOrderService.refundOnline(KsBeanUtil.convert(request.getReturnOrder(), ReturnOrder.class),
                KsBeanUtil.convert(request.getRefundOrder(), RefundOrder.class), request.getOperator());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse refundOnlineByWalletRecordVO(@RequestBody @Valid ReturnOrderOnlineByWalletRequest request) {
        returnOrderService.refundOnlineByWalletRecordVO(request.getWalletRecordVO(), request.getOperator());
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
        returnOrderService.refundOffline(request.getRid(), request.getCustomerAccount(),
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
        returnOrderService.supplierRefundOffline(request.getRid(), customerAccount, KsBeanUtil.convert(request
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
        returnOrderService.s2bBoosRefundOffline(request.getRid(), KsBeanUtil.convert(request.getRefundBill(),
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
        returnOrderService.refundReject(request.getRid(), request.getReason(), request.getOperator());
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
        returnOrderService.refundRejectAndRefuse(request.getRid(), request.getReason(), request.getOperator());
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
        returnOrderService.cancel(request.getRid(), request.getOperator(), request.getRemark());
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
        returnOrderService.reverse(request.getRid(), request.getOperator());
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
        returnOrderService.remedy(KsBeanUtil.convert(request.getNewReturnOrder(), ReturnOrder.class),
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
        returnOrderService.updateEmployeeId(request.getEmployeeId(), request.getCustomerId());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 完善没有业务员的退单
     *
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse fillEmployeeId() {
        returnOrderService.fillEmployeeId();
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 统一更新退单方法
     *
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse modify(@RequestBody @Valid ReturnOrderModifyRequest returnOrderModifyRequest) {
        returnOrderService.updateReturnOrder(KsBeanUtil.convert(returnOrderModifyRequest.getReturnOrderDTO(), ReturnOrder.class));
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
        returnOrderService.closeRefund(request.getRid(), request.getOperator());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<Object> refundOnlineByTid(@RequestBody @Valid ReturnOrderOnlineRefundByTidRequest request){

        List<Object> result = returnOrderService.refundOnlineByTid(request.getReturnOrderCode(),request.getOperator(),request.getWmsStats());

        if (CollectionUtils.isEmpty(result)){
            return BaseResponse.success(null);
        }
        return BaseResponse.success(result.get(0));
    }

    @Override
    public BaseResponse<Object> refundOnlineByTidNewPile(@RequestBody @Valid ReturnOrderOnlineRefundByTidRequest request){

        List<Object> result = returnOrderService.refundOnlineByTidNewPile(request.getReturnOrderCode(),request.getOperator(),request.getWmsStats());

        if (CollectionUtils.isEmpty(result)){
            return BaseResponse.success(null);
        }
        return BaseResponse.success(result.get(0));
    }

    @Override
    public BaseResponse freePileGoodsNum(CanReturnItemNumByIdRequest request) {
        returnOrderService.freePileGoodsNumByRid(request.getRid());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse updateReturnFlowState(@RequestBody @Valid UpdateReturnFlowStateRequest request) {
        ReturnOrder returnOrder = returnOrderService.findById(request.getRid());
        returnOrder.setReturnFlowState(request.getReturnFlowState());
        returnOrder.setWmsPushState(request.getWmsPushState());
        returnOrderService.updateReturnOrder(returnOrder);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse returnCoupon(String rid) {
        returnOrderService.returnCoupon(rid);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<List<ReturnOrderVO>> findByTid(String tid) {
        List<ReturnOrder> returnAllOrders = returnOrderService.findByTid(tid);
        return BaseResponse.success(KsBeanUtil.convert(returnAllOrders, ReturnOrderVO.class));
    }

    @Override
    public BaseResponse supplierAutoRefund(String rid) {
        returnOrderProducerService.supplierRefundOrderNotAuditProducer(rid, 3000L);
        return BaseResponse.SUCCESSFUL();
    }

}
