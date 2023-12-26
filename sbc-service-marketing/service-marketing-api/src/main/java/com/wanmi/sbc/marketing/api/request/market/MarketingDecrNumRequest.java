package com.wanmi.sbc.marketing.api.request.market;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2018-11-19 14:43
 */
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MarketingDecrNumRequest{


    private static final long serialVersionUID = -6765355492524287047L;

    @ApiModelProperty(value = "marketingId")
    private String marketingId;
    @ApiModelProperty(value = "levelId")
    private String levelId;
    @ApiModelProperty(value = "goodsInfoId")
    private String goodsInfoId;
    @ApiModelProperty(value = "num")
    private Long num;
}
