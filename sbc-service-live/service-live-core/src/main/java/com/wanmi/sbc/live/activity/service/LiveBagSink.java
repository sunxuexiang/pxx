package com.wanmi.sbc.live.activity.service;

import com.wanmi.sbc.live.api.constant.JmsBagDestinationConstants;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author: Geek Wang
 * @createDate: 2019/4/10 15:11
 * @version: 1.0
 */
public interface LiveBagSink {

	/**
	 * 福袋MQ处理
	 * @return
	 */
	@Input(JmsBagDestinationConstants.Q_LIVE_SERVICE_BAG_SEND_CONSUMER)
	SubscribableChannel sendBag();

	/**
	 * 断流MQ处理
	 * @return
	 */
	@Input(JmsBagDestinationConstants.Q_LIVE_SERVICE_STREAM_SEND_CONSUMER)
	SubscribableChannel sendStream();
}
