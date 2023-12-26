package com.wanmi.sbc.returnorder.api.provider.historylogisticscompany;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.returnorder.api.request.historylogisticscompany.HistoryLogisticsCompanyAddRequest;
import com.wanmi.sbc.returnorder.api.request.historylogisticscompany.HistoryLogisticsCompanyDelByIdListRequest;
import com.wanmi.sbc.returnorder.api.request.historylogisticscompany.HistoryLogisticsCompanyDelByIdRequest;
import com.wanmi.sbc.returnorder.api.request.historylogisticscompany.HistoryLogisticsCompanyModifyRequest;
import com.wanmi.sbc.returnorder.api.response.historylogisticscompany.HistoryLogisticsCompanyAddResponse;
import com.wanmi.sbc.returnorder.api.response.historylogisticscompany.HistoryLogisticsCompanyModifyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>物流信息历史记录保存服务Provider</p>
 * @author fcq
 * @date 2020-11-09 17:32:23
 */
@FeignClient(value = "${application.returnOrder.name}", url="${feign.url.returnOrder:#{null}}", contextId = "ReturnHistoryLogisticsCompanyProvider")
public interface HistoryLogisticsCompanyProvider {

	/**
	 * 新增物流信息历史记录API
	 *
	 * @author fcq
	 * @param historyLogisticsCompanyAddRequest 物流信息历史记录新增参数结构 {@link HistoryLogisticsCompanyAddRequest}
	 * @return 新增的物流信息历史记录信息 {@link HistoryLogisticsCompanyAddResponse}
	 */
	@PostMapping("/returnOrder/${application.order.version}/historylogisticscompany/add")
	BaseResponse<HistoryLogisticsCompanyAddResponse> add(@RequestBody @Valid HistoryLogisticsCompanyAddRequest historyLogisticsCompanyAddRequest);

	/**
	 * 修改物流信息历史记录API
	 *
	 * @author fcq
	 * @param historyLogisticsCompanyModifyRequest 物流信息历史记录修改参数结构 {@link HistoryLogisticsCompanyModifyRequest}
	 * @return 修改的物流信息历史记录信息 {@link HistoryLogisticsCompanyModifyResponse}
	 */
	@PostMapping("/returnOrder/${application.order.version}/historylogisticscompany/modify")
	BaseResponse<HistoryLogisticsCompanyModifyResponse> modify(@RequestBody @Valid HistoryLogisticsCompanyModifyRequest historyLogisticsCompanyModifyRequest);

	/**
	 * 单个删除物流信息历史记录API
	 *
	 * @author fcq
	 * @param historyLogisticsCompanyDelByIdRequest 单个删除参数结构 {@link HistoryLogisticsCompanyDelByIdRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/returnOrder/${application.order.version}/historylogisticscompany/delete-by-id")
	BaseResponse deleteById(@RequestBody @Valid HistoryLogisticsCompanyDelByIdRequest historyLogisticsCompanyDelByIdRequest);

	/**
	 * 批量删除物流信息历史记录API
	 *
	 * @author fcq
	 * @param historyLogisticsCompanyDelByIdListRequest 批量删除参数结构 {@link HistoryLogisticsCompanyDelByIdListRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/returnOrder/${application.order.version}/historylogisticscompany/delete-by-id-list")
	BaseResponse deleteByIdList(@RequestBody @Valid HistoryLogisticsCompanyDelByIdListRequest historyLogisticsCompanyDelByIdListRequest);

}

