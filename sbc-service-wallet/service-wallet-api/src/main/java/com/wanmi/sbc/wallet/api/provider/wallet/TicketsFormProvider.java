package com.wanmi.sbc.wallet.api.provider.wallet;


import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.wallet.api.request.wallet.CreateRechargeRequest;
import com.wanmi.sbc.wallet.api.request.wallet.TicketsFormAdoptRequest;
import com.wanmi.sbc.wallet.api.request.wallet.TicketsFormModifyRequest;
import com.wanmi.sbc.wallet.api.request.wallet.TicketsFormPageRequest;
import com.wanmi.sbc.wallet.api.response.wallet.TicketsFormListResponse;
import com.wanmi.sbc.wallet.api.response.wallet.TicketsFormQueryResponse;
import com.wanmi.sbc.wallet.bean.vo.TicketsFormQueryVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

@FeignClient(value = "${application.wallet.name}", contextId = "WalletTicketsFormProvider")
public interface TicketsFormProvider {

    @PostMapping("/wallet/${application.wallet.version}/wallet/ticketsFormAllList")
    BaseResponse<TicketsFormQueryResponse> ticketsFormAllList(@RequestBody @Valid TicketsFormPageRequest ticketsFormPageRequest);

    @PostMapping("/wallet/${application.wallet.version}/wallet/updateTicketsForm")
    BaseResponse saveTicketsForm(@RequestBody @Valid TicketsFormModifyRequest request);


    @PostMapping("/wallet/${application.wallet.version}/wallet/withdrawalList")
    BaseResponse<TicketsFormQueryResponse> withdrawalList(@RequestBody @Valid TicketsFormPageRequest ticketsFormPageRequest);

    @PostMapping("/wallet/${application.wallet.version}/wallet/rechargelList")
    BaseResponse<TicketsFormQueryResponse> rechargeList(@RequestBody @Valid TicketsFormPageRequest ticketsFormPageRequest);

    @PostMapping("/wallet/${application.wallet.version}/wallet/adopt")
    BaseResponse<List<TicketsFormQueryVO>> adopt(@RequestBody @Valid TicketsFormAdoptRequest request);

    @PostMapping("/wallet/${application.wallet.version}/wallet/payment")
    BaseResponse<List<TicketsFormQueryVO>> payment(@RequestBody @Valid TicketsFormAdoptRequest request);

    @PostMapping("/wallet/${application.wallet.version}/wallet/reject")
    BaseResponse<List<TicketsFormQueryVO>> reject(@RequestBody @Valid TicketsFormAdoptRequest request);

    @PostMapping("/wallet/${application.wallet.version}/wallet/createRecharge")
    BaseResponse createRecharge(@RequestBody @Valid CreateRechargeRequest request);

    @PostMapping("/wallet/${application.wallet.version}/wallet/rechargeAdopt")
    BaseResponse rechargeAdopt(@RequestBody @Valid TicketsFormAdoptRequest request);

    @PostMapping("/wallet/${application.wallet.version}/wallet/rechargeReject")
    BaseResponse rechargeReject(@RequestBody @Valid TicketsFormAdoptRequest request);

    /**
     * 根据查询条件查询所有工单列表-不分页
     * @param ticketsFormPageRequest
     * @return
     */
    @PostMapping("/wallet/${application.wallet.version}/wallet/ticketsFormAll")
    BaseResponse<TicketsFormListResponse> ticketsFormAll(@RequestBody TicketsFormPageRequest ticketsFormPageRequest);
}
