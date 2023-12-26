package com.wanmi.sbc.job;

import com.wanmi.sbc.wms.api.provider.pushWmsLog.PushWmsLogProvider;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 补偿推wms失败订单
 *
 * @author yitang
 * @version 1.0
 */
@Component
@Slf4j
@JobHandler(value="compensatePushWmsErrorJobHandler")
public class CompensatePushWmsErrorJobHandler extends IJobHandler {
    @Autowired
    private PushWmsLogProvider pushWmsLogProvider;


    public ReturnT<String> execute(String s) throws Exception{
        log.info("CompensatePushWmsErrorJobHandler start");
        pushWmsLogProvider.compensateFailedSalesOrders();
        return SUCCESS;
    }
}
