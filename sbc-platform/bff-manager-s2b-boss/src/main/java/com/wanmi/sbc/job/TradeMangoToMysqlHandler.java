package com.wanmi.sbc.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.order.api.provider.trade.TradeMangoToMysqlProvider;
import com.wanmi.sbc.order.api.request.trade.MangoTradeRequest;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@JobHandler(value="tradeMangoToMysqlHandler")
@Component
@Slf4j
public class TradeMangoToMysqlHandler extends IJobHandler {


    @Autowired
    private TradeMangoToMysqlProvider tradeMangoToMysqlProvider;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        MangoTradeRequest request = new MangoTradeRequest();
        if(StringUtils.isNotEmpty(param)){
            JSONObject jsonObject = JSONObject.parseObject(param);
            request.setBeginTime(jsonObject.get("beginTime").toString());
            request.setEndTime(jsonObject.get("endTime").toString());
        }

        tradeMangoToMysqlProvider.tradeSaveToMysql(request);

        return SUCCESS;
    }
}
