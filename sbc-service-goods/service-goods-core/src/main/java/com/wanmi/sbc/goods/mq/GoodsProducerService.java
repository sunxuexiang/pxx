package com.wanmi.sbc.goods.mq;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.MessageMQRequest;
import com.wanmi.sbc.goods.api.provider.contract.MessageGoodConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.binding.BinderAwareChannelResolver;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;

/**
 * <p>描述<p>
 *
 * @author zhaowei
 * @date 2021/6/2
 */
@Service
@EnableBinding
public class GoodsProducerService {



    @Autowired
    private BinderAwareChannelResolver resolver;

    /**
     * 发送push、站内信、短信
     * @param request
     */
    public void sendMessage(MessageMQRequest request){
        resolver.resolveDestination(MessageGoodConstants.Q_SMS_SERVICE_MESSAGE_SEND).send(new GenericMessage<>(JSONObject.toJSONString(request)));
    }

}
