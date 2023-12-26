package com.wanmi.sbc.live.api.constant;

public interface JmsBagDestinationConstants {

    /**
     * 福袋发送后，发送福袋开奖MQ消息
     */
    String Q_LIVE_SERVICE_BAG_SEND_PRODUCER = "sendbag-live-producer";

    /**
     * 福袋发送后，发送福袋开奖MQ消息 消费者
     */
    String Q_LIVE_SERVICE_BAG_SEND_CONSUMER="sendbag-live-consumer";


    /**
     * 断流发送MQ消息 生产者
     */
    String Q_LIVE_SERVICE_STREAM_SEND_PRODUCER = "send-stream-live-producer";

    /**
     * 断流发送MQ消息 消费者
     */
    String Q_LIVE_SERVICE_STREAM_SEND_CONSUMER = "send-stream-live-consumer";
}
