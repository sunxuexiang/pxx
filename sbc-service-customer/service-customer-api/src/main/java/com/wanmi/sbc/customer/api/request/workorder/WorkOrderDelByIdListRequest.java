package com.wanmi.sbc.customer.api.request.workorder;

import com.wanmi.sbc.customer.api.request.CustomerBaseRequest;
import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>批量删除工单请求参数</p>
 * @author baijz
 * @date 2020-05-17 16:03:15
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrderDelByIdListRequest extends CustomerBaseRequest {
private static final long serialVersionUID = 1L;

	/**
	 * 批量删除-工单IdList
	 */
	@ApiModelProperty(value = "批量删除-工单IdList")
	@NotEmpty
	private List<String> workOrderIdList;
}
