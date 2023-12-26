package com.wanmi.sbc.mq;

import com.wanmi.sbc.order.api.constant.JmsDestinationConstants;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author: Geek Wang
 * @createDate: 2019/4/10 15:11
 * @version: 1.0
 */
public interface BossSink {

	/**
	 * 订单支付MQ处理
	 * @return
	 */
	@Input(JmsDestinationConstants.Q_ORDER_SERVICE_ORDER_PAYED)
	SubscribableChannel doOrderPayed();

	/**
	 * 订单完成MQ处理
	 * @return
	 */
	@Input(JmsDestinationConstants.Q_ORDER_SERVICE_ORDER_COMPLETE)
	SubscribableChannel doOrderComplete();

	/**
	 * 积分订单完成MQ处理
	 * @return
	 */
	@Input(JmsDestinationConstants.Q_ORDER_SERVICE_POINTS_ORDER_COMPLETE)
	SubscribableChannel doPointsOrderComplete();


	/**
	 * 订单支付&订单完成MQ处理
	 * @return
	 */
	@Input(JmsDestinationConstants.Q_ORDER_SERVICE_ORDER_PAYED_AND_COMPLETE)
	SubscribableChannel doOrderPayedAndComplete();

	/**
	 * 订单退款作废MQ处理
	 * @return
	 */
	@Input(JmsDestinationConstants.Q_ORDER_SERVICE_ORDER_REFUND_VOID)
	SubscribableChannel doOrderRefundVoid();

	/**
	 * 订单退款作废MQ处理
	 * @return
	 */
	@Input(JmsDestinationConstants.Q_ORDER_SERVICE_ORDER_PILE_REFUND_VOID)
	SubscribableChannel doOrderPileRefundVoid();

	/**
	 * 退单状态变更MQ处理：分销任务临时表退单数量加减
	 * @return
	 */
	@Input(JmsDestinationConstants.Q_ORDER_SERVICE_RETURN_ORDER_FLOW)
	SubscribableChannel doReturnOrderInit();

	/**
	 * 囤货退单状态变更MQ处理：分销任务临时表退单数量加减
	 * @return
	 */
	@Input(JmsDestinationConstants.Q_ORDER_SERVICE_RETURN_PILE_ORDER_FLOW)
	SubscribableChannel doReturnPileOrderInit();

	/**
	 * wms回调缺货
	 */
	@Input(JmsDestinationConstants.Q_ORDER_SERVICE_MESSAGE_WMS_AUTO_CHARGEBACK)
	SubscribableChannel wmsAutoChargeback();

	/**
	 * 库存刷新es
	 * @return
	 */
	@Input(JmsDestinationConstants.Q_ORDER_SERVICE_STOCK_REFRESH_ES)
	SubscribableChannel stockRefreshEsData();

	/**
	 * 购买指定商品赠券
	 * @return
	 */
	@Input(JmsDestinationConstants.Q_BUY_GOODS_SEND_COUPON)
	SubscribableChannel buyGoodsSendCoupon();

	/**
	 * 直接退款无需审核
	 * @return
	 */
	@Input(JmsDestinationConstants.Q_REFUND_ORDER_NOT_AUDIT_CONSUMER)
	SubscribableChannel refundOrderNotAuditProducer();

	/**
	 * 直接退款无需审核
	 * @return
	 */
	@Input(JmsDestinationConstants.Q_REFUND_ORDER_NOT_AUDIT_NEW_PILE_CONSUMER)
	SubscribableChannel refundOrderNotAuditNewPileProducer();


	/**
	 * 热销排行
	 * @return
	 */
	@Input(JmsDestinationConstants.Q_HOT_SALE_AREA)
	SubscribableChannel hotSaleArea();
}
