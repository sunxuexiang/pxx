package com.wanmi.sbc.customer.service;

import com.wanmi.sbc.common.util.tencent.TencentImCustomerUtil;
import com.wanmi.sbc.common.util.tencent.TencentImMessageType;
import com.wanmi.sbc.customer.api.request.customer.PushNotifyRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

@Slf4j
@Service
public class PushNotifyService {


    @Value("${tencent.im.appId}")
    private long appId;

    @Value("${tencent.im.appKey}")
    private String appKey;

    @Value("${tencent.im.push.pageSize}")

    private Integer pageSize = 100;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private ThreadPoolExecutor threadPoolExecutor;

    @PostConstruct
    private void init () {
        BlockingDeque<Runnable> blockingDeque = new LinkedBlockingDeque<>(20);
        threadPoolExecutor = new ThreadPoolExecutor(4, 16, 60, TimeUnit.SECONDS, blockingDeque, Executors.defaultThreadFactory(), new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    public void pushNotify(PushNotifyRequest request) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String redisKey = "DATE_BUYER_VISIT_CACHE_" + simpleDateFormat.format(new Date());
            Set<String> phoneNos = redisTemplate.opsForSet().members(redisKey);
            if (ObjectUtils.isEmpty(phoneNos)) {
                log.info("给APP推送消息 收集号码为空");
            }
            log.info("给APP推送消息 读取缓存数据 {} {}", phoneNos.size(), phoneNos);
            List<String> phoneList = new ArrayList<>(phoneNos);
            int times = phoneList.size() % pageSize == 0 ? phoneList.size() / pageSize : phoneList.size() / pageSize + 1;
            for (int i = 0; i < times; i++) {
                int start = i * pageSize;
                int end = (i + 1) * pageSize;
                if (end > phoneList.size()) {
                    end = phoneList.size();
                }
                List<String> subList = phoneList.subList(start, end);
                threadPoolExecutor.execute(() -> {
                    StopWatch stopWatch = new StopWatch();
                    stopWatch.start();
                    try {
                        log.info("给APP推送消息 读取缓存数据 {}", subList);
                        if (ObjectUtils.isEmpty(subList)) {
                            stopWatch.stop();
                            return;
                        }
                        log.info("给APP推送消息 {} 数量 {} - {}", request.getTencentImMessageType(), subList.size(), subList);
                        subList.forEach(phone -> {
                            TencentImCustomerUtil.sendCustomMsg(phone, request.getTencentImMessageType().getMsgDesc(), request.getTencentImMessageType(), appId, appKey);
                        });
                        stopWatch.stop();
                        log.info("给APP推送消息耗时 {} {} {}", request.getTencentImMessageType(), subList.size(), stopWatch.getTotalTimeSeconds());
                    } catch (Exception e) {
                        stopWatch.stop();
                        log.error("给APP推送消息异常", e);
                    }
                });
            }
        }
        catch (Exception e) {
            log.error("给APP推送消息异常", e);
        }
    }
}
