package com.wanmi.sbc.marketing.bean.constant;

/**
 * @Author: songhanlin
 * @Date: Created In 3:32 PM 2018/9/13
 * @Description: 常量类
 */
public final class Constant {
    private Constant() {}

    /**
     * 优惠券中优惠券分类的最大数量
     */
    public static final Integer MAX_CATE_COUNT_FOR_COUPON = 3;

    /**
     * 优惠券分类中最多的数量
     */
    public static final Integer MAX_CATE_COUNT = 30;

    /**
     * 优惠券分类排序最大小值
     */
    public static final Integer MIN_CATE_SORT = 0;

    /**
     * boss默认店铺id
     */
    public static final Long BOSS_DEFAULT_STORE_ID = -1L;

    /**
     * 即将过期的天数，5天 2021-11-12 改为1天；update by XinJiang
     */
    public static final int OUT_OF_DAYS  = 1;

    /**
     * 拼团分类中最多的数量
     */
    public static final Integer MAX_GROUPON_CATE_COUNT = 30;

    /**
     * 拼团分类排序最大小值
     */
    public static final Integer MIN_GROUPON_CATE_SORT = 0;

    /**
     * 订单满赠的所有商品标识
     */
    public static final String FULL_GIT_ORDER_GOODS = "all";
    
    /**
     * 鲸币系统平台店铺id
     */
    public static final String WALLET_PLATFORM_STORE_ID = "123458023";
    
    /**
     * 订单返鲸币备注
     */
    public static final String SEND_ORDER_COIN_REMARK = "订单返鲸币";

    /**
     * 订单返鲸币退回备注
     */
    public static final String TAKE_BACK_ORDER_COIN_REMARK = "订单返鲸币退回";
    
    public static final String DATE_FORMAT = "M月d日";
    public static final String DATE_FORMAT1 = "M.d";
    
}
