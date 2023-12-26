package com.wanmi.sbc.marketing.request;

import com.wanmi.sbc.common.enums.BoolFlag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2018-11-30 14:03
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketingOneGoodsTerminationRequest implements Serializable {

    /**
     * 活动名称
     */
    @ApiModelProperty(value = "促销Id")
    private Long marketingId;

    @ApiModelProperty(value = "促销范围Id（skuId）")
    private String scopeId;

    @ApiModelProperty(value = "货品与促销规则表Id")
    private Long marketingScopeId;

    @ApiModelProperty(value = "'终止标志位：1：终止，0非终止',")
    private BoolFlag terminationFlag;


}
