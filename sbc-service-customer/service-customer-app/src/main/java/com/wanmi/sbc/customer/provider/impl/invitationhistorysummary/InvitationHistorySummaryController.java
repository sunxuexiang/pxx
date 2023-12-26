package com.wanmi.sbc.customer.provider.impl.invitationhistorysummary;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.invitationhistorysummary.InvitationHistorySummaryProvider;
import com.wanmi.sbc.customer.api.request.invitationhistorysummary.InvitationHistorySummaryAddRequest;
import com.wanmi.sbc.customer.api.request.invitationhistorysummary.InvitationHistorySummaryBatchAddRequest;
import com.wanmi.sbc.customer.api.request.invitationhistorysummary.InvitationHistorySummaryModifyRequest;
import com.wanmi.sbc.customer.api.response.invitationhistorysummary.InvitationHistorySummaryAddResponse;
import com.wanmi.sbc.customer.api.response.invitationhistorysummary.InvitationHistorySummaryModifyResponse;
import com.wanmi.sbc.customer.invitationhistorysummary.model.root.InvitationHistorySummary;
import com.wanmi.sbc.customer.invitationhistorysummary.service.InvitationHistorySummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>邀新历史汇总计表保存服务接口实现</p>
 * @author fcq
 * @date 2021-04-27 11:31:55
 */
@RestController
@Validated
public class InvitationHistorySummaryController implements InvitationHistorySummaryProvider {
	@Autowired
	private InvitationHistorySummaryService invitationHistorySummaryService;

	@Override
	public BaseResponse<InvitationHistorySummaryAddResponse> add(@RequestBody @Valid InvitationHistorySummaryAddRequest invitationHistorySummaryAddRequest) {
		InvitationHistorySummary invitationHistorySummary = KsBeanUtil.convert(invitationHistorySummaryAddRequest, InvitationHistorySummary.class);
		return BaseResponse.success(new InvitationHistorySummaryAddResponse(
				invitationHistorySummaryService.wrapperVo(invitationHistorySummaryService.add(invitationHistorySummary))));
	}

	@Override
	public BaseResponse batchAdd(@RequestBody @Valid InvitationHistorySummaryBatchAddRequest invitationHistorySummaryAddRequest) {
		List<InvitationHistorySummary> historySummaries =
				KsBeanUtil.convert(invitationHistorySummaryAddRequest.getHistorySummaryVO(),
				InvitationHistorySummary.class);
		invitationHistorySummaryService.batchAdd(historySummaries);
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse<InvitationHistorySummaryModifyResponse> modify(@RequestBody @Valid InvitationHistorySummaryModifyRequest invitationHistorySummaryModifyRequest) {
		InvitationHistorySummary invitationHistorySummary = KsBeanUtil.convert(invitationHistorySummaryModifyRequest, InvitationHistorySummary.class);
		return BaseResponse.success(new InvitationHistorySummaryModifyResponse(
				invitationHistorySummaryService.wrapperVo(invitationHistorySummaryService.modify(invitationHistorySummary))));
	}



}

