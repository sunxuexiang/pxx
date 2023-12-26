package com.wanmi.sbc.marketing.api.request.coupon;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: gaomuwei
 * @Date: Created In 1:23 PM 2018/9/28
 * @Description: 查询优惠券码列表请求
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CouponCodeQueryBindAccountRequest extends BaseQueryRequest  {


    private static final long serialVersionUID = 6459194608740111048L;

    @ApiModelProperty(value = "筛选条件")
    private CouponCodeQueryRequest couponCodeQueryRequest;

    @ApiModelProperty(value = "主账号id")
    private String parentId;

}
