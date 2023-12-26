package com.wanmi.sbc.customer.api.provider.invitationhistorysummary;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.customer.api.request.invitationhistorysummary.InvitationHistorySummaryPageRequest;
import com.wanmi.sbc.customer.api.response.invitationhistorysummary.InvitationHistorySummaryPageResponse;
import com.wanmi.sbc.customer.api.request.invitationhistorysummary.InvitationHistorySummaryListRequest;
import com.wanmi.sbc.customer.api.response.invitationhistorysummary.InvitationHistorySummaryListResponse;
import com.wanmi.sbc.customer.api.request.invitationhistorysummary.InvitationHistorySummaryByIdRequest;
import com.wanmi.sbc.customer.api.response.invitationhistorysummary.InvitationHistorySummaryByIdResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;

/**
 * <p>邀新历史汇总计表查询服务Provider</p>
 * @author fcq
 * @date 2021-04-27 11:31:55
 */
@FeignClient(value = "${application.customer.name}", url="${feign.url.customer:#{null}}", contextId = "InvitationHistorySummaryQueryProvider")
public interface InvitationHistorySummaryQueryProvider {

	/**
	 * 分页查询邀新历史汇总计表API
	 *
	 * @author fcq
	 * @param invitationHistorySummaryPageReq 分页请求参数和筛选对象 {@link InvitationHistorySummaryPageRequest}
	 * @return 邀新历史汇总计表分页列表信息 {@link InvitationHistorySummaryPageResponse}
	 */
	@PostMapping("/customer/${application.customer.version}/invitationhistorysummary/page")
	BaseResponse<InvitationHistorySummaryPageResponse> page(@RequestBody @Valid InvitationHistorySummaryPageRequest invitationHistorySummaryPageReq);

	/**
	 * 列表查询邀新历史汇总计表API
	 *
	 * @author fcq
	 * @param invitationHistorySummaryListReq 列表请求参数和筛选对象 {@link InvitationHistorySummaryListRequest}
	 * @return 邀新历史汇总计表的列表信息 {@link InvitationHistorySummaryListResponse}
	 */
	@PostMapping("/customer/${application.customer.version}/invitationhistorysummary/list")
	BaseResponse<InvitationHistorySummaryListResponse> list(@RequestBody @Valid InvitationHistorySummaryListRequest invitationHistorySummaryListReq);

	/**
	 * 单个查询邀新历史汇总计表API
	 *
	 * @author fcq
	 * @param invitationHistorySummaryByIdRequest 单个查询邀新历史汇总计表请求参数 {@link InvitationHistorySummaryByIdRequest}
	 * @return 邀新历史汇总计表详情 {@link InvitationHistorySummaryByIdResponse}
	 */
	@PostMapping("/customer/${application.customer.version}/invitationhistorysummary/get-by-id")
	BaseResponse<InvitationHistorySummaryByIdResponse> getById(@RequestBody @Valid InvitationHistorySummaryByIdRequest invitationHistorySummaryByIdRequest);

}

