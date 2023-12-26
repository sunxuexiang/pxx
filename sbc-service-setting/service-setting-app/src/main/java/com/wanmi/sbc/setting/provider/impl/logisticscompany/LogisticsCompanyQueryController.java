package com.wanmi.sbc.setting.provider.impl.logisticscompany;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.common.util.*;
import com.wanmi.sbc.common.util.excel.ExcelHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.setting.api.provider.logisticscompany.LogisticsCompanyQueryProvider;
import com.wanmi.sbc.setting.api.request.logisticscompany.LogisticsCompanyPageRequest;
import com.wanmi.sbc.setting.api.request.logisticscompany.LogisticsCompanyQueryRequest;
import com.wanmi.sbc.setting.api.response.logisticscompany.LogisticsCompanyPageResponse;
import com.wanmi.sbc.setting.api.request.logisticscompany.LogisticsCompanyListRequest;
import com.wanmi.sbc.setting.api.response.logisticscompany.LogisticsCompanyListResponse;
import com.wanmi.sbc.setting.api.request.logisticscompany.LogisticsCompanyByIdRequest;
import com.wanmi.sbc.setting.api.response.logisticscompany.LogisticsCompanyByIdResponse;
import com.wanmi.sbc.setting.bean.vo.LogisticsCompanyVO;
import com.wanmi.sbc.setting.logisticscompany.service.LogisticsCompanyService;
import com.wanmi.sbc.setting.logisticscompany.model.root.LogisticsCompany;
import javax.validation.Valid;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>物流公司查询服务接口实现</p>
 * @author fcq
 * @date 2020-11-06 13:37:51
 */
@RestController
@Validated
public class LogisticsCompanyQueryController implements LogisticsCompanyQueryProvider {
	@Autowired
	private LogisticsCompanyService logisticsCompanyService;

	@Override
	public BaseResponse<LogisticsCompanyPageResponse> page(@RequestBody @Valid LogisticsCompanyPageRequest logisticsCompanyPageReq) {
		LogisticsCompanyQueryRequest queryReq = KsBeanUtil.convert(logisticsCompanyPageReq, LogisticsCompanyQueryRequest.class);
		queryReq.setDelFlag(DeleteFlag.NO);
		Page<LogisticsCompany> logisticsCompanyPage = logisticsCompanyService.page(queryReq);
		Page<LogisticsCompanyVO> newPage = logisticsCompanyPage.map(entity -> logisticsCompanyService.wrapperVo(entity));
		MicroServicePage<LogisticsCompanyVO> microPage = new MicroServicePage<>(newPage, logisticsCompanyPageReq.getPageable());
		LogisticsCompanyPageResponse finalRes = new LogisticsCompanyPageResponse(microPage);
		return BaseResponse.success(finalRes);
	}

	@Override
	public BaseResponse<LogisticsCompanyListResponse> list(@RequestBody @Valid LogisticsCompanyListRequest logisticsCompanyListReq) {
		LogisticsCompanyQueryRequest queryReq = KsBeanUtil.convert(logisticsCompanyListReq, LogisticsCompanyQueryRequest.class);
		queryReq.setDelFlag(DeleteFlag.NO);
		//去掉空格
		if(queryReq.getLogisticsName()!=null){
			queryReq.setLogisticsName(queryReq.getLogisticsName().trim());
		}
		List<LogisticsCompany> logisticsCompanyList = logisticsCompanyService.list(queryReq);
		List<LogisticsCompanyVO> newList = logisticsCompanyList.stream().map(entity -> logisticsCompanyService.wrapperVo(entity)).collect(Collectors.toList());
		//Collections.sort(newList, Comparator.comparing(LogisticsCompanyVO::getCreateTime).reversed());
		Collections.sort(newList, new Comparator<LogisticsCompanyVO>() {
			@Override
			public int compare(LogisticsCompanyVO o1, LogisticsCompanyVO o2) {
				if(o1.getCreateTime()!=null && o2.getCreateTime()!=null){
					return o2.getCreateTime().compareTo(o1.getCreateTime());
				}
				return 1;
			}
		});
		return BaseResponse.success(new LogisticsCompanyListResponse(newList));
	}

	@Override
	public BaseResponse<LogisticsCompanyByIdResponse> getById(@RequestBody @Valid LogisticsCompanyByIdRequest logisticsCompanyByIdRequest) {
		LogisticsCompany logisticsCompany =
		logisticsCompanyService.getOne(logisticsCompanyByIdRequest.getId());
		if (Objects.isNull(logisticsCompany)) {
			return BaseResponse.SUCCESSFUL();
		}
		return BaseResponse.success(new LogisticsCompanyByIdResponse(logisticsCompanyService.wrapperVo(logisticsCompany)));
	}

}

