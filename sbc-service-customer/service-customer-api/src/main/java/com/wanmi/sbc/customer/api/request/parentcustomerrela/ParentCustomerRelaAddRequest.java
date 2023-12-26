package com.wanmi.sbc.customer.api.request.parentcustomerrela;

import com.wanmi.sbc.common.base.BaseRequest;
import lombok.*;
import javax.validation.constraints.*;
import javax.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>子主账号关联关系新增参数</p>
 * @author baijz
 * @date 2020-05-26 15:39:43
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParentCustomerRelaAddRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 会员Id
	 */
	@ApiModelProperty(value = "会员Id")
	@NotBlank
	@Length(max=32)
	private String customerId;

}