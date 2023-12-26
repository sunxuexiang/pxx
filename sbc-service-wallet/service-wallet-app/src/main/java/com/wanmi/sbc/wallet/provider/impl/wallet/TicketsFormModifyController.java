package com.wanmi.sbc.wallet.provider.impl.wallet;


import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.wallet.api.provider.wallet.TicketsFormProvider;
import com.wanmi.sbc.wallet.api.request.wallet.CreateRechargeRequest;
import com.wanmi.sbc.wallet.api.request.wallet.TicketsFormAdoptRequest;
import com.wanmi.sbc.wallet.api.request.wallet.TicketsFormModifyRequest;
import com.wanmi.sbc.wallet.api.request.wallet.TicketsFormPageRequest;
import com.wanmi.sbc.wallet.api.response.wallet.TicketsFormListResponse;
import com.wanmi.sbc.wallet.api.response.wallet.TicketsFormQueryResponse;
import com.wanmi.sbc.wallet.bean.vo.TicketsFormQueryVO;
import com.wanmi.sbc.wallet.wallet.model.root.TicketsForm;
import com.wanmi.sbc.wallet.wallet.service.TicketsFormModifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
public class TicketsFormModifyController implements TicketsFormProvider {
    @Autowired
    TicketsFormModifyService ticketsFormModifyService;

    @Override
    public BaseResponse saveTicketsForm(TicketsFormModifyRequest request) {
        TicketsForm convert = KsBeanUtil.convert(request, TicketsForm.class);
        return ticketsFormModifyService.saveTicketsForm(convert);
    }

    /**
     * 工单列表
     * @return
     */
    @Override
    public BaseResponse<TicketsFormQueryResponse> ticketsFormAllList(TicketsFormPageRequest ticketsFormPageRequest) {
        com.wanmi.sbc.wallet.wallet.request.TicketsFormPageRequest convert = KsBeanUtil.convert(ticketsFormPageRequest, com.wanmi.sbc.wallet.wallet.request.TicketsFormPageRequest.class);
        TicketsFormQueryResponse ticketsFormQueries = ticketsFormModifyService.ticketsFormAllList(convert);
        return BaseResponse.success(ticketsFormQueries);
    }

    /**
     * 提现列表
     * @return
     */
    @Override
    public BaseResponse<TicketsFormQueryResponse> withdrawalList(TicketsFormPageRequest ticketsFormPageRequest) {
        com.wanmi.sbc.wallet.wallet.request.TicketsFormPageRequest convert = KsBeanUtil.convert(ticketsFormPageRequest, com.wanmi.sbc.wallet.wallet.request.TicketsFormPageRequest.class);
        TicketsFormQueryResponse ticketsFormQueries = ticketsFormModifyService.withdrawalList(convert);
        return BaseResponse.success(ticketsFormQueries);
    }

    @Override
    public BaseResponse<TicketsFormQueryResponse> rechargeList(TicketsFormPageRequest ticketsFormPageRequest) {
        com.wanmi.sbc.wallet.wallet.request.TicketsFormPageRequest convert = KsBeanUtil.convert(ticketsFormPageRequest, com.wanmi.sbc.wallet.wallet.request.TicketsFormPageRequest.class);
        TicketsFormQueryResponse ticketsFormQueries = ticketsFormModifyService.rechargeList(convert);
        return BaseResponse.success(ticketsFormQueries);    }

    /**
     * 审核通过
     * @param ticketsFormAdoptRequest
     * @return
     */
    @Override
    public BaseResponse<List<TicketsFormQueryVO>> adopt(TicketsFormAdoptRequest ticketsFormAdoptRequest) {
        return ticketsFormModifyService.adopt(ticketsFormAdoptRequest);
    }

    /**
     * 打款
     * @param ticketsFormAdoptRequest
     * @return
     */
    @Override
    public BaseResponse<List<TicketsFormQueryVO>> payment(TicketsFormAdoptRequest ticketsFormAdoptRequest) {
        return ticketsFormModifyService.payment(ticketsFormAdoptRequest);
    }

    @Override
    public BaseResponse<List<TicketsFormQueryVO>> reject(TicketsFormAdoptRequest ticketsFormAdoptRequest) {
        return ticketsFormModifyService.reject(ticketsFormAdoptRequest);
    }

    /**
     * 后台发起充值工单
     * @param request
     * @return
     */
    @Override
    public BaseResponse createRecharge(CreateRechargeRequest request) {
        return ticketsFormModifyService.createRecharge(request);
    }

    @Override
    public BaseResponse rechargeAdopt(TicketsFormAdoptRequest request) {
        return ticketsFormModifyService.rechargeAdopt(request);
    }

    @Override
    public BaseResponse rechargeReject(TicketsFormAdoptRequest request) {
        return ticketsFormModifyService.rechargeReject(request);
    }

    @Override
    public BaseResponse<TicketsFormListResponse> ticketsFormAll(TicketsFormPageRequest ticketsFormPageRequest) {
        com.wanmi.sbc.wallet.wallet.request.TicketsFormPageRequest convert = KsBeanUtil.convert(ticketsFormPageRequest, com.wanmi.sbc.wallet.wallet.request.TicketsFormPageRequest.class);
        List<TicketsFormQueryVO> ticketsFormQueryVOS = ticketsFormModifyService.ticketsFormAll(convert);
        return BaseResponse.success(TicketsFormListResponse.builder().ticketsFormQueryVOList(ticketsFormQueryVOS).build());
    }
}
