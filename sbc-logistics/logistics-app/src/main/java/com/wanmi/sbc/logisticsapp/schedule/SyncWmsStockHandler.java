package com.wanmi.sbc.logisticsapp.schedule;

import com.wanmi.sbc.logisticsapp.controller.SyncWmsStockController;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * wms同步定时器
 */
@JobHandler("syncWmsStockHandler")
@Component
public class SyncWmsStockHandler extends IJobHandler {

    @Autowired
    private SyncWmsStockController syncWmsStockController;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        syncWmsStockController.findAllWmsStock();
        return SUCCESS;
    }
}


