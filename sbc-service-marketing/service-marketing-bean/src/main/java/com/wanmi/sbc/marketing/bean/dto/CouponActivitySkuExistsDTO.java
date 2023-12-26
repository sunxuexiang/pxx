package com.wanmi.sbc.marketing.bean.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @description: 校验优惠券活动关联sku是否重复请求类
 * @author: XinJiang
 * @time: 2022/2/23 16:26
 */
@ApiModel
@Data
public class CouponActivitySkuExistsDTO implements Serializable {

    private static final long serialVersionUID = 1947944732385981328L;

    /**
     * skuId集合，逗号分割
     */
    @ApiModelProperty(value = "skuId集合，逗号分割")
    @NotNull
    private List<String> skuIds;

    /**
     * 优惠券活动id，修改时传递
     */
    @ApiModelProperty(value = "优惠券活动id，修改时传递")
    private String activityId;
}
