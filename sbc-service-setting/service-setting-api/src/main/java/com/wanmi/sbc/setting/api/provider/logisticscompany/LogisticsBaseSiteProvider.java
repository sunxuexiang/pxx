package com.wanmi.sbc.setting.api.provider.logisticscompany;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.logisticscompany.LogisticsBaseSiteRequest;
import com.wanmi.sbc.setting.api.response.logisticscompany.LogisticsBaseSiteListResponse;
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
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "LogisticsBaseSiteProvider")
public interface LogisticsBaseSiteProvider {

	@PostMapping("/setting/${application.setting.version}/logisticsBaseSite/add")
	BaseResponse add(@RequestBody LogisticsBaseSiteRequest logisticsBaseSiteAddRequest);

	@PostMapping("/setting/${application.setting.version}/logisticsBaseSite/modify")
	BaseResponse modify(@RequestBody LogisticsBaseSiteRequest logisticsBaseSiteModifyRequest);

	@PostMapping("/setting/${application.setting.version}/logisticsBaseSite/deleteByParam")
	BaseResponse deleteByParam(@RequestBody LogisticsBaseSiteRequest logisticsBaseSiteLineRequest);

	@GetMapping("/setting/${application.setting.version}/logisticsBaseSite/get-list-by-logisticId")
	BaseResponse<LogisticsBaseSiteListResponse> selectLogisticsBaseSiteByLogisticId(@RequestParam(value="logisticId")Long logisticId);

	@GetMapping("/setting/${application.setting.version}/logisticsBaseSite/get-list-by-customerid")
	BaseResponse<LogisticsBaseSiteListResponse> selectLogisticsBaseSiteBycustomerId(@RequestParam(value="customerId")String customerId);

	@GetMapping("/setting/${application.setting.version}/logisticsBaseSite/getLatestSiteNameByCustomerId")
	BaseResponse<String> getLatestSiteNameByCustomerId(@RequestParam(value="customerId")String customerId);
}

