package com.wanmi.sbc.shopcart.redis;

public class RedisKeyConstants {
//=======================================批发============================================================================
    /**
     * 购物车存储数量key和是否选中  shopping_cart_hash:[ID][WARE_ID]
     */
    public static final String SHOPPING_CART_HASH = "shopping_cart_hash:";
    /**
     * 商品数量
     */
    public static final String good_num = "good_num:";
    /**
     * 是否选中
     */
    public static final String is_check = "is_check:";
    /**
     * 批发购物车存储实体KEY shopping_cart_extra_hash:[ID][WARE_ID]
     */
    public static final String SHOPPING_CART_EXTRA_HASH = "shopping_cart_extra_hash:";

//=======================================批发============================================================================


//=======================================散批============================================================================
    /**
     * 散批购物车存储数量key和是否选中  shopping_cart_hash:[ID]
     */
    public static final String RTAIL_SHOPPING_CART_HASH = "retail_shopping_cart_hash:";
    /**
     * 散批商品数量
     */
    public static final String retail_good_num = "retail_good_num:";
    /**
     * 散批是否选中
     */
    public static final String retail_is_check = "retail_is_check:";
    /**
     * 散批批发购物车存储实体KEY shopping_cart_extra_hash:[ID]
     */
    public static final String RTAIL_SHOPPING_CART_EXTRA_HASH = "retail_shopping_cart_extra_hash:";
//=======================================散批============================================================================



    //=======================================囤货购物车============================================================================
    /**
     * 散批购物车存储数量key和是否选中  shopping_cart_hash:[ID]
     */
    public static final String STORE_SHOPPING_CART_HASH = "store_shopping_cart_hash:";
    /**
     * 散批商品数量
     */
    public static final String store_good_num = "store_good_num:";
    /**
     * 散批是否选中
     */
    public static final String store_is_check = "store_is_check:";
    /**
     * 散批批发购物车存储实体KEY shopping_cart_extra_hash:[ID]
     */
    public static final String STORE_SHOPPING_CART_EXTRA_HASH = "store_shopping_cart_extra_hash:";
    //=======================================囤货购物车============================================================================



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


    /**
     * 营销第一次快照 first _snapshot:[userId] hash
     */
    public static final String FIRST_SNAPSHOT = "first_snapshot:";
}
