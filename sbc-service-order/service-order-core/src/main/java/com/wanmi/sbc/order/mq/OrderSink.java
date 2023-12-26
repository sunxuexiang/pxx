package com.wanmi.sbc.order.mq;

import com.wanmi.sbc.order.api.constant.JmsDestinationConstants;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author: Geek Wang
 * @createDate: 2019/4/10 15:11
 * @version: 1.0
 */
public interface OrderSink {

	/**
	 * 团长开团MQ处理
	 * @return
	 */
	@Input(JmsDestinationConstants.Q_ORDER_SERVICE_OPEN_GROUPON_CONSUMER)
	SubscribableChannel openGroupon();

	/**
	 * 超过一定时间未支付，自动取消订单
	 * @return
	 */
	@Input(JmsDestinationConstants.Q_ORDER_SERVICE_CANCEL_ORDER_CONSUMER)
	SubscribableChannel cancelOrder();

	/**
	 * 新版本囤货超过一定时间未支付，自动取消订单
	 * @return
	 */
	@Input(JmsDestinationConstants.Q_ORDER_SERVICE_CANCEL_ORDER_NEW_PILE_CONSUMER)
	SubscribableChannel cancelNewPileOrder();

	/**
	 * 超过一定时间未支付，自动取消囤货订单
	 * @return
	 */
	@Input(JmsDestinationConstants.Q_ORDER_SERVICE_CANCEL_PILE_ORDER_CONSUMER)
	SubscribableChannel cancelPileOrder();

	/**
	 * 拼团订单-支付成功，订单异常，自动退款
	 * @return
	 */
	@Input(JmsDestinationConstants.Q_ORDER_SERVICE_GROUPON_PAY_SUCCESS_AUTO_REFUND)
	SubscribableChannel handleGrouponOrderRefund();

	/**
	 * 拼团订单-支付成功，订单异常，自动退款
	 * @return
	 */
	@Input(JmsDestinationConstants.Q_ORDER_SERVICE_GROUPON_JOIN_NUM_LIMIT_CONSUMER)
	SubscribableChannel handleGrouponNumLimit();

	/**
	 * 业务员交接数据
	 * @return
	 */
	@Input(JmsDestinationConstants.Q_ORDER_SERVICE_MODIFY_EMPLOYEE_DATA)
	SubscribableChannel modifyEmployeeData();

	/**
	 * 业务员交接数据
	 * @return
	 */
	@Input(JmsDestinationConstants.Q_ORDER_SERVICE_INSERT_HISTORY_COMPANY_DATA)
	SubscribableChannel insertCompanyInfo();

	/**
	 * 向wms推送支付单消费
	 * @return
	 */
	@Input(JmsDestinationConstants.Q_ORDER_SERVICE_DELAY_PUSHING_PAYMENT_CONSUMER)
	SubscribableChannel delayPushingPayment();

	/**
	 * 向金蝶推送订单信息
	 * @return
	 */
	@Input(JmsDestinationConstants.Q_ORDER_SERVICE_KINGDEE_PUSH_ORDER_CONSUMER)
	SubscribableChannel kingdeePushOrder();

	/**
	 * 购物车向金蝶推送订单信息
	 * @return
	 */
	@Input(JmsDestinationConstants.Q_ORDER_SERVICE_KINGDEE_PUSH_NEW_PILE_ORDER_CONSUMER)
	SubscribableChannel newPilekingdeePushShopCartOrder();

	/**
	 * 购物车向金蝶推送订单信息
	 * @return
	 */
	@Input(JmsDestinationConstants.Q_ORDER_SERVICE_SHOPPINGCART_KINGDEE_PUSH_ORDER_CONSUMER)
	SubscribableChannel shoppingCartKingdeePushOrder();

	/**
	 * 取消金蝶订单
	 * @return
	 */
	@Input(JmsDestinationConstants.Q_ORDER_SERVICE_CANCEL_KINGDEE_ORDER_CONSUMER)
	SubscribableChannel kingdeeCancelOrder();

	/**
	 * 推金蝶囤货单
	 * @return
	 */
	@Input(JmsDestinationConstants.Q_ORDER_SERVICE_PUSH_STOCK_KINGDEE_CONSUMER)
	SubscribableChannel pushStockkingdee();

	/**
	 * 推囤货退货单到金蝶
	 * @return
	 */
	@Input(JmsDestinationConstants.Q_ORDER_SERVICE_PUSH_STOCK_RETURN_GOODS_KINGDEE_CONSUMER)
	SubscribableChannel pushStockReturnGoodsKingdee();

	/**
	 * 电商推wms支付单
	 * @return
	 */
	@Input(JmsDestinationConstants.Q_ORDER_SERVICE_PUSH_WMS_PAY_CONSUMER)
	SubscribableChannel pushWmsPay();

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

	/**
	 * 订单确定支付
	 * @return
	 */
	@Input(JmsDestinationConstants.Q_ORDER_CONFIRM_PAY_CONSUMER)
	SubscribableChannel confirmPayConsumer();


	/**
	 * 向ERP推送佣金支付单消费
	 * @return
	 */
	@Input(JmsDestinationConstants.Q_ORDER_SERVICE_PUSH_COMMISSION_PAYMENT_CONSUMER)
	SubscribableChannel delayPushingCommissionPaymentConsumer();

	/**
	 * 向wms推送佣金支付单消费
	 * @return
	 */
	@Input(JmsDestinationConstants.Q_ORDER_SERVICE_PUSH_COMMISSION_REFUND_CONSUMER)
	SubscribableChannel Q_ORDER_SERVICE_PUSH_COMMISSION_REFUND_CONSUMER();

}
