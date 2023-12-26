package com.wanmi.sbc.marketing.mq;


import com.wanmi.sbc.customer.api.constant.JmsDestinationConstants;
import com.wanmi.sbc.marketing.api.provider.constants.MarketingJmsDestinationConstants;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface GoodsSink {
    /**
     * 到期自动同步到goodsInfo
     * @return
     */
    @Input(MarketingJmsDestinationConstants.Q_SYNCHRONIZATION_GOODS_INFO_PURCHASE_NUM_CONSUMER)
    SubscribableChannel synchronizatioGoodsInfo();
}
