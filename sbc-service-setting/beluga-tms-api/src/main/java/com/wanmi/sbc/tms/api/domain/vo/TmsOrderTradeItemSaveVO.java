package com.wanmi.sbc.tms.api.domain.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-09-19 09:55
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TmsOrderTradeItemSaveVO implements Serializable {
    private static final long serialVersionUID = 1466616553963975952L;

    //   ("SpuId")
    private String spuId;

    //   ("SkuId")
    private String skuId;

    private String skuNo;

    //   ("货物名称")
    private String skuName;

    //   ("货物类型")
    private String goodsType;

    //   ("货物单位")
    private String unit;

    //   ("货品货值")
    private BigDecimal cargoValue;

    //   ("货品条码")
    private String cargoBarcode;

    //   ("货品数量")
    private Integer quantity;

    //   ("货品体积")
    private BigDecimal volume;

    //   ("货品重量")
    private BigDecimal weight;

    //   ("备注")
    private String remark;

    // 规格
    private String spec;
}
