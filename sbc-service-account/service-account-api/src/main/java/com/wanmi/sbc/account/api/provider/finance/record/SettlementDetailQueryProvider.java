package com.wanmi.sbc.account.api.provider.finance.record;

import com.wanmi.sbc.account.api.request.finance.record.*;
import com.wanmi.sbc.account.api.response.finance.record.SettlementDetailByParamResponse;
import com.wanmi.sbc.account.api.response.finance.record.SettlementDetailListBySettleUuidResponse;
import com.wanmi.sbc.common.base.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>对账结算明细查询接口</p>
 * Created by of628-wenzhi on 2018-10-13-下午6:23.
 */
@FeignClient(value = "${application.account.name}", url="${feign.url.account:#{null}}", contextId = "SettlementDetailQueryProvider")
public interface SettlementDetailQueryProvider {

    /**
     * 根据条件查询单条结算明细
     *
     * @param settlementDetailByParamRequest 查询条件 {@link SettlementDetailByParamRequest}
     * @return 结算明细返回结构 {@link SettlementDetailByParamResponse}
     */
    @PostMapping("/account/${application.account.version}/finance/record/settlement/detail/get-by-param")
    BaseResponse<SettlementDetailByParamResponse> getByParam(@RequestBody @Valid SettlementDetailByParamRequest
                                                                     settlementDetailByParamRequest);

    /**
     * 根据结算单id查询结算明细列表
     *
     * @param settlementDetailListBySettleUuidRequest 包含结算单id的查询条件 {@link SettlementDetailListBySettleUuidRequest}
     * @return 返回的计算明细列表 {@link SettlementDetailListBySettleUuidResponse}
     */
    @PostMapping("/account/${application.account.version}/finance/record/settlement/detail/list-by-settle-uuid")
    BaseResponse<SettlementDetailListBySettleUuidResponse> listBySettleUuid(@RequestBody @Valid
                                                                                    SettlementDetailListBySettleUuidRequest
                                                                                    settlementDetailListBySettleUuidRequest);
}
