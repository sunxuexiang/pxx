package com.wanmi.sbc.returnorder.api.provider.trade;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.returnorder.api.request.trade.MangoTradeRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "${application.returnOrder.name}", url="${feign.url.returnOrder:#{null}}", contextId = "ReturnTradeMangoToMysqlProvider")
public interface TradeMangoToMysqlProvider {


    @PostMapping("/returnOrder/${application.order.version}/trade/mangoToMysql/pile-trade-save-to-mysql")
    BaseResponse tradePileSaveToMysql(@RequestBody MangoTradeRequest request);

    @PostMapping("/returnOrder/${application.order.version}/trade/mangoToMysql/trade-save-to-mysql")
    BaseResponse tradeSaveToMysql(@RequestBody MangoTradeRequest request);
}
