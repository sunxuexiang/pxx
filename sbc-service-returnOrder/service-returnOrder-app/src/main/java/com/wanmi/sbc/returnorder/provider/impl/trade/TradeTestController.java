package com.wanmi.sbc.returnorder.provider.impl.trade;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.enums.Platform;
import com.wanmi.sbc.returnorder.refund.service.RefundFactory;
import com.wanmi.sbc.returnorder.returnorder.model.root.NewPileReturnOrder;
import com.wanmi.sbc.returnorder.returnorder.model.root.ReturnOrder;
import com.wanmi.sbc.returnorder.returnorder.service.ReturnOrderService;
import com.wanmi.sbc.returnorder.returnorder.service.newPileOrder.NewPileReturnOrderService;
import com.wanmi.sbc.returnorder.trade.model.newPileTrade.NewPileTrade;
import com.wanmi.sbc.returnorder.trade.model.root.Trade;
import com.wanmi.sbc.returnorder.trade.service.TradeService;
import com.wanmi.sbc.returnorder.trade.service.newPileTrade.NewPileTradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/test")
public class TradeTestController {

    @Autowired
    TradeService tradeService;
    @Autowired
    ReturnOrderService returnOrderService;

    @Autowired
    NewPileTradeService newPileTradeService;
    @Autowired
    NewPileReturnOrderService newPileReturnOrderService;

    @PostMapping("/testRefundNewPickReturn")
    public String testRefundNewPickReturn(@RequestBody Map<String, String> params) {
        Trade trade = tradeService.detail(params.get("tid"));
        ReturnOrder returnOrder = returnOrderService.findById(params.get("rid"));

        RefundFactory.getTradeRefundImpl(RefundFactory.TradeRefundType.NEW_PICK_RETURN).refund(trade, returnOrder);
        return "success";
    }

    @PostMapping("/testRefundTradeReturn")
    public String testRefundTradeReturn(@RequestBody Map<String, String> params) {
        Trade trade = tradeService.detail(params.get("tid"));
        ReturnOrder returnOrder = returnOrderService.findById(params.get("rid"));

        RefundFactory.getTradeRefundImpl(RefundFactory.TradeRefundType.TRADE_RETURN).refund(trade, returnOrder);
        return "success";
    }

    @PostMapping("/testRefundNewPileReturn")
    public String testRefundNewPileReturn(@RequestBody Map<String, String> params) {
        NewPileTrade trade = newPileTradeService.detail(params.get("tid"));
        NewPileReturnOrder returnOrder = newPileReturnOrderService.findById(params.get("rid"));

        RefundFactory.getNewPileRefundImpl(RefundFactory.NewPileRefundType.NEW_PILE_RETURN).refund(trade, returnOrder);
        return "success";
    }

    @PostMapping("/cancelOrder/{orderId}")
    public BaseResponse cancelOrder(@PathVariable String orderId) {
        Operator operator = Operator.builder().adminId("1").name("system").account("system").ip("127.0.0.1").platform(Platform
                .PLATFORM).build();
        tradeService.cancel(orderId, operator);
        return BaseResponse.SUCCESSFUL();
    }

    @PostMapping("/autoCancelOrder/{orderId}")
    public BaseResponse autoCancelOrder(@PathVariable String orderId) {
        Operator operator = Operator.builder().adminId("1").name("system").account("system").ip("127.0.0.1").platform(Platform
                .PLATFORM).build();
        tradeService.autoCancelOrder(orderId, operator);
        return BaseResponse.SUCCESSFUL();
    }
}
