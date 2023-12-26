package com.wanmi.sbc.advertising.redis;

public class RedisKeyConstants {

	/**
	 * 广告活动保存锁
	 */
	public static final String ADVERTISING_SAVE_LOCK = "ad_act_save_lock:";
	
	/**
	 * 广告活动付款锁
	 */
	public static final String ADVERTISING_PAY_LOCK = "ad_act_pay_lock:";
	
	/**
	 * 广告活动退款锁
	 */
	public static final String ADVERTISING_REFUND_LOCK = "ad_act_refund_lock:";
	
	
	public static final String ACTIVE_AD_KEY_TPL = "active_ad_key:{0}_{1}_{2}_{3}_{4}";

}
