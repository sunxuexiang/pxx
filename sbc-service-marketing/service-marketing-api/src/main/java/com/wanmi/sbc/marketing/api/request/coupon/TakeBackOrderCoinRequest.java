package com.wanmi.sbc.marketing.api.request.coupon;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TakeBackOrderCoinRequest implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    @ApiModelProperty(value = "退单id")
	private String rid;
	
    @ApiModelProperty(value = "余额不足是否需要抛异常")
	private Boolean needThrowException;
	


}
