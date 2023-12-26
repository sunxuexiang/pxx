package com.wanmi.sbc.wms.api.request.erp;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 推金蝶退货表体
 *
 * @author yitang
 * @version 1.0
 */
@Setter
@Getter
public class PileTradePushReturnGoodsTableBodyRequest implements Serializable {
    private static final long serialVersionUID = 8461585104970003372L;

    /**
     * 物料编码
     */
    private Map fMaterialId;

    /**
     * 退囤货数量
     */
    private Long fQty;

    /**
     * 含税单价
     */
    private BigDecimal fTaxPrice;

    /**
     * 仓库
     */
    private Map fStockId;

    /**
     * 批号
     */
    private Map fLot;

    /**
     * 有效期至
     */
    private String fExpiryDate;
}
