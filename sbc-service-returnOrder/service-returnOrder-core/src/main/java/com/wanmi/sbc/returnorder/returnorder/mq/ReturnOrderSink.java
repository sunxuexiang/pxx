package com.wanmi.sbc.returnorder.returnorder.mq;

import com.wanmi.sbc.returnorder.api.constant.JmsDestinationConstants;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/9/25 17:53
 */
public interface ReturnOrderSink {

    /**
     * 商家后台退款自动完成
     * @return
     */
    @Input(JmsDestinationConstants.Q_SUPPLIER_REFUND_ORDER_AUTO_AUDIT_CONSUMER)
    SubscribableChannel Q_SUPPLIER_REFUND_ORDER_AUTO_AUDIT_CONSUMER();
}
