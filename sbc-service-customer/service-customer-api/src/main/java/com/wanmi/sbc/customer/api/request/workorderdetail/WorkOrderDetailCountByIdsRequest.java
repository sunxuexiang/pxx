package com.wanmi.sbc.customer.api.request.workorderdetail;

import com.wanmi.sbc.customer.api.request.CustomerBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

/**
 * <p>批量查询工单详情</p>
 * @author baijz
 * @date 2020-05-17 16:03:58
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrderDetailCountByIdsRequest extends CustomerBaseRequest {

	private static final long serialVersionUID = -3376917799775199712L;
	/**
	 * 批量删除-工单处理明细IdList
	 */
	@ApiModelProperty(value = "批量查询工单详情")
	@NotEmpty
	private List<String> worOrderIds;
}
