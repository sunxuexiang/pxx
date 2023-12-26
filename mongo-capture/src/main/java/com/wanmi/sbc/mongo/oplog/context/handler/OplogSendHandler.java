package com.wanmi.sbc.mongo.oplog.context.handler;

import com.wanmi.sbc.mongo.oplog.config.TunnelConfig;
import com.wanmi.sbc.mongo.oplog.data.OplogData;
import com.wanmi.sbc.mongo.oplog.tunnel.TunnelParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

 /**
   * @Description:   MongoDB 日志数据处理器
   * @Author: ZhangLingKe
   * @CreateDate: 2019/8/14 17:31
   */
@Slf4j
@Component
public class OplogSendHandler {

    /*@Value("${tunnel.type}")*/
    private String tunnel;

    public OplogSendHandler(TunnelConfig config){
        this.tunnel = config.getType();
    }
    public void send(OplogData oplogData) {

        //拆解后的日志数据 具体使用的业务 (在此仅输出)
        log.debug("OPLOG ===> {}",oplogData );

        TunnelParser.parse(tunnel).dispatch(oplogData);
    }

}
