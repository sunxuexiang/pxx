package com.wanmi.sbc.logisticscore.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wanmi.sbc.logisticscore.entity.WareHouse;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author lm
 * @date 2022/11/08 11:04
 */
public interface WareHouseMapper extends BaseMapper<WareHouse> {

    /**
     * 查询所有仓库ID
     * @return
     */
    List<WareHouse> findAllWareHouse(@Param("wareHouseCodes") Set<String> wareHouseCodes);
}
