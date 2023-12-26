package com.wanmi.sbc.setting.api.provider.logisticscompany;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.logisticscompany.LogisticsCompanyPageRequest;
import com.wanmi.sbc.setting.api.response.logisticscompany.LogisticsCompanyPageResponse;
import com.wanmi.sbc.setting.api.request.logisticscompany.LogisticsCompanyListRequest;
import com.wanmi.sbc.setting.api.response.logisticscompany.LogisticsCompanyListResponse;
import com.wanmi.sbc.setting.api.request.logisticscompany.LogisticsCompanyByIdRequest;
import com.wanmi.sbc.setting.api.response.logisticscompany.LogisticsCompanyByIdResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;

/**
 * <p>物流公司查询服务Provider</p>
 * @author fcq
 * @date 2020-11-06 13:37:51
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "LogisticsCompanyQueryProvider")
public interface LogisticsCompanyQueryProvider {

	/**
	 * 分页查询物流公司API
	 *
	 * @author fcq
	 * @param logisticsCompanyPageReq 分页请求参数和筛选对象 {@link LogisticsCompanyPageRequest}
	 * @return 物流公司分页列表信息 {@link LogisticsCompanyPageResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/logisticscompany/page")
	BaseResponse<LogisticsCompanyPageResponse> page(@RequestBody @Valid LogisticsCompanyPageRequest logisticsCompanyPageReq);

	/**
	 * 列表查询物流公司API
	 *
	 * @author fcq
	 * @param logisticsCompanyListReq 列表请求参数和筛选对象 {@link LogisticsCompanyListRequest}
	 * @return 物流公司的列表信息 {@link LogisticsCompanyListResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/logisticscompany/list")
	BaseResponse<LogisticsCompanyListResponse> list(@RequestBody @Valid LogisticsCompanyListRequest logisticsCompanyListReq);

	/**
	 * 单个查询物流公司API
	 *
	 * @author fcq
	 * @param logisticsCompanyByIdRequest 单个查询物流公司请求参数 {@link LogisticsCompanyByIdRequest}
	 * @return 物流公司详情 {@link LogisticsCompanyByIdResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/logisticscompany/get-by-id")
	BaseResponse<LogisticsCompanyByIdResponse> getById(@RequestBody @Valid LogisticsCompanyByIdRequest logisticsCompanyByIdRequest);

}

