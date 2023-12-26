package com.wanmi.sbc.advertising.api.request.activity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class ActAuditRequest {

	@ApiModelProperty("活动id")
	@NotBlank
	private String actId;

	@ApiModelProperty("审批通过")
	@NotNull
	private Boolean pass;

	@ApiModelProperty("审批意见")
	private String auditComment;

}
