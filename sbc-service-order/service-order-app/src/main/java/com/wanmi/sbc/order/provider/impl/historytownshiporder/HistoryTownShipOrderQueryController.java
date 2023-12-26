package com.wanmi.sbc.order.provider.impl.historytownshiporder;


import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.order.api.provider.historyTownShipOrder.HistoryTownShipOrderQueryProvider;
import com.wanmi.sbc.order.api.provider.historylogisticscompany.HistoryLogisticsCompanyQueryProvider;
import com.wanmi.sbc.order.api.request.historylogisticscompany.HistoryLogisticsCompanyByIdRequest;
import com.wanmi.sbc.order.api.request.historylogisticscompany.HistoryLogisticsCompanyListRequest;
import com.wanmi.sbc.order.api.request.historylogisticscompany.HistoryLogisticsCompanyPageRequest;
import com.wanmi.sbc.order.api.request.historylogisticscompany.HistoryLogisticsCompanyQueryRequest;
import com.wanmi.sbc.order.api.request.historytownshiporder.HistoryTownShipOrderStockRequest;
import com.wanmi.sbc.order.api.response.historylogisticscompany.HistoryLogisticsCompanyByIdResponse;
import com.wanmi.sbc.order.api.response.historylogisticscompany.HistoryLogisticsCompanyListResponse;
import com.wanmi.sbc.order.api.response.historylogisticscompany.HistoryLogisticsCompanyPageResponse;
import com.wanmi.sbc.order.api.response.histoytownshiporder.HistoryTownShipOrderStockResponse;
import com.wanmi.sbc.order.bean.vo.HistoryLogisticsCompanyVO;
import com.wanmi.sbc.order.bean.vo.TrueStockVO;
import com.wanmi.sbc.order.historylogisticscompany.model.root.HistoryLogisticsCompany;
import com.wanmi.sbc.order.historylogisticscompany.service.HistoryLogisticsCompanyService;
import com.wanmi.sbc.order.historytownshiporder.response.TrueStock;
import com.wanmi.sbc.order.historytownshiporder.service.HistoryTownShipOrderService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


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

