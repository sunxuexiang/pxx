package com.wanmi.sbc.marketing.api.request.market;

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
 * @Date: 2018-11-16 16:39
 */
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TerminationMarketingScopeRequest implements Serializable {

    /**
     * 活动名称
     */
    @ApiModelProperty(value = "促销Id")
    private Long marketingId;

    @ApiModelProperty(value = "促销范围Id（skuId）")
    private String scopeId;

    @ApiModelProperty(value = "'终止标志位：1：终止，0非终止")
    private BoolFlag terminationFlag =BoolFlag.YES;

    @ApiModelProperty(value = "货品与促销规则表Id")
    private Long marketingScopeId;

}
