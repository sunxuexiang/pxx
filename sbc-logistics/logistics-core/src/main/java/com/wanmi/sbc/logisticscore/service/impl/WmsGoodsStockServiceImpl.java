package com.wanmi.sbc.logisticscore.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wanmi.sbc.logisticscore.dao.WmsGoodsStockMapper;
import com.wanmi.sbc.logisticscore.entity.WmsGoodsStock;
import com.wanmi.sbc.logisticscore.service.WmsGoodsStockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author lm
 * @date 2022/11/08 11:17
 */
@Service
@Slf4j
@Transactional
public class WmsGoodsStockServiceImpl extends ServiceImpl<WmsGoodsStockMapper,WmsGoodsStock> implements WmsGoodsStockService {

    @Autowired
    private WmsGoodsStockMapper wmsGoodsStockMapper;


    @DS("oracle")
    @Override
    public List<WmsGoodsStock> findAllWmsStock(String warehouseCode) {
        List<WmsGoodsStock> allWmsStock = wmsGoodsStockMapper.findAllWmsStock(warehouseCode);
        return allWmsStock;
    }

    @DS("mysql")
    public Integer saveAllWmsStockToUs(List<WmsGoodsStock> allWmsStock){
        if(CollectionUtils.isEmpty(allWmsStock)){
            log.info("wms库存信息为空");
            return  0;
        }
        return wmsGoodsStockMapper.saveOrUpdateWmsStock(allWmsStock);
    }

}
