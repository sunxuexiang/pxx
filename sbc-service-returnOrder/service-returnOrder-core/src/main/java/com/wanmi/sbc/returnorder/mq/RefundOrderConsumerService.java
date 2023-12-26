package com.wanmi.sbc.returnorder.mq;

import com.wanmi.sbc.order.api.constant.JmsDestinationConstants;
import com.wanmi.sbc.returnorder.returnextra.service.RefundExtraRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/12/12 17:20
 */
@Slf4j
@Service
@EnableBinding(RefundOrderSink.class)
public class RefundOrderConsumerService {

    @Autowired
    private RefundExtraRecordService refundExtraRecordService;

    /**
     * 处理运费加收退款
     * @param message
     */
    @StreamListener(JmsDestinationConstants.Q_ORDER_SERVICE_PUSH_REFUND_EXTRA_CONSUMER)
    public void refundExtraConsumer(String message){
        log.info("运费加收退款延迟消费消息 : {}", message);
        try {
            String[] strs = message.split("#");
            String tid = strs[0];
            String rid = strs[1];
            refundExtraRecordService.refundExtra(tid, rid);
            log.info("运费加收退款延迟消费消息,已完成");

        }catch (Exception e){
            log.error("运费加收退款延迟消费消息 错误：", e);
        }
    }
}
