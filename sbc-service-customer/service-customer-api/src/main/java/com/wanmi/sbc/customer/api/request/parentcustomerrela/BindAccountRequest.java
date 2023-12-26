package com.wanmi.sbc.customer.api.request.parentcustomerrela;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

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
public class BindAccountRequest extends BaseQueryRequest {


	private static final long serialVersionUID = 7405447955552956048L;
	/**
	 * 父Id
	 */
	@ApiModelProperty(value = "父Id")
	@NotBlank
	private String parentId;

	/**
	 * 会员Id
	 */
	@ApiModelProperty(value = "会员Id")
	private List<String> customerIds;


}