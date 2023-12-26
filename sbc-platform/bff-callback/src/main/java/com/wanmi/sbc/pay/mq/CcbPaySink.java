package com.wanmi.sbc.pay.mq;

import com.wanmi.sbc.pay.api.constant.CcbPayJmsConstants;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/12/20 9:05
 */
public interface CcbPaySink {

    @Input(CcbPayJmsConstants.DELAY_CCB_PAY_CONFIRM_CONSUMER)
    SubscribableChannel ccbPayConfirmConsumer();
}
