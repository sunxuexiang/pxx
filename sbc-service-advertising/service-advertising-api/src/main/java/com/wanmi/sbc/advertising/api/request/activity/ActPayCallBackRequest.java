package com.wanmi.sbc.advertising.api.request.activity;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActPayCallBackRequest {
	
	private String adActId;
	
	private String bizId;
	
	private boolean result;
	
	private String msg;

}
