package com.wanmi.sbc.setting.packingconfig.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.setting.api.constant.SettingErrorCode;

import com.wanmi.sbc.setting.packingconfig.model.root.PackingConfigItem;
import com.wanmi.sbc.setting.packingconfig.repository.PackingItemRepository;
import com.wanmi.sbc.setting.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service("PackingConfigItemService")
public class PackingConfigItemService {
    @Autowired
    private PackingItemRepository packingItemRepository;

    @Autowired
    private RedisService redisService;

    private static final String PACKING_CONFIG_ITEMS = "PACKING_CONFIG_ITEMS";

    /**
     * 新增PackingConfigItemService 和修改
     * @author lc
     */
    @Transactional
    public void save(PackingConfigItem entity) {
            if (Objects.isNull(entity)){
                throw new SbcRuntimeException(SettingErrorCode.ILLEGAL_REQUEST_ERROR);
            }
            entity = packingItemRepository.save(entity);
            //生成缓存
            fillRedis(entity);
    }

    public PackingConfigItem list() {
        if(redisService.hasKey(PACKING_CONFIG_ITEMS)) {
            return JSONObject.parseObject(redisService.getString(PACKING_CONFIG_ITEMS),PackingConfigItem.class);
        }
        List<PackingConfigItem> packingConfigItems = packingItemRepository.findAll();
        if (CollectionUtils.isNotEmpty(packingConfigItems)) {
            fillRedis(packingConfigItems.get(0));
        } else {
            throw new SbcRuntimeException(SettingErrorCode.ILLEGAL_REQUEST_ERROR);
        }
        return JSONObject.parseObject(redisService.getString(PACKING_CONFIG_ITEMS),PackingConfigItem.class);
    }


    /**
     * 生成redis缓存
     * @param packingConfigItem
     */
    public void fillRedis(PackingConfigItem packingConfigItem) {
        redisService.setString(PACKING_CONFIG_ITEMS, JSON.toJSONString(packingConfigItem, SerializerFeature.DisableCircularReferenceDetect));
    }

}
