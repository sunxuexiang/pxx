package com.wanmi.sbc.order.provider.impl.trade;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.order.api.provider.trade.TradeMangoToMysqlProvider;
import com.wanmi.sbc.order.api.request.trade.MangoTradeRequest;
import com.wanmi.sbc.order.trade.service.MangoTradeMainService;
import com.wanmi.sbc.order.trade.service.MangoPileTradeMainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
public class TradeMangoToMysqlController implements TradeMangoToMysqlProvider {

    @Autowired
    private MangoPileTradeMainService mangoTradeMainService;

    @Autowired
    private MangoTradeMainService mangoPileTradeMainService;

    @Override
    public BaseResponse tradePileSaveToMysql(MangoTradeRequest request) {
        mangoTradeMainService.savePileTrade(request.getBeginTime(),request.getEndTime());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse tradeSaveToMysql(MangoTradeRequest request) {
        mangoPileTradeMainService.saveTrade(request.getBeginTime(),request.getEndTime());
        return BaseResponse.SUCCESSFUL();
    }
}
