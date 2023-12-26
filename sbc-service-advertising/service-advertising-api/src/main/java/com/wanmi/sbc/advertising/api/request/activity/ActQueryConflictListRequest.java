package com.wanmi.sbc.advertising.api.request.activity;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.time.DateFormatUtils;

import com.wanmi.sbc.advertising.bean.constant.AdConstants;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActQueryConflictListRequest {

//	@ApiModelProperty("广告位类型")
//	@NotNull
//	private SlotType slotType;
//
//	@ApiModelProperty("批发市场id")
//	private Integer marketId;
//
//	@ApiModelProperty("商城id")
//	private Integer mallTabId;
//	
//	@ApiModelProperty("商品品类id")
//	private Integer goodsCateId;
//
//	@ApiModelProperty("广告位在同一组广告位中的序号")
//	@NotNull
//	private Integer slotGroupSeq;

	@ApiModelProperty("广告位id")
	@NotNull
	private Integer slotId;

	@ApiModelProperty("开始时间")
	@NotNull
	private Date startTime;

	@ApiModelProperty("结束时间")
	@NotNull
	private Date endTime;

	public String getLockKey() {
		StringBuilder sb = new StringBuilder();
		sb.append(slotId)
		.append(DateFormatUtils.format(startTime, AdConstants.DATE_TIME_PATTERN2))
		.append(DateFormatUtils.format(endTime, AdConstants.DATE_TIME_PATTERN2));
		return sb.toString();
	}

}
