package com.wanmi.sbc.customer.api.provider.invitationhistorysummary;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.customer.api.request.invitationhistorysummary.InvitationHistorySummaryAddRequest;
import com.wanmi.sbc.customer.api.request.invitationhistorysummary.InvitationHistorySummaryBatchAddRequest;
import com.wanmi.sbc.customer.api.request.invitationhistorysummary.InvitationHistorySummaryModifyRequest;
import com.wanmi.sbc.customer.api.response.invitationhistorysummary.InvitationHistorySummaryAddResponse;
import com.wanmi.sbc.customer.api.response.invitationhistorysummary.InvitationHistorySummaryModifyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>邀新历史汇总计表保存服务Provider</p>
 * @author fcq
 * @date 2021-04-27 11:31:55
 */
@FeignClient(value = "${application.customer.name}", url="${feign.url.customer:#{null}}", contextId = "InvitationHistorySummaryProvider")
public interface InvitationHistorySummaryProvider {

	/**
	 * 新增邀新历史汇总计表API
	 *
	 * @author fcq
	 * @param invitationHistorySummaryAddRequest 邀新历史汇总计表新增参数结构 {@link InvitationHistorySummaryAddRequest}
	 * @return 新增的邀新历史汇总计表信息 {@link InvitationHistorySummaryAddResponse}
	 */
	@PostMapping("/customer/${application.customer.version}/invitationhistorysummary/add")
	BaseResponse<InvitationHistorySummaryAddResponse> add(@RequestBody @Valid InvitationHistorySummaryAddRequest invitationHistorySummaryAddRequest);

	@PostMapping("/customer/${application.customer.version}/invitationhistorysummary/batchAdd")
	BaseResponse<InvitationHistorySummaryAddResponse> batchAdd(@RequestBody @Valid InvitationHistorySummaryBatchAddRequest request);


	/**
	 * 修改邀新历史汇总计表API
	 *
	 * @author fcq
	 * @param invitationHistorySummaryModifyRequest 邀新历史汇总计表修改参数结构 {@link InvitationHistorySummaryModifyRequest}
	 * @return 修改的邀新历史汇总计表信息 {@link InvitationHistorySummaryModifyResponse}
	 */
	@PostMapping("/customer/${application.customer.version}/invitationhistorysummary/modify")
	BaseResponse<InvitationHistorySummaryModifyResponse> modify(@RequestBody @Valid InvitationHistorySummaryModifyRequest invitationHistorySummaryModifyRequest);





}

