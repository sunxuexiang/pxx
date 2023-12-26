package com.wanmi.sbc.order.api.response.trade;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author lm
 * @date 2022/10/12 16:39
 */
@Data
public class NewPileTradeTotalPileExcel {

    @ApiModelProperty(value = "erp编码")
    private String erpGoodsInfoNo;

    @ApiModelProperty(value = "商品名称")
    private String skuName;

    @ApiModelProperty(value = "商品SKU编码")
    private String skuNo;

    @ApiModelProperty(value = "仓库ID")
    private Long wareId;

    @ApiModelProperty(value = "囤货数量（包含已提货数量）")
    private Long totalNum;

    @ApiModelProperty(value = "囤货单金额(实际付款金额）")
    private BigDecimal actualPrice;

    @ApiModelProperty(value = "囤货仓库")
    private String wareName;

    @ApiModelProperty(value = "商品大类")
    private String productCate;
}
