package com.wanmi.sbc.setting.retaildeliveryconfig.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.setting.api.constant.SettingErrorCode;

import com.wanmi.sbc.setting.redis.RedisService;
import com.wanmi.sbc.setting.retaildeliveryconfig.model.root.RetailDeliveryConfigItem;
import com.wanmi.sbc.setting.retaildeliveryconfig.repository.RetailDeliveryConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service("RetailDeliverConifgService")
public class RetailDeliverConifgService {
    @Autowired
    private RetailDeliveryConfigRepository retailDeliveryConfigRepository;

    @Autowired
    private RedisService redisService;

    private static final String RETAIL_DELIVERY_CONFIG_ITEMS = "RETAIL_DELIVERY_CONFIG_ITEM";

    /**
     * 新增PackingConfigItemService 和修改
     * @author lc
     */
    @Transactional
    public void save( RetailDeliveryConfigItem entity) {
            if (Objects.isNull(entity)){
                throw new SbcRuntimeException(SettingErrorCode.ILLEGAL_REQUEST_ERROR);
            }
            entity = retailDeliveryConfigRepository.save(entity);
            //生成缓存
            fillRedis(entity);
    }

    public RetailDeliveryConfigItem list() {
        if(redisService.hasKey(RETAIL_DELIVERY_CONFIG_ITEMS)) {
            return JSONObject.parseObject(redisService.getString(RETAIL_DELIVERY_CONFIG_ITEMS),RetailDeliveryConfigItem.class);
        }
        List<RetailDeliveryConfigItem> packingConfigItems = retailDeliveryConfigRepository.findAll();
        if (CollectionUtils.isNotEmpty(packingConfigItems)) {
            fillRedis(packingConfigItems.get(0));
        } else {
            throw new SbcRuntimeException(SettingErrorCode.ILLEGAL_REQUEST_ERROR);
        }
        return JSONObject.parseObject(redisService.getString(RETAIL_DELIVERY_CONFIG_ITEMS),RetailDeliveryConfigItem.class);
    }


    /**
     * 生成redis缓存
     */
    public void fillRedis(RetailDeliveryConfigItem retailDeliveryConfigItem) {
        redisService.setString(RETAIL_DELIVERY_CONFIG_ITEMS, JSON.toJSONString(retailDeliveryConfigItem, SerializerFeature.DisableCircularReferenceDetect));
    }

}
