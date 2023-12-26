package com.wanmi.sbc.logisticscore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wanmi.sbc.logisticscore.dao.WareHouseMapper;
import com.wanmi.sbc.logisticscore.entity.WareHouse;
import com.wanmi.sbc.logisticscore.service.WareHouseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * @author lm
 * @date 2022/11/08 11:04
 */
@Service
@Slf4j
@Transactional
public class WareHouseServiceImpl extends ServiceImpl<WareHouseMapper,WareHouse> implements WareHouseService {

    @Autowired
    private WareHouseMapper wareHouseMapper;

    /**
     * 查询所有仓库
     *
     * @param wareHouseCodes
     * @return
     */
    @Override
    public List<WareHouse> findAllWareHouse(Set<String> wareHouseCodes) {
        return wareHouseMapper.findAllWareHouse(wareHouseCodes);
    }
}
