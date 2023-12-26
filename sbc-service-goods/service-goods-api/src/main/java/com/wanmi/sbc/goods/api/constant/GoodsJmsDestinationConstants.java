package com.wanmi.sbc.goods.api.constant;

/**
 * MQ消息目的地
 * @author: Geek Wang
 * @createDate: 2019/2/25 13:57
 * @version: 1.0
 */
public class GoodsJmsDestinationConstants {

    /**
     * 更新已成团人数
     */
    public static final String Q_GROUPON_GOODS_INFO_MODIFY_ALREADY_GROUPON_NUM = "q.groupon.goods.info.modify.already.groupon.num";

    /**
     * 更新商品销售量、订单量、交易额
     */
    public static final String Q_GROUPON_GOODS_INFO_MODIFY_ORDER_PAY_STATISTICS = "q.groupon.goods.info.modify.order.pay.statistics";


    /**
     * 竞价排名活动开始的生产者
     */
    public static final String Q_GOODS_SERVICE_START_BIDDING_ACTIVITY_PRODUCER = "start-bidding-producer";

    /**
     * 竞价排名活动开始的消费者
     */
    public static final String Q_GOODS_SERVICE_START_BIDDING_ACTIVITY_CONSUMER = "start-bidding-consumer";

    /**
     * 竞价排名活动结束的生产者
     */
    public static final String Q_GOODS_SERVICE_FINISH_BIDDING_ACTIVITY_PRODUCER = "finish-bidding-producer";

    /**
     * 竞价排名活动结束的消费者
     */
    public static final String Q_GOODS_SERVICE_FINISH_BIDDING_ACTIVITY_CONSUMER = "finish-bidding-consumer";

    /**
     * 缺货的消息
     */
    public static final String Q_GOODS_SERVICE_LACK_GOODS_STOCK = "lack-goods-stock";
}
