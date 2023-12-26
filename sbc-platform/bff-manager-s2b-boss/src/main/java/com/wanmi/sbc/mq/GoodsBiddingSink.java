package com.wanmi.sbc.mq;

import com.wanmi.sbc.goods.api.constant.GoodsJmsDestinationConstants;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * @ClassName GoodsAboutNumSink
 * @Description TODO 商品竞价
 * @Author baijz
 * @Date 2019/4/17 15:30
 **/
public interface GoodsBiddingSink {


    /**
     * 开始竞价排名的活动——更新goodsInfos,es,redis
     * @return
     */
    @Input(GoodsJmsDestinationConstants.Q_GOODS_SERVICE_START_BIDDING_ACTIVITY_CONSUMER)
    SubscribableChannel startBiddingActivity();

    /**
     * 结束竞价排名的活动——更新goodsInfos,es,redis
     * @return
     */
    @Input(GoodsJmsDestinationConstants.Q_GOODS_SERVICE_FINISH_BIDDING_ACTIVITY_CONSUMER)
    SubscribableChannel finishBiddingActivity();
}
