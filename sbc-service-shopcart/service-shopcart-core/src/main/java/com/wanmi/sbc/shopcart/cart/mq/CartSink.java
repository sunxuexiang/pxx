package com.wanmi.sbc.shopcart.cart.mq;

import com.wanmi.sbc.shopcart.constant.JmsDestinationConstants;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author: Geek Wang
 * @createDate: 2019/4/10 15:11
 * @version: 1.0
 */
public interface CartSink {

	/**
	 * 加购成功后消费者
	 * @return
	 */
	@Input(JmsDestinationConstants.Q_SHOP_CAR_ADD_CUSTOMER)
	SubscribableChannel pushShopCarGoods();

	/**
	 * 加购成功后消费者
	 * @return
	 */
	@Input(JmsDestinationConstants.Q_SHOP_CAR_RETAIL_ADD_CUSTOMER)
	SubscribableChannel pushRetailshopCarGoods();

	/**
	 * 加购成功后消费者(散批)
	 * @return
	 */
	@Input(JmsDestinationConstants.Q_SHOP_CAR_BULK_ADD_CUSTOMER)
	SubscribableChannel pushBulkShopCarGoods();

}
