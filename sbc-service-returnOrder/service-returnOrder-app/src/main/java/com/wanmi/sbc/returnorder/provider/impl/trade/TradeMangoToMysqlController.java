package com.wanmi.sbc.returnorder.provider.impl.trade;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.returnorder.api.provider.trade.TradeMangoToMysqlProvider;
import com.wanmi.sbc.returnorder.api.request.trade.MangoTradeRequest;
import com.wanmi.sbc.returnorder.trade.service.MangoPileTradeMainService;
import com.wanmi.sbc.returnorder.trade.service.MangoTradeMainService;
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
