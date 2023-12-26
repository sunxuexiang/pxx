package com.wanmi.sbc.setting.bean.vo;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;


@Data
public class PackingConfigVO implements Serializable{

    private static final long serialVersionUID = -3340163164542315510L;
 


    /**
     * 包装配置ID
     */
    @ApiModelProperty(value = "packing_id")
    private Long packingId;

    /**
     * 包装标志0满金额，1满数量
     */
    @ApiModelProperty(value = "packing_type")
    @NotNull
    private Integer packingType;

    /**
     * 如果是满金额存的就是金额字段，如果是满数量那就存的是数量
     */
    @ApiModelProperty(value = "packing_amount_num")
    @NotNull
    private BigDecimal packingAmountNum;

    /**
     * 金额
     */
    @ApiModelProperty(value = "packing_amount")
    @NotNull
    private BigDecimal packingAmount = BigDecimal.ZERO;

    /**
     * sku_id
     */
    @ApiModelProperty(value = "goods_info_id")
    @NotNull
    private String goodsInfoId ="9999999999999999999999999";

    /**
     * sku_id
     */
    @ApiModelProperty(value = "goods_info_name")
    @NotNull
    private String goodsInfoName="打包";


}
