package com.wanmi.sbc.returnorder.api.provider.settlement;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2018-12-07 13:49
 */
@FeignClient(value = "${application.returnOrder.name}", url="${feign.url.returnOrder:#{null}}", contextId = "ReturnSettlementAnalyseQueryProvider")
public interface SettlementAnalyseQueryProvider {
}
