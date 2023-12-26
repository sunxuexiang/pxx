package com.wanmi.ares.replay.trade.model.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;


/**
 * @author Administrator
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class SaleSynthesisNewData implements Serializable {

    private static final long serialVersionUID = 4483705671530457555L;

    @ApiModelProperty(value = "省份ID")
    private String provinceId;

    @ApiModelProperty(value = "省份名称")
    private String provinceName;

    @ApiModelProperty(value = "销售额")
    private BigDecimal salePrice;

    @ApiModelProperty(value = "下单用户数")
    private Integer userNum;

    @ApiModelProperty(value = "订单数")
    private Integer orderNum;

    @ApiModelProperty(value = "下单箱数")
    private Long orderItemNum;

}
