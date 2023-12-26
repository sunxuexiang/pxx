package com.wanmi.sbc.coupon.response;

import java.util.List;

import com.wanmi.sbc.marketing.bean.vo.CouponVO;
import com.wanmi.sbc.marketing.bean.vo.StoreCouponVO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CouponCacheCenterPageResponseNew {
	
    /**
     * 按店铺分组的优惠券集合
     */
    @ApiModelProperty(value = "按店铺分组的优惠券集合")
    private List<StoreCouponVO> storeCouponVOList;
    

}
