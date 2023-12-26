package com.wanmi.sbc.shopcart.cart.cache;

import com.wanmi.sbc.common.enums.DefaultFlag;

import java.util.HashMap;
import java.util.Map;

/**
 * 散批购物车缓存助手类
 */
public interface BulkShopCartCacheSupport {

    /**
     * 散批购物车存储数量key和是否选中  bulk_shopping_cart_hash:[customer_id]:[ware_id]
     * 内key是：
     *  bulk_good_num:[goods_info_id]
     *  bulk_is_check:[goods_info_id]
     */
    public static final String BULK_SHOPING_CART_HASH = "bulk_shopping_cart_hash:";
    /**
     * 散批批发购物车存储实体KEY bulk_shopping_cart_extra_hash:[customer_id]:[ware_id]
     * 内key是：[goods_info_id]
     */
    public static final String BULK_SHOPPING_CART_EXTRA_HASH = "bulk_shopping_cart_extra_hash:";
    /**
     * 散批商品数量 bulk_good_num:[goods_info_id]
     */
    public static final String BULK_GOOD_NUM = "bulk_good_num:";
    /**
     * 散批是否选中 bulk_is_check:[goods_info_id]
     */
    public static final String BULK_IS_CHECK = "bulk_is_check:";

    static String buildKey(String customerId, Long wareId) {
        return BULK_SHOPING_CART_HASH + customerId+":"+wareId;
    }

    static String buildExtraKey(String customerId, Long wareId) {
        return BULK_SHOPPING_CART_EXTRA_HASH + customerId + ":" + wareId;
    }

    static String buildHashKeyOfIsCheck(String goodInfoId) {
        return BULK_IS_CHECK + goodInfoId;
    }

    static String buildHashKeyOfGoodNum(String goodInfoId) {
        return BULK_GOOD_NUM + goodInfoId ;
    }

    static Map<String, String> buildExtraValue(String goodInfoId, Long num, DefaultFlag isCheck) {
        Map<String, String> value = new HashMap<>();
        value.put(BULK_GOOD_NUM + goodInfoId, String.valueOf(num));
        value.put(BULK_IS_CHECK + goodInfoId, String.valueOf(isCheck.toValue()));
        return value;
    }
}
