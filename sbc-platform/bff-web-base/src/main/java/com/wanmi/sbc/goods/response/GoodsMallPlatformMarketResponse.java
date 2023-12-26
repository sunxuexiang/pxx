package com.wanmi.sbc.goods.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @program: sbc-backgroud
 * @description: 批发市场列表
 * @author: gdq
 * @create: 2023-06-16 08:57
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "批发市场列表")
public class GoodsMallPlatformMarketResponse implements Serializable {

    private static final long serialVersionUID = 4239429839327965050L;

    @ApiModelProperty(value = "市场Id")
    private Long marketId;

    @ApiModelProperty(value = "市场名称")
    private String marketName;

    private BigDecimal sort;

    /**
    * 是否默认市场
    */
    private Boolean isDefault;

}
