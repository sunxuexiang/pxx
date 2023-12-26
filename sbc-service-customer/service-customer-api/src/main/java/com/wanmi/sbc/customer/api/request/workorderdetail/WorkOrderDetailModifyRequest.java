package com.wanmi.sbc.customer.api.request.workorderdetail;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import java.time.LocalDateTime;
import com.wanmi.sbc.common.base.BaseRequest;
import lombok.*;
import javax.validation.constraints.*;
import javax.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>工单明细修改参数</p>
 * @author baijz
 * @date 2020-05-17 16:03:58
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrderDetailModifyRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 工单处理明细Id
	 */
	@ApiModelProperty(value = "工单处理明细Id")
	@Length(max=32)
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
	@Max(127)
	private Integer status;

	/**
	 * 处理建议
	 */
	@ApiModelProperty(value = "处理建议")
	@Length(max=255)
	private String suggestion;

	/**
	 * 工单Id
	 */
	@ApiModelProperty(value = "工单Id")
	@Length(max=32)
	private String workOrderId;

}