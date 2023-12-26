package com.wanmi.sbc.customer.api.request.workorderdetail;

import com.wanmi.sbc.common.base.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;

/**
 * <p>工单明细新增参数</p>
 * @author baijz
 * @date 2020-05-17 16:03:58
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrderDetailAddRequest extends BaseRequest {


	private static final long serialVersionUID = 4053882659439138193L;
	/**
	 * 处理状态
	 */
	@ApiModelProperty(value = "处理状态0:待处理，1：已完成")
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