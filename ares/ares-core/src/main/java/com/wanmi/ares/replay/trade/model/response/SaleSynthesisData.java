package com.wanmi.ares.replay.trade.model.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class SaleSynthesisData extends BaseData implements Serializable {

    @ApiModelProperty(value = "销售额")
    private BigDecimal salePrice;

    @ApiModelProperty(value = "下单用户数")
    private Integer userNum;

    @ApiModelProperty(value = "订单数")
    private Integer orderNum;

    @ApiModelProperty(value = "下单箱数")
    private Long orderItemNum;

}
