package com.wanmi.sbc.order.util;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.util.HttpCommonResult;
import com.wanmi.sbc.common.util.HttpCommonUtil;
import com.wanmi.sbc.order.redis.RedisService;
import com.wanmi.sbc.order.trade.model.entity.value.kingdeeRes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 金蝶登录
 *
 * @author yitang
 * @version 1.0
 */
@Slf4j
@Service
public class KingdeeLoginUtils {

    @Autowired
    private RedisService redisService;

    private static final String kingdee_login_key = "kingdee_login_key";

//    private static final Long expiration_time = 60 * 60 * 24L;

    private static final Long expiration_time = 60 * 60 * 24 * 10L;

    public String userLoginKingdee(Map<String,Object> requestLogMap,String loginUrl){
        //获取redis
        String loginKey = redisService.getString(kingdee_login_key);
        if (StringUtils.isEmpty(loginKey)) {
            HttpCommonResult result = HttpCommonUtil.post(loginUrl, requestLogMap);
            if (result != null && !result.getResultCode().equals("200")) {
                log.info("KingdeeLoginUtils.userLoginKingdee login error:{}", result.getResultCode());
                return " ";
            }

            log.info("KingdeeLoginUtils.userLoginKingdee Log result Code:{}", result.getResultCode());
            kingdeeRes kingdeeRes = JSONObject.parseObject(result.getResultData(), kingdeeRes.class);
            if (StringUtils.isNotEmpty(kingdeeRes.getData())){
                redisService.setString(kingdee_login_key,kingdeeRes.getData(),expiration_time);
                return kingdeeRes.getData();
            }
            return " ";
        }
        return loginKey;
    }
}
