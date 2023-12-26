package com.wanmi.sbc.customer.util;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.util.HttpCommonResult;
import com.wanmi.sbc.common.util.HttpCommonUtil;
import com.wanmi.sbc.customer.redis.RedisService;
import com.wanmi.sbc.customer.service.model.CustomerKingdeeRes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 描述
 *
 * @author yitang
 * @version 1.0
 */
@Slf4j
@Service
public class CustomerKingdeeLoginUtils {

    @Autowired
    private RedisService redisService;

    private static final String kingdee_login_key = "kingdee_login_key";

//    private static final Long expiration_time = 60 * 60 * 24L;
    private static final Long expiration_time = 60 * 60 * 24 * 10L;

    public String userLoginKingdee(Map<String,Object> requestLogMap, String loginUrl) {
        //获取redis
        String loginKey = redisService.getString(kingdee_login_key);
        if (StringUtils.isEmpty(loginKey)) {
            HttpCommonResult result = HttpCommonUtil.post(loginUrl, requestLogMap);
            if (result != null && !result.getResultCode().equals("200")) {
                log.info("KingdeeLoginUtils.userLoginKingdee login error:{}", result.getResultCode());
                return " ";
            }

            log.info("KingdeeLoginUtils.userLoginKingdee Log result Code:{}", result.getResultCode());
            CustomerKingdeeRes kingdeeRes = JSONObject.parseObject(result.getResultData(), CustomerKingdeeRes.class);
            if (StringUtils.isNotEmpty(kingdeeRes.getData())) {
                redisService.setString(kingdee_login_key, kingdeeRes.getData(), expiration_time);
                return kingdeeRes.getData();
            }
            return " ";
        }
        return loginKey;
    }
}
