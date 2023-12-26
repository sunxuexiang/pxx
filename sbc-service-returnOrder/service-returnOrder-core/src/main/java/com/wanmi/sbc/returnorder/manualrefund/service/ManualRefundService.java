package com.wanmi.sbc.returnorder.manualrefund.service;

import com.wanmi.sbc.account.bean.enums.RefundStatus;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.GeneratorService;
import com.wanmi.sbc.returnorder.api.request.manualrefund.ManualRefundImgRequest;
import com.wanmi.sbc.returnorder.api.response.manualrefund.ManualRefundImgResponse;
import com.wanmi.sbc.returnorder.api.response.manualrefund.ManualRefundResponse;
import com.wanmi.sbc.returnorder.bean.enums.FlowState;
import com.wanmi.sbc.returnorder.bean.enums.PayState;
import com.wanmi.sbc.returnorder.bean.vo.ManualRefundImgVO;
import com.wanmi.sbc.returnorder.bean.vo.TradeVO;
import com.wanmi.sbc.returnorder.manualrefund.model.root.ManualRefund;
import com.wanmi.sbc.returnorder.manualrefund.repository.ManualRefundRepository;
import com.wanmi.sbc.returnorder.payorder.response.PayOrderResponse;
import com.wanmi.sbc.returnorder.trade.model.entity.TradeState;
import com.wanmi.sbc.returnorder.trade.model.newPileTrade.NewPileTrade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ManualRefundService {

    @Autowired
    private ManualRefundRepository manualRefundRepository;

    @Autowired
    private GeneratorService generatorService;

    @Autowired
    private ManualRefundImgService manualRefundImgService;


    /**
     * 根据订单号查询退款列表
     *
     * @param orderCode 订单编号
     * @return 退款单列表
     */
    public List<ManualRefundResponse> findManualRefundOrdersRespByOrderCode(String orderCode) {

        List<ManualRefundResponse> manualRefundResponses = new ArrayList<ManualRefundResponse>();

        List<ManualRefund> manualRefunds = manualRefundRepository.findByOrderCodeAndRefundStatus(orderCode, RefundStatus.FINISH);

        ManualRefundImgResponse manualRefundImgResponse = manualRefundImgService.listManualRefundImgsByrefundIds(ManualRefundImgRequest.builder().refundIdList(manualRefunds.stream().map(ManualRefund::getRefundId).collect(Collectors.toList())).build());
        List<ManualRefundImgVO> manualRefundImgVOList = manualRefundImgResponse.getManualRefundImgVOList();
        Boolean b = !CollectionUtils.isEmpty(manualRefundImgVOList);

        manualRefunds.forEach(manualRefund -> {
            ManualRefundResponse manualRefundResponse = new ManualRefundResponse();
            BeanUtils.copyProperties(manualRefund, manualRefundResponse);

            List<ManualRefundImgVO> manualRefundImgVOListTem = b ? manualRefundImgVOList.stream().filter(manualRefundImgVO -> Objects.equals(manualRefund.getRefundId(), manualRefundImgVO.getRefundId())).collect(Collectors.toList()) : null;
            manualRefundResponse.setManualRefundImgVOList(manualRefundImgVOListTem);
            manualRefundResponses.add(manualRefundResponse);
        });

        return manualRefundResponses;
    }

    public Optional<ManualRefund> save(TradeVO trade, BigDecimal applyPrice, String payOrderNo, String refuseReason, String customerDetailId) {
        ManualRefund refund = new ManualRefund();
        refund.setCustomerDetailId(customerDetailId);
        refund.setPayType(1);
        refund.setTotalPrice(trade.getTradePrice().getTotalPrice());
        refund.setApplyPrice(applyPrice);
        refund.setOrderCode(trade.getId());
        refund.setPayOrderNo(payOrderNo);
        refund.setRefundCode(generatorService.generateRid());
        refund.setRefundStatus(RefundStatus.TODO);
        refund.setRefuseReason(refuseReason);
        refund.setDelFlag(DeleteFlag.NO);
        refund.setCreateTime(LocalDateTime.now());
        return Optional.ofNullable(manualRefundRepository.saveAndFlush(refund));
    }

    public void update(ManualRefund manualRefund) {
        manualRefundRepository.saveAndFlush(manualRefund);
    }

    /**
     * 根据支付单来判断是否显示退款
     *
     * @param payOrderResponse
     * @return
     */
    public PayOrderResponse judgeDisplayRefund(PayOrderResponse payOrderResponse) {
        log.info("渠道id={}", payOrderResponse.getPayChannelId());
        payOrderResponse.setIsDisplay(false);
        if (payOrderResponse.getPayChannelId() == null) {
            return payOrderResponse;
        }
        if (payOrderResponse.getPayChannelId().longValue() == 29l || payOrderResponse.getPayChannelId().longValue() == 30l) {
            TradeState tradeState = payOrderResponse.getTradeState();
            if (Objects.nonNull(tradeState)) {
                if (PayState.NOT_PAID.equals(tradeState.getPayState()) ||
                        PayState.UNCONFIRMED.equals(tradeState.getPayState()) ||
                        FlowState.REFUND.equals(tradeState.getFlowState())) {//支付状态：未支付、未确认；流程状态：已退款、已作废 不能退款
                    return payOrderResponse;
                }
            } else {
                return payOrderResponse;
            }
            BigDecimal totalPrice = payOrderResponse.getTotalPrice();//订单总金额
            BigDecimal refundPrice = new BigDecimal(0);//退款总金额
            List<ManualRefund> manualRefunds = manualRefundRepository.findByOrderCodeAndRefundStatus(payOrderResponse.getOrderCode(), RefundStatus.FINISH);
            if (!CollectionUtils.isEmpty(manualRefunds)) {
                for (ManualRefund manualRefund : manualRefunds) {
                    refundPrice = refundPrice.add(manualRefund.getApplyPrice());
                }
                if (refundPrice.compareTo(totalPrice) == -1) {//退款金额小于订单金额
                    payOrderResponse.setIsDisplay(true);
                }
            } else {
                payOrderResponse.setIsDisplay(true);
            }
            payOrderResponse.setRefundPrice(refundPrice);
            return payOrderResponse;
        }
        return payOrderResponse;
    }

    public Optional<ManualRefund> saveNewPileTrade(NewPileTrade newPileTrade, BigDecimal applyPrice, String payOrderNo, String refuseReason, String customerDetailId) {
        ManualRefund refund = new ManualRefund();
        refund.setCustomerDetailId(customerDetailId);
        refund.setPayType(1);
        refund.setTotalPrice(newPileTrade.getTradePrice().getTotalPrice());
        refund.setApplyPrice(applyPrice);
        refund.setOrderCode(newPileTrade.getId());
        refund.setPayOrderNo(payOrderNo);
        refund.setRefundCode(generatorService.generateRid());
        refund.setRefundStatus(RefundStatus.TODO);
        refund.setRefuseReason(refuseReason);
        refund.setDelFlag(DeleteFlag.NO);
        refund.setCreateTime(LocalDateTime.now());
        return Optional.ofNullable(manualRefundRepository.saveAndFlush(refund));
    }
}
