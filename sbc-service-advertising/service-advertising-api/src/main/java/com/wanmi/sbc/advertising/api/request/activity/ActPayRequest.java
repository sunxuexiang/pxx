package com.wanmi.sbc.advertising.api.request.activity;

import javax.validation.constraints.NotBlank;

import com.wanmi.sbc.advertising.bean.enums.PayType;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActPayRequest {
	
	/**
	 * 广告活动id
	 */
	@ApiModelProperty("广告活动id")
	@NotBlank
	private String id;
	
	/**
	 * 支付类型
	 */
	@ApiModelProperty("支付类型")
	private PayType payType;

}
