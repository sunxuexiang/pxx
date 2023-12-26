package com.wanmi.sbc.marketing.marketing.cache;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.marketing.common.model.root.Marketing;
import com.wanmi.sbc.marketing.redis.RedisKeyConstants;

/**
 * 营销缓存key-value support
 */
public interface MarketingInfoCacheSupport {

    static String buildKey(Long marketId) {
        return RedisKeyConstants.MARKETING_INFO_STRING + marketId;
    }

    static String buildValue(Marketing marketing) {
        return JSONObject.toJSONString(marketing);
    }
}
