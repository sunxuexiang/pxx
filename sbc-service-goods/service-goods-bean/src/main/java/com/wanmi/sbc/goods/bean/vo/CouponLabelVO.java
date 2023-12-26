package com.wanmi.sbc.goods.bean.vo;

import com.wanmi.sbc.common.enums.DefaultFlag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 营销标签
 * Created by hht on 2018/9/18.
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CouponLabelVO {

    /**
     * 优惠券Id
     */
    @ApiModelProperty(value = "优惠券Id")
    private String couponInfoId;

    /**
     * 优惠券活动Id
     */
    @ApiModelProperty(value = "优惠券活动Id")
    private String couponActivityId;

    /**
     * 促销描述
     */
    @ApiModelProperty(value = "促销描述")
    private String couponDesc;

    @ApiModelProperty(value = "优惠卷金额")
    private Double denomination;

    /**
     * 优惠券是否有剩余
     */
    @ApiModelProperty(value = "优惠券是否有剩余")
    private DefaultFlag hasLeft;

    /**
     * 领取状态
     */
    private Boolean hasFetched;

    /**
     * 优惠券类型 0通用券 1店铺券 2运费券
     */
    @ApiModelProperty(value = "优惠券类型 0通用券 1店铺券 2运费券")
    private Integer couponType;

    /**
     * 商户id
     */
    private Long storeId;

    /**
     * 是否平台优惠券 0平台 1店铺
     */
    private DefaultFlag platformFlag;

}
