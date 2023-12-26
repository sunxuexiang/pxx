package com.wanmi.sbc.returnorder.returnorder.mq;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.MessageMQRequest;
import com.wanmi.sbc.returnorder.api.constant.JmsDestinationConstants;
import com.wanmi.sbc.returnorder.api.constant.MessageConstants;
import com.wanmi.sbc.returnorder.api.request.distribution.ReturnOrderSendMQRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.binding.BinderAwareChannelResolver;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;

/**
 * @Description: 退单状态变更生产者
 * @Autho qiaokang
 * @Date：2019-03-06 16:37:14
 */
@Service
@EnableBinding
public class NewPileReturnOrderProducerService {

    @Autowired
    private BinderAwareChannelResolver resolver;

    /**
     * 退单状态变更，发送MQ消息：用于分销任务临时表退单数量加减处理
     *
     * @param request
     */
    public void returnOrderFlow(ReturnOrderSendMQRequest request) {
        resolver.resolveDestination(JmsDestinationConstants.Q_ORDER_SERVICE_NEW_PILE_RETURN_ORDER_FLOW).send(new GenericMessage<>(JSONObject.toJSONString(request)));

    }

    /**
     * 囤货退单状态变更，发送MQ消息：用于分销任务临时表退单数量加减处理
     *
     * @param request
     */
    public void returnPileOrderFlow(ReturnOrderSendMQRequest request) {
        resolver.resolveDestination(JmsDestinationConstants.Q_ORDER_SERVICE_RETURN_NEW_PILE_ORDER_FLOW).send(new GenericMessage<>(JSONObject.toJSONString(request)));

    }

    /**
     * 发送push、站内信、短信
     *
     * @param request
     */
    public void sendMessage(MessageMQRequest request) {
        resolver.resolveDestination(MessageConstants.Q_SMS_SERVICE_MESSAGE_SEND).send(new GenericMessage<>(JSONObject.toJSONString(request)));
    }
}
