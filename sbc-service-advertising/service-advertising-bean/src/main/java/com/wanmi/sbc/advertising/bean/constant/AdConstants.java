package com.wanmi.sbc.advertising.bean.constant;

public class AdConstants {
	
    /**
     * 消息id key
     */
    public static final String AD_MSG_ID_KEY = "amqp_messageId";
    
    /**
     * 记录广告统计信息mq队列
     */
    public static final String AD_STATISTIC_ADD = "q.ad.statistic.add";
    
    /**
     * 取消购买广告延时消息生产者key
     */
    public static final String AD_PAY_CANCEL_PRODUCER = "ad-pay-cancel-producer";
    
    /**
     * 取消购买广告延时消息消费者key
     */
    public static final String AD_PAY_CANCEL_CONSUMER = "ad-pay-cancel-consumer";
    
    /**
     * 修改广告活动展示信息消费者key
     */
    public static final String AD_UPDATE_CONSUMER = "ad_update_consumer";
    
    /**
     * 修改广告活动展示信息生产者key
     */
    public static final String AD_UPDATE_PRODUCER = "ad_update_producer";
    
    /**
     * 广告活动id前缀
     */
    public static final String AD_ACTIVITY_ID_PREFIX = "AD";
    
    /**
     * 广告系统文件前缀
     */
    public static final String AD_FILE_PREFIX = "ad_file_";
    
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String DATE_PATTERN2 = "yyyyMMdd";
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_TIME_PATTERN2 = "yyyyMMddHHmmss";
    
    public static final String START_TIME_SUFFIX = " 00:00:00";
    public static final String END_TIME_SUFFIX = " 23:59:59";
    
    /**
     * 鲸币支付备注
     */
    public static final String COIN_PAY_REMARK = "购买广告";
    public static final String COIN_REFUND_REMARK = "广告退款";
    
    public static final String SUCCESS = "success";
    public static final String FAILED = "failed";
    
}
