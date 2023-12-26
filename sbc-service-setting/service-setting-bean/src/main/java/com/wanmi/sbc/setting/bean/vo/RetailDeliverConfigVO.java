package com.wanmi.sbc.setting.bean.vo;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;


@Data
public class RetailDeliverConfigVO implements Serializable{


    private static final long serialVersionUID = 7321589493706570855L;
    /**
     * id
     */
    @ApiModelProperty(value = "retail_delivery_id")
    private Long retailDeliveryId;



    /**
     * 小于等于1千克多少元
     */
    @ApiModelProperty(value = "less_money")
    @NotNull
    private BigDecimal lessMoney;

    /**
     * 大于1千克
     */
    @ApiModelProperty(value = "greater_money")
    @NotNull
    private BigDecimal greaterMoney;



}
