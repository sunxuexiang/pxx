package com.wanmi.sbc.advertising.mq;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

import com.wanmi.sbc.advertising.bean.constant.AdConstants;


public interface AdSink {

    @Input(AdConstants.AD_STATISTIC_ADD)
    SubscribableChannel receiveStatisticAddMq();
    
    @Input(AdConstants.AD_PAY_CANCEL_CONSUMER)
    SubscribableChannel receivePayCancelMq();
    
    @Input(AdConstants.AD_UPDATE_CONSUMER)
    SubscribableChannel receiveAdUpdateMq();

}
