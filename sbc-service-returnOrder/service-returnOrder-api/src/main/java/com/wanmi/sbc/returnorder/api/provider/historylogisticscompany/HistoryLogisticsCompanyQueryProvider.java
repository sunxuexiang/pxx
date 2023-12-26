package com.wanmi.sbc.returnorder.api.provider.historylogisticscompany;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.returnorder.api.request.historylogisticscompany.HistoryLogisticsCompanyByIdRequest;
import com.wanmi.sbc.returnorder.api.request.historylogisticscompany.HistoryLogisticsCompanyListRequest;
import com.wanmi.sbc.returnorder.api.request.historylogisticscompany.HistoryLogisticsCompanyPageRequest;
import com.wanmi.sbc.returnorder.api.response.historylogisticscompany.HistoryLogisticsCompanyByIdResponse;
import com.wanmi.sbc.returnorder.api.response.historylogisticscompany.HistoryLogisticsCompanyListResponse;
import com.wanmi.sbc.returnorder.api.response.historylogisticscompany.HistoryLogisticsCompanyPageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>物流公司历史记录查询服务Provider</p>
 * @author fcq
 * @date 2020-11-09 17:32:23
 */
@FeignClient(value = "${application.returnOrder.name}", url="${feign.url.returnOrder:#{null}}", contextId = "ReturnHistoryLogisticsCompanyQueryProvider")
public interface HistoryLogisticsCompanyQueryProvider {

	/**
	 * 分页查询物流公司历史记录API
	 *
	 * @author fcq
	 * @param historyLogisticsCompanyPageReq 分页请求参数和筛选对象 {@link HistoryLogisticsCompanyPageRequest}
	 * @return 物流公司历史记录分页列表信息 {@link HistoryLogisticsCompanyPageResponse}
	 */
	@PostMapping("/returnOrder/${application.order.version}/historylogisticscompany/page")
	BaseResponse<HistoryLogisticsCompanyPageResponse> page(@RequestBody @Valid HistoryLogisticsCompanyPageRequest historyLogisticsCompanyPageReq);

	/**
	 * 列表查询物流公司历史记录API
	 *
	 * @author fcq
	 * @param historyLogisticsCompanyListReq 列表请求参数和筛选对象 {@link HistoryLogisticsCompanyListRequest}
	 * @return 物流公司历史记录的列表信息 {@link HistoryLogisticsCompanyListResponse}
	 */
	@PostMapping("/returnOrder/${application.order.version}/historylogisticscompany/list")
	BaseResponse<HistoryLogisticsCompanyListResponse> list(@RequestBody @Valid HistoryLogisticsCompanyListRequest historyLogisticsCompanyListReq);

	/**
	 * 单个查询物流公司历史记录API
	 *
	 * @author fcq
	 * @param historyLogisticsCompanyByIdRequest 单个查询物流公司历史记录请求参数 {@link HistoryLogisticsCompanyByIdRequest}
	 * @return 物流公司历史记录详情 {@link HistoryLogisticsCompanyByIdResponse}
	 */
	@PostMapping("/returnOrder/${application.order.version}/historylogisticscompany/get-by-id")
	BaseResponse<HistoryLogisticsCompanyByIdResponse> getById(@RequestBody @Valid HistoryLogisticsCompanyByIdRequest historyLogisticsCompanyByIdRequest);



}

