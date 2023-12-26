package com.wanmi.sbc.advertising.api.response;

import java.util.List;

import com.wanmi.sbc.advertising.bean.dto.AdActivityDTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryActiveActResp {
	
    @ApiModelProperty(value = "运行中的广告列表")
	List<AdActivityDTO> activeActs;
	
}
