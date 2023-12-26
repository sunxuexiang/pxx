package com.wanmi.sbc.customer.api.provider.invitationstatistics;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.customer.api.request.invitationstatistics.InvitationRegisterStatisticsRequest;
import com.wanmi.sbc.customer.api.request.invitationstatistics.InvitationTradeStatisticsRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>邀新统计保存服务Provider</p>
 * @author lvheng
 * @date 2021-04-23 10:57:45
 */
@FeignClient(value = "${application.customer.name}", url="${feign.url.customer:#{null}}", contextId = "InvitationStatisticsProvider")
public interface InvitationStatisticsProvider {

	/**
	 * 邀新统计  注册数
	 * @param request
	 * @return
	 */
	@PostMapping("/customer/${application.customer.version}/register/statistic")
	BaseResponse registerStatistics(@RequestBody @Valid InvitationRegisterStatisticsRequest request);

	/**
	 * 邀新统计  订单信息
	 */
	@PostMapping("/customer/${application.customer.version}/trade/statistic")
	BaseResponse tradeStatistics(@RequestBody @Valid InvitationTradeStatisticsRequest request);
}

