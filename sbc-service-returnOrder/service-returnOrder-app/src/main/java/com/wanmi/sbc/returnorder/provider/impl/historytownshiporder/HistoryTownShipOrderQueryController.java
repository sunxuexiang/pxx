package com.wanmi.sbc.returnorder.provider.impl.historytownshiporder;


import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.returnorder.api.provider.historyTownShipOrder.HistoryTownShipOrderQueryProvider;
import com.wanmi.sbc.returnorder.api.request.historytownshiporder.HistoryTownShipOrderStockRequest;
import com.wanmi.sbc.returnorder.api.response.histoytownshiporder.HistoryTownShipOrderStockResponse;
import com.wanmi.sbc.returnorder.bean.vo.TrueStockVO;
import com.wanmi.sbc.returnorder.historytownshiporder.response.TrueStock;
import com.wanmi.sbc.returnorder.historytownshiporder.service.HistoryTownShipOrderService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;


@RestController
@Validated
public class HistoryTownShipOrderQueryController implements HistoryTownShipOrderQueryProvider {

	@Autowired
	private HistoryTownShipOrderService historyTownShipOrderService;

	@Override
	public BaseResponse<HistoryTownShipOrderStockResponse> getById(HistoryTownShipOrderStockRequest historyTownShipOrderStockRequest) {
		if (CollectionUtils.isEmpty(historyTownShipOrderStockRequest.getSkuids()) || Objects.isNull(historyTownShipOrderStockRequest.getWareId())){
			throw new SbcRuntimeException("请校验参数,要求必填!");
		}
		List<TrueStock> getskusstock = historyTownShipOrderService.getskusstock(historyTownShipOrderStockRequest.getSkuids());
		List<TrueStockVO> trueStockVOS = KsBeanUtil.convert(getskusstock, TrueStockVO.class);
		return BaseResponse.success(HistoryTownShipOrderStockResponse.builder().trueStockVO(trueStockVOS).build());
	}
}

