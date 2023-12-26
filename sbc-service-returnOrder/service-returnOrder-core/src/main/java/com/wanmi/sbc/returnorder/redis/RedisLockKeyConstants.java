package com.wanmi.sbc.returnorder.redis;


/**
 * 分布式锁
 */
public class RedisLockKeyConstants {
    /**
     * 营销活动修改锁前缀
     */
    public static final String MARKETING_UPDATE_LOCK_PREFIX = "marketing_update_lock:";

    public static final String MARKET_REQUEST_KEY = "MARKET_REQUEST_KEY:";


    public static final String SHOP_CAR_CACHE_KEY = "shop_car_cache_key:";

    /**
     *
     */
    public static final String BULK_SHOPPING_CART_PERSISTENCE_KEY = "BULK_SHOPPING_CART_PERSISTENCE_KEY:";
}