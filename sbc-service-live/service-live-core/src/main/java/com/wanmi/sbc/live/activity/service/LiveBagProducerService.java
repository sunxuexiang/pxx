package com.wanmi.sbc.live.activity.service;

import com.wanmi.sbc.live.api.constant.JmsBagDestinationConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.binding.BinderAwareChannelResolver;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@EnableBinding
public class LiveBagProducerService {

    @Autowired
    private BinderAwareChannelResolver resolver;

     /* 福袋推送，发送MQ延时队列消息
     * @param tradeId
     */
    public void sendBag(String bagId,long millis) {
        resolver.resolveDestination(JmsBagDestinationConstants.Q_LIVE_SERVICE_BAG_SEND_PRODUCER).send(MessageBuilder.withPayload(bagId).setHeader("x-delay", millis ).build());
    }

    /* 断流，发送MQ延时队列消息
     * @param tradeId
     */
    public void sendStream(String liveId,long millis) {
        resolver.resolveDestination(JmsBagDestinationConstants.Q_LIVE_SERVICE_STREAM_SEND_PRODUCER).send(MessageBuilder.withPayload(liveId).setHeader("x-delay", millis ).build());
    }
}
