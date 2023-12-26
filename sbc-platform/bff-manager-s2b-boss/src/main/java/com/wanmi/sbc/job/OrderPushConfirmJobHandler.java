package com.wanmi.sbc.job;

import com.wanmi.sbc.order.TradeController;
import com.wanmi.sbc.order.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.order.api.response.trade.TradeListAllResponse;
import com.wanmi.sbc.order.bean.vo.TradeVO;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务Handler（Bean模式）
 * 订单定时任务
 */
@JobHandler(value="OrderPushConfirmJobHandler")
@Component
@Slf4j
public class OrderPushConfirmJobHandler extends IJobHandler {

    @Autowired
    private TradeQueryProvider tradeQueryProvider;

    @Autowired
    private TradeController tradeController;


    /**
     * 20min执行一次，推送确认订单失败的订单那
     */
    @Override
    @Transactional
    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log("========================== 确认订单推送定时任务执行 " + LocalDateTime.now());
        log.info("订单定时任务执行 " + LocalDateTime.now());
        TradeListAllResponse listAllResponse = tradeQueryProvider.queryAllConfirmFailed().getContext();
        List<TradeVO> tradeVOList = listAllResponse.getTradeVOList();
        tradeVOList.stream().map(t->tradeController.pushWMSOrder(t.getId()));
        return SUCCESS;
    }
}
