package com.wanmi.sbc.marketing.api.request.market;

import com.wanmi.sbc.common.enums.BoolFlag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivitGoodsRequest implements Serializable {
    /**
     * 促销范围Id
     */
    @ApiModelProperty(value = "促销范围Id")
    private String scopeId;

    /**
     * 是否终止
     */
    @ApiModelProperty(value = "终止标志位：1：终止，0非终止")
    private BoolFlag terminationFlag=BoolFlag.NO;

    /**
     * 是否为必选商品  0：非必选  1：必选
     */
    @ApiModelProperty(value = "是否为必选商品：0：非必选  1：必选")
    private BoolFlag whetherChoice;

    /**
     * 限购数量
     */
    @ApiModelProperty(value = "限购数量")
    private Long purchaseNum;
}
