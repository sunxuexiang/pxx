package com.wanmi.sbc.goods.freight.service;

import com.wanmi.sbc.goods.freight.model.root.FreightTemplateDeliveryArea;
import com.wanmi.sbc.goods.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @desc  
 * @author shiy  2023/11/16 10:53
*/
@Service
public class FreightTemplateDeliveryAreaRedisService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FreightTemplateDeliveryAreaRedisService.class);

    private static final String STORE_DELIVERY_AREA_KEY = "store_delivery_area:%s:%s";

    private static final long EXPIRE_TIME_SECONDS =86400; //60*60*24;

    @Autowired
    private RedisService redisService;

    private String redisKeyStoreDeliveryArea(Long storeId,Integer destinationType){
        return String.format(STORE_DELIVERY_AREA_KEY,storeId,destinationType);
    }

    public void delete(Long storeId,Integer destinationType){
        String key = redisKeyStoreDeliveryArea(storeId,destinationType);
        redisService.delete(key);
    }

    public void put(Long storeId,Integer destinationType,List<FreightTemplateDeliveryArea> deliveryAreas){
        String key = redisKeyStoreDeliveryArea(storeId,destinationType);
        boolean result = redisService.setObj(key,deliveryAreas,EXPIRE_TIME_SECONDS);
        if(!result){
            LOGGER.info("商家ID:{}业务:{}存储redis数据失败",storeId,destinationType);
        }
    }

    public List<FreightTemplateDeliveryArea> get(Long storeId,Integer destinationType){
        String key = redisKeyStoreDeliveryArea(storeId,destinationType);
        List<FreightTemplateDeliveryArea> redisList = redisService.getList(key,FreightTemplateDeliveryArea.class);
        return redisList;
    }
}