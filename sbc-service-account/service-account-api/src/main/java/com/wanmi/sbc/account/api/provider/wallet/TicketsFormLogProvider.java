package com.wanmi.sbc.account.api.provider.wallet;

import com.wanmi.sbc.common.base.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(value = "${application.account.name}", url="${feign.url.account:#{null}}", contextId = "TicketsFormLogProvider")
public interface TicketsFormLogProvider {

}
