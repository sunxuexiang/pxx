package com.wanmi.sbc.customer.api.provider.invitationstatistics;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.customer.api.request.invitationstatistics.InvitationRegisterStatisticsRequest;
import com.wanmi.sbc.customer.api.response.invitationstatistics.InvitationStatisticsListResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>邀新统计查询服务Provider</p>
 * @author lvheng
 * @date 2021-04-23 10:57:45
 */
@FeignClient(value = "${application.customer.name}", url="${feign.url.customer:#{null}}", contextId = "InvitationStatisticsQueryProvider")
public interface InvitationStatisticsQueryProvider {

    /**
     * 查询当月邀新
     * @param invitationStatisticsListReq
     * @return
     */
    @PostMapping("/customer/${application.customer.version}/invitationstatistics/list-month")
    BaseResponse<InvitationStatisticsListResponse> getMonthByEmployeeId(@RequestBody @Valid InvitationRegisterStatisticsRequest invitationStatisticsListReq);

    /**
     * 查询当前数据
     * @param invitationStatisticsListReq
     * @return
     */
    @PostMapping("/customer/${application.customer.version}/invitationstatistics/list-today")
    BaseResponse<InvitationStatisticsListResponse> getToday(@RequestBody @Valid InvitationRegisterStatisticsRequest invitationStatisticsListReq);
}

