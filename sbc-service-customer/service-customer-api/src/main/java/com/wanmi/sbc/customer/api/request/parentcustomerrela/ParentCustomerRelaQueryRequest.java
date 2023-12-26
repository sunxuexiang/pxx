package com.wanmi.sbc.customer.api.request.parentcustomerrela;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import lombok.*;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>子主账号关联关系通用查询请求参数</p>
 * @author baijz
 * @date 2020-05-26 15:39:43
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParentCustomerRelaQueryRequest extends BaseQueryRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 批量查询-父IdList
	 */
	@ApiModelProperty(value = "批量查询-父IdList")
	private List<String> parentIdList;

	/**
	 * 父Id
	 */
	@ApiModelProperty(value = "父Id")
	private String parentId;

	/**
	 * 会员Id
	 */
	@ApiModelProperty(value = "会员Id")
	private String customerId;

	/**
	 * 批量查询-会员Id
	 */
	@ApiModelProperty(value = "批量查询-会员Id")
	private List<String> customerIdList;

}