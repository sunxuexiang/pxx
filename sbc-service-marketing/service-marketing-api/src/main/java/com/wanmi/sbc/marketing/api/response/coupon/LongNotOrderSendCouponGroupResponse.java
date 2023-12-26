package com.wanmi.sbc.marketing.api.response.coupon;

import com.wanmi.sbc.marketing.bean.vo.CouponCodeVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 久未下单赠券返回值
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LongNotOrderSendCouponGroupResponse implements Serializable {

    private static final long serialVersionUID = 9026873765833658626L;

    /**
     * 标题
     */
    @ApiModelProperty(value = "标题")
    private String title;

    /**
     * 优惠券列表
     */
    @ApiModelProperty(value = "优惠券列表")
    private List<GetCouponGroupResponse> couponList;
}
