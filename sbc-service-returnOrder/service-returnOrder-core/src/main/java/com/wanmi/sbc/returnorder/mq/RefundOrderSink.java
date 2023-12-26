package com.wanmi.sbc.returnorder.mq;

import com.wanmi.sbc.order.api.constant.JmsDestinationConstants;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/12/12 17:10
 */
public interface RefundOrderSink {
    @Input(JmsDestinationConstants.Q_ORDER_SERVICE_PUSH_REFUND_EXTRA_CONSUMER)
    SubscribableChannel refundExtraConsumer();
}
