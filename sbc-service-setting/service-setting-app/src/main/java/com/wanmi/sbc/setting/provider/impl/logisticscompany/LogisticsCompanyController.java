package com.wanmi.sbc.setting.provider.impl.logisticscompany;

import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.setting.api.request.logisticscompany.*;
import com.wanmi.sbc.setting.api.response.logisticscompany.LogisticsCompanyImportResponse;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.setting.api.provider.logisticscompany.LogisticsCompanyProvider;
import com.wanmi.sbc.setting.api.response.logisticscompany.LogisticsCompanyAddResponse;
import com.wanmi.sbc.setting.api.response.logisticscompany.LogisticsCompanyModifyResponse;
import com.wanmi.sbc.setting.logisticscompany.service.LogisticsCompanyService;
import com.wanmi.sbc.setting.logisticscompany.model.root.LogisticsCompany;
import javax.validation.Valid;

/**
 * <p>物流公司保存服务接口实现</p>
 * @author fcq
 * @date 2020-11-06 13:37:51
 */
@RestController
@Validated
public class LogisticsCompanyController implements LogisticsCompanyProvider {
	@Autowired
	private LogisticsCompanyService logisticsCompanyService;

	@Override
	public BaseResponse<Long> getCountByLogisticsName(@RequestBody LogisticsCompanyAddRequest logisticsCompanyAddRequest) {
		LogisticsCompany logisticsCompany = KsBeanUtil.convert(logisticsCompanyAddRequest, LogisticsCompany.class);
		Long vCount = logisticsCompanyService.getCountByLogisticsName(logisticsCompany);
		return BaseResponse.success(vCount);
	}

	@Override
	public BaseResponse<LogisticsCompanyAddResponse> add(@RequestBody @Valid LogisticsCompanyAddRequest logisticsCompanyAddRequest) {
		LogisticsCompany logisticsCompany = KsBeanUtil.convert(logisticsCompanyAddRequest, LogisticsCompany.class);
		if(logisticsCompany.getId()!=null){
			return BaseResponse.success(new LogisticsCompanyAddResponse(
					logisticsCompanyService.wrapperVo(logisticsCompanyService.modify(logisticsCompany))));
		}
		return BaseResponse.success(new LogisticsCompanyAddResponse(
				logisticsCompanyService.wrapperVo(logisticsCompanyService.add(logisticsCompany))));
	}

	@Override
	public BaseResponse<LogisticsCompanyAddResponse> addByApp(LogisticsCompanyAddRequest logisticsCompanyAddRequest) {
		LogisticsCompany logisticsCompany = KsBeanUtil.convert(logisticsCompanyAddRequest, LogisticsCompany.class);
		if(logisticsCompany.getId()!=null){
			LogisticsCompany dbCompany = logisticsCompanyService.getOne(logisticsCompany.getId());
			if(dbCompany!=null) {
				if (Constants.BOSS_DEFAULT_MARKET_ID.compareTo(dbCompany.getMarketId()) == 0) {
					dbCompany.setLogisticsName(logisticsCompany.getLogisticsName());
					return BaseResponse.success(new LogisticsCompanyAddResponse(
							logisticsCompanyService.wrapperVo(logisticsCompanyService.modify(dbCompany))));
				}else{
					return BaseResponse.success(new LogisticsCompanyAddResponse(
							logisticsCompanyService.wrapperVo(dbCompany)));
				}
			}
		}else{
			LogisticsCompany dbCompany = logisticsCompanyService.selectLogisticsListByMarketIdAndLogisticsName(logisticsCompany);
			if(dbCompany!=null){
				return BaseResponse.success(new LogisticsCompanyAddResponse(
						logisticsCompanyService.wrapperVo(dbCompany)));
			}
		}
		return BaseResponse.success(new LogisticsCompanyAddResponse(
				logisticsCompanyService.wrapperVo(logisticsCompanyService.add(logisticsCompany))));
	}

	@Override
	public BaseResponse<LogisticsCompanyModifyResponse> modify(@RequestBody @Valid LogisticsCompanyModifyRequest logisticsCompanyModifyRequest) {
		LogisticsCompany logisticsCompany = KsBeanUtil.convert(logisticsCompanyModifyRequest, LogisticsCompany.class);
		return BaseResponse.success(new LogisticsCompanyModifyResponse(
				logisticsCompanyService.wrapperVo(logisticsCompanyService.modify(logisticsCompany))));
	}

	@Override
	public BaseResponse deleteById(@RequestBody @Valid LogisticsCompanyDelByIdRequest logisticsCompanyDelByIdRequest) {
		LogisticsCompany logisticsCompany = KsBeanUtil.convert(logisticsCompanyDelByIdRequest, LogisticsCompany.class);
		logisticsCompany.setDelFlag(DeleteFlag.YES);
		logisticsCompanyService.deleteById(logisticsCompany);
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse deleteByIdList(@RequestBody @Valid LogisticsCompanyDelByIdListRequest logisticsCompanyDelByIdListRequest) {
		logisticsCompanyService.deleteByIdList(logisticsCompanyDelByIdListRequest.getIdList());
		return BaseResponse.SUCCESSFUL();
	}


	@Override
	public BaseResponse saveAll(LogisticsCompanyImportExcelRequest collect) {
		logisticsCompanyService.saveAll(collect.getLogisticsCompanyVOS());
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse<LogisticsCompanyImportResponse> selectLogisticsCompanyNumbers() {
		return BaseResponse.success(LogisticsCompanyImportResponse.builder().logisticsNames(logisticsCompanyService.selectLogisticsNames()).build());

	}

	@Override
	public BaseResponse<LogisticsCompanyImportResponse> selectLogisticsCompanyNumbersByStoreId(Long storeId,Integer logisticsType) {
		return BaseResponse.success(LogisticsCompanyImportResponse.builder().logisticsCompanyNumber(logisticsCompanyService.selectLogisticsCompanyNames(storeId,null,logisticsType)).build());
	}

	@Override
	public BaseResponse<LogisticsCompanyImportResponse> selectLogisticsCompanyNumbersByMarketId(Long marketId, Integer logisticsType) {
		return BaseResponse.success(LogisticsCompanyImportResponse.builder().logisticsCompanyNumber(logisticsCompanyService.selectLogisticsCompanyNames(null,marketId,logisticsType)).build());
	}

	@Override
	public  BaseResponse<LogisticsCompanyImportResponse> selectMaxId() {
		return BaseResponse.success(LogisticsCompanyImportResponse.builder().maxId(logisticsCompanyService.selectMaxId()).build());
	}

	@Override
	public BaseResponse<LogisticsCompanyAddResponse> syncLogisticsCompany(LogisticsCompanySyncRequest addReq) {
		logisticsCompanyService.syncLogisticsCompany(addReq);
		return BaseResponse.SUCCESSFUL();
	}
}

