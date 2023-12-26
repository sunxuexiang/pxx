package com.wanmi.sbc.returnorder.api.provider.historyTownShipOrder;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.returnorder.api.request.historytownshiporder.HistoryTownShipOrderStockRequest;
import com.wanmi.sbc.returnorder.api.response.histoytownshiporder.HistoryTownShipOrderStockResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;


@FeignClient(value = "${application.returnOrder.name}", url="${feign.url.returnOrder:#{null}}", contextId = "ReturnHistoryTownShipOrderQueryProvider")
public interface HistoryTownShipOrderQueryProvider {



	@PostMapping("/returnOrder/${application.order.version}/historyTownShipOrder/get-stock-byskuids")
	BaseResponse<HistoryTownShipOrderStockResponse> getById(@RequestBody @Valid HistoryTownShipOrderStockRequest historyTownShipOrderStockRequest);



}

