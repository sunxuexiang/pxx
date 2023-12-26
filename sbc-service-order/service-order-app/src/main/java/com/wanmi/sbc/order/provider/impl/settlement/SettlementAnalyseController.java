package com.wanmi.sbc.order.provider.impl.settlement;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.order.api.provider.settlement.SettlementAnalyseProvider;
import com.wanmi.sbc.order.api.request.settlement.SettlementAnalyseRequest;
import com.wanmi.sbc.order.settlement.SettlementAnalyseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2018-12-07 13:51
 */
@Validated
@RestController
public class SettlementAnalyseController implements SettlementAnalyseProvider {

    @Autowired
    private SettlementAnalyseService settlementAnalyseService;

    /**
     * 分析结算单
     *
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    @LcnTransaction
    public BaseResponse analyse(@RequestBody @Valid SettlementAnalyseRequest request) {
        settlementAnalyseService.analyseSettlement(new Date(), request);
        return BaseResponse.SUCCESSFUL();
    }
}
