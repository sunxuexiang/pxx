package com.wanmi.sbc.returnorder.trade.model.entity.value;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 描述
 *
 * @author yitang
 * @version 1.0
 * @date 2021/07/29 16:22:24
 */
@Getter
@Setter
public class FSaleOrderEntry implements Serializable {
    private static final long serialVersionUID = -4141479713309694024L;

    /**
     * 物料编码
     */
    @JSONField(name = "FMaterialId",ordinal = 0)
    private Map FMaterialId;
    /**
     * 是否赠品
     */
    @JSONField(name = "FIsFree",ordinal = 1)
    private String FIsFree;
    /**
     * 销售数量
     */
    @JSONField(name = "FQty",ordinal = 2)
    private BigDecimal FQty;

    /**
     * 单价
     */
    @JSONField(name = "FPrice",ordinal = 3)
    private BigDecimal FPrice;

    /**
     * 含税单价
     */
    @JSONField(name = "FTaxPrice",ordinal = 4)
    private BigDecimal FTaxPrice;



//    /**
//     * 税率
//     */
//    @JSONField(name = "FEntryTaxRate")
//    private BigDecimal FEntryTaxRate;

    /**
     * 打包费
     */
    @JSONField(name = "F_PackPrice",ordinal = 5)
    private BigDecimal FPackPrice;

    /**
     * 金额
     */
    @JSONField(name = "FAmount",ordinal = 6)
    private BigDecimal FAmount;

    /**
     * 价税合计
     */
    @JSONField(name = "FAllAmount",ordinal = 7)
    private BigDecimal FAllAmount;

    /**
     * 电商商品编号
     */
    @JSONField(name = "F_ora_Text",ordinal = 8)
    private String fOraText;

    /**
     * 囤货单号
     */
    @JSONField(name = "F_According",ordinal = 9)
    private String f_According;

    @JSONField(name = "F_ora_SPZJ",ordinal = 10)
    private String f_ora_SPZJ;
}
