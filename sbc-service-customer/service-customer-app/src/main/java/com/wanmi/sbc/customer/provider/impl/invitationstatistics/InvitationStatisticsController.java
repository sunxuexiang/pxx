package com.wanmi.sbc.customer.provider.impl.invitationstatistics;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.customer.api.provider.invitationstatistics.InvitationStatisticsProvider;
import com.wanmi.sbc.customer.api.request.invitationstatistics.InvitationRegisterStatisticsRequest;
import com.wanmi.sbc.customer.api.request.invitationstatistics.InvitationTradeStatisticsRequest;
import com.wanmi.sbc.customer.invitationstatistics.service.InvitationStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * <p>邀新统计保存服务接口实现</p>
 *
 * @author lvheng
 * @date 2021-04-23 10:57:45
 */
@RestController
@Validated
public class InvitationStatisticsController implements InvitationStatisticsProvider {

    @Autowired
    private InvitationStatisticsService invitationStatisticsService;


    @Override
    public BaseResponse registerStatistics(@Valid InvitationRegisterStatisticsRequest request) {
        invitationStatisticsService.registerStatistics(request.getEmployeeId());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse tradeStatistics(@Valid InvitationTradeStatisticsRequest request) {
        invitationStatisticsService.tradeStatistics(request.getEmployeeId(), request.getTradePrice(), request.getGoodsCount(),request.getOrderId());
        return BaseResponse.SUCCESSFUL();
    }
}

