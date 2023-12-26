package com.wanmi.sbc.account.provider.impl.wallet;

import com.wanmi.sbc.account.api.provider.wallet.CustomerWalletProvider;
import com.wanmi.sbc.account.api.provider.wallet.CustomerWalletQueryProvider;
import com.wanmi.sbc.account.api.provider.wallet.TicketsFormProvider;
import com.wanmi.sbc.account.api.provider.wallet.WalletRecordProvider;
import com.wanmi.sbc.account.api.request.wallet.*;
import com.wanmi.sbc.account.api.response.wallet.TicketsFormListResponse;
import com.wanmi.sbc.account.api.response.wallet.TicketsFormQueryResponse;
import com.wanmi.sbc.account.bean.vo.CustomerWalletVO;
import com.wanmi.sbc.account.bean.vo.TicketsFormQueryVO;
import com.wanmi.sbc.account.bean.vo.WalletRecordVO;
import com.wanmi.sbc.account.wallet.model.root.TicketsForm;
import com.wanmi.sbc.account.wallet.model.root.TicketsFormQuery;
import com.wanmi.sbc.account.wallet.repository.TicketsFormRepository;
import com.wanmi.sbc.account.wallet.service.TicketsFormModifyService;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
        com.wanmi.sbc.account.wallet.request.TicketsFormPageRequest convert = KsBeanUtil.convert(ticketsFormPageRequest, com.wanmi.sbc.account.wallet.request.TicketsFormPageRequest.class);
        TicketsFormQueryResponse ticketsFormQueries = ticketsFormModifyService.ticketsFormAllList(convert);
        return BaseResponse.success(ticketsFormQueries);
    }

    /**
     * 提现列表
     * @return
     */
    @Override
    public BaseResponse<TicketsFormQueryResponse> withdrawalList(TicketsFormPageRequest ticketsFormPageRequest) {
        com.wanmi.sbc.account.wallet.request.TicketsFormPageRequest convert = KsBeanUtil.convert(ticketsFormPageRequest, com.wanmi.sbc.account.wallet.request.TicketsFormPageRequest.class);
        TicketsFormQueryResponse ticketsFormQueries = ticketsFormModifyService.withdrawalList(convert);
        return BaseResponse.success(ticketsFormQueries);
    }

    @Override
    public BaseResponse<TicketsFormQueryResponse> rechargeList(TicketsFormPageRequest ticketsFormPageRequest) {
        com.wanmi.sbc.account.wallet.request.TicketsFormPageRequest convert = KsBeanUtil.convert(ticketsFormPageRequest, com.wanmi.sbc.account.wallet.request.TicketsFormPageRequest.class);
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
     * @description  运营后台-财务-提现列表-已完成的单据支持修改支付、打款凭证
     * @author  shiy
     * @date    2023/3/21 10:13
     * @params  [com.wanmi.sbc.account.api.request.wallet.TicketsFormAdoptRequest]
     * @return  com.wanmi.sbc.common.base.BaseResponse<java.util.List<com.wanmi.sbc.account.bean.vo.TicketsFormQueryVO>>
    */
    @Override
    public BaseResponse<List<TicketsFormQueryVO>> updateImgAfterReject(TicketsFormAdoptRequest ticketsFormAdoptRequest) {
        return ticketsFormModifyService.updateImgAfterReject(ticketsFormAdoptRequest);
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
        com.wanmi.sbc.account.wallet.request.TicketsFormPageRequest convert = KsBeanUtil.convert(ticketsFormPageRequest, com.wanmi.sbc.account.wallet.request.TicketsFormPageRequest.class);
        List<TicketsFormQueryVO> ticketsFormQueryVOS = ticketsFormModifyService.ticketsFormAll(convert);
        return BaseResponse.success(TicketsFormListResponse.builder().ticketsFormQueryVOList(ticketsFormQueryVOS).build());
    }
}
