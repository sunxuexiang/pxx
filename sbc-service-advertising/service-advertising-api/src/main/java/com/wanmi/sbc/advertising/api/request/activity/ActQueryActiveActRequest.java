package com.wanmi.sbc.advertising.api.request.activity;

import javax.validation.constraints.NotNull;

import com.wanmi.sbc.advertising.bean.enums.SlotType;

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
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActQueryActiveActRequest{

	@ApiModelProperty("广告位类型")
	@NotNull
	private SlotType slotType;
	
    @ApiModelProperty("批发市场id，商城广告位需要")
	private Integer marketId;
    
    @ApiModelProperty("商城id，商城广告位需要")
	private Integer mallTabId;
    
	private Integer goodsCateId;

	@ApiModelProperty("广告位在同一组广告位中的序号，不传查整组广告位")
	private Integer slotGroupSeq;


}
