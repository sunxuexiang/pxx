package com.wanmi.sbc.rediscache;

import com.wanmi.sbc.common.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-11-29 16:10
 **/
@Component
public class HomePageRedisCache {

    private static final String DATE_BUYER_VISIT_CACHE = "DATE_BUYER_VISIT_CACHE_";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public void cacheHomePageVisit(String account) {
        if (StringUtils.isBlank(account)) return;
        stringRedisTemplate.opsForSet().add(DATE_BUYER_VISIT_CACHE + DateUtil.getDate(LocalDateTime.now()), account);
    }
}
