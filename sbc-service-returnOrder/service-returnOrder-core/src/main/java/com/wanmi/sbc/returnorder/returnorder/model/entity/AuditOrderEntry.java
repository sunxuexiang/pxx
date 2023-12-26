package com.wanmi.sbc.returnorder.returnorder.model.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
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
 * @date 2021/07/29 19:28:10
 */
@Data
@Setter
@Getter
public class AuditOrderEntry implements Serializable {
    private static final long serialVersionUID = 6147227026260517107L;

    /**
     * 物料编码
     */
    @JSONField(name = "FMaterialId",ordinal = 1)
    private Map FMaterialId;

    /**
     * 销售数量
     */
    @JSONField(name = "FQty",ordinal = 2)
    private BigDecimal FRealQty;

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

    /**
     * 税率
     */
    @JSONField(name = "FEntryTaxRat",ordinal = 5)
    private BigDecimal FEntryTaxRate;

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
     * 仓库
     */
    @JSONField(name = "FStockId",ordinal = 8)
    private Map FStockId;

    /**
     * 批号
     */
    @JSONField(name = "FLot",ordinal = 9)
    private Map FLot;

    /**
     * 生产日期
     */
    @JSONField(name = "FProduceDate",ordinal = 10)
    private String FProduceDate;

    /**
     * 有效期至
     */
    @JSONField(name = "FExpiryDate",ordinal = 11)
    private String FExpiryDate;

    /**
     * 囤货单号
     */
    @JSONField(name = "F_According",ordinal = 12)
    private String f_According;

    /**
     * 是否赠品
     */
    @JSONField(name = "FIsFree", ordinal = 13)
    private Boolean fIsFree;

    /**
     * 拆箱商品主键
     */
    @JSONField(name = "FSkucxId",ordinal = 14)
    private String FSkucxId;
}
