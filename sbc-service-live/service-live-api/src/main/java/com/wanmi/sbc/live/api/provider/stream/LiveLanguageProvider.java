package com.wanmi.sbc.live.api.provider.stream;

import com.wanmi.sbc.common.base.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "${application.live.name}", url="${feign.url.live:#{null}}",contextId = "LiveLanguageProvider")
public interface LiveLanguageProvider {

    @PostMapping("/live/${application.live.version}/liveLanguage/getList")
    BaseResponse getList();
}
