package com.wanmi.sbc.tms.api.domain.vo;



import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 承运单的订单项表
 * </p>
 *
 * @author xyy
 * @since 2023-09-18
 */
@Getter
@Setter
public class TmsOrderTradeItemVO implements Serializable {

    private static final long serialVersionUID = 1L;

    //   ("id")
    private Long id;

    //   ("订单id")
    private String tradeOrderId;

    //   ("运单id")
    private String tmsOrderId;

    //   ("SpuId")
    private String spuId;

    //   ("SkuId")
    private String skuId;

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

    //   ("删除标志（0代表存在 1代表删除）")
    private Integer delFlag;


}
