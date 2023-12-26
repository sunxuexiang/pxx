package com.wanmi.sbc.wallet.wallet.service;

import com.wanmi.sbc.wallet.api.provider.wallet.CustomerWalletProvider;
import com.wanmi.sbc.wallet.api.provider.wallet.CustomerWalletQueryProvider;
import com.wanmi.sbc.wallet.api.provider.wallet.WalletRecordProvider;
import com.wanmi.sbc.wallet.api.request.wallet.AddWalletRecordRecordRequest;
import com.wanmi.sbc.wallet.api.request.wallet.CustomerAddAmountRequest;
import com.wanmi.sbc.wallet.api.request.wallet.CustomerWalletRefundRequest;
import com.wanmi.sbc.wallet.api.request.wallet.WalletByCustomerIdQueryRequest;
import com.wanmi.sbc.wallet.bean.enums.BudgetType;
import com.wanmi.sbc.wallet.bean.enums.TradeStateEnum;
import com.wanmi.sbc.wallet.bean.enums.WalletDetailsType;
import com.wanmi.sbc.wallet.bean.enums.WalletRecordTradeType;
import com.wanmi.sbc.wallet.bean.vo.CusWalletVO;
//import com.wanmi.sbc.order.api.request.refund.ModifyWalletBalanceForRefundRequest;
//import com.wanmi.sbc.order.refund.model.root.RefundOrder;
//import com.wanmi.sbc.order.returnorder.model.entity.ReturnItem;
//import com.wanmi.sbc.order.returnorder.model.root.ReturnOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
@Transactional
public class CustomerWalletRefundService {

    @Autowired
    private CustomerWalletQueryProvider walletQueryProvider;

    @Autowired
    private CustomerWalletProvider customerWalletProvider;

    @Autowired
    private WalletRecordProvider walletRecordProvider;

    public void modifyWalletBalanceForRefund(CustomerWalletRefundRequest customerWalletRefundRequest) {
        modifyWalletBalanceForRefundCommon(customerWalletRefundRequest);
    }

    /**
     * 退款到余额
     *
     * @param customerWalletRefundRequest
     */
    public void modifyWalletBalanceForRefundCommon(CustomerWalletRefundRequest customerWalletRefundRequest) {
        //用户余额信息
        CusWalletVO cusWalletVO = walletQueryProvider.getBalanceByCustomerId(WalletByCustomerIdQueryRequest.builder().customerId(customerWalletRefundRequest.getBuyerId()).build())
                .getContext().getCusWalletVO();

        AddWalletRecordRecordRequest request = new AddWalletRecordRecordRequest();
        request.setTradeRemark(WalletDetailsType.INCREASE_ORDER_REFUND.getDesc() + "-" + customerWalletRefundRequest.getRid() + "-" + customerWalletRefundRequest.getTradeRemark());
        //客户账号
        request.setCustomerAccount(cusWalletVO.getCustomerAccount());
        log.info("========================退款的账号：{}", cusWalletVO.getCustomerAccount());
        request.setRelationOrderId(customerWalletRefundRequest.getRid());
        request.setTradeType(WalletRecordTradeType.BALANCE_REFUND);
        request.setBudgetType(BudgetType.INCOME);
        request.setDealPrice(customerWalletRefundRequest.getAmount());
        request.setRemark(WalletDetailsType.INCREASE_ORDER_REFUND.getDesc() + "-" + customerWalletRefundRequest.getRid() + "-" + customerWalletRefundRequest.getRemark());
        request.setCurrentBalance(cusWalletVO.getBalance());
        request.setTradeState(TradeStateEnum.PAID);
        request.setPayType(1);
//                            request.setBalance(amount.add(refundRequest.getBalancePrice()).add(customerWalletVO.getBalance()));
        request.setBalance(customerWalletRefundRequest.getAmount().add(cusWalletVO.getBalance()));
        walletRecordProvider.addWalletRecord(request);

        customerWalletProvider.addAmount(CustomerAddAmountRequest.builder()
                .amount(customerWalletRefundRequest.getAmount())
                .customerId(customerWalletRefundRequest.getBuyerId())
                .businessId(customerWalletRefundRequest.getRid())
                .customerAccount(customerWalletRefundRequest.getCustomerAccount())
                .build());
    }
/*

    */
/**
     * 退款到余额
     *
     * @param trade
     * @param returnOrder
     * @param refundOrder
     *//*

    public void modifyWalletBalanceForRefund(Trade trade, ReturnOrder returnOrder, RefundOrder refundOrder) {
        //线下付款也是走此退至鲸币，所以trade包含了O单和OPK单，现在需要，O单不退至鲸币，OPK单还退至鲸币，所以过滤掉O单即可
        if (Objects.nonNull(trade.getActivityType()) && TradeActivityTypeEnum.TRADE.toActivityType().equals(trade.getActivityType())) {
            return;
        }
        BigDecimal amount = returnOrder.getReturnPrice().getApplyStatus() ? returnOrder.getReturnPrice().getApplyPrice() : refundOrder.getReturnPrice();
        TradePrice tradePrice = trade.getTradePrice();
        BigDecimal reduceSplitPrice = trade.getTradeItems().stream().map(TradeItem::getSplitPrice).reduce(BigDecimal.ZERO, BigDecimal::add);

        String tradePriceRemark = "商品费用:" + "(" + reduceSplitPrice + ")" + "-鲸币抵扣:" + "(" + tradePrice.getBalancePrice() + ")"
                + "-运费:" + "(" + tradePrice.getDeliveryPrice() + ")" + "-包装费用:" + "(" + tradePrice.getPackingPrice() + ")";

        //用户余额信息
        CustomerWalletVO customerWalletVO = walletQueryProvider.getBalanceByCustomerId(WalletByCustomerIdQueryRequest.builder().customerId(trade.getBuyer().getId()).build())
                .getContext().getCustomerWalletVO();

        AddWalletRecordRecordRequest request = new AddWalletRecordRecordRequest();
        request.setTradeRemark(WalletDetailsType.INCREASE_ORDER_REFUND.getDesc() + "-" + refundOrder.getReturnOrderCode() + "-" + tradePriceRemark);
        //客户账号
        request.setCustomerAccount(customerWalletVO.getCustomerAccount());
        log.info("========================退款的账号：{}", customerWalletVO.getCustomerAccount());
        request.setRelationOrderId(refundOrder.getReturnOrderCode());
        request.setTradeType(WalletRecordTradeType.BALANCE_REFUND);
        request.setBudgetType(BudgetType.INCOME);
        request.setDealPrice(amount);
        request.setRemark(WalletDetailsType.INCREASE_ORDER_REFUND.getDesc() + "-" + refundOrder.getReturnOrderCode() + "-" + tradePriceRemark);
        request.setCurrentBalance(customerWalletVO.getBalance());
        request.setTradeState(TradeStateEnum.PAID);
        request.setPayType(1);
//                            request.setBalance(amount.add(refundRequest.getBalancePrice()).add(customerWalletVO.getBalance()));
        request.setBalance(amount.add(customerWalletVO.getBalance()));
        walletRecordProvider.addWalletRecord(request);

        customerWalletProvider.addAmount(CustomerAddAmountRequest.builder()
                .amount(amount)
                .customerId(trade.getBuyer().getId())
                .businessId(trade.getId())
                .customerAccount(trade.getBuyer().getAccount())
                .build());
    }

    */
/**
     * 退款到余额
     *
     * @param modifyRequest
     *//*

    public void modifyWalletBalanceForRefundCommon(ModifyWalletBalanceForRefundRequest modifyRequest) {
        //用户余额信息
        CustomerWalletVO customerWalletVO = walletQueryProvider.getBalanceByCustomerId(WalletByCustomerIdQueryRequest.builder().customerId(modifyRequest.getBuyerId()).build())
                .getContext().getCustomerWalletVO();

        AddWalletRecordRecordRequest request = new AddWalletRecordRecordRequest();
        request.setTradeRemark(WalletDetailsType.INCREASE_ORDER_REFUND.getDesc() + "-" + modifyRequest.getRid() + "-" + modifyRequest.getTradeRemark());
        //客户账号
        request.setCustomerAccount(customerWalletVO.getCustomerAccount());
        log.info("========================退款的账号：{}", customerWalletVO.getCustomerAccount());
        request.setRelationOrderId(modifyRequest.getRid());
        request.setTradeType(WalletRecordTradeType.BALANCE_REFUND);
        request.setBudgetType(BudgetType.INCOME);
        request.setDealPrice(modifyRequest.getAmount());
        request.setRemark(WalletDetailsType.INCREASE_ORDER_REFUND.getDesc() + "-" + modifyRequest.getRid() + "-" + modifyRequest.getRemark());
        request.setCurrentBalance(customerWalletVO.getBalance());
        request.setTradeState(TradeStateEnum.PAID);
        request.setPayType(1);
//                            request.setBalance(amount.add(refundRequest.getBalancePrice()).add(customerWalletVO.getBalance()));
        request.setBalance(modifyRequest.getAmount().add(customerWalletVO.getBalance()));
        walletRecordProvider.addWalletRecord(request);

        customerWalletProvider.addAmount(CustomerAddAmountRequest.builder()
                .amount(modifyRequest.getAmount())
                .customerId(modifyRequest.getBuyerId())
                .businessId(modifyRequest.getRid())
                .customerAccount(modifyRequest.getCustomerAccount())
                .build());
    }

    */
/**
     * 获取余额退款总额
     *
     * @param trade
     * @param returnOrder
     * @return
     *//*

    private BigDecimal getRefundBalancePrice(Trade trade, ReturnOrder returnOrder) {
        BigDecimal balancePrice = BigDecimal.ZERO;
        List<ReturnItem> returnItems = returnOrder.getReturnItems();
        List<TradeItem> tradeItems = trade.getTradeItems();

        List<String> returnItemSkuIds = returnItems.stream().map(ReturnItem::getSkuId).collect(Collectors.toList());
        List<TradeItem> returnItem = tradeItems.stream().filter(tradeItem -> returnItemSkuIds.contains(tradeItem.getSkuId())).collect(Collectors.toList());

        balancePrice = returnItem
                .stream().flatMap(tradeItem -> tradeItem.getWalletSettlements().stream()).collect(Collectors.toList())
                .stream().map(TradeItem.WalletSettlement::getReduceWalletPrice).reduce(BigDecimal.ZERO, BigDecimal::add);

        return balancePrice;
    }

    */
/**
     * 退款到余额
     *
     * @param trade
     * @param refundOrder
     * @param amount
     *//*

    private void modifyWalletBalanceForRefundNewPile(NewPileTrade trade, RefundOrder refundOrder, BigDecimal amount) {
        BalanceByCustomerIdResponse balanceByCustomerIdResponse = walletQueryProvider.getBalanceByCustomerId(WalletByCustomerIdQueryRequest.builder().customerId(
                trade.getBuyer().getId()).build()).getContext();

        TradePrice tradePrice = trade.getTradePrice();
        BigDecimal reduceSplitPrice = trade.getTradeItems().stream().map(TradeItem::getSplitPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        String tradePriceRemark = "商品费用:" + "(" + reduceSplitPrice + ")" + "-鲸币抵扣:" + "(" + tradePrice.getBalancePrice() + ")"
                + "-运费:" + "(" + tradePrice.getDeliveryPrice() + ")" + "-包装费用:" + "(" + tradePrice.getPackingPrice() + ")";

        AddWalletRecordRecordRequest request = new AddWalletRecordRecordRequest();
        request.setTradeRemark(WalletDetailsType.INCREASE_ORDER_REFUND.getDesc() + "-" + refundOrder.getReturnOrderCode() + "-" + tradePriceRemark);
        //客户账号
        request.setCustomerAccount(balanceByCustomerIdResponse.getCustomerWalletVO().getCustomerAccount());
        log.info("========================退款的账号：{}", balanceByCustomerIdResponse.getCustomerWalletVO().getCustomerAccount());
        request.setRelationOrderId(refundOrder.getReturnOrderCode());
        request.setTradeType(WalletRecordTradeType.BALANCE_REFUND);
        request.setBudgetType(BudgetType.INCOME);
        request.setDealPrice(amount);
        request.setRemark(WalletDetailsType.INCREASE_ORDER_REFUND.getDesc() + "-" + refundOrder.getReturnOrderCode() + "-" + tradePriceRemark);
        request.setCurrentBalance(balanceByCustomerIdResponse.getCustomerWalletVO().getBalance());
        request.setTradeState(TradeStateEnum.PAID);
        request.setPayType(1);
        request.setBalance(amount.add(balanceByCustomerIdResponse.getCustomerWalletVO().getBalance()));
        walletRecordProvider.addWalletRecord(request);
        customerWalletProvider.addAmount(CustomerAddAmountRequest.builder()
                .amount(amount)
                .customerId(trade.getBuyer().getId())
                .businessId(trade.getId())
                .customerAccount(trade.getBuyer().getAccount())
                .build());
    }
*/

}
