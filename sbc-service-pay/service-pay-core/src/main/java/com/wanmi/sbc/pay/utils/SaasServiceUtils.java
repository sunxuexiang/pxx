package com.wanmi.sbc.pay.utils;

import com.wanmi.sbc.common.constant.VASStatus;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.pay.service.RedisService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: songhanlin
 * @Date: Created In 17:34 2020/1/15
 * @Description: Saas化配置工具类
 */
@Service
public class SaasServiceUtils {

    @Autowired
    private RedisService redisService;

    /**
     * 判断商城是否启用Saas化配置
     * @return
     */
    public boolean getSaasStatus() {
        return StringUtils.equals(VASStatus.ENABLE.toValue(), redisService.getString(CacheKeyConstant.SAAS_SETTING));
    }

    /**
     * 获取默认店铺Id
     * @param storeId
     * @return
     */
    public Long getStoreIdWithDefault(Long storeId){
        if(StringUtils.equals(VASStatus.ENABLE.toValue(), redisService.getString(CacheKeyConstant.SAAS_SETTING))){
            return storeId;
        }
        return Constants.BOSS_DEFAULT_STORE_ID;
    }

}
