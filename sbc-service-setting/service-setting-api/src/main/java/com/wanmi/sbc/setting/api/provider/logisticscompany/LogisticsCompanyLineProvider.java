package com.wanmi.sbc.setting.api.provider.logisticscompany;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.logisticscompany.LogisticsCompanyLineRequest;
import com.wanmi.sbc.setting.api.response.logisticscompany.LogisticsCompanyLineListResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <p>物流线路</p>
 * @author fcq
 * @date 2020-11-06 13:37:51
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "LogisticsCompanyLineProvider")
public interface LogisticsCompanyLineProvider {

	@PostMapping("/setting/${application.setting.version}/logisticscompanyLine/add")
	BaseResponse add(@RequestBody LogisticsCompanyLineRequest logisticsCompanyLineAddRequest);

	@PostMapping("/setting/${application.setting.version}/logisticscompanyLine/modify")
	BaseResponse modify(@RequestBody LogisticsCompanyLineRequest logisticsCompanyLineModifyRequest);

	@PostMapping("/setting/${application.setting.version}/logisticscompanyLine/deleteByParam")
	BaseResponse deleteByParam(@RequestBody LogisticsCompanyLineRequest logisticsCompanyLineLineRequest);

	@GetMapping("/setting/${application.setting.version}/logisticscompanyLine/get-list-by-logisticId")
	BaseResponse<LogisticsCompanyLineListResponse> selectLogisticsCompanyLineNumbersByStoreId(@RequestParam(value="logisticId")Long logisticId);
}

