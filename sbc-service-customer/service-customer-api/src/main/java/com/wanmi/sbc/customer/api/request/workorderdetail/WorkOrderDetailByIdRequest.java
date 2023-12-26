package com.wanmi.sbc.customer.api.request.workorderdetail;

import com.wanmi.sbc.common.base.BaseRequest;
import lombok.*;
import javax.validation.constraints.NotNull;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 * <p>单个查询工单明细请求参数</p>
 * @author baijz
 * @date 2020-05-17 16:03:58
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrderDetailByIdRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 工单处理明细Id
	 */
	@ApiModelProperty(value = "工单处理明细Id")
	@NotNull
	private String workOrderDelId;

}