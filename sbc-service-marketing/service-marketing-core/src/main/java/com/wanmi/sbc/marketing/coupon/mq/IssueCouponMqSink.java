package com.wanmi.sbc.marketing.coupon.mq;

import com.wanmi.sbc.common.constant.MQConstant;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * @Author: songhanlin
 * @Date: Created In 15:05 2019-04-03
 * @Description: mq接收发放优惠券的信息 Sink
 */
public interface IssueCouponMqSink {

    @Input(MQConstant.ISSUE_COUPONS)
    SubscribableChannel receiveOperationLogMq();

}
