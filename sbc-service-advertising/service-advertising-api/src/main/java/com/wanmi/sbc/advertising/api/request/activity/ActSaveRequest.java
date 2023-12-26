package com.wanmi.sbc.advertising.api.request.activity;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import com.wanmi.sbc.advertising.bean.dto.AdActivityDTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class ActSaveRequest{
	
	@ApiModelProperty("广告活动集合")
	@NotEmpty
	List<AdActivityDTO> acts;
}
