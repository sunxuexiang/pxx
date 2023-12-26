package com.wanmi.sbc.customer.api.request.workorderdetail;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import java.time.LocalDateTime;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import lombok.*;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>工单明细通用查询请求参数</p>
 * @author baijz
 * @date 2020-05-17 16:03:58
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrderDetailQueryRequest extends BaseQueryRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 批量查询-工单处理明细IdList
	 */
	@ApiModelProperty(value = "批量查询-工单处理明细IdList")
	private List<String> workOrderDelIdList;

	/**
	 * 工单处理明细Id
	 */
	@ApiModelProperty(value = "工单处理明细Id")
	private String workOrderDelId;

	/**
	 * 搜索条件:处理时间开始
	 */
	@ApiModelProperty(value = "搜索条件:处理时间开始")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime dealTimeBegin;
	/**
	 * 搜索条件:处理时间截止
	 */
	@ApiModelProperty(value = "搜索条件:处理时间截止")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime dealTimeEnd;

	/**
	 * 处理状态
	 */
	@ApiModelProperty(value = "处理状态")
	private Integer status;

	/**
	 * 处理建议
	 */
	@ApiModelProperty(value = "处理建议")
	private String suggestion;

	/**
	 * 工单Id
	 */
	@ApiModelProperty(value = "工单Id")
	private String workOrderId;

}