package com.wanmi.sbc.marketing.bean.constant;

/**
 * @author chenchang
 * @since 2022/09/08
 * 囤货错误类
 */
public final class PileErrorCode {
    /**
     * 开始时间不可晚于结束时间
     */
    public final static String END_DATE_ERROR = "K-080401";
    /**
     * 在该时间区间已有囤货活动
     */
    public static final String TIME_NOT_AVAILABLE = "K-080402";

    private PileErrorCode() {
    }

}
