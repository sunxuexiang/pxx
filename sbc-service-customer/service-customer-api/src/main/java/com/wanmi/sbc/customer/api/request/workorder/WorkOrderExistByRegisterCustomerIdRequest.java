package com.wanmi.sbc.customer.api.request.workorder;

import com.wanmi.sbc.customer.api.request.CustomerBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;


/**
 * <p>工单请求参数</p>
 * @author baijz
 * @date 2020-05-17 16:03:15
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrderExistByRegisterCustomerIdRequest extends CustomerBaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 已注册会员的Id
	 */
	@ApiModelProperty(value = "已注册会员的Id")
	@NotNull
	private String customerId;

}