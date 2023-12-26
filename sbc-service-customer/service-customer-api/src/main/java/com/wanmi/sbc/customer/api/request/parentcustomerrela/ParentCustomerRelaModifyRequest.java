package com.wanmi.sbc.customer.api.request.parentcustomerrela;

import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.common.base.Operator;
import lombok.*;
import javax.validation.constraints.*;
import javax.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>子主账号关联关系修改参数</p>
 * @author baijz
 * @date 2020-05-26 15:39:43
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParentCustomerRelaModifyRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 父Id
	 */
	@ApiModelProperty(value = "父Id")
	@Length(max=32)
	private String parentId;

	/**
	 * 会员Id
	 */
	@ApiModelProperty(value = "会员Id")
	@NotBlank
	@Length(max=32)
	private String customerId;

	/**
	 * 工单表Id
	 */
	@ApiModelProperty(value = "工单表Id")
	@NotBlank
	@Length(max=32)
	private String workOrderId;

	/**
	 * 操作人
	 */
	@ApiModelProperty(value = "操作人")
	private Operator operator;

}