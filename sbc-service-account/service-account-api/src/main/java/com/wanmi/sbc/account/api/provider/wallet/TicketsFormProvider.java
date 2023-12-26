package com.wanmi.sbc.account.api.provider.wallet;

import com.wanmi.sbc.account.api.request.wallet.CreateRechargeRequest;
import com.wanmi.sbc.account.api.request.wallet.TicketsFormAdoptRequest;
import com.wanmi.sbc.account.api.request.wallet.TicketsFormModifyRequest;
import com.wanmi.sbc.account.api.request.wallet.TicketsFormPageRequest;
import com.wanmi.sbc.account.api.response.wallet.TicketsFormListResponse;
import com.wanmi.sbc.account.api.response.wallet.TicketsFormQueryResponse;
import com.wanmi.sbc.account.bean.vo.TicketsFormQueryVO;
import com.wanmi.sbc.common.base.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

@FeignClient(value = "${application.account.name}", url="${feign.url.account:#{null}}", contextId = "TicketsFormProvider")
public interface TicketsFormProvider {

    @PostMapping("/account/${application.account.version}/wallet/ticketsFormAllList")
    BaseResponse<TicketsFormQueryResponse> ticketsFormAllList(@RequestBody @Valid TicketsFormPageRequest ticketsFormPageRequest);

    @PostMapping("/account/${application.account.version}/wallet/updateTicketsForm")
    BaseResponse saveTicketsForm(@RequestBody @Valid TicketsFormModifyRequest request);


    @PostMapping("/account/${application.account.version}/wallet/withdrawalList")
    BaseResponse<TicketsFormQueryResponse> withdrawalList(@RequestBody @Valid TicketsFormPageRequest ticketsFormPageRequest);

    @PostMapping("/account/${application.account.version}/wallet/rechargelList")
    BaseResponse<TicketsFormQueryResponse> rechargeList(@RequestBody @Valid TicketsFormPageRequest ticketsFormPageRequest);

    @PostMapping("/account/${application.account.version}/wallet/adopt")
    BaseResponse<List<TicketsFormQueryVO>> adopt(@RequestBody @Valid TicketsFormAdoptRequest request);

    @PostMapping("/account/${application.account.version}/wallet/payment")
    BaseResponse<List<TicketsFormQueryVO>> payment(@RequestBody @Valid TicketsFormAdoptRequest request);

    @PostMapping("/account/${application.account.version}/wallet/reject")
    BaseResponse<List<TicketsFormQueryVO>> reject(@RequestBody @Valid TicketsFormAdoptRequest request);

    @PostMapping("/account/${application.account.version}/wallet/updateImgAfterReject")
    BaseResponse<List<TicketsFormQueryVO>> updateImgAfterReject(@RequestBody @Valid TicketsFormAdoptRequest request);

    @PostMapping("/account/${application.account.version}/wallet/createRecharge")
    BaseResponse createRecharge(@RequestBody @Valid CreateRechargeRequest request);

    @PostMapping("/account/${application.account.version}/wallet/rechargeAdopt")
    BaseResponse rechargeAdopt(@RequestBody @Valid TicketsFormAdoptRequest request);

    @PostMapping("/account/${application.account.version}/wallet/rechargeReject")
    BaseResponse rechargeReject(@RequestBody @Valid TicketsFormAdoptRequest request);

    /**
     * 根据查询条件查询所有工单列表-不分页
     * @param ticketsFormPageRequest
     * @return
     */
    @PostMapping("/account/${application.account.version}/wallet/ticketsFormAll")
    BaseResponse<TicketsFormListResponse> ticketsFormAll(@RequestBody TicketsFormPageRequest ticketsFormPageRequest);
}
