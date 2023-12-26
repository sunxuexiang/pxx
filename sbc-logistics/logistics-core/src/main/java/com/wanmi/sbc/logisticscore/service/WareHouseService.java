package com.wanmi.sbc.logisticscore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wanmi.sbc.logisticscore.entity.WareHouse;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author lm
 * @date 2022/11/08 11:04
 */
public interface WareHouseService extends IService<WareHouse> {

    /**
     * 查询所有仓库ID
     * @return
     */
    List<WareHouse> findAllWareHouse(@Param("wareHouseCodes") Set<String> wareHouseCodes);
}
