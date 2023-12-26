package com.wanmi.sbc.advertising.bean.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.wanmi.sbc.advertising.bean.enums.SlotSeqState;
import com.wanmi.sbc.advertising.bean.enums.UnitType;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class AdSlotPriceDTO {

	@ApiModelProperty("id")
	private Integer id;

	/**
	 * 广告位id
	 */
	@ApiModelProperty("广告位id")
	private Integer slotId;

	/**
	 * 单位价格
	 */
	@ApiModelProperty("单位价格")
	private BigDecimal unitPrice;

	/**
	 * 单位
	 */
	@ApiModelProperty("单位")
	private UnitType unit;

	/**
	 * 日期
	 */
	@ApiModelProperty("生效日期")
	private Date effectiveDate;

	/**
	 * 状态
	 */
	@ApiModelProperty("状态")
	private SlotSeqState state;
	
	private String actId;

	private Date createTime;

}
