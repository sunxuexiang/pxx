package com.wanmi.sbc.setting.provider.impl.logisticscompany;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.provider.logisticscompany.LogisticsBaseSiteProvider;
import com.wanmi.sbc.setting.api.request.logisticscompany.LogisticsBaseSiteQueryRequest;
import com.wanmi.sbc.setting.api.request.logisticscompany.LogisticsBaseSiteRequest;
import com.wanmi.sbc.setting.api.response.logisticscompany.LogisticsBaseSiteListResponse;
import com.wanmi.sbc.setting.logisticscompany.model.root.LogisticsBaseSite;
import com.wanmi.sbc.setting.logisticscompany.service.LogisticsBaseSiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @desc  物流线路
 * @author shiy  2023/11/7 9:31
*/
@RestController
@Validated
public class LogisticsBaseSiteController implements LogisticsBaseSiteProvider {
	@Autowired
	private LogisticsBaseSiteService logisticsBaseSiteService;

	@Override
	public BaseResponse add(LogisticsBaseSiteRequest logisticsBaseSiteAddRequest) {
		LogisticsBaseSite logisticsBaseSite = KsBeanUtil.convert(logisticsBaseSiteAddRequest, LogisticsBaseSite.class);
		LogisticsBaseSiteQueryRequest baseSiteQueryRequest = KsBeanUtil.convert(logisticsBaseSiteAddRequest, LogisticsBaseSiteQueryRequest.class);
		long vcount = logisticsBaseSiteService.count(baseSiteQueryRequest);
		if(vcount==0) {
			logisticsBaseSiteService.add(logisticsBaseSite);
		}
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse modify(LogisticsBaseSiteRequest logisticsBaseSiteModifyRequest) {
		LogisticsBaseSite logisticsBaseSite = KsBeanUtil.convert(logisticsBaseSiteModifyRequest, LogisticsBaseSite.class);
		logisticsBaseSiteService.modify(logisticsBaseSite);
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse deleteByParam(LogisticsBaseSiteRequest logisticsBaseSiteLineRequest) {
		return null;
	}

	@Override
	public BaseResponse<LogisticsBaseSiteListResponse> selectLogisticsBaseSiteByLogisticId(Long logisticId) {
		return null;
	}

	@Override
	public BaseResponse<LogisticsBaseSiteListResponse> selectLogisticsBaseSiteBycustomerId(String customerId) {
		LogisticsBaseSiteQueryRequest queryRequest = new LogisticsBaseSiteQueryRequest();
		queryRequest.setCreatePerson(customerId);
		queryRequest.setDelFlag(DeleteFlag.NO);
		queryRequest.setSiteCrtType(0);
		List<LogisticsBaseSite> logisticsBaseSiteList = logisticsBaseSiteService.list(queryRequest);
		LogisticsBaseSiteListResponse listResponse = new LogisticsBaseSiteListResponse();
		listResponse.setLogisticsBaseSiteVOList(logisticsBaseSiteService.wrapperVoList(logisticsBaseSiteList));
		return BaseResponse.success(listResponse);
	}

	@Override
	public BaseResponse<String> getLatestSiteNameByCustomerId(String customerId) {
		return BaseResponse.success(logisticsBaseSiteService.getLatestSiteNameByCustomerId(customerId));
	}
}

