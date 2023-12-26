package com.wanmi.sbc.advertising.bean.dto;

import com.wanmi.sbc.advertising.bean.enums.SlotSeqState;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SlotSeqDTO {

	/**
	 * 广告位在同一组广告位中的序号
	 */
    @ApiModelProperty("广告位在同一组广告位中的序号")
	private Integer slotGroupSeq;
    
	/**
	 * 序号状态
	 */
    @ApiModelProperty("序号状态")
	private SlotSeqState slotSeqState;
    
    @ApiModelProperty("广告位id")
	private Integer slotId;
    
    
}
