package com.wanmi.sbc.message.mq;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.wanmi.sbc.message.bean.enums.SendStatus;
import com.wanmi.sbc.message.smssend.model.root.SmsSend;
import com.wanmi.sbc.message.smssend.repository.SmsSendRepository;
import com.wanmi.sbc.message.smssend.service.SmsSendTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


/**
 * 发送任务消费
 * Author: zgl
 * Time: 2019/12/11.10:54
 */
@Slf4j
@Component
@EnableBinding(SmsSendDataRequestSink.class)
public class SmsSendDataRequestConsumer {
    @Autowired
    private SmsSendTaskService smsSendTaskService;
    @Autowired
    private SmsSendRepository smsSendRepository;

    /**
     *
     */
    @Transactional
    @StreamListener(SmsSendDataRequestSink.INPUT)
    public void receive(String json,
                        @Header(AmqpHeaders.CHANNEL) Channel channel,
                        @Header(AmqpHeaders.DELIVERY_TAG) Long deliveryTag) throws Exception  {
        System.out.println(json);
        SmsSend smsSend = JSONObject.parseObject(json, SmsSend.class);
        try {
            this.smsSendTaskService.send(smsSend);
        }catch (Exception e){
            log.error("发送任务失败：{}",e);
            smsSend.setStatus(SendStatus.FAILED);
            smsSend.setMessage("执行发送任务异常！");
            smsSendRepository.save(smsSend);
        }finally {
            channel.basicAck(deliveryTag,true);
        }

    }


}
