package com.wanmi.sbc.order.api.response.trade;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author lm
 * @date 2022/10/12 16:39
 */
@Data
public class NewPileTradeNoPileExcel {

    @ApiModelProperty(value = "erp编码")
    private String erpGoodsInfoNo;

    @ApiModelProperty(value = "商品名称")
    private String skuName;

    @ApiModelProperty(value = "商品SKU编码")
    private String skuNo;

    @ApiModelProperty(value = "仓库ID")
    private Long wareId;

    @ApiModelProperty(value = "囤货未提数量")
    private Long noPickNum;

    @ApiModelProperty(value = "囤货未提金额（实际付款）")
    private BigDecimal actualPrice;

    @ApiModelProperty(value = "囤货未提仓库")
    private String wareName;

    @ApiModelProperty(value = "商品大类")
    private String productCate;
}
