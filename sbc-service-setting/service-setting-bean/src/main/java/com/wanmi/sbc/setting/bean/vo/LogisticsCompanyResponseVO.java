package com.wanmi.sbc.setting.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>物流公司VO</p>
 * @author fcq
 * @date 2020-11-06 13:37:51
 */
@ApiModel
@Data
public class LogisticsCompanyResponseVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@ApiModelProperty(value = "id")
	private Long id;

	/**
	 * 公司名称
	 */
	@ApiModelProperty(value = "公司名称")
	private String logisticsName;

	/**
	 * 公司电话
	 */
	@ApiModelProperty(value = "公司电话")
	private String logisticsPhone;

	/**
	 * 物流公司地址
	 */
	@ApiModelProperty(value = "物流公司地址")
	private String logisticsAddress;

	private String receivingPoint;
}