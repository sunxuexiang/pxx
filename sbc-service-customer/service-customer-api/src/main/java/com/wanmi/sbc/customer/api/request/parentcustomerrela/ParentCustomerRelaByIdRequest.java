package com.wanmi.sbc.customer.api.request.parentcustomerrela;

import com.wanmi.sbc.common.base.BaseRequest;
import lombok.*;
import javax.validation.constraints.NotNull;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 * <p>单个查询子主账号关联关系请求参数</p>
 * @author baijz
 * @date 2020-05-26 15:39:43
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParentCustomerRelaByIdRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 父Id
	 */
	@ApiModelProperty(value = "父Id")
	@NotNull
	private String parentId;

}