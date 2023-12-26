package com.wanmi.sbc.marketing.request;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.marketing.bean.enums.MarketingStatus;
import com.wanmi.sbc.marketing.bean.enums.MarketingSubType;

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
public class MarketingPageListRequest extends BaseQueryRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 店铺id
	 */
	@ApiModelProperty(value = "店铺id")
	private Long storeId;

	/**
	 * 活动名称
	 */
	@ApiModelProperty(value = "活动名称")
	private String marketingName;

	/**
	 * 活动类型
	 */
	@ApiModelProperty(value = "活动类型")
	private MarketingSubType marketingSubType;

	/**
	 * 开始时间
	 */
	@ApiModelProperty(value = "开始时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime startTime;

	/**
	 * 结束时间
	 */
	@ApiModelProperty(value = "结束时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime endTime;

	/**
	 * 目标客户
	 */
	@ApiModelProperty(value = "目标客户")
	private Long targetLevelId;

	/**
	 * 查询类型，0：全部，1：进行中，2：暂停中，3：未开始，4：已结束，5：进行中&未开始，6：终止，7：草稿
	 */
	@ApiModelProperty(value = "查询类型")
	private MarketingStatus queryTab;

	/**
	 * 删除标记 0：正常，1：删除
	 */
	@ApiModelProperty(value = "删除标记")
	private DeleteFlag delFlag;

}
