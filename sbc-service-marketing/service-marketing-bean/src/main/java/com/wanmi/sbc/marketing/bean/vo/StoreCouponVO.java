package com.wanmi.sbc.marketing.bean.vo;

import java.util.List;

import com.wanmi.sbc.common.enums.CompanyType;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoreCouponVO {
    /**
     * 优惠券集合
     */
    @ApiModelProperty(value = "优惠券集合")
	private  List<CouponVO> couponVOs;
    
    /**
     * 店铺id
     */
    @ApiModelProperty(value = "店铺id")
	private Long storeId;
    
    /**
     * 店铺名称
     */
    @ApiModelProperty(value = "店铺名称")
	private String storeName;
    
    /**
     * 商家类型
     */
    @ApiModelProperty(value = "商家类型")
    private CompanyType companyType;

}
