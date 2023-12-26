package com.wanmi.sbc.live.chat.service;

import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.live.redis.RedisService;
import com.wanmi.sbc.live.util.TencentImUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
public class ChatAppService {

    @Autowired
    private RedisService redisService;

    public String getUserSig(String userId){
        String userSig = TencentImUtil.getTxCloudUserSig(userId);
//        String userSig = redisService.getString(CacheKeyConstant.REDIS_IM_USER_SIG + userId);
//        if(Objects.isNull(userSig)){
//            userSig=TencentImUtil.getTxCloudUserSig(userId);
//            //为了确保缓存中的的签名有效期在腾讯中的失效时间更短
//            redisService.setString(CacheKeyConstant.REDIS_IM_USER_SIG + userId,userSig,TencentImUtil.SIGN_CACHE_TIME);
//        }
        return userSig;
    }
}
