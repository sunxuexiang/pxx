package com.wanmi.sbc.advertising.api.request.slot;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class SlotOptRequest {
	/**
	 * 广告位id
	 */
	@ApiModelProperty("广告位id")
    @NotNull
	private Integer id;
	
	/**
	 * 操作用户
	 */
	@ApiModelProperty("操作用户")
	private String optUser;

}
