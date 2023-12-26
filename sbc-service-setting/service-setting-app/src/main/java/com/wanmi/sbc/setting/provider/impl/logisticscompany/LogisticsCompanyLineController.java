package com.wanmi.sbc.setting.provider.impl.logisticscompany;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.provider.logisticscompany.LogisticsCompanyLineProvider;
import com.wanmi.sbc.setting.api.request.logisticscompany.LogisticsCompanyLineRequest;
import com.wanmi.sbc.setting.api.response.logisticscompany.LogisticsCompanyLineListResponse;
import com.wanmi.sbc.setting.logisticscompany.model.root.LogisticsCompanyLine;
import com.wanmi.sbc.setting.logisticscompany.service.LogisticsCompanyLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * @desc  物流线路
 * @author shiy  2023/11/7 9:31
*/
@RestController
@Validated
public class LogisticsCompanyLineController implements LogisticsCompanyLineProvider {
	@Autowired
	private LogisticsCompanyLineService logisticsCompanyLineService;

	@Override
	public BaseResponse add(LogisticsCompanyLineRequest logisticsCompanyLineAddRequest) {
		LogisticsCompanyLine logisticsCompanyLine = KsBeanUtil.convert(logisticsCompanyLineAddRequest, LogisticsCompanyLine.class);
		logisticsCompanyLineService.add(logisticsCompanyLine);
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse modify(LogisticsCompanyLineRequest logisticsCompanyLineModifyRequest) {
		LogisticsCompanyLine logisticsCompanyLine = KsBeanUtil.convert(logisticsCompanyLineModifyRequest, LogisticsCompanyLine.class);
		logisticsCompanyLineService.modify(logisticsCompanyLine);
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse deleteByParam(LogisticsCompanyLineRequest logisticsCompanyLineLineRequest) {
		return null;
	}

	@Override
	public BaseResponse<LogisticsCompanyLineListResponse> selectLogisticsCompanyLineNumbersByStoreId(Long logisticId) {
		return null;
	}
}

