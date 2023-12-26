package com.wanmi.sbc.marketing.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @description: 优惠券活动关联商品VO
 * @author: XinJiang
 * @time: 2022/2/14 11:33
 */
@Data
@ApiModel
public class CouponActivityGoodsVO implements Serializable {

    private static final long serialVersionUID = -7614698438712687716L;

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private Long id;

    /**
     * 优惠券活动id
     */
    @ApiModelProperty(value = "优惠券活动id")
    private String activityId;

    /**
     * 商品信息skuid
     */
    @ApiModelProperty(value = "商品信息skuid")
    private String goodsInfoId;


}
