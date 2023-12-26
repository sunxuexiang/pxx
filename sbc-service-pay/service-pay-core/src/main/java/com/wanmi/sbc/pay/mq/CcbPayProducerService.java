package com.wanmi.sbc.pay.mq;

import com.wanmi.sbc.pay.api.constant.CcbPayJmsConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.binding.BinderAwareChannelResolver;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/12/20 9:21
 */
@Service
@Slf4j
@EnableBinding
public class CcbPayProducerService {

    @Autowired
    private BinderAwareChannelResolver resolver;

    public void delayCcbConfirm(String sendStr, long millis) {
        log.info("惠市宝延迟确认支付状态，发送消息：流水号：{},延迟时间:{}", sendStr, millis);
        resolver.resolveDestination(CcbPayJmsConstants.DELAY_CCB_PAY_CONFIRM_PRODUCER).send(MessageBuilder.withPayload(sendStr).setHeader("x-delay", millis).build());
    }
}
