package com.wanmi.sbc.returnorder.provider.impl.refund;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.GeneratorService;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.returnorder.api.provider.refund.RefundOrderProvider;
import com.wanmi.sbc.returnorder.api.request.manualrefund.RefundForClaimsApplyPageRequest;
import com.wanmi.sbc.returnorder.api.request.manualrefund.RefundForClaimsRequest;
import com.wanmi.sbc.returnorder.api.response.manualrefund.RefundForClaimsApplyPageResponse;
import com.wanmi.sbc.returnorder.bean.vo.RefundForClaimsApplyVO;
import com.wanmi.sbc.returnorder.claims.model.root.ClaimsApply;
import com.wanmi.sbc.returnorder.claims.service.ClaimsApplyService;
import com.wanmi.sbc.returnorder.refund.model.root.RefundOrder;
import com.wanmi.sbc.returnorder.refund.service.RefundFactory;
import com.wanmi.sbc.returnorder.refund.service.RefundOrderService;
import com.wanmi.sbc.returnorder.returnorder.model.root.NewPileReturnOrder;
import com.wanmi.sbc.returnorder.returnorder.model.root.ReturnOrder;
import com.wanmi.sbc.returnorder.returnorder.model.value.ReturnPrice;
import com.wanmi.sbc.returnorder.returnorder.repository.ReturnOrderRepository;
import com.wanmi.sbc.returnorder.returnorder.service.ReturnOrderService;
import com.wanmi.sbc.returnorder.returnorder.service.newPileOrder.NewPileReturnOrderService;
import com.wanmi.sbc.returnorder.trade.model.newPileTrade.NewPileTrade;
import com.wanmi.sbc.returnorder.trade.model.root.Trade;
import com.wanmi.sbc.returnorder.trade.service.TradeService;
import com.wanmi.sbc.returnorder.trade.service.newPileTrade.NewPileTradeService;
import com.wanmi.sbc.returnorder.api.request.refund.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author: wanggang
 * @createDate: 2018/12/3 13:46
 * @version: 1.0
 */
@Validated
@RestController
public class RefundOrderController implements RefundOrderProvider {

    @Autowired
    private RefundOrderService refundOrderService;

    @Autowired
    private ReturnOrderService returnOrderService;

    @Autowired
    private ReturnOrderRepository returnOrderRepository;

    @Autowired
    TradeService tradeService;

    @Autowired
    private NewPileTradeService newPileTradeService;

    @Autowired
    private NewPileReturnOrderService newPileReturnOrderService;

    /**
     * 作废退款单
     *
     * @param refundOrderDeleteByIdRequest {@link RefundOrderDeleteByIdRequest }
     * @return {@link BaseResponse }
     */
    @Override
    public BaseResponse deleteById(@RequestBody @Valid RefundOrderDeleteByIdRequest refundOrderDeleteByIdRequest) {
        refundOrderService.destory(refundOrderDeleteByIdRequest.getId());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse destory(@RequestBody @Valid RefundOrderDestoryRequest refundOrderDestoryRequest) {
        String refundId = refundOrderDestoryRequest.getRefundId();
        Optional<RefundOrder> optional = refundOrderService.findById(refundId);
        if (optional.isPresent()) {
            ReturnOrder returnOrder = returnOrderService.findById(optional.get().getReturnOrderCode());
            if (Objects.nonNull(returnOrder.getHasBeanSettled()) && returnOrder.getHasBeanSettled()) {
                throw new SbcRuntimeException("K-050006");
            }
        }
        refundOrderService.destory(refundId);
        refundOrderService.findById(refundId).ifPresent(refundOrder -> {
            returnOrderService.reverse(refundOrder.getReturnOrderCode(), refundOrderDestoryRequest.getOperator());
            ReturnOrder order = returnOrderService.findById(refundOrder.getReturnOrderCode());
            if (Objects.nonNull(order) && Objects.nonNull(order.getReturnPrice().getActualReturnPrice())) {
                order.getReturnPrice().setActualReturnPrice(null);
                if (order.getReturnPrice().getApplyStatus()) {
                    order.getReturnPrice().setApplyStatus(false);
                    order.getReturnPrice().setApplyPrice(order.getReturnPrice().getTotalPrice());
                }
                returnOrderRepository.save(order);
            }
        });
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse refuse(@RequestBody @Valid RefundOrderRefuseRequest refundOrderRefuseRequest) {
        refundOrderService.refuse(refundOrderRefuseRequest.getId(), refundOrderRefuseRequest.getRefuseReason());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse refundFailed(@RequestBody @Valid RefundOrderRefundRequest request) {
        returnOrderService.refundFailed(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse refundForNewPickNotAudit(RefundForNewPickNotAudit request) {
        RefundFactory.getTradeRefundImpl(RefundFactory.TradeRefundType.NEW_PICK_RETURN)
                .refund(
                        KsBeanUtil.convert(request.getTradeVO(), Trade.class),
                        KsBeanUtil.convert(request.getReturnOrder(), ReturnOrder.class)
                );
        return BaseResponse.SUCCESSFUL();
    }

    @Autowired
    ClaimsApplyService claimsApplyService;

    @Override
    public BaseResponse refundForClaims(RefundForClaimsRequest request) {
        Assert.notNull(request.getOrderNo(),"订单号不能为空");
        Assert.notNull(request.getCustomerAccount(),"客户账号不能为空");
        boolean isNP=request.getOrderNo().startsWith(GeneratorService._NEW_PILE_PREFIX_TRADE_ID);
        if(!isNP) {
            claimsByOrder(request);
        }else{
            //囤货单
            claimsByNewPileOrder(request);
        }
        return BaseResponse.SUCCESSFUL();
    }

    private void claimsByOrder(RefundForClaimsRequest request) {
        Trade trade = tradeService.detail(request.getOrderNo());
        if (Objects.isNull(trade)) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "订单不存在");
        }
        if (!StringUtils.equals(trade.getBuyer().getAccount(), request.getCustomerAccount())) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "订单购买客户与填写客户不匹配");
        }

        if (StringUtils.isNotBlank(request.getReturnOrderNo())) {
            ReturnOrder returnOrder = returnOrderService.findByIdNoException(request.getReturnOrderNo());
            if (Objects.isNull(returnOrder)) {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "退单不存在");
            }
            if (!StringUtils.equals(returnOrder.getTid(), trade.getId())) {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "订单号与退单号不匹配");
            }
        }

        ClaimsApply claimsApply = claimsApplyService.saveApply(request);

        ReturnOrder returnOrder = new ReturnOrder();
        ReturnPrice returnPrice = new ReturnPrice();
        returnPrice.setApplyPrice(request.getRechargeBalance());
        returnOrder.setId(claimsApply.getApplyNo());
        returnOrder.setReturnPrice(returnPrice);
        returnOrder.setDescription(request.getRemark());
        returnOrder.setChaimApllyType(claimsApply.getApplyType().getId());

        RefundFactory.getTradeRefundImpl(RefundFactory.TradeRefundType.CLAIMS)
                .refund(trade, returnOrder);
    }

    private void claimsByNewPileOrder(RefundForClaimsRequest request) {
        NewPileTrade trade = newPileTradeService.detail(request.getOrderNo());
        if (Objects.isNull(trade)) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "订单不存在");
        }
        if (!StringUtils.equals(trade.getBuyer().getAccount(), request.getCustomerAccount())) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "订单购买客户与填写客户不匹配");
        }

        if (StringUtils.isNotBlank(request.getReturnOrderNo())) {
            NewPileReturnOrder returnOrder = newPileReturnOrderService.findReturnPileOrderById(request.getReturnOrderNo());
            if (Objects.isNull(returnOrder)) {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "退单不存在");
            }
            if (!StringUtils.equals(returnOrder.getTid(), trade.getId())) {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "订单号与退单号不匹配");
            }
        }


        ClaimsApply claimsApply = claimsApplyService.saveApply(request);

        NewPileReturnOrder returnOrder = new NewPileReturnOrder();
        ReturnPrice returnPrice = new ReturnPrice();
        returnPrice.setApplyPrice(request.getRechargeBalance());
        returnOrder.setId(claimsApply.getApplyNo());
        returnOrder.setReturnPrice(returnPrice);
        returnOrder.setDescription(request.getRemark());
        returnOrder.setChaimApllyType(claimsApply.getApplyType().getId());

        RefundFactory.getNewPileRefundImpl(RefundFactory.NewPileRefundType.NEW_PILE_CLAIMS)
                .refund(trade, returnOrder);
    }

    @Override
    public BaseResponse<RefundForClaimsApplyPageResponse> getRefundForClaimsApplyPage(RefundForClaimsApplyPageRequest request) {
        return BaseResponse.success(claimsApplyService.page(request));
    }

    @Override
    public BaseResponse<List<RefundForClaimsApplyVO>> exportChaimsApply(RefundForClaimsApplyPageRequest request) {
        return BaseResponse.success(claimsApplyService.queryList(request));
    }

    @Override
    public BaseResponse<RefundForClaimsApplyVO> getApplyDetail(String applyNo) {
        return BaseResponse.success(claimsApplyService.getDetail(applyNo));
    }

    @Override
    public BaseResponse refundOrderSuccess(String businessId, String rid, Boolean refunded, String msg) {
        refundOrderService.refundOrderSuccess(businessId, rid, refunded, msg);
        return BaseResponse.SUCCESSFUL();
    }

}