package com.wanmi.sbc.advertising.api.request.activity;

import javax.validation.constraints.NotBlank;

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
public class ActGetByIdRequest {
	/**
	 * 广告活动id
	 */
	@ApiModelProperty("广告活动id")
	@NotBlank
	private String id;

}
