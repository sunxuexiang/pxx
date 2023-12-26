package com.wanmi.sbc.customer.provider.impl.invitationstatistics;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.customer.api.provider.invitationstatistics.InvitationStatisticsQueryProvider;
import com.wanmi.sbc.customer.api.request.invitationstatistics.InvitationRegisterStatisticsRequest;
import com.wanmi.sbc.customer.api.response.invitationstatistics.InvitationStatisticsListResponse;
import com.wanmi.sbc.customer.bean.vo.InvitationStatisticsVO;
import com.wanmi.sbc.customer.invitationstatistics.model.root.InvitationStatistics;
import com.wanmi.sbc.customer.invitationstatistics.service.InvitationStatisticsService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>邀新统计查询服务接口实现</p>
 * @author lvheng
 * @date 2021-04-23 10:57:45
 */
@RestController
@Validated
public class InvitationStatisticsQueryController implements InvitationStatisticsQueryProvider {
	@Autowired
	private InvitationStatisticsService invitationStatisticsService;

	@Override
	public BaseResponse<InvitationStatisticsListResponse> getMonthByEmployeeId(@RequestBody @Valid InvitationRegisterStatisticsRequest request) {
		List<InvitationStatistics> invitationStatisticsList = invitationStatisticsService.getMonthByEmployeeId(request);
        if (CollectionUtils.isEmpty(invitationStatisticsList)) {
            return BaseResponse.success(new InvitationStatisticsListResponse());
        }
		List<InvitationStatisticsVO> newList = invitationStatisticsList.stream().map(entity -> invitationStatisticsService.wrapperVo(entity)).collect(Collectors.toList());
		return BaseResponse.success(new InvitationStatisticsListResponse(newList));
	}


	@Override
	public BaseResponse<InvitationStatisticsListResponse> getToday(@RequestBody @Valid InvitationRegisterStatisticsRequest request) {
		List<InvitationStatistics> invitationStatisticsList = invitationStatisticsService.getToday(request);
		if (CollectionUtils.isEmpty(invitationStatisticsList)) {
			return BaseResponse.success(new InvitationStatisticsListResponse());
		}
		List<InvitationStatisticsVO> newList = invitationStatisticsList.stream().map(entity -> invitationStatisticsService.wrapperVo(entity)).collect(Collectors.toList());
		return BaseResponse.success(new InvitationStatisticsListResponse(newList));
	}

}

