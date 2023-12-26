package com.wanmi.sbc.marketing.api.provider.constants;

/**
 * MQ消息目的地
 * @author: Geek Wang
 * @createDate: 2019/2/25 13:57
 * @version: 1.0
 */
public class MarketingJmsDestinationConstants {

    /**
     * 根据不同拼团状态更新不同的统计数据（已成团、待成团、团失败人数）
     */
    public static final String Q_MARKET_GROUPON_MODIFY_STATISTICS_NUM = "q.market.groupon.modify.statistics.num";

    /**
     * 延迟同步到商品表
     */
    public static final String Q_SYNCHRONIZATION_GOODS_INFO_PURCHASE_NUM = "q.synchronization.goods.info.purchase.num";

    /**
     * 延迟同步到商品表消费者
     */
    public static final String Q_SYNCHRONIZATION_GOODS_INFO_PURCHASE_NUM_CONSUMER = "synchronization-goods-info-consumer";

    /**
     * 生产者
     */
    public static final String Q_SYNCHRONIZATION_GOODS_INFO_PURCHASE_NUM_PRODUCER = "synchronization-goods-info-producer";
}
