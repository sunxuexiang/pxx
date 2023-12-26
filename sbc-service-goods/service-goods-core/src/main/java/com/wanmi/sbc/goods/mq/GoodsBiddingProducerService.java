package com.wanmi.sbc.goods.mq;

import com.wanmi.sbc.goods.api.constant.GoodsJmsDestinationConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.binding.BinderAwareChannelResolver;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * @author baijianzhong
 * @ClassName GoodsProducerService
 * @Date 2020-08-13 17:14
 * @Description TODO
 **/
@Service
@EnableBinding
public class GoodsBiddingProducerService {

    @Autowired
    private BinderAwareChannelResolver resolver;


    /**
     * 发送开始活动的消息
     * @param biddingId
     * @param millis
     * @return
     */
    public Boolean sendStartBiddingActivity(String biddingId, Long millis){
        Boolean send = resolver.resolveDestination(GoodsJmsDestinationConstants.Q_GOODS_SERVICE_START_BIDDING_ACTIVITY_PRODUCER).send
                (MessageBuilder.withPayload(biddingId).setHeader("x-delay", millis ).build());
        return send;
    }

}
