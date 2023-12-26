package com.wanmi.sbc.advertising.api.request.activity;

import java.util.Date;

import javax.validation.constraints.NotNull;

import com.wanmi.sbc.advertising.bean.enums.ActivityState;
import com.wanmi.sbc.advertising.bean.enums.SlotType;
import com.wanmi.sbc.common.base.BaseQueryRequest;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author zc
 *
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActQueryListPageRequest extends BaseQueryRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 广告位类型
	 */
	@ApiModelProperty("广告位类型")
	@NotNull
	private SlotType slotType;

	/**
	 * 广告活动状态
	 */
	@ApiModelProperty("广告活动状态")
	private ActivityState activityState;

	@ApiModelProperty("提交审核时间开始")
	private Date submitTime1;

	@ApiModelProperty("提交审核时间结束")
	private Date submitTime2;

	@ApiModelProperty("生效时间开始")
	private Date startTime1;

	@ApiModelProperty("生效时间结束")
	private Date startTime2;
	
	@ApiModelProperty("所属商家id")
	private Long storeId;
	
	@ApiModelProperty("审核时间开始")
	private Date auditTime1;

	@ApiModelProperty("审核时间结束")
	private Date auditTime2;
	
	@ApiModelProperty("提交时间开始")
	private Date createTime1;

	@ApiModelProperty("提交时间结束")
	private Date createTime2;
	
	
	
	

}
