package com.wanmi.sbc.wms.api.request.erp;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
 *
 * 推金蝶囤货销售订单表体
 * @author yitang
 * @version 1.0
 */
@Setter
@Getter
public class PileTradePushTableBodyRequest implements Serializable {

    private static final long serialVersionUID = -8976376156838349323L;
    /**
     * 物料编码
     */
    private Map fMaterialId;

    /**
     * 囤货数量
     */
    private Long fQty;

    /**
     * 单价
     */
    private BigDecimal fPrice;

    /**
     * 含税单价
     */
    private BigDecimal fTaxPrice;

    /**
     * 是否赠品
     */
    private Boolean fIsFree;

    /**
     * 电商物料编码
     */
    private String fDSBMText;
}
