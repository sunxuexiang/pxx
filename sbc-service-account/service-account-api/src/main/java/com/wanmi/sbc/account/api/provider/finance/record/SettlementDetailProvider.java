package com.wanmi.sbc.account.api.provider.finance.record;

import com.wanmi.sbc.account.api.request.finance.record.SettlementDetailAddRequest;
import com.wanmi.sbc.account.api.request.finance.record.SettlementDetailDeleteRequest;
import com.wanmi.sbc.account.api.request.finance.record.SettlementDetailListAddRequest;
import com.wanmi.sbc.common.base.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>对账结算明细操作接口</p>
 * Created by of628-wenzhi on 2018-10-13-下午6:23.
 */
@FeignClient(value = "${application.account.name}", url="${feign.url.account:#{null}}", contextId = "SettlementDetailProvider")
public interface SettlementDetailProvider {

    /**
     * 新增计算明细列表
     *
     * @param settlementDetailListAddRequest 新增结算明细列表数据结构 {@link SettlementDetailListAddRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/account/${application.account.version}/finance/record/settlement/detail/add-list")
    BaseResponse addList(@RequestBody @Valid SettlementDetailListAddRequest settlementDetailListAddRequest);

    /**
     * 新增单条结算明细
     *
     * @param settlementDetailAddRequest 新增结算明细数据结构 {@link SettlementDetailAddRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/account/${application.account.version}/finance/record/settlement/detail/add")
    BaseResponse add(@RequestBody @Valid SettlementDetailAddRequest settlementDetailAddRequest);

    /**
     * 根据条件删除结算明细
     *
     * @param settlementDetailDeleteRequest 删除条件 {@link SettlementDetailDeleteRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/account/${application.account.version}/finance/record/settlement/detail/delete")
    BaseResponse delete(@RequestBody @Valid SettlementDetailDeleteRequest settlementDetailDeleteRequest);

}
