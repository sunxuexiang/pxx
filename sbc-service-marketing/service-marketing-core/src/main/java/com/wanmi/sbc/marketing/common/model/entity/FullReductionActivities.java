package com.wanmi.sbc.marketing.common.model.entity;

import com.wanmi.sbc.common.enums.BoolFlag;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 满减活动商品
 *
 * @author yitang
 * @version 1.0
 */
@Setter
@Getter
public class FullReductionActivities implements Serializable {
    private static final long serialVersionUID = 4644577091281903577L;

    /**
     * 商品编号
     */
    private String skuIds;

    /**
     * 是否为必选商品  0：非必选  1：必选
     */
    @ApiModelProperty(value = "是否为必选商品")
    private BoolFlag whetherChoice;

    /**
     * 限购数量
     */
    @ApiModelProperty(value = "限购数量")
    private Long purchaseNum;
}
