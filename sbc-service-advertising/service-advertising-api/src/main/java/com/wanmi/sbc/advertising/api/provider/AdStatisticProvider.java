package com.wanmi.sbc.advertising.api.provider;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author zc
 *
 */
@FeignClient(value = "${application.advertising.name}", url = "${feign.url.advertising:#{null}}", contextId = "AdStatisticProvider")
public interface AdStatisticProvider {




}
