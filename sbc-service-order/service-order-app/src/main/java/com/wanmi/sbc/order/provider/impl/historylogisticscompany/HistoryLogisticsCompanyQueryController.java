package com.wanmi.sbc.order.provider.impl.historylogisticscompany;


import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.order.api.provider.historylogisticscompany.HistoryLogisticsCompanyQueryProvider;
import com.wanmi.sbc.order.api.request.historylogisticscompany.HistoryLogisticsCompanyPageRequest;
import com.wanmi.sbc.order.api.request.historylogisticscompany.HistoryLogisticsCompanyQueryRequest;
import com.wanmi.sbc.order.api.response.historylogisticscompany.HistoryLogisticsCompanyPageResponse;
import com.wanmi.sbc.order.api.request.historylogisticscompany.HistoryLogisticsCompanyListRequest;
import com.wanmi.sbc.order.api.response.historylogisticscompany.HistoryLogisticsCompanyListResponse;
import com.wanmi.sbc.order.api.request.historylogisticscompany.HistoryLogisticsCompanyByIdRequest;
import com.wanmi.sbc.order.api.response.historylogisticscompany.HistoryLogisticsCompanyByIdResponse;
import com.wanmi.sbc.order.bean.vo.HistoryLogisticsCompanyVO;
import com.wanmi.sbc.order.historylogisticscompany.service.HistoryLogisticsCompanyService;
import com.wanmi.sbc.order.historylogisticscompany.model.root.HistoryLogisticsCompany;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>物流公司历史记录查询服务接口实现</p>
 * @author fcq
 * @date 2020-11-09 17:32:23
 */
@RestController
@Validated
public class HistoryLogisticsCompanyQueryController implements HistoryLogisticsCompanyQueryProvider {
	@Autowired
	private HistoryLogisticsCompanyService historyLogisticsCompanyService;

	@Override
	public BaseResponse<HistoryLogisticsCompanyPageResponse> page(@RequestBody @Valid HistoryLogisticsCompanyPageRequest historyLogisticsCompanyPageReq) {
		HistoryLogisticsCompanyQueryRequest queryReq = KsBeanUtil.convert(historyLogisticsCompanyPageReq, HistoryLogisticsCompanyQueryRequest.class);
		Page<HistoryLogisticsCompany> historyLogisticsCompanyPage = historyLogisticsCompanyService.page(queryReq);
		Page<HistoryLogisticsCompanyVO> newPage = historyLogisticsCompanyPage.map(entity -> historyLogisticsCompanyService.wrapperVo(entity));
		MicroServicePage<HistoryLogisticsCompanyVO> microPage = new MicroServicePage<>(newPage, historyLogisticsCompanyPageReq.getPageable());
		HistoryLogisticsCompanyPageResponse finalRes = new HistoryLogisticsCompanyPageResponse(microPage);
		return BaseResponse.success(finalRes);
	}

	@Override
	public BaseResponse<HistoryLogisticsCompanyListResponse> list(@RequestBody @Valid HistoryLogisticsCompanyListRequest historyLogisticsCompanyListReq) {
		HistoryLogisticsCompanyQueryRequest queryReq = KsBeanUtil.convert(historyLogisticsCompanyListReq, HistoryLogisticsCompanyQueryRequest.class);
		List<HistoryLogisticsCompany> historyLogisticsCompanyList = historyLogisticsCompanyService.list(queryReq);
		List<HistoryLogisticsCompanyVO> newList = historyLogisticsCompanyList.stream().map(entity -> historyLogisticsCompanyService.wrapperVo(entity)).collect(Collectors.toList());
		return BaseResponse.success(new HistoryLogisticsCompanyListResponse(newList));
	}

	@Override
	public BaseResponse<HistoryLogisticsCompanyByIdResponse> getById(@RequestBody @Valid HistoryLogisticsCompanyByIdRequest historyLogisticsCompanyByIdRequest) {
		HistoryLogisticsCompany historyLogisticsCompany =
		historyLogisticsCompanyService.getOne(historyLogisticsCompanyByIdRequest.getId());
		return BaseResponse.success(new HistoryLogisticsCompanyByIdResponse(historyLogisticsCompanyService.wrapperVo(historyLogisticsCompany)));
	}


}

