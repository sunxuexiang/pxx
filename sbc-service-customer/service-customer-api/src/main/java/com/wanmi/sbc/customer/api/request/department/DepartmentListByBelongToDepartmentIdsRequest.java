package com.wanmi.sbc.customer.api.request.department;

import com.wanmi.sbc.common.base.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>部门管理列表查询请求参数</p>
 * @author wanggang
 * @date 2020-02-26 19:02:40
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentListByBelongToDepartmentIdsRequest extends BaseRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 归属部门集合
	 */
	@ApiModelProperty(value = "归属部门集合")
	private String belongToDepartmentIds;

}