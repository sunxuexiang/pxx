package com.wanmi.sbc.order.api.provider.historyTownShipOrder;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.order.api.request.historytownshiporder.HistoryTownShipOrderStockRequest;
import com.wanmi.sbc.order.api.response.histoytownshiporder.HistoryTownShipOrderStockResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;


@FeignClient(value = "${application.order.name}", url="${feign.url.order:#{null}}", contextId = "HistoryTownShipOrderQueryProvider")
public interface HistoryTownShipOrderQueryProvider {



	@PostMapping("/order/${application.order.version}/historyTownShipOrder/get-stock-byskuids")
	BaseResponse<HistoryTownShipOrderStockResponse> getById(@RequestBody @Valid HistoryTownShipOrderStockRequest historyTownShipOrderStockRequest);



}

