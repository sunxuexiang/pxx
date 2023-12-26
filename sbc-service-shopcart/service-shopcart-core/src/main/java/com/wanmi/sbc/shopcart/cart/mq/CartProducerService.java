package com.wanmi.sbc.shopcart.cart.mq;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.shopcart.constant.JmsDestinationConstants;
import com.wanmi.sbc.shopcart.cart.BulkShopCartVO;
import com.wanmi.sbc.shopcart.cart.RetailShopCartVo;
import com.wanmi.sbc.shopcart.cart.ShopCartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.binding.BinderAwareChannelResolver;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;

/**
 * @Description: 订单状态变更生产者
 * @Autho qiaokang
 * @Date：2019-03-05 17:47:18
 */
@Service
@EnableBinding
public class CartProducerService {

    @Autowired
    private BinderAwareChannelResolver resolver;



    /**
     * 加购成功后，发生MQ消息
     * @param bulkShopCart
     */
    public void sendMQForOrderBulkShopCar(BulkShopCartVO bulkShopCart){
        resolver.resolveDestination(JmsDestinationConstants.Q_SHOP_CAR_BULK_ADD_CUSTOMER).send(new GenericMessage<>(JSONObject.toJSONString(bulkShopCart)));
    }

    /**
     * 加购成功后，发生MQ消息
     * @param shopCart
     */
    public void sendMQForOrderShopCar(ShopCartVO shopCart){
        resolver.resolveDestination(JmsDestinationConstants.Q_SHOP_CAR_ADD_CUSTOMER).send(new GenericMessage<>(JSONObject.toJSONString(shopCart)));
    }

    /**
     * 加购成功后，发生MQ消息
     * @param retailShopCartVo
     */
    public void sendMQForOrderRetailShopCar(RetailShopCartVo retailShopCartVo){
        resolver.resolveDestination(JmsDestinationConstants.Q_SHOP_CAR_RETAIL_ADD_CUSTOMER).send(new GenericMessage<>(JSONObject.toJSONString(retailShopCartVo)));
    }


    /**
     * 加购成功后，发生MQ消息
     * @param shopCart
     */
    public void sendMQForOrderStoreShopCar(ShopCartVO shopCart){
        resolver.resolveDestination(JmsDestinationConstants.Q_SHOP_CAR_ADD_CUSTOMER).send(new GenericMessage<>(JSONObject.toJSONString(shopCart)));
    }
}
