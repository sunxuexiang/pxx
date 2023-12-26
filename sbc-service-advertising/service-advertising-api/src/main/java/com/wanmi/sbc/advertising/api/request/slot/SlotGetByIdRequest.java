package com.wanmi.sbc.advertising.api.request.slot;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zc
 *
 */
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SlotGetByIdRequest {

	/**
	 * 广告位id
	 */
	@ApiModelProperty("广告位id")
    @NotNull
	private Integer id;

}
