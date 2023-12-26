//package com.wanmi.sbc.job;
//
//import com.wanmi.sbc.order.api.provider.trade.ErpWmsShopControllerProvider;
//import com.wanmi.sbc.order.api.provider.trade.newPileTrade.NewPileTradeProvider;
//import com.xxl.job.core.biz.model.ReturnT;
//import com.xxl.job.core.handler.IJobHandler;
//import com.xxl.job.core.handler.annotation.JobHandler;
//import lombok.extern.slf4j.Slf4j;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
///**
// * @ClassName: CheckInventory
// * @Description: TODO
// * @Date: 2020/5/30 10:25
// * @Version: 1.0
// */
//@JobHandler(value = "erpWmsShopHandler")
//@Component
//@Slf4j
//public class ErpWmsShopHandler extends IJobHandler {
//
//
//    private Logger logger = LoggerFactory.getLogger(ErpWmsShopHandler.class);
//
//    @Autowired
//    private ErpWmsShopControllerProvider erpWmsShopControllerProvider;
//
//    @Override
//    public ReturnT<String> execute(String param) throws Exception {
//        log.info("开始调用数据恢复.....{}",System.currentTimeMillis());
//        erpWmsShopControllerProvider.payStateBack();
//        log.info("结束调用数据恢复.....{}",System.currentTimeMillis());
//        return SUCCESS;
//    }
//}
