package com.wanmi.sbc.pay.mq;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.pay.api.constant.CcbPayConstants;
import com.wanmi.sbc.pay.api.constant.CcbPayJmsConstants;
import com.wanmi.sbc.pay.api.provider.CcbPayProvider;
import com.wanmi.sbc.pay.api.response.CcbPayRecordResponse;
import com.wanmi.sbc.pay.api.response.CcbPayStatusQueryResponse;
import com.wanmi.sbc.pay.service.CcbPayCallbackService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/12/20 11:22
 */
@Service
@Slf4j
@EnableBinding(CcbPaySink.class)
public class CcbPayConsumerService {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private CcbPayProvider ccbPayProvider;

    @Autowired
    private CcbPayCallbackService ccbPayCallbackService;

    /**
     * 建行支付单支付状态查询
     * @param pyTrnNo
     */
    @StreamListener(CcbPayJmsConstants.DELAY_CCB_PAY_CONFIRM_CONSUMER)
    public void ccbPayConfirmConsumer(String pyTrnNo) {
        RAtomicLong atomicLong = redissonClient.getAtomicLong(CcbPayConstants.CCB_PAY_STATUS_QUERY_COUNT_KEY + pyTrnNo);
        long times = atomicLong.incrementAndGet();
        atomicLong.expire(10, TimeUnit.MINUTES);

        log.info("建行支付状态查询,支付流水号:{},消息消费第{}次", pyTrnNo, times);
        if (times > 5L) {
            log.info("建行支付状态查询,支付流水号:{},查询超过最大次数:{},不再处理。", pyTrnNo, times);
            return;
        }

        RLock rLock = redissonClient.getFairLock(CcbPayConstants.CCB_PAY_STATUS_LOCK_KEY + pyTrnNo);
        rLock.lock();
        try {
            CcbPayRecordResponse ccbPayRecordResponse = ccbPayProvider.queryCcbPayRecordByPyTrnNo(pyTrnNo).getContext();
            if (Objects.isNull(ccbPayRecordResponse)) {
                log.info("建行支付状态查询,支付流水号:{},支付记录不存在,不再处理", pyTrnNo);
                return;
            }
            if (Objects.equals(1, ccbPayRecordResponse.getStatus())) {
                log.info("建行支付状态查询,支付流水号:{},订单状态已支付,不再处理", pyTrnNo);
                return;
            }
            CcbPayStatusQueryResponse payStatus = ccbPayProvider.queryCcbPayStatusByPyTrnNo(pyTrnNo).getContext();
            log.info("建行支付状态查询,支付流水号:{},建行返回结果:{}", pyTrnNo, JSON.toJSONString(payStatus));
            if (Objects.isNull(payStatus) || !Objects.equals("2", payStatus.getOrdr_Stcd())) {
                // 发送延迟消息
                ccbPayProvider.delayCcbConfirm(pyTrnNo, 60L * 1000L);
                return;
            }
            // 支付成功处理
            ccbPayCallbackService.ccbPayStatusSuccess(JSON.toJSONString(payStatus), pyTrnNo);
        } catch (Exception e){
            log.error("建行支付状态查询失败", e);
        }finally {
            rLock.unlock();
        }

    }
}
