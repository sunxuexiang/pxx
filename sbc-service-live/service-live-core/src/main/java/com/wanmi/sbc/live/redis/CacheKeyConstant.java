package com.wanmi.sbc.live.redis;

public interface CacheKeyConstant {
    /**
     * 直播在线人数、点赞数量增长系数
     */
    String LIVE_RULE_COE="LIVE:RULE:COE:";

    /**
     * 直播在线人数、点赞数量增长固定数量
     */
    String LIVE_RULE_FIX="LIVE:RULE:FIX:";
}
