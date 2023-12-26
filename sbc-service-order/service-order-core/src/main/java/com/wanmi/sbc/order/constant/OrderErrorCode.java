package com.wanmi.sbc.order.constant;

/**
 * @Author: gaomuwei
 * @Date: Created In 上午9:49 2019/5/25
 * @Description: 订单服务错误码
 */
public final class OrderErrorCode {

    /**
     * 拼团单校验--商品未关联进行中的拼团活动
     */
    public final static String ORDER_MARKETING_GROUPON_NO_ACTIVITY = "K-050401";

    /**
     * 拼团单校验--当前已开团，不可再开团
     */
    public final static String ORDER_MARKETING_GROUPON_WAIT = "K-050402";

    /**
     * 拼团单校验--当前已参同一团活动，不可再参团
     */
    public final static String ORDER_MARKETING_GROUPON_ALREADY_JOINED = "K-050403";

    /**
     * 拼团单校验--已成团/团已作废，不可参团
     */
    public final static String ORDER_MARKETING_GROUPON_ALREADY_END = "K-050404";

    /**
     * 拼团单校验--未达到拼团商品起售数
     */
    public final static String ORDER_MARKETING_GROUPON_START_NUM = "K-050405";

    /**
     * 拼团单校验--已超过拼团商品限购数
     */
    public final static String ORDER_MARKETING_GROUPON_LIMIT_NUM = "K-050406";

    /**
     * 拼团单校验--拼团订单不存在
     */
    public final static String ORDER_MARKETING_GROUPON_ORDER_NOT_FUND = "K-050409";

    /**
     * 推送wms失败
     */
    public final static String ORDER_PUSH_ERRO = "K-050501";

    /**
     * 订单推送请求超时
     */
    public final static String ORDER_PUSH_TIME_OUT = "K-050509";


    /**
     * 申请退款失败，商品已拣货
     */
    public final static String ORDER_HAS_BEEN_PICK_IN_WMS = "K-050603";


    /**
     * 订单取消失败，已拣货
     */
    public final static String CANCEL_ORDER_FAILD_IN_WMS = "K-050602";

}
