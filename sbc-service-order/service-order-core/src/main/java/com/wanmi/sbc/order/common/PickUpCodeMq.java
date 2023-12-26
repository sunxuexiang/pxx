package com.wanmi.sbc.order.common;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.MessageMQRequest;
import com.wanmi.sbc.customer.api.constant.JmsDestinationConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.binding.BinderAwareChannelResolver;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;

/**
 * @author liguang
 * @description do something here
 * @date 2018年11月21日 18:04
 */
@Service
@EnableBinding
public class PickUpCodeMq {

    @Autowired
    private BinderAwareChannelResolver resolver;

    /**
     * 发送自提码和提心信息
     *
     */
    public void sendPickUpCode(MessageMQRequest messageMQRequest) {
        resolver.resolveDestination(JmsDestinationConstants.Q_SMS_SERVICE_MESSAGE_SEND).send(new GenericMessage<>(JSONObject.toJSONString(messageMQRequest)));
    }
}
