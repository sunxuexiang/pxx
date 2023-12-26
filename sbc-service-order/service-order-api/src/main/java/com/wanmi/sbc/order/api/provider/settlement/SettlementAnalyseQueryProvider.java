package com.wanmi.sbc.order.api.provider.settlement;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2018-12-07 13:49
 */
@FeignClient(value = "${application.order.name}", url="${feign.url.order:#{null}}", contextId = "SettlementAnalyseQueryProvider")
public interface SettlementAnalyseQueryProvider {
}
