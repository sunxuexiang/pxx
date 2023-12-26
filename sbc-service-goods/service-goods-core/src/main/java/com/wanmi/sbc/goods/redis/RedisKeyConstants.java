package com.wanmi.sbc.goods.redis;

public class RedisKeyConstants {

    /**
     * 营销活动信息hash key
     */
    public static final String MARKETING_INFO_HASH = "marketing_info_hash:";

    /**
     * 营销活动与商品关系 zset key
     */
    public static final String MARKETING_SCOPE_ZSET = "marketing_scope_zset:";

    /**
     * 营销活动与商品关系 hash key
     */
    public static final String MARKETING_SCOPE_HASH = "marketing_scope_hash:";

    /**
     * 赠品赠机与赠品之间的关联关系
     */
    public static final String MARKETING_GIFT_DETAIL_HASH = "marketing_gift_detail_hash:";


    /**
     * 营销第一次快照 first _snapshot:[userId]
     */
    public static final String FIRST_SNAPSHOT = "first_snapshot:";

    /**
     * 购物车存储实体KEY shopping_cart_extra_hash:[ID][WARE_ID]
     */
    public static final String SHOPPING_CART_EXTRA_HASH = "shopping_cart_extra_hash:";


    /**
     * 商品库存redis缓存
     */
    public static final String GOODS_INFO_STOCK_HASH = "goods_info_stock_hash:";

    /**
     * 同步库存内key
     */
    public static final String GOODS_INFO_WMS_STOCK = "goods_info_wms_stock:";


    /**
     * 同步压库存内key
     */
    public static final String GOODS_INFO_YK_STOCK = "goods_info_yk_stock:";
}
