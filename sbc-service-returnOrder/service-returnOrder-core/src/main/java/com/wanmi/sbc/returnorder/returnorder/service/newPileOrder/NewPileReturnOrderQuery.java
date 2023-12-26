package com.wanmi.sbc.returnorder.returnorder.service.newPileOrder;

import com.wanmi.sbc.account.api.provider.finance.record.AccountRecordProvider;
import com.wanmi.sbc.account.api.request.finance.record.AccountRecordAddRequest;
import com.wanmi.sbc.account.bean.enums.PayWay;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.returnorder.refund.model.root.RefundOrder;
import com.wanmi.sbc.returnorder.refund.service.RefundOrderService;
import com.wanmi.sbc.returnorder.returnorder.model.root.NewPileReturnOrder;
import com.wanmi.sbc.returnorder.returnorder.repository.NewPileReturnOrderRepository;
import com.wanmi.sbc.pay.api.provider.PayQueryProvider;
import com.wanmi.sbc.pay.api.request.TradeRecordByOrderCodeRequest;
import com.wanmi.sbc.pay.api.response.PayTradeRecordResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class NewPileReturnOrderQuery {

    @Autowired
    private NewPileReturnOrderRepository newPileReturnOrderRepository;

    @Autowired
    private RefundOrderService refundOrderService;

    @Autowired
    private PayQueryProvider payQueryProvider;

    @Autowired
    private AccountRecordProvider accountRecordProvider;


    /**
     * 查询可退金额囤货
     *
     * @param rid
     * @return
     */
    public NewPileReturnOrder findByIdNewPile(String rid) {
        NewPileReturnOrder returnOrder = newPileReturnOrderRepository.findById(rid).orElse(null);
        if (returnOrder == null) {
            throw new SbcRuntimeException("K-050003");
        }
        return returnOrder;
    }

    /**
     * 保存退款对账明细
     *
     * @param returnPileOrder
     * @param payWayStr
     */
    @Transactional
    public void saveReconciliation(NewPileReturnOrder returnPileOrder, String payWayStr, String businessId, String tradeNo) {
        RefundOrder refundOrder = refundOrderService.findRefundOrderByReturnOrderNo(returnPileOrder.getId());
        if (Objects.isNull(refundOrder)) {
            return;
        }
        AccountRecordAddRequest reconciliation = new AccountRecordAddRequest();
        reconciliation.setAmount(returnPileOrder.getReturnPrice().getApplyStatus() ?
                returnPileOrder.getReturnPrice().getApplyPrice() : returnPileOrder.getReturnPrice().getTotalPrice());
        reconciliation.setCustomerId(returnPileOrder.getBuyer().getId());
        reconciliation.setCustomerName(returnPileOrder.getBuyer().getName());
        reconciliation.setOrderCode(returnPileOrder.getTid());
        reconciliation.setOrderTime(returnPileOrder.getCreateTime());
        reconciliation.setTradeTime(Objects.isNull(refundOrder.getRefundBill()) ? LocalDateTime.now() :
                refundOrder.getRefundBill().getCreateTime());

        if (StringUtils.isNotBlank(businessId)) {
            // 根据订单id查询流水号并存进对账明细
            TradeRecordByOrderCodeRequest request = new TradeRecordByOrderCodeRequest();
            request.setOrderId(businessId);
            BaseResponse<PayTradeRecordResponse> record = payQueryProvider.getTradeRecordByOrderCode(request);
            if (Objects.isNull(record)) {
                record = payQueryProvider.getTradeRecordByOrderCode(new TradeRecordByOrderCodeRequest(businessId));
            }
            if (Objects.nonNull(record) && Objects.nonNull(record.getContext()) && StringUtils.isNotEmpty(record
                    .getContext().getTradeNo())) {
                tradeNo = record.getContext().getTradeNo();
            }
        }
        reconciliation.setTradeNo(tradeNo);
        // 退款金额等于0 退款渠道标记为银联渠道
        if (refundOrder.getReturnPrice().compareTo(BigDecimal.ZERO) == 0) {
            payWayStr = "unionpay";
        }
        PayWay payWay;
        payWayStr = StringUtils.isBlank(payWayStr) ? payWayStr : payWayStr.toUpperCase();
        switch (payWayStr) {
            case "ALIPAY":
                payWay = PayWay.ALIPAY;
                break;
            case "WECHAT":
                payWay = PayWay.WECHAT;
                break;
            case "UNIONPAY":
                payWay = PayWay.UNIONPAY;
                break;
            case "UNIONPAY_B2B":
                payWay = PayWay.UNIONPAY_B2B;
                break;
            case "BALANCE":
                payWay = PayWay.BALANCE;
                break;
            default:
                payWay = PayWay.CASH;
        }
        reconciliation.setPayWay(payWay);
        reconciliation.setReturnOrderCode(returnPileOrder.getId());
        reconciliation.setStoreId(returnPileOrder.getCompany().getStoreId());
        reconciliation.setSupplierId(returnPileOrder.getCompany().getCompanyInfoId());
        reconciliation.setType((byte) 1);

        LocalDateTime tradeTime = LocalDateTime.now();
        if (Objects.nonNull(refundOrder.getRefundBill()) && Objects.nonNull(refundOrder.getRefundBill().getCreateTime())) {
            tradeTime = refundOrder.getRefundBill().getCreateTime();
        }
        reconciliation.setTradeTime(tradeTime);
        accountRecordProvider.add(reconciliation);
    }

    /**
     * 修改文档
     * 专门用于数据修改服务,不允许数据新增的时候调用
     *
     * @param returnOrder
     */
    //@MongoRollback(persistence = NewPileReturnOrder.class, operation = Operation.UPDATE)
    public void updateReturnOrder(NewPileReturnOrder returnOrder) {
        newPileReturnOrderRepository.save(returnOrder);
    }

    /**
     * 退单号批量获取
     * @param returnOrderNos
     * @return
     */
    public List<NewPileReturnOrder> getNewPileReturnOrderByReturnOrderNos(List<String> returnOrderNos){
        return newPileReturnOrderRepository.findByIdIn(returnOrderNos);
    }
}
