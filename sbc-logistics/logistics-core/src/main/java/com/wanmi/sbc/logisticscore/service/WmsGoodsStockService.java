package com.wanmi.sbc.logisticscore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wanmi.sbc.logisticscore.entity.WmsGoodsStock;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author lm
 * @date 2022/11/08 11:17
 */
public interface WmsGoodsStockService extends IService<WmsGoodsStock> {

    /**
     * 更新或保存wms库存信息
     * @param allWmsStock
     * @return
     */
    Integer saveAllWmsStockToUs(@Param("allWmsStock") List<WmsGoodsStock> allWmsStock);


    /**
     * 查询所有wms库存
     * @return
     */
    List<WmsGoodsStock> findAllWmsStock(@Param("wareHouseCode") String wareHouseCode);
}
