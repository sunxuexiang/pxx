package com.wanmi.sbc.marketing.redis;

public class RedisKeyConstants {
    /**
     * 营销活动信息key
     */
    public static final String MARKETING_INFO_STRING = "marketing_info_string:";

    /**
     * 营销活动与商品关系 key
     * 不存放已经被终止的商品
     */
    public static final String MARKETING_SCOPE_STRING = "marketing_scope_string:";

    /**
     * 营销活动与商品关系 hash key
     */
    public static final String MARKETING_SCOPE_HASH = "marketing_scope_hash:";

    /**
     * 赠品等级与赠品之间的关联关系
     */
    public static final String MARKETING_GIFT_DETAIL_HASH = "marketing_gift_detail_hash:";
}
