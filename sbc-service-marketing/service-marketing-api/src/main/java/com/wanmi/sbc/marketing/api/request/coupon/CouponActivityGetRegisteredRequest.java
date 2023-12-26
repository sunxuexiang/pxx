package com.wanmi.sbc.marketing.api.request.coupon;

import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.marketing.bean.enums.CouponActivityType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 注册类型优惠券活动请求类
 * @author: XinJiang
 * @time: 2021/11/6 15:55
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CouponActivityGetRegisteredRequest extends BaseRequest {

    private static final long serialVersionUID = -1562655682651148156L;

    /**
     * 活动类型
     */
    @ApiModelProperty(value = "活动类型")
    private CouponActivityType activityType;
}
