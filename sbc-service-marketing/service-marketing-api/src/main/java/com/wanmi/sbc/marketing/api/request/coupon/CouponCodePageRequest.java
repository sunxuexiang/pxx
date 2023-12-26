package com.wanmi.sbc.marketing.api.request.coupon;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.marketing.bean.enums.CouponActivityType;
import com.wanmi.sbc.marketing.bean.enums.CouponType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 分页查询优惠券列表请求结构
 * @author CHENLI
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
public class CouponCodePageRequest extends BaseQueryRequest {

    private static final long serialVersionUID = 2389168657805595252L;

    /**
     *  领取人id,同时表示领取状态
     */
    @ApiModelProperty(value = "领取人id")
    private String customerId;

    /**
     *  使用状态,0(未使用)，1(使用)，2(已过期)
     */
    @ApiModelProperty(value = "优惠券使用状态，0(未使用)，1(使用)，2(已过期)")
    private int useStatus;

    /**
     * 优惠券类型 0通用券 1店铺券 2运费券
     */
    @ApiModelProperty(value = "优惠券类型")
    private CouponType couponType;

    @ApiModelProperty(value = "店铺Id", hidden = true)
    private Long storeId;

    /**
     * 用户账号
     */
    @ApiModelProperty(value = "用户账号")
    private String customerAccount;

    /**
     * 活动类型
     */
    @ApiModelProperty(value = "活动类型")
    private CouponActivityType activityType;

    @ApiModelProperty("优惠券所属仓集合")
    private List<Long> wareIds;
}
