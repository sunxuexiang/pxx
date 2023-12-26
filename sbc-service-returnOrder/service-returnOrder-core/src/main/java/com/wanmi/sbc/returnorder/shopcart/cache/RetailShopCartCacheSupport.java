package com.wanmi.sbc.returnorder.shopcart.cache;

import com.wanmi.sbc.common.enums.DefaultFlag;

import java.util.HashMap;
import java.util.Map;

/**
 * 零售购物车缓存助手类
 */
public interface RetailShopCartCacheSupport {

    /**
     * 散批购物车存储数量key和是否选中  shopping_cart_hash:[ID]:[WARE_ID]
     */
    public static final String RETAIL_SHOPING_CART_HASH = "retail_shopping_cart_hash:";
    /**
     * 散批批发购物车存储实体KEY shopping_cart_extra_hash:[ID]:[WARE_ID]
     */
    public static final String RETAIL_SHOPPING_CART_EXTRA_HASH = "retail_shopping_cart_extra_hash:";
    /**
     * 散批商品数量
     */
    public static final String RETAIL_GOOD_NUM = "retail_good_num:";
    /**
     * 散批是否选中
     */
    public static final String RETAIL_IS_CHECK = "retail_is_check:";

    static String buildKey(String customerId, Long wareId) {
        return RETAIL_SHOPING_CART_HASH + customerId;
    }

    static String buildExtraKey(String customerId, Long wareId) {
        return RETAIL_SHOPPING_CART_EXTRA_HASH+customerId;
    }

    static String buildHashKeyOfIsCheck(String goodInfoId) {
        return RETAIL_IS_CHECK+ goodInfoId;
    }

    static String buildHashKeyOfGoodNum(String goodInfoId) {
        return RETAIL_GOOD_NUM + goodInfoId ;
    }


    static Map<String, String> buildExtraValue(String goodInfoId, Long num, DefaultFlag isCheck) {
        Map<String, String> value = new HashMap<>();
        value.put(RETAIL_GOOD_NUM+goodInfoId, String.valueOf(num));
        value.put(RETAIL_IS_CHECK+goodInfoId, String.valueOf(isCheck.toValue()));
        return value;
    }
}
