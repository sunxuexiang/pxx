package com.wanmi.sbc.wms.erp.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 推金蝶囤货表体销售订单
 *
 * @author yitang
 * @version 1.0
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PileTradePushtableBodyModel implements Serializable {
    private static final long serialVersionUID = -5664576701412113L;

    /**
     * 物料编码
     */
    @JSONField(name = "FMaterialId",ordinal = 1)
    private Map fMaterialId;

    /**
     * 囤货数量
     */
    @JSONField(name = "FQty",ordinal = 2)
    private Long fQty;

    /**
     * 单价
     */
    @JSONField(name = "FPrice",ordinal = 3)
    private BigDecimal fPrice;

    /**
     * 含税单价
     */
    @JSONField(name = "FTaxPrice",ordinal = 4)
    private BigDecimal fTaxPrice;

    /**
     * 是否赠品
     */
    @JSONField(name = "FIsFree",ordinal = 5)
    private Boolean fIsFree;

    /**
     * 电商物料编码
     */
    @JSONField(name = "FDSBMText",ordinal = 6)
    private String fDSBMText;
}
