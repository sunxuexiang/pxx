package com.wanmi.sbc.coupon.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel
@Data
public class CouponInfoQueryRequest {

    /**
     * 分页条数
     */
    @ApiModelProperty(value = "分页条数")
    private List<String> goodsInfoIds;

}
