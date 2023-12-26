package com.wanmi.sbc.marketing.mq;

import com.wanmi.sbc.customer.api.constant.JmsDestinationConstants;
import com.wanmi.sbc.marketing.api.provider.constants.MarketingJmsDestinationConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.binding.BinderAwareChannelResolver;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@EnableBinding
public class GoodsSynchronizationProducerService {
    @Autowired
    private BinderAwareChannelResolver resolver;
    /**
     * 到期自动删除goodsInfo信息
     * @param marketingId
     * @param millis
     * @return
     */
    public Boolean synchronizationGoodsInfo(Long marketingId,Long millis){
        Boolean send = resolver.resolveDestination(MarketingJmsDestinationConstants.Q_SYNCHRONIZATION_GOODS_INFO_PURCHASE_NUM_PRODUCER).send
                (MessageBuilder.withPayload(marketingId).setHeader("x-delay", millis ).build());
        return send;
    }
}
