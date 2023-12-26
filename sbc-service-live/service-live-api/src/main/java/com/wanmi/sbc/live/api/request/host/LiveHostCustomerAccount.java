package com.wanmi.sbc.live.api.request.host;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LiveHostCustomerAccount {

//	private Integer hostId;

	@ApiModelProperty(value = "直播账号id")
	private String customerId;

	@ApiModelProperty(value = "直播账号")
	private String customerAccount;
}