package com.wanmi.sbc.customer.api.request.parentcustomerrela;

import com.wanmi.sbc.customer.api.request.CustomerBaseRequest;
import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>批量删除子主账号关联关系请求参数</p>
 * @author baijz
 * @date 2020-05-26 15:39:43
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParentCustomerRelaDelByIdListRequest extends CustomerBaseRequest {
private static final long serialVersionUID = 1L;

	/**
	 * 批量删除-父IdList
	 */
	@ApiModelProperty(value = "批量删除-父IdList")
	@NotEmpty
	private List<String> parentIdList;
}
