package com.wanmi.sbc.account.api.provider.wallet;

import com.wanmi.sbc.account.api.request.wallet.TicketsFormImgRequest;
import com.wanmi.sbc.common.base.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "${application.account.name}", url="${feign.url.account:#{null}}", contextId = "TicketsFormImgProvider")
public interface TicketsFormImgProvider {

    @PostMapping("/account/${application.account.version}/TicketsFormImg/addTicketsFormImgs")
    BaseResponse addTicketsFormImgs(@RequestBody TicketsFormImgRequest ticketsFormImgRequest);

    @PostMapping("/account/${application.account.version}/TicketsFormImg/deleteTicketsFormImgs")
    BaseResponse deleteTicketsFormImgs(@RequestBody TicketsFormImgRequest ticketsFormImgRequest);

    @PostMapping("/account/${application.account.version}/TicketsFormImg/listTicketsFormImgs")
    BaseResponse listTicketsFormImgs(@RequestBody TicketsFormImgRequest ticketsFormImgRequest);

}
