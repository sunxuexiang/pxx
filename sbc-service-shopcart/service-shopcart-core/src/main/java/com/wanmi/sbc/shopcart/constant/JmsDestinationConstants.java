package com.wanmi.sbc.shopcart.constant;

/**
 * @Description: MQ消息目的地常量
 * @Autho qiaokang
 * @Date：2019-03-05 17:54:47
 */
public interface JmsDestinationConstants {

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

    String Q_SHOP_CAR_STORE_ADD_CUSTOMER = "q.shop.car.store.new.add.customer";


}
