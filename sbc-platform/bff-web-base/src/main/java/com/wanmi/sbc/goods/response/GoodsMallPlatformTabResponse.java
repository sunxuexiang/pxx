package com.wanmi.sbc.goods.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.annotation.Nonnegative;
import java.io.Serializable;

/**
 * @program: sbc-backgroud
 * @description: 商家市场列表
 * @author: gdq
 * @create: 2023-06-16 08:57
 **/
@Data
@Nonnegative
@AllArgsConstructor
@Builder
@ApiModel(value = "商家市场列表")
public class GoodsMallPlatformTabResponse implements Serializable {

    private static final long serialVersionUID = 4239429839327965050L;

    @ApiModelProperty(value = "市场Id")
    private Long marketId;

    @ApiModelProperty(value = "市场名称")
    private String marketName;
}
