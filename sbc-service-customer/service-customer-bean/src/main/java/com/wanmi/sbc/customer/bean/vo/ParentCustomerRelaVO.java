package com.wanmi.sbc.customer.bean.vo;

import lombok.Data;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>子主账号关联关系VO</p>
 * @author baijz
 * @date 2020-05-26 15:39:43
 */
@ApiModel
@Data
public class ParentCustomerRelaVO implements Serializable {
	private static final long serialVersionUID = 1L;

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

}