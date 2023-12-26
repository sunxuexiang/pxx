package com.wanmi.sbc.order;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.wanmi.sbc.account.bean.enums.PayWay;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.enums.Platform;
import com.wanmi.sbc.order.api.request.trade.TradeCommitRequest;
import com.wanmi.sbc.order.api.request.trade.TradePayOnlineCallBackRequest;
import com.wanmi.sbc.order.payorder.model.root.PayOrder;
import com.wanmi.sbc.order.refund.model.root.RefundOrder;
import com.wanmi.sbc.order.refund.service.RefundOrderService;
import com.wanmi.sbc.order.returnorder.model.root.ReturnOrder;
import com.wanmi.sbc.order.returnorder.service.ReturnOrderService;
import com.wanmi.sbc.order.trade.model.entity.TradeItem;
import com.wanmi.sbc.order.trade.model.root.Trade;
import com.wanmi.sbc.order.trade.service.TradeService;
import com.wanmi.sbc.wallet.bean.vo.CusWalletVO;
import io.jsonwebtoken.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class TradeServiceTest extends BaseTest {

    @Autowired
    private TradeService tradeService;
    boolean needUpdateTrade = true;

    /**
     * 支付时使用很少鲸币 6.36，0.6
     */
    @Test
    public void useWalletSecondEdition() {
        List<Trade> trades = tradeService.getListByIds(Arrays.asList("O202305041559434261"));

        List<BigDecimal> splitPrices = Arrays.asList(
                BigDecimal.valueOf(83),
                BigDecimal.valueOf(50),
                BigDecimal.valueOf(96),
                BigDecimal.valueOf(52),
                BigDecimal.valueOf(52),
                BigDecimal.valueOf(104),
                BigDecimal.valueOf(80),
                BigDecimal.valueOf(117),
                BigDecimal.valueOf(80),
                BigDecimal.valueOf(94),
                BigDecimal.valueOf(142),
                BigDecimal.valueOf(46),
                BigDecimal.valueOf(64),
                BigDecimal.valueOf(69),
                BigDecimal.valueOf(64),
                BigDecimal.valueOf(112),
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(71),
                BigDecimal.valueOf(336),
                BigDecimal.valueOf(54),
                BigDecimal.valueOf(60),
                BigDecimal.valueOf(52),
                BigDecimal.valueOf(76),
                BigDecimal.valueOf(90),
                BigDecimal.valueOf(99),
                BigDecimal.valueOf(80),
                BigDecimal.valueOf(64),
                BigDecimal.valueOf(52),
                BigDecimal.valueOf(80),
                BigDecimal.valueOf(112),
                BigDecimal.valueOf(106),
                BigDecimal.valueOf(153),
                BigDecimal.valueOf(81),
                BigDecimal.valueOf(97),
                BigDecimal.valueOf(28.5),
                BigDecimal.valueOf(61),
                BigDecimal.valueOf(42),
                BigDecimal.valueOf(75),
                BigDecimal.valueOf(116),
                BigDecimal.valueOf(62)
        );

        trades.forEach(trade -> {
            List<TradeItem> tradeItems = trade.getTradeItems();
            for (int i = 0; i < tradeItems.size(); i++) {
                TradeItem item = tradeItems.get(i);
                item.setBNum(BigDecimal.valueOf(item.getNum()));
                item.setSplitPrice(splitPrices.get(i));
            }
        });

        TradeCommitRequest tradeCommitRequest = new TradeCommitRequest();
        tradeCommitRequest.setWalletBalance(BigDecimal.valueOf(0.6));

        CusWalletVO cusWalletVO = new CusWalletVO();
        cusWalletVO.setBalance(BigDecimal.valueOf(0.6));

        List<Trade> tradesResult = tradeService.useWalletSecondEdition(trades, tradeCommitRequest, cusWalletVO);
        tradesResult.forEach(trade -> {
            log.info("trade={}", trade);
            trade.getTradeItems().forEach(item -> {
                if (CollectionUtils.isNotEmpty(item.getWalletSettlements())) {
                    item.getWalletSettlements().forEach(walletSettlement -> {
                        Assert.isTrue(!(walletSettlement.getSplitPrice().compareTo(BigDecimal.ZERO) < 0), "分摊金额应不小于0:" + walletSettlement.getSplitPrice());
                        Assert.isTrue(!(walletSettlement.getReduceWalletPrice().compareTo(BigDecimal.ZERO) < 0), "分摊鲸币应不小于0：" + walletSettlement.getReduceWalletPrice());
                    });
                }
            });

            BigDecimal totalSplitPrice = trade.getTradeItems().stream().flatMap(item -> item.getWalletSettlements().stream())
                    .map(item -> item.getReduceWalletPrice().add(item.getSplitPrice()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            Assert.isTrue(trade.getTradePrice().getTotalPrice().compareTo(totalSplitPrice) == 0, "订单应付金额与分摊金额不一致");
        });
    }


    /**
     * 支付时使用很少现金 0.86
     */
    @Test
    public void useWalletSecondEdition2() {
        List<Trade> trades = tradeService.getListByIds(Arrays.asList("O202305171238277452"));

        List<BigDecimal> splitPrices = Arrays.asList(
                BigDecimal.valueOf(68),
                BigDecimal.valueOf(236),
                BigDecimal.valueOf(50),
                BigDecimal.valueOf(122),
                BigDecimal.valueOf(105),
                BigDecimal.valueOf(90),
                BigDecimal.valueOf(94),
                BigDecimal.valueOf(76),
                BigDecimal.valueOf(105),
                BigDecimal.valueOf(73),
                BigDecimal.valueOf(433),
                BigDecimal.valueOf(228),
                BigDecimal.valueOf(86),
                BigDecimal.valueOf(86),
                BigDecimal.valueOf(118),
                BigDecimal.valueOf(56),
                BigDecimal.valueOf(240),
                BigDecimal.valueOf(68),
                BigDecimal.valueOf(68),
                BigDecimal.valueOf(130),
                BigDecimal.valueOf(103),
                BigDecimal.valueOf(58),
                BigDecimal.valueOf(140),
                BigDecimal.valueOf(68),
                BigDecimal.valueOf(130),
                BigDecimal.valueOf(518),
                BigDecimal.valueOf(66),
                BigDecimal.valueOf(66),
                BigDecimal.valueOf(76),
                BigDecimal.valueOf(120),
                BigDecimal.valueOf(67),
                BigDecimal.valueOf(125),
                BigDecimal.valueOf(156),
                BigDecimal.valueOf(76),
                BigDecimal.valueOf(105)
                );

        trades.forEach(trade -> {
            List<TradeItem> tradeItems = trade.getTradeItems();
            for (int i = 0; i < tradeItems.size(); i++) {
                TradeItem item = tradeItems.get(i);
                item.setBNum(BigDecimal.valueOf(item.getNum()));
                item.setSplitPrice(splitPrices.get(i));
            }
        });

        TradeCommitRequest tradeCommitRequest = new TradeCommitRequest();
        tradeCommitRequest.setWalletBalance(BigDecimal.valueOf(4405.30));

        CusWalletVO cusWalletVO = new CusWalletVO();
        cusWalletVO.setBalance(BigDecimal.valueOf(4405.30));

        List<Trade> tradesResult = tradeService.useWalletSecondEdition(trades, tradeCommitRequest, cusWalletVO);
        tradesResult.forEach(trade -> {
            log.info("trade={}", trade);
            trade.getTradeItems().forEach(item -> {
                if (CollectionUtils.isNotEmpty(item.getWalletSettlements())) {
                    item.getWalletSettlements().forEach(walletSettlement -> {
                        Assert.isTrue(!(walletSettlement.getSplitPrice().compareTo(BigDecimal.ZERO) < 0), "分摊金额应不小于0:" + walletSettlement.getSplitPrice());
                        Assert.isTrue(!(walletSettlement.getReduceWalletPrice().compareTo(BigDecimal.ZERO) < 0), "分摊鲸币应不小于0：" + walletSettlement.getReduceWalletPrice());

                        Assert.isTrue(!(walletSettlement.getSplitPrice().compareTo(BigDecimal.ZERO) == 0), "分摊金额应!=0:" + walletSettlement.getSplitPrice());
                        Assert.isTrue(!(walletSettlement.getReduceWalletPrice().compareTo(BigDecimal.ZERO) == 0), "分摊鲸币应!=0：" + walletSettlement.getReduceWalletPrice());
                    });
                }
            });

            BigDecimal totalSplitPrice = trade.getTradeItems().stream().flatMap(item -> item.getWalletSettlements().stream())
                    .map(item -> item.getReduceWalletPrice().add(item.getSplitPrice()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            Assert.isTrue(trade.getTradePrice().getTotalPrice().compareTo(totalSplitPrice) == 0, "订单应付金额与分摊金额不一致");

            if(needUpdateTrade){
                tradeService.updateTrade(trade);
            }
        });
    }

    @Test
    public void useWalletSecondEdition3() {
        List<Trade> trades = tradeService.detailsByParentId("PO231013090219788009");
        trades.forEach(trade -> {
            trade.getTradeItems().forEach(item ->{
                item.setSplitPrice(item.getOriginalPrice().multiply(new BigDecimal(item.getNum())));
            });
        });

        // Collections.reverse(trades);
        trades = trades.stream()
                .sorted(Comparator.comparing(trade -> trade.getTradePrice().getGoodsPrice()))
                .collect(Collectors.toList());
        TradeCommitRequest tradeCommitRequest = new TradeCommitRequest();
        tradeCommitRequest.setWalletBalance(BigDecimal.valueOf(4405.30));

        CusWalletVO cusWalletVO = new CusWalletVO();
        cusWalletVO.setBalance(BigDecimal.valueOf(4405.30));

        List<Trade> tradesResult = tradeService.useWalletSecondEdition(trades, tradeCommitRequest, cusWalletVO);

        tradesResult.forEach(trade -> {
            System.out.println(trade.getId());
            System.out.println(JSON.toJSONString(trade.getTradePrice(), SerializerFeature.PrettyFormat));
            System.out.println(JSON.toJSONString(trade.getTradeItems(), SerializerFeature.PrettyFormat));
        });
    }

    @Test
    public void testCupsPayOnlineCallBack() {
        TradePayOnlineCallBackRequest request = new TradePayOnlineCallBackRequest();
        String str = "{\n" +
                "  \"msgType\": \"wx.notify\",\n" +
                "  \"payTime\": \"2023-07-03 17:35:48\",\n" +
                "  \"buyerCashPayAmt\": \"10\",\n" +
                "  \"connectSys\": \"UNIONPAY\",\n" +
                "  \"sign\": \"1E36DC73BBD5F0A35ACCE90EEFC6D384C47599B88F284872A8A47800ECEEBC59\",\n" +
                "  \"merName\": \"湖南喜吖吖商业服务有限公司\",\n" +
                "  \"mid\": \"898130453112912\",\n" +
                "  \"invoiceAmount\": \"10\",\n" +
                "  \"settleDate\": \"2023-07-03\",\n" +
                "  \"billFunds\": \"现金:10\",\n" +
                "  \"buyerId\": \"otdJ_uJP_B7zShHHl9uCeBT6xrxI\",\n" +
                "  \"mchntUuid\": \"2d9081bd802dacb801803aa221c63fec\",\n" +
                "  \"tid\": \"04961674\",\n" +
                "  \"instMid\": \"MINIDEFAULT\",\n" +
                "  \"receiptAmount\": \"10\",\n" +
                "  \"couponAmount\": \"0\",\n" +
                "  \"cardAttr\": \"BALANCE\",\n" +
                "  \"targetOrderId\": \"4200001824202307037176651922\",\n" +
                "  \"signType\": \"SHA256\",\n" +
                "  \"billFundsDesc\": \"现金支付0.10元。\",\n" +
                "  \"subBuyerId\": \"oNWOw5Dub9Upkf08bLPKuEWmrZ8s\",\n" +
                "  \"SP\": \"OJps\",\n" +
                "  \"orderDesc\": \"伊利·220mL笑脸包 万众一心 订单\",\n" +
                "  \"seqId\": \"34998510538N\",\n" +
                "  \"merOrderId\": \"31YTOD202307031734305123\",\n" +
                "  \"targetSys\": \"WXPay\",\n" +
                "  \"bankInfo\": \"OTHERS\",\n" +
                "  \"totalAmount\": \"10\",\n" +
                "  \"createTime\": \"2023-07-03 17:34:30\",\n" +
                "  \"buyerPayAmount\": \"10\",\n" +
                "  \"notifyId\": \"30129dcc-fbf2-4b08-9194-20a42c346cc0\",\n" +
                "  \"subInst\": \"102800\",\n" +
                "  \"status\": \"TRADE_SUCCESS\"\n" +
                "}";
        request.setCupsPayCallBackResultStr(str);
        request.setBusinessId("O202307031733496769");
        try {
            tradeService.cupsPayOnlineCallBack(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testWxPayOnlineCallBack() {
        TradePayOnlineCallBackRequest request = new TradePayOnlineCallBackRequest();
        String str = "<xml><appid><![CDATA[wx68367f181fcc3248]]></appid><bank_type><![CDATA[OTHERS]]></bank_type><cash_fee><![CDATA[5]]></cash_fee><fee_type><![CDATA[CNY]]></fee_type><is_subscribe><![CDATA[N]]></is_subscribe><mch_id><![CDATA[1608742803]]></mch_id><nonce_str><![CDATA[tdc2rpxhvJjrCdO8y1jMbws2h5celyTJ]]></nonce_str><openid><![CDATA[o24cQwEnzqsvj1a6GO4H0SYUBBQQ]]></openid><out_trade_no><![CDATA[OD202307041117595237]]></out_trade_no><result_code><![CDATA[SUCCESS]]></result_code><return_code><![CDATA[SUCCESS]]></return_code><sign><![CDATA[4B0D60AE04A85E1A8A8DC775759F1067]]></sign><time_end><![CDATA[20230704111818]]></time_end><total_fee>5</total_fee><trade_type><![CDATA[JSAPI]]></trade_type><transaction_id><![CDATA[4200001876202307043521650461]]></transaction_id></xml>";
        request.setWxPayCallBackResultStr(str);
        request.setWxPayCallBackResultXmlStr(str);
        request.setBusinessId("O202307041117330517");
        try {
            tradeService.wxPayOnlineCallBack(request);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Autowired
    private ReturnOrderService returnOrderService;

    @Autowired
    private RefundOrderService refundOrderService;



    @Test
    public void testRefundPushKingDee() {
        String rid = "R202308030950279815";
        ReturnOrder returnOrder = returnOrderService.findById(rid);
        RefundOrder refundOrder = refundOrderService.findRefundOrderByReturnOrderNo(rid);

        returnOrderService.pushRefundOrderKingdee(returnOrder, refundOrder, PayWay.CCB);

    }

    @Test
    public void  refundOnlineByTid() {
        Operator operator = Operator.builder()
                .account("system")
                .platform(Platform.PLATFORM)
                .adminId("2c8080815cd3a74a015cd3ae86850001")
                .name("system")
                .userId("2c8080815cd3a74a015cd3ae86850001")
                .build();
        returnOrderService.refundOnlineByTid("R202308230848393580", operator, true);
    }

    @Test
    public void testCreatePayOrderIfNull() {
        Trade trade = tradeService.getById("O230914170054339256");
        PayOrder payOrder = tradeService.createPayOrderIfNull(trade, null);
        System.out.println(JSON.toJSONString(payOrder, SerializerFeature.PrettyFormat));
    }

}
