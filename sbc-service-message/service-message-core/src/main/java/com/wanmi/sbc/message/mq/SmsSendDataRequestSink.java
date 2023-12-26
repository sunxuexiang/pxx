package com.wanmi.sbc.message.mq;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * @Author: zgl
 * @Date:
 * @Description: 短信发送消息队列 Sink
 */
@EnableBinding
public interface SmsSendDataRequestSink {

    String OUTPUT = "sms-send-output";
    String INPUT = "sms-send-input";


    @Input(INPUT)
    SubscribableChannel receive();

    @Output(OUTPUT)
    MessageChannel output();

}
