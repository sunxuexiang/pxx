package com.wanmi.sbc.mq;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.order.api.constant.JmsDestinationConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.binding.BinderAwareChannelResolver;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author chenchang
 * @since 2023/06/01 10:06
 */
@Service
@EnableBinding
public class ProducerService {

    @Autowired
    private BinderAwareChannelResolver resolver;

    /**
     * 创建取消支付延时任务
     *
     * @param tids
     */
    public Boolean createCancelPayDelayTask(List<String> tids) {
        //5分钟后，取消支付
        long millis = 5 * 60 * 1000;
        return resolver.resolveDestination(JmsDestinationConstants.Q_ORDER_CONFIRM_PAY_PRODUCER).send
                (MessageBuilder.withPayload(JSON.toJSONString(tids)).setHeader("x-delay", millis).build());
    }
}
