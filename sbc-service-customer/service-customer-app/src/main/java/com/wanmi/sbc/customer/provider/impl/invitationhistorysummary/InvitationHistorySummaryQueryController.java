package com.wanmi.sbc.customer.provider.impl.invitationhistorysummary;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.invitationhistorysummary.InvitationHistorySummaryQueryProvider;
import com.wanmi.sbc.customer.api.request.invitationhistorysummary.InvitationHistorySummaryPageRequest;
import com.wanmi.sbc.customer.api.request.invitationhistorysummary.InvitationHistorySummaryQueryRequest;
import com.wanmi.sbc.customer.api.response.invitationhistorysummary.InvitationHistorySummaryPageResponse;
import com.wanmi.sbc.customer.api.request.invitationhistorysummary.InvitationHistorySummaryListRequest;
import com.wanmi.sbc.customer.api.response.invitationhistorysummary.InvitationHistorySummaryListResponse;
import com.wanmi.sbc.customer.api.request.invitationhistorysummary.InvitationHistorySummaryByIdRequest;
import com.wanmi.sbc.customer.api.response.invitationhistorysummary.InvitationHistorySummaryByIdResponse;
import com.wanmi.sbc.customer.bean.vo.InvitationHistorySummaryVO;
import com.wanmi.sbc.customer.invitationhistorysummary.service.InvitationHistorySummaryService;
import com.wanmi.sbc.customer.invitationhistorysummary.model.root.InvitationHistorySummary;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>邀新历史汇总计表查询服务接口实现</p>
 * @author fcq
 * @date 2021-04-27 11:31:55
 */
@RestController
@Validated
public class InvitationHistorySummaryQueryController implements InvitationHistorySummaryQueryProvider {
	@Autowired
	private InvitationHistorySummaryService invitationHistorySummaryService;

	@Override
	public BaseResponse<InvitationHistorySummaryPageResponse> page(@RequestBody @Valid InvitationHistorySummaryPageRequest invitationHistorySummaryPageReq) {
		InvitationHistorySummaryQueryRequest queryReq = KsBeanUtil.convert(invitationHistorySummaryPageReq, InvitationHistorySummaryQueryRequest.class);
		Page<InvitationHistorySummary> invitationHistorySummaryPage = invitationHistorySummaryService.page(queryReq);
		Page<InvitationHistorySummaryVO> newPage = invitationHistorySummaryPage.map(entity -> invitationHistorySummaryService.wrapperVo(entity));
		MicroServicePage<InvitationHistorySummaryVO> microPage = new MicroServicePage<>(newPage, invitationHistorySummaryPageReq.getPageable());
		InvitationHistorySummaryPageResponse finalRes = new InvitationHistorySummaryPageResponse(microPage);
		return BaseResponse.success(finalRes);
	}

	@Override
	public BaseResponse<InvitationHistorySummaryListResponse> list(@RequestBody @Valid InvitationHistorySummaryListRequest invitationHistorySummaryListReq) {
		InvitationHistorySummaryQueryRequest queryReq = KsBeanUtil.convert(invitationHistorySummaryListReq, InvitationHistorySummaryQueryRequest.class);
		List<InvitationHistorySummary> invitationHistorySummaryList = invitationHistorySummaryService.list(queryReq);
		List<InvitationHistorySummaryVO> newList = invitationHistorySummaryList.stream().map(entity -> invitationHistorySummaryService.wrapperVo(entity)).collect(Collectors.toList());
		return BaseResponse.success(new InvitationHistorySummaryListResponse(newList));
	}

	@Override
	public BaseResponse<InvitationHistorySummaryByIdResponse> getById(@RequestBody @Valid InvitationHistorySummaryByIdRequest invitationHistorySummaryByIdRequest) {
		InvitationHistorySummary invitationHistorySummary =
		invitationHistorySummaryService.getOne(invitationHistorySummaryByIdRequest.getEmployeeId());
		return BaseResponse.success(new InvitationHistorySummaryByIdResponse(invitationHistorySummaryService.wrapperVo(invitationHistorySummary)));
	}

}

