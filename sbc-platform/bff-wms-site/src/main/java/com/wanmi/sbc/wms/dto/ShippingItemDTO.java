package com.wanmi.sbc.wms.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @program: open
 * @description:
 * @author: Mr.Tian
 * @create: 2020-05-15 11:34
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class ShippingItemDTO implements Serializable {

    @ApiModelProperty(value = "系统发货数量")
    @Min(1L)
    private Long itemNum;

    @ApiModelProperty(value = "WMS发货的数量")
    private BigDecimal deliveryNum;

    @ApiModelProperty(value = "货品的编码")
    private String goodsInfoNo;

    @ApiModelProperty(value = "拆箱id")
    private Long devanningId;

    @ApiModelProperty(value = "批次号")
    private String goodsBatchNo;
}
