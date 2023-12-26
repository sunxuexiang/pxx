package com.wanmi.sbc.order.api.provider.trade;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.order.api.request.trade.MangoTradeRequest;
import com.wanmi.sbc.order.api.request.trade.TradeItemByCustomerIdRequest;
import com.wanmi.sbc.order.api.response.trade.TradeItemByCustomerIdResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(value = "${application.order.name}", url="${feign.url.order:#{null}}", contextId = "TradeMangoToMysqlProvider")
public interface TradeMangoToMysqlProvider {


    @PostMapping("/order/${application.order.version}/trade/mangoToMysql/pile-trade-save-to-mysql")
    BaseResponse tradePileSaveToMysql(@RequestBody MangoTradeRequest request);

    @PostMapping("/order/${application.order.version}/trade/mangoToMysql/trade-save-to-mysql")
    BaseResponse tradeSaveToMysql(@RequestBody MangoTradeRequest request);
}
