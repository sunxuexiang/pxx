package com.wanmi.sbc.returnorder.provider.impl.settlement;

import com.wanmi.sbc.returnorder.api.provider.settlement.SettlementAnalyseQueryProvider;
import com.wanmi.sbc.returnorder.settlement.SettlementAnalyseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2018-12-07 13:51
 */
@Validated
@RestController
public class SettlementAnalyseQueryController implements SettlementAnalyseQueryProvider {

    @Autowired
    private SettlementAnalyseService settlementAnalyseService;

}
