package com.wanmi.sbc.order.redisservice;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.WareHouseType;
import com.wanmi.sbc.goods.api.constant.WareHouseConstants;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseQueryProvider;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseListRequest;
import com.wanmi.sbc.goods.bean.vo.WareHouseVO;
import com.wanmi.sbc.order.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: WareHouseService
 * @Description: TODO
 * @Date: 2020/9/2 21:45
 * @Version: 1.0
 */
@Slf4j
@Service
public class WareHouseRedisService {
    @Autowired
    private RedisService redisService;

    @Autowired
    private WareHouseQueryProvider wareHouseQueryProvider;

    /**
     * 获取所有可用的分仓信息
     * @return
     */
    public List<WareHouseVO> queryWareHouses(List<Long> storeIdList, WareHouseType wareHouseType){
        String wareHousesStr = redisService.hget(WareHouseConstants.WARE_HOUSES,WareHouseConstants.WARE_HOUSE_MAIN_FILED);
        if(StringUtils.isNotEmpty(wareHousesStr)){
            List<WareHouseVO> wareHouseVOS = JSON.parseArray(wareHousesStr, WareHouseVO.class);
            return wareHouseVOS.stream().filter(param -> storeIdList.contains(param.getStoreId())
                    &&param.getDelFlag().equals(DeleteFlag.NO)&&wareHouseType.equals(param.getWareHouseType())).collect(Collectors.toList());
        }
        return wareHouseQueryProvider.list(WareHouseListRequest.builder()
                .delFlag(DeleteFlag.NO)
                .wareHouseType(wareHouseType)
                .storeIdList(storeIdList)
                .build()).getContext().getWareHouseVOList();
    }
}
