package com.wanmi.sbc.order.provider.impl.historylogisticscompany;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.order.api.provider.historylogisticscompany.HistoryLogisticsCompanyProvider;
import com.wanmi.sbc.order.api.request.historylogisticscompany.HistoryLogisticsCompanyAddRequest;
import com.wanmi.sbc.order.api.response.historylogisticscompany.HistoryLogisticsCompanyAddResponse;
import com.wanmi.sbc.order.api.request.historylogisticscompany.HistoryLogisticsCompanyModifyRequest;
import com.wanmi.sbc.order.api.response.historylogisticscompany.HistoryLogisticsCompanyModifyResponse;
import com.wanmi.sbc.order.api.request.historylogisticscompany.HistoryLogisticsCompanyDelByIdRequest;
import com.wanmi.sbc.order.api.request.historylogisticscompany.HistoryLogisticsCompanyDelByIdListRequest;
import com.wanmi.sbc.order.historylogisticscompany.service.HistoryLogisticsCompanyService;
import com.wanmi.sbc.order.historylogisticscompany.model.root.HistoryLogisticsCompany;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;

/**
 * <p>物流公司历史记录保存服务接口实现</p>
 * @author fcq
 * @date 2020-11-09 17:32:23
 */
@RestController
@Validated
public class HistoryLogisticsCompanyController implements HistoryLogisticsCompanyProvider {
	@Autowired
	private HistoryLogisticsCompanyService historyLogisticsCompanyService;

	@Override
	public BaseResponse<HistoryLogisticsCompanyAddResponse> add(@RequestBody @Valid HistoryLogisticsCompanyAddRequest historyLogisticsCompanyAddRequest) {
		HistoryLogisticsCompany historyLogisticsCompany = KsBeanUtil.convert(historyLogisticsCompanyAddRequest, HistoryLogisticsCompany.class);
		return BaseResponse.success(new HistoryLogisticsCompanyAddResponse(
				historyLogisticsCompanyService.wrapperVo(historyLogisticsCompanyService.add(historyLogisticsCompany))));
	}

	@Override
	public BaseResponse<HistoryLogisticsCompanyModifyResponse> modify(@RequestBody @Valid HistoryLogisticsCompanyModifyRequest historyLogisticsCompanyModifyRequest) {
		HistoryLogisticsCompany historyLogisticsCompany = KsBeanUtil.convert(historyLogisticsCompanyModifyRequest, HistoryLogisticsCompany.class);
		return BaseResponse.success(new HistoryLogisticsCompanyModifyResponse(
				historyLogisticsCompanyService.wrapperVo(historyLogisticsCompanyService.modify(historyLogisticsCompany))));
	}

	@Override
	public BaseResponse deleteById(@RequestBody @Valid HistoryLogisticsCompanyDelByIdRequest historyLogisticsCompanyDelByIdRequest) {
		HistoryLogisticsCompany historyLogisticsCompany = KsBeanUtil.convert(historyLogisticsCompanyDelByIdRequest, HistoryLogisticsCompany.class);
		historyLogisticsCompany.setDelFlag(DeleteFlag.YES);
		historyLogisticsCompanyService.deleteById(historyLogisticsCompany);
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse deleteByIdList(@RequestBody @Valid HistoryLogisticsCompanyDelByIdListRequest historyLogisticsCompanyDelByIdListRequest) {
		List<HistoryLogisticsCompany> historyLogisticsCompanyList = historyLogisticsCompanyDelByIdListRequest.getIdList().stream()
			.map(Id -> {
				HistoryLogisticsCompany historyLogisticsCompany = KsBeanUtil.convert(Id, HistoryLogisticsCompany.class);
				historyLogisticsCompany.setDelFlag(DeleteFlag.YES);
				return historyLogisticsCompany;
			}).collect(Collectors.toList());
		historyLogisticsCompanyService.deleteByIdList(historyLogisticsCompanyList);
		return BaseResponse.SUCCESSFUL();
	}

}

