package com.wanmi.sbc.wms.erp.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.*;

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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PilePushKingdeeReturnGoodsTableBodyModel implements Serializable {
    private static final long serialVersionUID = -3477521216691242490L;

    /**
     * 物料编码
     */
    @JSONField(name = "FMaterialId",ordinal = 1)
    private Map fMaterialId;

    /**
     * 退囤货数量
     */
    @JSONField(name = "FQty",ordinal = 2)
    private Long fQty;

    /**
     * 含税单价
     */
    @JSONField(name = "FTaxPrice",ordinal = 3)
    private BigDecimal fTaxPrice;

    /**
     * 仓库
     */
    @JSONField(name = "FStockId",ordinal = 4)
    private Map fStockId;

    /**
     * 批号
     */
    @JSONField(name = "FLot",ordinal = 5)
    private Map fLot;
}
