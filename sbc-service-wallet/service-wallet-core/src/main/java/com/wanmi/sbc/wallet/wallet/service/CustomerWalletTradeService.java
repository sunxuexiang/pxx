package com.wanmi.sbc.wallet.wallet.service;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.walletorder.bean.vo.*;
import com.wanmi.sbc.wallet.api.provider.wallet.CustomerWalletProvider;
import com.wanmi.sbc.wallet.api.provider.wallet.CustomerWalletQueryProvider;
import com.wanmi.sbc.wallet.api.provider.wallet.WalletRecordProvider;
import com.wanmi.sbc.wallet.api.request.wallet.*;
import com.wanmi.sbc.wallet.api.response.wallet.AddWalletRecordResponse;
import com.wanmi.sbc.wallet.bean.enums.BudgetType;
import com.wanmi.sbc.wallet.bean.enums.TradeStateEnum;
import com.wanmi.sbc.wallet.bean.enums.WalletDetailsType;
import com.wanmi.sbc.wallet.bean.enums.WalletRecordTradeType;
import com.wanmi.sbc.wallet.bean.vo.CusWalletVO;
import com.wanmi.sbc.wallet.mq.MessageMqService;
import com.wanmi.sbc.wallet.wallet.repository.CustomerWalletRepository;
import com.wanmi.sbc.wallet.wallet.repository.WalletRecordRepository;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.ResultCode;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.GeneratorService;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.loginregister.CustomerSiteProvider;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.walletorder.trade.model.entity.TradeItem;
import com.wanmi.sbc.walletorder.trade.model.entity.value.Buyer;
import com.wanmi.sbc.walletorder.trade.model.entity.value.Supplier;
import com.wanmi.sbc.walletorder.trade.model.entity.value.TradePrice;
import com.wanmi.sbc.walletorder.trade.model.newPileTrade.NewPileTrade;
import com.wanmi.sbc.walletorder.trade.model.root.Trade;
//import com.wanmi.sbc.order.trade.service.TradeItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@Slf4j
@Transactional
public class CustomerWalletTradeService {

    @Autowired
    private CustomerWalletRepository customerWalletRepository;

    @Autowired
    private WalletRecordRepository walletRecordRepository;

    @Autowired
    private GeneratorService generatorService;

    @Autowired
    private CustomerQueryProvider customerQueryProvider;

    @Autowired
    private CustomerSiteProvider customerSiteProvider;

    @Autowired
    private MessageMqService messageMqService;

//    @Autowired
//    private TradeItemService tradeItemService;

    @Autowired
    private CustomerWalletQueryProvider walletQueryProvider;

    @Autowired
    private CustomerWalletProvider customerWalletProvider;

    @Autowired
    private WalletRecordProvider walletRecordProvider;

    public CusWalletVO checkoutWallet(CustomerWalletCheckoutRequest build) {
        BigDecimal walletBalance = build.getWalletBalance() == null ? BigDecimal.ZERO : build.getWalletBalance();
        //校验
        if (!(walletBalance.compareTo(BigDecimal.ZERO) > 0)) {
            return null;
        }
        //用户余额信息
        CusWalletVO cusWalletVO = walletQueryProvider.getBalanceByCustomerId(WalletByCustomerIdQueryRequest.builder().customerId(build.getCustomer().getCustomerId()).build())
                .getContext().getCusWalletVO();
        if (cusWalletVO == null || !(cusWalletVO.getBalance().compareTo(BigDecimal.ZERO) > 0) || build.getWalletBalance().compareTo(cusWalletVO.getBalance()) > 0) {
            throw new SbcRuntimeException("没有可用余额, 请重新检查");
        }
        return cusWalletVO;
    }

    public List<TradeVO> useWallet(CustomerWalletUseRequest customerWalletUseRequest) {
        List<Trade> convert = convert = KsBeanUtil.convert(customerWalletUseRequest.getTradeList(), Trade.class);
        useWalletSecondEdition(convert, customerWalletUseRequest.getWalletBalance(), customerWalletUseRequest.getCustomer(), customerWalletUseRequest.getCusWalletVO());
        return KsBeanUtil.convert(convert,TradeVO.class);
    }


    //todo:balance待优化

    /**
     * 使用余额扣减订单
     *
     * @param trades
     * @param customer
     * @param cusWalletVO
     */
    public void useWallet(List<Trade> trades, BigDecimal walletBalance, CustomerVO customer, CusWalletVO cusWalletVO) {
        //校验
        if (walletBalance == null || !(walletBalance.compareTo(BigDecimal.ZERO) > 0)) {
            return;
        }
        //可用余额
        BigDecimal balance = cusWalletVO.getBalance();

        //运费问题: 如果有运费, 则商品应付金额可以用余额全部抵扣, 如果没有运费, 则商品应付金额应该保留1元, 其他用余额抵扣均摊
        //订单是否存在运费
        boolean booleanBeliveryPrice = trades.stream().anyMatch(trade -> trade.getTradePrice().getDeliveryPrice().compareTo(BigDecimal.ZERO) == 1);

        //===============================先抵扣商品总价
        //1 需要余额扣减的订单商品
        List<TradeItem> collect = trades.stream().flatMap(trade -> trade.getTradeItems().stream()).collect(Collectors.toList());
        log.info("================ trades ================:{}", JSONObject.toJSONString(trades));

        //最低商品支付金额(使用bNum)
        //Long totalNum = collect.stream().map(TradeItem::getNum).reduce(0L, Long::sum);
        BigDecimal reduce = collect.stream().map(TradeItem::getBNum).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal bottomPrice = new BigDecimal("0.02").multiply(reduce);

        //2 订单商品总价
        BigDecimal total = this.calcSkusTotalPrice(collect);
        //2.1 新 订单商品总价 (没有加运费)
        BigDecimal newTotal = total.subtract(bottomPrice).compareTo(balance) > 0 ? total.subtract(balance) : bottomPrice;
        //此次扣减的余额
        BigDecimal deductionTotal = total.subtract(newTotal);
        //剩余余额
        BigDecimal residueBalance = balance.subtract(deductionTotal);
        log.info("================ total:{}   newTotal:{}   deductionTotal:{}"
                , JSONObject.toJSONString(total), JSONObject.toJSONString(newTotal), JSONObject.toJSONString(deductionTotal));

        //设置余额结算信息
        setWalletSettlement(trades, collect, total, newTotal);


        /*//3 设置关联商品的结算信息
        for (TradeItem tradeItem : collect) {
            tradeItem.getWalletSettlements().add(
                    TradeItem.WalletSettlement.builder().reduceWalletPrice(BigDecimal.ZERO).splitPrice(tradeItem.getSplitPrice()).build()
            );
        }

        //4 设置关联商品的均摊价格 (已修改均摊价格)
        this.calcSplitPrice(collect, newTotal, total);

        //5 刷新关联商品的结算信息
        for (TradeItem tradeItem : collect) {
            TradeItem.WalletSettlement walletSettlement = tradeItem.getWalletSettlements().get(tradeItem.getWalletSettlements().size() - 1);
            walletSettlement.setReduceWalletPrice(walletSettlement.getSplitPrice().subtract(tradeItem.getSplitPrice()));
            walletSettlement.setSplitPrice(tradeItem.getSplitPrice());
        }

        //===============================再抵扣包装费用(不可以抵扣包装费用)
        //addBalancePackingPrice(trades, residueBalance);

        //6 按照店铺分组被均摊的商品, 刷新相应的订单的价格信息
        Map<Long, List<TradeItem>> itemsMap = collect.stream().collect(Collectors.groupingBy(TradeItem::getStoreId));

        for (Long storeIdKey : itemsMap.keySet()) {
            //找到店铺对应订单的价格信息
            Trade trade = trades.stream().filter(t -> t.getSupplier().getStoreId().equals(storeIdKey)).findFirst().orElse(null);
            List<TradeItem> tradeItems = itemsMap.get(storeIdKey);
            //订单金额, (余额暂未修改)
            TradePrice tradePrice = trade.getTradePrice();

            //设置余额扣减金额
            //tradePrice.setBalancePrice(deductionTotal);
            BigDecimal sumReduceWalletPrice = tradeItems
                    .stream().flatMap(tradeItem -> tradeItem.getWalletSettlements().stream()).collect(Collectors.toList())
                    .stream().map(TradeItem.WalletSettlement::getReduceWalletPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            tradePrice.setBalancePrice(sumReduceWalletPrice);

            //商品集合的均摊总价
            BigDecimal couponTotalPrice = this.calcSkusTotalPrice(itemsMap.get(storeIdKey));
            //重设订单总价(并追加配送费用)(并追加包装费用)
            //tradePrice.setTotalPrice(couponTotalPrice);
            //tradePrice.setOriginPrice(tradePrice.getGoodsPrice());
            tradePrice.setTotalPrice(couponTotalPrice.add(tradePrice.getDeliveryPrice()).add(tradePrice.getPackingPrice()));

            trade.getTradeItems().forEach(tradeItem -> {
                TradeItem matchItem = collect.stream().filter(item -> item.getSkuId().equals(tradeItem.getSkuId())).findFirst().orElse(null);
                if (Objects.nonNull(tradeItem.getDevanningId())) {
                    matchItem = collect.stream().filter(item -> item.getDevanningId().equals(tradeItem.getDevanningId())).findFirst().orElse(null);
                }
                if (matchItem != null) {
                    tradeItem.setSplitPrice(matchItem.getSplitPrice());
                }
            });
        }
        log.info("================ trades ================:{}", JSONObject.toJSONString(trades));*/
    }

    //todo:balance待优化

    /**
     * 使用余额扣减订单-第二版
     *
     * @param trades
     * @param customer
     * @param cusWalletVO
     */
    public void useWalletSecondEdition(List<Trade> trades, BigDecimal walletBalance, CustomerVO customer, CusWalletVO cusWalletVO) {
        //校验
//        if (walletBalance == null || !(walletBalance.compareTo(BigDecimal.ZERO) > 0)) {
//            return;
//        }
        if (walletBalance == null || !(walletBalance.compareTo(BigDecimal.ZERO) > 0)) {
            return;
        }
        if (walletBalance.compareTo(BigDecimal.ZERO) > 0) {
            throw new SbcRuntimeException("K-000099", "鲸币暂停使用，请关闭使用鲸币选项，请联系客服！！！");
        }

        //可用余额
        BigDecimal balance = cusWalletVO.getBalance();

        //运费问题: 如果有运费, 则商品应付金额可以用余额全部抵扣, 如果没有运费, 则商品应付金额应该保留1元, 其他用余额抵扣均摊
        //订单是否存在运费
        boolean booleanBeliveryPrice = trades.stream().anyMatch(trade -> trade.getTradePrice().getDeliveryPrice().compareTo(BigDecimal.ZERO) == 1);

        //===============================先抵扣商品总价
        //1 需要余额扣减的订单商品
        List<TradeItem> collect = trades.stream().flatMap(trade -> trade.getTradeItems().stream()).collect(Collectors.toList());
        log.info("================ trades ================:{}", JSONObject.toJSONString(trades));

        //最低商品支付金额(使用bNum)
        //Long totalNum = collect.stream().map(TradeItem::getNum).reduce(0L, Long::sum);
        BigDecimal reduce = collect.stream().map(TradeItem::getBNum).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal bottomPrice = new BigDecimal("0.02").multiply(reduce).setScale(2, BigDecimal.ROUND_DOWN);

        //2 订单商品总价
        BigDecimal total = this.calcSkusTotalPrice(collect);
        //2.1 新 订单商品总价 (没有加运费)
        BigDecimal newTotal = total.subtract(bottomPrice).compareTo(balance) > 0 ? total.subtract(balance) : bottomPrice;
        //此次扣减的余额
        BigDecimal deductionTotal = total.subtract(newTotal);
        //剩余余额
        BigDecimal residueBalance = balance.subtract(deductionTotal);
        log.info("================ total:{}   newTotal:{}   deductionTotal:{}"
                , JSONObject.toJSONString(total), JSONObject.toJSONString(newTotal), JSONObject.toJSONString(deductionTotal));

        //未抵扣鲸贴, 直接返回
        if (deductionTotal.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        BigDecimal splitPriceTotal = BigDecimal.ZERO;//累积平摊价，将剩余扣给最后一个元素
        for (int i = 0; i < trades.size(); i++) {
            //A.1需要扣减的订单
            Trade trade = trades.get(i);
            List<TradeItem> tradeItems = trade.getTradeItems();

            //A.2 此订单商品总价
            BigDecimal totalTradeItem = this.calcSkusTotalPrice(tradeItems);

            //A.3新 此订单商品总价
            BigDecimal bigDecimal = totalTradeItem.divide(total, 10, BigDecimal.ROUND_DOWN).multiply(newTotal).setScale(2, BigDecimal.ROUND_DOWN);
            //A.4此 最低商品支付金额(使用bNum)
            //Long totalNum = collect.stream().map(TradeItem::getNum).reduce(0L, Long::sum);
            BigDecimal thisReduce = tradeItems.stream().map(TradeItem::getBNum).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal thisBottomPrice = new BigDecimal("0.02").multiply(thisReduce).setScale(2, BigDecimal.ROUND_DOWN);
            //A.5需要满足此订单商品最小金额准则(商品数量*0.02)
            bigDecimal = (bigDecimal.compareTo(thisBottomPrice) < 0) ? thisBottomPrice : bigDecimal;

            if (i == trades.size() - 1) {
                bigDecimal = newTotal.subtract(splitPriceTotal);
            } else {
                splitPriceTotal = splitPriceTotal.add(bigDecimal);
            }

            setWalletSettlement(Stream.of(trade).collect(Collectors.toList()), tradeItems, totalTradeItem, bigDecimal);
        }
    }

    //todo:balance待优化

    /**
     * 设置余额结算信息
     *
     * @param trades
     * @param tradeItems
     * @param total
     * @param newTotal
     */
    private void setWalletSettlement(List<Trade> trades, List<TradeItem> tradeItems, BigDecimal total, BigDecimal newTotal) {
        //3 设置关联商品的结算信息
        for (TradeItem tradeItem : tradeItems) {
            tradeItem.getWalletSettlements().add(
                    TradeItem.WalletSettlement.builder().reduceWalletPrice(BigDecimal.ZERO).splitPrice(tradeItem.getSplitPrice()).build()
            );
        }

        //4 设置关联商品的均摊价格 (已修改均摊价格)
        this.calcSplitPriceMinimumAmount(tradeItems, newTotal, total);

        //5 刷新关联商品的结算信息
        for (TradeItem tradeItem : tradeItems) {
            TradeItem.WalletSettlement walletSettlement = tradeItem.getWalletSettlements().get(tradeItem.getWalletSettlements().size() - 1);
            walletSettlement.setReduceWalletPrice(walletSettlement.getSplitPrice().subtract(tradeItem.getSplitPrice()));
            walletSettlement.setSplitPrice(tradeItem.getSplitPrice());
        }

        //===============================再抵扣包装费用(不可以抵扣包装费用)
        //addBalancePackingPrice(trades, residueBalance);

        //6 按照店铺分组被均摊的商品, 刷新相应的订单的价格信息
        Map<Long, List<TradeItem>> itemsMap = tradeItems.stream().collect(Collectors.groupingBy(TradeItem::getStoreId));

        for (Long storeIdKey : itemsMap.keySet()) {
            //找到店铺对应订单的价格信息
            Trade trade = trades.stream().filter(t -> t.getSupplier().getStoreId().equals(storeIdKey)).findFirst().orElse(null);
            List<TradeItem> tradeItemsTem = itemsMap.get(storeIdKey);
            //订单金额, (余额暂未修改)
            TradePrice tradePrice = trade.getTradePrice();

            //设置余额扣减金额
            //tradePrice.setBalancePrice(deductionTotal);
            BigDecimal sumReduceWalletPrice = tradeItemsTem
                    .stream().flatMap(tradeItem -> tradeItem.getWalletSettlements().stream()).collect(Collectors.toList())
                    .stream().map(TradeItem.WalletSettlement::getReduceWalletPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            tradePrice.setBalancePrice(sumReduceWalletPrice);

            //商品集合的均摊总价
            BigDecimal couponTotalPrice = this.calcSkusTotalPrice(itemsMap.get(storeIdKey));
            //重设订单总价(并追加配送费用)(追加包装费用)
            //tradePrice.setTotalPrice(couponTotalPrice);
            //tradePrice.setOriginPrice(tradePrice.getGoodsPrice());
            tradePrice.setTotalPrice(couponTotalPrice.add(tradePrice.getDeliveryPrice()).add(tradePrice.getPackingPrice()));

            trade.getTradeItems().forEach(tradeItem -> {
                TradeItem matchItem = tradeItems.stream().filter(item -> item.getSkuId().equals(tradeItem.getSkuId())).findFirst().orElse(null);
                if (Objects.nonNull(tradeItem.getDevanningId())) {
                    matchItem = tradeItems.stream().filter(item -> item.getDevanningId().equals(tradeItem.getDevanningId())).findFirst().orElse(null);
                }
                if (matchItem != null) {
                    tradeItem.setSplitPrice(matchItem.getSplitPrice());
                }
            });
        }
        log.info("================ trades ================:{}", JSONObject.toJSONString(trades));
    }

    public List<NewPileTradeVO> useWalletForNewPileTrade(CustomerWalletUseRequest customerWalletUseRequest) {
        List<NewPileTrade> convert = KsBeanUtil.convert(customerWalletUseRequest.getNewPileTradeList(), NewPileTrade.class);
        useWalletForNewPileTrade(convert, customerWalletUseRequest.getWalletBalance(), customerWalletUseRequest.getCustomer(), customerWalletUseRequest.getCusWalletVO());
        return KsBeanUtil.convert(convert, NewPileTradeVO.class);
    }


    //todo:balance待优化

    /**
     * 使用鲸币
     *
     * @param trades
     * @param customer
     * @param cusWalletVO
     */
    public void useWalletForNewPileTrade(List<NewPileTrade> trades, BigDecimal walletBalance, CustomerVO customer, CusWalletVO cusWalletVO) {
        BigDecimal walletBalanceTem = walletBalance == null ? BigDecimal.ZERO : walletBalance;
        //校验
//        if (!(walletBalance.compareTo(BigDecimal.ZERO) > 0)) {
//            return;
//        }

        if (walletBalanceTem == null || !(walletBalanceTem.compareTo(BigDecimal.ZERO) > 0)) {
            return;
        }
        if (walletBalanceTem.compareTo(BigDecimal.ZERO) > 0) {
            throw new SbcRuntimeException("K-000099", "鲸币暂停使用，请关闭使用鲸币选项，请联系客服！！！");
        }
        //可用余额
        BigDecimal balance = cusWalletVO.getBalance();

        //运费问题: 如果有运费, 则商品应付金额可以用余额全部抵扣, 如果没有运费, 则商品应付金额应该保留1元, 其他用余额抵扣均摊
        //订单是否存在运费
        boolean booleanBeliveryPrice = trades.stream().anyMatch(trade -> trade.getTradePrice().getDeliveryPrice().compareTo(BigDecimal.ZERO) == 1);

        //1 需要余额扣减的订单商品
        List<TradeItem> collect = trades.stream().flatMap(trade -> trade.getTradeItems().stream()).collect(Collectors.toList());
        log.info("================ trades ================:{}", JSONObject.toJSONString(trades));

        //最低商品支付金额
        Long totalNum = collect.stream().map(TradeItem::getNum).reduce(0L, Long::sum);
        //BigDecimal reduce = collect.stream().map(TradeItem::getBNum).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal bottomPrice = new BigDecimal("0.02").multiply(new BigDecimal(totalNum));

        //2 订单商品总价
        BigDecimal total = this.calcSkusTotalPrice(collect);

        //2.1 新 订单商品总价 (没有加运费)
       /* BigDecimal newTotal = booleanBeliveryPrice
                ? total.compareTo(balance) > 0 ? total.subtract(balance) : BigDecimal.ZERO
                : total.subtract(BigDecimal.ONE).compareTo(balance) > 0 ? total.subtract(balance) : BigDecimal.ONE;*/
        BigDecimal newTotal = total.subtract(bottomPrice).compareTo(balance) > 0 ? total.subtract(balance) : bottomPrice;

        //此次扣减的余额
        BigDecimal deductionTotal = total.subtract(newTotal);

        //3 设置关联商品的结算信息
        for (TradeItem tradeItem : collect) {
            tradeItem.getWalletSettlements().add(
                    TradeItem.WalletSettlement.builder().reduceWalletPrice(balance.subtract(newTotal)).splitPrice(tradeItem.getSplitPrice()).build()
            );
        }

        //4 设置关联商品的均摊价格 (已修改均摊价格)
        this.calcSplitPrice(collect, newTotal, total);

        //5 刷新关联商品的结算信息
        for (TradeItem tradeItem : collect) {
            TradeItem.WalletSettlement walletSettlement = tradeItem.getWalletSettlements().get(tradeItem.getWalletSettlements().size() - 1);
            walletSettlement.setReduceWalletPrice(walletSettlement.getSplitPrice().subtract(tradeItem.getSplitPrice()));
            walletSettlement.setSplitPrice(tradeItem.getSplitPrice());
        }

        //6 按照店铺分组被均摊的商品, 刷新相应的订单的价格信息
        Map<Long, List<TradeItem>> itemsMap = collect.stream().collect(Collectors.groupingBy(TradeItem::getStoreId));

        for (Long storeIdKey : itemsMap.keySet()) {
            //找到店铺对应订单的价格信息
            NewPileTrade trade = trades.stream().filter(t -> t.getSupplier().getStoreId().equals(storeIdKey)).findFirst().orElse(null);
            List<TradeItem> tradeItems = itemsMap.get(storeIdKey);
            //订单金额, (余额暂未修改)
            TradePrice tradePrice = trade.getTradePrice();

            //设置余额扣减金额
            BigDecimal sumReduceWalletPrice = tradeItems
                    .stream().flatMap(tradeItem -> tradeItem.getWalletSettlements().stream()).collect(Collectors.toList())
                    .stream().map(TradeItem.WalletSettlement::getReduceWalletPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            tradePrice.setBalancePrice(sumReduceWalletPrice);

            //商品集合的均摊总价
            BigDecimal couponTotalPrice = this.calcSkusTotalPrice(itemsMap.get(storeIdKey));
            //重设订单总价(并追加配送费用)
            //tradePrice.setTotalPrice(couponTotalPrice);
            //tradePrice.setOriginPrice(tradePrice.getGoodsPrice());
            tradePrice.setTotalPrice(couponTotalPrice.add(tradePrice.getDeliveryPrice()));

            trade.getTradeItems().forEach(tradeItem -> {
                TradeItem matchItem = collect.stream().filter(item -> item.getSkuId().equals(tradeItem.getSkuId())).findFirst().orElse(null);
                if (Objects.nonNull(tradeItem.getDevanningId())) {
                    matchItem = collect.stream().filter(item -> item.getDevanningId().equals(tradeItem.getDevanningId())).findFirst().orElse(null);
                }
                if (matchItem != null) {
                    tradeItem.setSplitPrice(matchItem.getSplitPrice());
                }
            });
        }
        log.info("================ trades ================:{}", JSONObject.toJSONString(trades));
    }


    public List<TradeVO> modifyWalletBalance(CustomerWalletModifyWalletRequest customerWalletModifyWalletRequest) {
        List<Trade> convert = KsBeanUtil.convert(customerWalletModifyWalletRequest.getTradeList(), Trade.class);
        this.modifyWalletBalance(convert, customerWalletModifyWalletRequest.getIncreaseDeductionFlag());
        return KsBeanUtil.convert(convert,TradeVO.class);
    }

    //todo:balance待优化

    /**
     * 余额修改
     * - 订单生成余额扣除
     * - 订单取消, 自动取消, 余额增加
     *
     * @param trades
     * @param increaseDeductionFlag true增加 false扣减
     */
    public void modifyWalletBalance(List<Trade> trades, Boolean increaseDeductionFlag) {
        boolean b = trades.stream().anyMatch(trade -> trade.getTradePrice().getBalancePrice() != null && trade.getTradePrice().getBalancePrice().compareTo(BigDecimal.ZERO) > 0);
        if (!b) {
            return;
        }
        Trade tradeTem = trades.stream().filter(trade -> trade.getBuyer() != null && trade.getBuyer().getId() != null).findFirst().orElseThrow(() -> new SbcRuntimeException("没有找到购买人信息, 请重新检查"));
        //用户余额信息
        CusWalletVO cusWalletVO = walletQueryProvider.getBalanceByCustomerId(WalletByCustomerIdQueryRequest.builder().customerId(tradeTem.getBuyer().getId()).build())
                .getContext().getCusWalletVO();

        log.info("=====订单信息=====>> trades:{}", JSONObject.toJSONString(trades));
        log.info("=====余额信息=====>> customerWalletVO:{}", JSONObject.toJSONString(cusWalletVO));

        // 订单总金额
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (Trade trade : trades) {
            List<TradeItem> tradeItems = trade.getTradeItems();
            TradePrice tradePrice = trade.getTradePrice();
            Buyer buyer = trade.getBuyer();
            Supplier supplier = trade.getSupplier();

            if (!(tradePrice.getBalancePrice() != null && tradePrice.getBalancePrice().compareTo(BigDecimal.ZERO) > 0)) {
                return;
            }

            totalPrice = totalPrice.add(tradePrice.getBalancePrice());

            AddWalletRecordRecordRequest request = new AddWalletRecordRecordRequest();

            //交易备注
            String tradeRemark = !increaseDeductionFlag ? WalletDetailsType.DEDUCTION_ORDER_DEDUCTION.getDesc() + "-" + trade.getId() : WalletDetailsType.INCREASE_ORDER_CANCEL.getDesc() + "-" + trade.getId();
            //备注
            String remark = !increaseDeductionFlag ? WalletDetailsType.DEDUCTION_ORDER_DEDUCTION.getDesc() + "-" + trade.getId() : WalletDetailsType.INCREASE_ORDER_CANCEL.getDesc() + "-" + trade.getId();
            //交易类型【1充值，2提现，3余额支付】
            WalletRecordTradeType tradeType = !increaseDeductionFlag ? WalletRecordTradeType.BALANCE_PAY : WalletRecordTradeType.RECHARGE;
            //收支类型 [1 收入, 2支出]
            BudgetType budgetType = !increaseDeductionFlag ? BudgetType.EXPENDITURE : BudgetType.INCOME;
            //最新余额
            BigDecimal balance = !increaseDeductionFlag ? cusWalletVO.getBalance().subtract(tradePrice.getBalancePrice()) : cusWalletVO.getBalance().add(tradePrice.getBalancePrice());
            String activityType = trade.getActivityType();

            request.setTradeRemark(tradeRemark);
            request.setCustomerAccount(buyer.getAccount());
            request.setRelationOrderId(trade.getId());
            request.setTradeType(tradeType);
            request.setBudgetType(budgetType);
            request.setDealPrice(trade.getTradePrice().getBalancePrice());
            request.setRemark(remark);
//                request.setDealTime(LocalDateTime.now());
            request.setCurrentBalance(cusWalletVO.getBalance());
            request.setTradeState(TradeStateEnum.PAID);
            request.setPayType(1);
            request.setBalance(balance);
            request.setActivityType(activityType);

            BaseResponse<AddWalletRecordResponse> result = walletRecordProvider.addWalletRecord(request);
            addStoreRecord(trade.getId(),supplier.getSupplierName(),trade.getTradePrice().getBalancePrice(),new BigDecimal("99999999"),activityType);
            if (!result.getCode().equals(ResultCode.SUCCESSFUL)) {
                throw new SbcRuntimeException("余额执行操作失败, 请重新操作! ");
            }
        }

        if (totalPrice.compareTo(BigDecimal.ZERO) > 0) {
            Buyer buyer = tradeTem.getBuyer();

            WalletRequest walletRequest = WalletRequest.builder()
                    .customerId(buyer.getId())
                    .expenseAmount(totalPrice)
                    .customerAccount(buyer.getAccount()).build();

            String balanceResultCode = "";
            if (!increaseDeductionFlag) {
                //扣除余额
                balanceResultCode = customerWalletProvider.balancePay(walletRequest).getCode();
            }
            if (increaseDeductionFlag) {
                //增加余额
                balanceResultCode = customerWalletProvider.addAmount(walletRequest).getCode();
            }
            if (!balanceResultCode.equals(ResultCode.SUCCESSFUL)) {
                throw new SbcRuntimeException("余额执行操作失败, 请重新操作! ");
            }
        }
    }

    public List<NewPileTradeVO> modifyWalletBalanceForNewPileTrade(CustomerWalletModifyWalletRequest customerWalletModifyWalletRequest) {
        List<NewPileTrade> convert = KsBeanUtil.convert(customerWalletModifyWalletRequest.getNewPileTradeList(), NewPileTrade.class);
        this.modifyWalletBalanceForNewPileTrade(convert, customerWalletModifyWalletRequest.getIncreaseDeductionFlag());
        return KsBeanUtil.convert(convert,NewPileTradeVO.class);
    }

    private void addStoreRecord(String tradeId,String account,BigDecimal tradePrice,BigDecimal suppelierBalance,String activityType) {
        AddWalletRecordRecordRequest request = new AddWalletRecordRecordRequest();
        request.setTradeRemark(WalletDetailsType.DEDUCTION_WITHDRAW_BUY.getDesc() +"-"+tradeId);
        request.setCustomerAccount(account);
        request.setRelationOrderId(tradeId);
        request.setTradeType(WalletRecordTradeType.BALANCE_PAY);
        request.setBudgetType(BudgetType.INCOME);
        request.setDealPrice(tradePrice);
        request.setRemark(WalletDetailsType.DEDUCTION_WITHDRAW_BUY.getDesc() +"-"+tradeId);
        request.setCurrentBalance(suppelierBalance);
        request.setTradeState(TradeStateEnum.PAID);
        request.setPayType(1);
        request.setActivityType(activityType);
        BaseResponse<AddWalletRecordResponse> result = walletRecordProvider.addWalletRecord(request);
        if (!result.getCode().equals(ResultCode.SUCCESSFUL)) {
            throw new SbcRuntimeException("余额执行操作失败, 请重新操作! ");
        }
    }

    /**
     * 余额修改
     *
     * @param trades
     * @param increaseDeductionFlag true增加 false扣减
     */
    private void modifyWalletBalanceForNewPileTrade(List<NewPileTrade> trades, Boolean increaseDeductionFlag) {
        boolean b = trades.stream().anyMatch(trade -> trade.getTradePrice().getBalancePrice() != null && trade.getTradePrice().getBalancePrice().compareTo(BigDecimal.ZERO) > 0);
        if (!b) {
            return;
        }
        NewPileTrade tradeTem = trades.stream().filter(trade -> trade.getBuyer() != null && trade.getBuyer().getId() != null).findFirst().orElseThrow(() -> new SbcRuntimeException("没有找到购买人信息, 请重新检查"));
        //用户余额信息
        CusWalletVO cusWalletVO = walletQueryProvider.getBalanceByCustomerId(WalletByCustomerIdQueryRequest.builder().customerId(tradeTem.getBuyer().getId()).build())
                .getContext().getCusWalletVO();

        log.info("=====订单信息=====>> trades:{}", JSONObject.toJSONString(trades));
        log.info("=====余额信息=====>> customerWalletVO:{}", JSONObject.toJSONString(cusWalletVO));

        // 订单总金额
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (NewPileTrade trade : trades) {
            List<TradeItem> tradeItems = trade.getTradeItems();
            TradePrice tradePrice = trade.getTradePrice();
            Buyer buyer = trade.getBuyer();

            if (!(tradePrice.getBalancePrice() != null && tradePrice.getBalancePrice().compareTo(BigDecimal.ZERO) > 0)) {
                return;
            }

            totalPrice = totalPrice.add(tradePrice.getBalancePrice());

            AddWalletRecordRecordRequest request = new AddWalletRecordRecordRequest();

            //交易备注
            String tradeRemark = !increaseDeductionFlag ? WalletDetailsType.DEDUCTION_ORDER_DEDUCTION.getDesc() + "-" + trade.getId() : WalletDetailsType.INCREASE_ORDER_CANCEL.getDesc() + "-" + trade.getId();
            //备注
            String remark = !increaseDeductionFlag ? WalletDetailsType.DEDUCTION_ORDER_DEDUCTION.getDesc() + "-" + trade.getId() : WalletDetailsType.INCREASE_ORDER_CANCEL.getDesc() + "-" + trade.getId();
            //交易类型【1充值，2提现，3余额支付】
            WalletRecordTradeType tradeType = !increaseDeductionFlag ? WalletRecordTradeType.BALANCE_PAY : WalletRecordTradeType.RECHARGE;
            //收支类型 [1 收入, 2支出]
            BudgetType budgetType = !increaseDeductionFlag ? BudgetType.EXPENDITURE : BudgetType.INCOME;
            //订单类型
            String activityType = trade.getActivityType();

            request.setTradeRemark(tradeRemark);
            request.setCustomerAccount(buyer.getAccount());
            request.setRelationOrderId(trade.getId());
            request.setTradeType(tradeType);
            request.setBudgetType(budgetType);
            request.setDealPrice(trade.getTradePrice().getBalancePrice());
            request.setRemark(remark);
//                request.setDealTime(LocalDateTime.now());
            request.setCurrentBalance(cusWalletVO.getBalance());
            request.setTradeState(TradeStateEnum.PAID);
            request.setPayType(1);
            request.setBalance(cusWalletVO.getBalance().subtract(tradePrice.getBalancePrice()));
            request.setActivityType(activityType);

            BaseResponse<AddWalletRecordResponse> result = walletRecordProvider.addWalletRecord(request);
            if (!result.getCode().equals(ResultCode.SUCCESSFUL)) {
                throw new SbcRuntimeException("余额执行操作失败, 请重新操作! ");
            }
        }

        if (totalPrice.compareTo(BigDecimal.ZERO) > 0) {
            Buyer buyer = tradeTem.getBuyer();

            WalletRequest walletRequest = WalletRequest.builder()
                    .customerId(buyer.getId())
                    .expenseAmount(totalPrice)
                    .customerAccount(buyer.getAccount()).build();

            String balanceResultCode = "";
            if (!increaseDeductionFlag) {
                //扣除余额
                balanceResultCode = customerWalletProvider.balancePay(walletRequest).getCode();
            }
            if (increaseDeductionFlag) {
                //增加余额
                balanceResultCode = customerWalletProvider.addAmount(walletRequest).getCode();
            }
            if (!balanceResultCode.equals(ResultCode.SUCCESSFUL)) {
                throw new SbcRuntimeException("余额执行操作失败, 请重新操作! ");
            }
        }
    }

    /**
     * 计算商品集合的均摊总价
     */
    public BigDecimal calcSkusTotalPrice(List<TradeItem> tradeItems) {
        return tradeItems.stream()
                .filter(tradeItem -> tradeItem.getSplitPrice() != null && tradeItem.getSplitPrice().compareTo(BigDecimal.ZERO) > 0)
                .map(TradeItem::getSplitPrice)
                .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }


    /**
     * 计算商品均摊价
     * --添加商品最小金额准则(商品数量*0.02)
     *
     * @param tradeItems 待计算的商品列表
     * @param newTotal   新的总价
     * @param total      旧的商品总价
     */
    public void calcSplitPriceMinimumAmount(List<TradeItem> tradeItems, BigDecimal newTotal, BigDecimal total) {
        //内部总价为零或相等不用修改
        if (total.equals(newTotal)) {
            return;
        }

        int size = tradeItems.size();
        BigDecimal splitPriceTotal = BigDecimal.ZERO;//累积平摊价，将剩余扣给最后一个元素
        Long totalNum = tradeItems.stream().map(tradeItem -> tradeItem.getNum()).reduce(0L, Long::sum);

        for (int i = 0; i < size; i++) {
            TradeItem tradeItem = tradeItems.get(i);
            if (i == size - 1) {
                tradeItem.setSplitPrice(newTotal.subtract(splitPriceTotal));
            } else {
                BigDecimal splitPrice = tradeItem.getSplitPrice() != null ? tradeItem.getSplitPrice() : BigDecimal.ZERO;
                //全是零元商品按数量均摊
                if (BigDecimal.ZERO.equals(total)) {
                    BigDecimal newSplitPrice = newTotal.multiply(BigDecimal.valueOf(tradeItem.getNum()))
                            .divide(BigDecimal.valueOf(totalNum), 2, BigDecimal.ROUND_HALF_UP);

                    tradeItem.setSplitPrice(newSplitPrice);

                } else {
                    BigDecimal newSplitPrice = splitPrice
                            .divide(total, 10, BigDecimal.ROUND_DOWN)
                            .multiply(newTotal)
                            .setScale(2, BigDecimal.ROUND_DOWN);

                    //最小金额准则, 平摊价格小于商品 数量*0.02, 则以 商品数量*0.02 为准
                    BigDecimal minimumAmountPrice = new BigDecimal("0.02").multiply(tradeItem.getBNum()).setScale(2, BigDecimal.ROUND_DOWN);
                    newSplitPrice = (newSplitPrice.compareTo(minimumAmountPrice) < 0) ? minimumAmountPrice : newSplitPrice;
                    tradeItem.setSplitPrice(newSplitPrice);

                }
                splitPriceTotal = splitPriceTotal.add(tradeItem.getSplitPrice());
            }
        }
    }

//    /**
//     * 计算商品集合的均摊总价
//     */
//    public BigDecimal calcSkusTotalPrice(List<TradeItem> tradeItems) {
//        return tradeItems.stream()
//                .filter(tradeItem -> tradeItem.getSplitPrice() != null && tradeItem.getSplitPrice().compareTo(BigDecimal.ZERO) > 0)
//                .map(TradeItem::getSplitPrice)
//                .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
//    }

    /**
     * 计算商品均摊价
     *
     * @param tradeItems 待计算的商品列表
     * @param newTotal   新的总价
     * @param total      旧的商品总价
     */
    public void calcSplitPrice(List<TradeItem> tradeItems, BigDecimal newTotal, BigDecimal total) {
        //内部总价为零或相等不用修改
        if (total.equals(newTotal)) {
            return;
        }

        int size = tradeItems.size();
        BigDecimal splitPriceTotal = BigDecimal.ZERO;//累积平摊价，将剩余扣给最后一个元素
        Long totalNum = tradeItems.stream().map(tradeItem -> tradeItem.getNum()).reduce(0L, Long::sum);

        for (int i = 0; i < size; i++) {
            TradeItem tradeItem = tradeItems.get(i);
            if (i == size - 1) {
                tradeItem.setSplitPrice(newTotal.subtract(splitPriceTotal));
            } else {
                BigDecimal splitPrice = tradeItem.getSplitPrice() != null ? tradeItem.getSplitPrice() : BigDecimal.ZERO;
                //全是零元商品按数量均摊
                if (BigDecimal.ZERO.equals(total)) {
                    tradeItem.setSplitPrice(
                            newTotal.multiply(BigDecimal.valueOf(tradeItem.getNum()))
                                    .divide(BigDecimal.valueOf(totalNum), 2, BigDecimal.ROUND_HALF_UP));
                } else {
                    tradeItem.setSplitPrice(
                            splitPrice
                                    .divide(total, 10, BigDecimal.ROUND_DOWN)
                                    .multiply(newTotal)
                                    .setScale(2, BigDecimal.ROUND_DOWN));
                }
                splitPriceTotal = splitPriceTotal.add(tradeItem.getSplitPrice());
            }
        }
    }

    public BaseResponse<AddWalletRecordResponse> addWalletBalance(CustomerWalletAwardRequest customerWalletAwardRequest){
        //用户余额信息
        CusWalletVO cusWalletVO = walletQueryProvider.getBalanceByCustomerId(WalletByCustomerIdQueryRequest.builder().customerId(customerWalletAwardRequest.getBuyerId()).build())
                .getContext().getCusWalletVO();

        AddWalletRecordRecordRequest request = new AddWalletRecordRecordRequest();
        //交易备注
        String tradeRemark = WalletDetailsType.INCREASE_ORDER_AWARD.getDesc() + "-" + customerWalletAwardRequest.getId();
        //备注
        String remark = WalletDetailsType.INCREASE_ORDER_AWARD.getDesc() + "-" + customerWalletAwardRequest.getProvinceId();
        //交易类型【1充值，2提现，3余额支付】
        WalletRecordTradeType tradeType = WalletRecordTradeType.RECHARGE;
        //收支类型 [1 收入, 2支出]
        BudgetType budgetType = BudgetType.INCOME;

        request.setTradeRemark(tradeRemark);
        request.setCustomerAccount(customerWalletAwardRequest.getCustomerAccount());
        request.setRelationOrderId(customerWalletAwardRequest.getId());
        request.setTradeType(tradeType);
        request.setBudgetType(budgetType);
        request.setDealPrice(customerWalletAwardRequest.getFreightCouponPrice());
        request.setRemark(remark);
//                request.setDealTime(LocalDateTime.now());
        request.setCurrentBalance(cusWalletVO.getBalance());
        request.setTradeState(TradeStateEnum.PAID);
        request.setPayType(1);
        request.setBalance(cusWalletVO.getBalance().add(customerWalletAwardRequest.getFreightCouponPrice()));

        BaseResponse<AddWalletRecordResponse> result = walletRecordProvider.addWalletRecord(request);
        if (!result.getCode().equals(ResultCode.SUCCESSFUL)) {
            throw new SbcRuntimeException("余额执行操作失败, 请重新操作! ");
        }

        if (customerWalletAwardRequest.getFreightCouponPrice().compareTo(BigDecimal.ZERO) > 0) {

            WalletRequest walletRequest = WalletRequest.builder()
                    .customerId(customerWalletAwardRequest.getBuyerId())
                    .expenseAmount(customerWalletAwardRequest.getFreightCouponPrice())
                    .customerAccount(customerWalletAwardRequest.getCustomerAccount()).build();

            String balanceResultCode = customerWalletProvider.addAmount(walletRequest).getCode();
            if (!balanceResultCode.equals(ResultCode.SUCCESSFUL)) {
                throw new SbcRuntimeException("余额执行操作失败, 请重新操作! ");
            }
            return result;
        }
        return BaseResponse.success(null);
    }

}
