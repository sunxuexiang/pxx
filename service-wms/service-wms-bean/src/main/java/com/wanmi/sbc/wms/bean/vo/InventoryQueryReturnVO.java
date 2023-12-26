package com.wanmi.sbc.wms.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @ClassName: InventoryQueryResponse
 * @Description: TODO
 * @Author: yxb
 * @Date: 2020/5/7 17:43
 * @Version: 1.0
 */
@Data
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class InventoryQueryReturnVO implements Serializable {

    private static final long serialVersionUID = 598617373441322708L;

    @ApiModelProperty(value = "仓库ID")
    private String warehouseId;
    @ApiModelProperty(value = "货主ID")
    private String customerId;
    @ApiModelProperty(value = "产品")
    private String sku;
    @ApiModelProperty(value = "数量")
    private BigDecimal qty;
    @ApiModelProperty(value = "数量")
    private BigDecimal stockNum;
    @ApiModelProperty(value = "预留字段")
    private String lotatt01;
    @ApiModelProperty(value = "预留字段")
    private String lotatt02;
    @ApiModelProperty(value = "预留字段")
    private String lotatt03;
    @ApiModelProperty(value = "预留字段")
    private String lotatt04;
    @ApiModelProperty(value = "商品的编码")
    private String userDefine5;

}
