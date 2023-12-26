
package com.wanmi.sbc.common.constant;

/**
 * @ClassName RedisKeyConstant
 * @Description redis key对应常量
 * @Author lvzhenwei
 * @Date 2019/6/15 14:15
 **/
public final class RedisKeyConstant {

    private RedisKeyConstant() {

    }

    /**
     * 秒杀+积分兑换优惠券迭代---商品秒杀抢购商品信息对应key前缀
     */
    public static final String FLASH_SALE_GOODS_INFO = "flashSaleGoodsInfo:";

    /**
     * 秒杀+积分兑换优惠券迭代---商品秒杀抢购资格对应key前缀
     */
    public static final String FLASH_SALE_GOODS_QUALIFICATIONS = "flashSaleGoodsQualifications:";

    /**
     * 秒杀+积分兑换优惠券迭代---商品秒杀抢购已抢购商品数量对应key前缀
     */
    public static final String FLASH_SALE_GOODS_HAVE_PANIC_BUYING = "flashSaleGoodsHavePanicBuying:";

    /**
     * 秒杀+积分兑换优惠券迭代---商品秒杀抢购商品库存对应key前缀
     */
    public static final String FLASH_SALE_GOODS_INFO_STOCK = "flashSaleGoodsInfoStock:";


    public static final String GOODS_DETAIL_CACHE = "goodsdetail:";

    /**
     * 直播商品数据
     */
    public static final String GOODS_LIVE_INFO = "goodsLiveInfo:";

    /**
     * 导航栏数据
     */
    public static final String NAVIGATION_TAB_CONFIG_DATA = "navigationConfigData:";

    /**
     * 预置词缓存
     */
    public static final String PRESET_SEARCH_TERMS_DATA = "presetSearchTermsData:";

    /**
     * 热销排行榜
     */
    public static final String HIT_LIST = "HIT_LIST";

    /**
     * 热销周总榜
     */
    public static final String TOTAL_HOT_SALE_WEEK = "TOTAL_HOT_SALE:week";

    /**
     * 分类热销周总榜
     */
    public static final String CATE_HOT_SALE_WEEK = "CATE_HOT_SALE:week";

    /**
     * 零售超市分类图标缓存
     */
    public static final String RETAIL_GOODS_CATE_IMG = "RETAIL_GOODS_CATE_IMG";

    /**
     * 移动端推荐商品列表缓存key
     */
    public static final String RECOMMEND_PAGE_SETTING = "RECOMMEND_PAGE_SETTING";
    /**
     * (商户)移动端推荐商品列表缓存key
     */
    public static final String STORE_RECOMMEND_PAGE_SETTING = "STORE_RECOMMEND_PAGE_SETTING";

    /**
     * 设置推荐商品缓存key
     */
    public static final String GOODS_RECOMMEND_GOODS = "GOODS_RECOMMEND_GOODS";

    /**
     * 推荐商品配置信息缓存key
     */
    public static final String GOODS_RECOMMEND_SETTING = "GOODS_RECOMMEND_SETTING";

    /**
     * 增加囤货活动缓存key
     */
    public static final String PILE_ACTIVITY_ADD = "PILE_ACTIVITY_ADD:";

    /**
     * 散批购物车商品空缓存 key
     */
    public static final String BULK_SHOPPING_CART_EMPTY = "bulk_shopping_cart_empty:";

    /**
     * 购物车商品空缓存标识
     */
    public static final String SHOPPING_CART_EMPTY_CACHE_IDENTIFY = "$";


    /**
     * (商户)移动端推荐商品列表缓存key
     */
    public static final String MERCHANT_RECOMMEND_PAGE_SETTING = "MERCHANT_RECOMMEND_PAGE_SETTING:";

    /**
     * (商户)设置推荐商品缓存key
     */
    public static final String MERCHANT_GOODS_RECOMMEND_GOODS = "MERCHANT_GOODS_RECOMMEND_GOODS:";

    /**
     * (商户)推荐商品配置信息缓存key
     */
    public static final String MERCHANT_GOODS_RECOMMEND_SETTING = "MERCHANT_GOODS_RECOMMEND_SETTING:";


    /**
     * (商户)移动端推荐分类列表缓存key
     */
    public static final String CONFIG_RECOMMEND_PAGE_SETTING = "CONFIG_RECOMMEND_PAGE_SETTING:";

    /**
     * (商户)设置推荐分类缓存key
     */
    public static final String CONFIG_TYPE_RECOMMEND_TYPE = "CONFIG_TYPE_RECOMMEND_TYPE:";

    /**
     * (商户)推荐分类配置信息缓存key
     */
    public static final String CONFIG_TYPE_RECOMMEND_SETTING = "CONFIG_TYPE_RECOMMEND_SETTING:";

    /**
     * (商户)商户推荐信息
     */
    public static final String SCREEN_ORDER_ADD_LAST_TIME = "MERCHANT_RECOMMEND_GOODS:";
}