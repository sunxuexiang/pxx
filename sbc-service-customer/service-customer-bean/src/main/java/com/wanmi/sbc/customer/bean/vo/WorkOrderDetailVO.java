package com.wanmi.sbc.customer.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import java.time.LocalDateTime;
import lombok.Data;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>工单明细VO</p>
 * @author baijz
 * @date 2020-05-17 16:03:58
 */
@ApiModel
@Data
public class WorkOrderDetailVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 工单处理明细Id
	 */
	@ApiModelProperty(value = "工单处理明细Id")
	private String workOrderDelId;

	/**
	 * 处理时间
	 */
	@ApiModelProperty(value = "处理时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime dealTime;

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