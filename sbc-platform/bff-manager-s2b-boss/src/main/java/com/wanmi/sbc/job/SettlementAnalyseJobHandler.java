package com.wanmi.sbc.job;

import com.wanmi.sbc.common.enums.StoreType;
import com.wanmi.sbc.order.api.provider.settlement.SettlementAnalyseProvider;
import com.wanmi.sbc.order.api.request.settlement.SettlementAnalyseRequest;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 定时任务Handler（Bean模式）
 * 商铺结算任务
 *
 * @author bail 2019-3-24
 */
@JobHandler(value="settlementAnalyseJobHandler")
@Component
@Slf4j
public class SettlementAnalyseJobHandler extends IJobHandler {
    @Autowired
    private SettlementAnalyseProvider settlementAnalyseProvider;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        settlementAnalyseProvider.analyse(new SettlementAnalyseRequest(param, StoreType.SUPPLIER));
        return SUCCESS;
    }

}
