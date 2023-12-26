package com.wanmi.sbc.returnorder.api.constant;

/**
 * @Description: MQ消息目的地常量
 * @Autho qiaokang
 * @Date：2019-03-05 17:54:47
 */
public interface JmsDestinationConstants {

    /**
     * 订单支付后，发送订单支付MQ消息
     */
    String Q_ORDER_SERVICE_ORDER_PAYED = "q.order.service.order.payed";



    /**
     * 购物车添加商品后发生MQ方法添加mysql(批发)
     */
    String Q_SHOP_CAR_ADD_CUSTOMER = "q.shop.car.add.customer";

    /**
     * 购物车添加商品后发生MQ方法添加mysql(零售)
     */
    String Q_SHOP_CAR_BULK_ADD_CUSTOMER = "q.shop.car.bulk.add.customer";

    /**
     * 购物车添加商品后发生MQ方法添加mysql(散批)
     */
    String Q_SHOP_CAR_RETAIL_ADD_CUSTOMER = "q.shop.car.retail.add.customer";


    /**
     * 购物车添加商品后发生MQ方法添加mysql（囤货）
     */
    String Q_SHOP_CAR_STORE_ADD_CUSTOMER = "q.shop.car.store.new.add.customer";


    /**
     * 订单完成时，发送订单完成MQ消息
     */
    String Q_ORDER_SERVICE_ORDER_COMPLETE = "q.order.service.order.complete";
    /**
     * 订单完成时，发送订单完成MQ消息
     */
    String Q_ORDER_SERVICE_POINTS_ORDER_COMPLETE = "q.order.service.points_order.complete";

    /**
     * 订单退款作废时，发送订单MQ消息
     */
    String Q_ORDER_SERVICE_ORDER_REFUND_VOID = "q.order.service.order.refund.void";

    /**
     * 囤货订单退款作废时，发送订单MQ消息
     */
    String Q_ORDER_SERVICE_ORDER_PILE_REFUND_VOID = "q.order.service.order.pile.refund.void";

    /**
     * 退单状态变更，发送MQ消息
     */
    String Q_ORDER_SERVICE_RETURN_ORDER_FLOW = "q.order.service.return.order.flow";

    /**
     * 退单状态变更，发送MQ消息
     */
    String Q_ORDER_SERVICE_NEW_PILE_RETURN_ORDER_FLOW = "q.order.service.new.pile.return.order.flow";

    /**
     * 囤货退单状态变更，发送MQ消息
     */
    String Q_ORDER_SERVICE_RETURN_PILE_ORDER_FLOW = "q.order.service.return.pile.order.flow";

    /**
     * 囤货退单状态变更，发送MQ消息
     */
    String Q_ORDER_SERVICE_RETURN_NEW_PILE_ORDER_FLOW = "q.order.service.return.new.pile.order.flow";

    /**
     * 发送订单支付、订单完成MQ消息
     */
    String Q_ORDER_SERVICE_ORDER_PAYED_AND_COMPLETE = "q.order.service.order.payed.and.complete";


    /**
     * 订单提交，发送一个超过一定时间未支付，取消订单MQ生产者消息通道
     */
    String Q_ORDER_SERVICE_CANCEL_ORDER_PRODUCER = "cancel-order-producer";

    /**
     * 订单提交，发送一个超过一定时间未支付，取消订单MQ消费者消息通道
     */
    String Q_ORDER_SERVICE_CANCEL_ORDER_CONSUMER = "cancel-order-consumer";

    /**
     * 新版本囤货订单提交，发送一个超过一定时间未支付，取消订单MQ生产者消息通道
     */
    String Q_ORDER_SERVICE_CANCEL_ORDER_NEW_PILE_PRODUCER = "cancel-order-new-pile-producer";

    /**
     * 新版本囤货订单提交，发送一个超过一定时间未支付，取消订单MQ消费者消息通道
     */
    String Q_ORDER_SERVICE_CANCEL_ORDER_NEW_PILE_CONSUMER = "cancel-order-new-pile-consumer";

    /**
     * 囤货订单提交，发送一个超过一定时间未支付，取消囤货订单MQ生产者消息通道
     */
    String Q_ORDER_SERVICE_CANCEL_PILE_ORDER_PRODUCER = "cancel-pile-order-producer";

    /**
     * 囤货订单提交，发送一个超过一定时间未支付，取消囤货订单MQ消费者消息通道
     */
    String Q_ORDER_SERVICE_CANCEL_PILE_ORDER_CONSUMER = "cancel-pile-order-consumer";

    /**
     * 团长开团延迟MQ生产者消息通道
     */
    String Q_ORDER_SERVICE_OPEN_GROUPON_PRODUCER = "open-groupon-producer";

    /**
     * 团长开团延迟MQ消费者消息通道
     */
    String Q_ORDER_SERVICE_OPEN_GROUPON_CONSUMER = "open-groupon-consumer";

    /**
     * 开团发送消息推送（C端展示）
     */
    String Q_ORDER_SERVICE_OPEN_GROUPON_MESSAGE_PUSH_TO_C= "q.order.service.open.groupon.msg.push.to.c";

    /**
     * 秒杀商品订单取消订单发送mq还库存
     */
    String Q_FLASH_SALE_GOODS_ORDER_CANCEL_RETURN_STOCK = "flash_sale_goods_order_cancel_return_stock";

    /**
     * 拼团订单-支付成功，订单异常，自动退款
     */
    String Q_ORDER_SERVICE_GROUPON_PAY_SUCCESS_AUTO_REFUND = "q.order.service.groupon.pay.success.auto.refund";

    /**
     * 参团人数延迟消息生产者
     */
    String Q_ORDER_SERVICE_GROUPON_JOIN_NUM_LIMIT_PRODUCER = "groupon-num-limit-producer";

    /**
     * 参团人数延迟消息消费这
     */
    String Q_ORDER_SERVICE_GROUPON_JOIN_NUM_LIMIT_CONSUMER = "groupon-num-limit-consumer";

    /**
     * 业务员交接数据
     */
    String Q_ORDER_SERVICE_MODIFY_EMPLOYEE_DATA = "q.order.service.modify.employee.data";

    /**
     * 公司历史记录
     */
    String Q_ORDER_SERVICE_INSERT_HISTORY_COMPANY_DATA = "q.order.service.insert.company.history.data";

    /**
     *  wms少货时退单消息发送
     */
    String Q_ORDER_SERVICE_MESSAGE_WMS_AUTO_CHARGEBACK="q.order.service.message.wms.auto.chargeback";

    /**
     *  订单库存相关操作更新mq
     */
    String Q_ORDER_SERVICE_STOCK_REFRESH_ES="q.order.service.stock.refresh.es";

    /**
     * 推送wms支付单生产者
     */
    String Q_ORDER_SERVICE_DELAY_PUSHING_PAYMENT_PRODUCER="delay-pushing-payment-producer";

    /**
     * 推送wms支付单消费者
     */
    String Q_ORDER_SERVICE_DELAY_PUSHING_PAYMENT_CONSUMER="delay-pushing-payment-consumer";

    /**
     * 向金蝶推送订单数据生产者
     */
    String Q_ORDER_SERVICE_KINGDEE_PUSH_ORDER_PRODUCER="kingdee-push-order-producer";

    /**
     * 向金蝶推送订单数据消费者
     */
    String Q_ORDER_SERVICE_KINGDEE_PUSH_ORDER_CONSUMER="kingdee-push-order-consumer";

    /**
     * 囤货向金蝶推送购物车数据生产者
     */
    String Q_ORDER_SERVICE_KINGDEE_PUSH_NEW_PILE_ORDER_PRODUCER="kingdee-push-new-pile-order-producer";

    /**
     * 囤货向金蝶推送购物车数据消费者
     */
    String Q_ORDER_SERVICE_KINGDEE_PUSH_NEW_PILE_ORDER_CONSUMER="kingdee-push-new-pile-order-consumer";

    /**
     * 购物车向金蝶推送订单数据消费者
     */
    String Q_ORDER_SERVICE_SHOPPINGCART_KINGDEE_PUSH_ORDER_CONSUMER="shoppingCart-kingdee-push-order-consumer";

    /**
     * 购物车向金蝶推送订单数据生产者
     */
    String Q_ORDER_SERVICE_SHOPPINGCART_KINGDEE_PUSH_ORDER_PRODUCER="shoppingCart-kingdee-push-order-producer";

    /**
     * 取消金蝶订单生产者
     */
    String Q_ORDER_SERVICE_CANCEL_KINGDEE_ORDER_PRODUCER="cancel-kingdee-order-producer";

    /**
     * 取消金蝶订单消费者
     */
    String Q_ORDER_SERVICE_CANCEL_KINGDEE_ORDER_CONSUMER="cancel-kingdee-order-consumer";

    /**
     * 推金蝶囤货单生产者
     */
    String Q_ORDER_SERVICE_PUSH_STOCK_KINGDEE_PRODUCER="push-stock-kingdee-producer";

    /**
     * 推金蝶囤货单消费者
     */
    String Q_ORDER_SERVICE_PUSH_STOCK_KINGDEE_CONSUMER="push-stock-kingdee-consumer";

    /**
     * 推金蝶囤货退货单生产者
     */
    String Q_ORDER_SERVICE_PUSH_STOCK_RETURN_GOODS_KINGDEE_PRODUCER="push-stock-Return-goods-kingdee-producer";

    /**
     * 推金蝶囤货退货单消费者
     */
    String Q_ORDER_SERVICE_PUSH_STOCK_RETURN_GOODS_KINGDEE_CONSUMER="push-stock-Return-goods-kingdee-consumer";

    /**
     * 推wms支付单生产者
     */
    String Q_ORDER_SERVICE_PUSH_WMS_PAY_PRODUCER="push-wms-pay-producer";

    /**
     * 推wms支付单消费者
     */
    String Q_ORDER_SERVICE_PUSH_WMS_PAY_CONSUMER="push-wms-pay-consumer";

    /**
     * 购买指定商品赠券
     */
    String Q_BUY_GOODS_SEND_COUPON = "buy-goods-send-coupon";

    /**
     * 退款无审核
     */
    String Q_REFUND_ORDER_NOT_AUDIT_PRODUCER = "refund-order-not-audit-producer";
    String Q_REFUND_ORDER_NOT_AUDIT_CONSUMER = "refund-order-not-audit-consumer";


    /**
     * 退款无审核(新版本囤货)
     */
    String Q_REFUND_ORDER_NOT_AUDIT_NEW_PILE_PRODUCER = "refund-order-not-audit-new-pile-producer";
    String Q_REFUND_ORDER_NOT_AUDIT_NEW_PILE_CONSUMER = "refund-order-not-audit-new-pile-consumer";


    /**
     * 购买指定商品赠券
     */
    String Q_HOT_SALE_AREA = "hot-sale-area";

    /**
     * 订单确定支付生产通道
     */
    String Q_ORDER_CONFIRM_PAY_PRODUCER = "q-order-confirm-pay-producer";

    /**
     * 订单确定支付消费通道
     */
    String Q_ORDER_CONFIRM_PAY_CONSUMER = "q-order-confirm-pay-consumer";


    /**
     * 推送wms支付单生产者
     */
    String Q_ORDER_SERVICE_PUSH_COMMISSION_PAYMENT_PRODUCER="delay-push-commission-payment-producer";

    /**
     * 推送wms支付单消费者
     */
    String Q_ORDER_SERVICE_PUSH_COMMISSION_PAYMENT_CONSUMER="delay-push-commission-payment-consumer";

    /**
     * 推送ERP退款单生产者
     */
    String Q_ORDER_SERVICE_PUSH_COMMISSION_REFUND_PRODUCER="delay-push-commission-refund-producer";

    /**
     * 推送ERP退款单消费者
     */
    String Q_ORDER_SERVICE_PUSH_COMMISSION_REFUND_CONSUMER="delay-push-commission-refund-consumer";

    /**
     * 商家后台退款自动审核
     */
    String Q_SUPPLIER_REFUND_ORDER_AUTO_AUDIT_PRODUCER = "delay-supplier-auto-refund-order-producer";
    String Q_SUPPLIER_REFUND_ORDER_AUTO_AUDIT_CONSUMER = "delay-supplier-auto-refund-order-consumer";

}
