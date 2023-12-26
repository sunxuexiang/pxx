package com.wanmi.sbc.logisticscore.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author lm
 * @date 2022/11/03 10:33
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WmsGoodsStock {

    /**
     * 仓库id
     */
    private String warehouseId;

    /**
     * skuId
     */
    private String sku;

    /**
     * 可用库存数量
     */
    private BigDecimal qty;

    /**
     * lotatt01 批次号
     */
    private String batchNo;

    /**
     * lotatt04 erp仓库ID
     */
    private String erpWareId;

    /**
     * 库存
     */
    private BigDecimal stockNum;
}
