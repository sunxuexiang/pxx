package com.wanmi.sbc.setting.bean.vo;

import lombok.Data;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>导航配置VO</p>
 * @author lvheng
 * @date 2021-04-19 18:49:30
 */
@ApiModel
@Data
public class ActivityConfigVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 活动配置id
	 */
	@ApiModelProperty(value = "活动配置id")
	private Long id;

	/**
	 * 配置名称
	 */
	@ApiModelProperty(value = "配置名称")
	private String name;

	/**
	 * 配置别名
	 */
	@ApiModelProperty(value = "配置别名")
	private String alias;

	/**
	 * 配置别名
	 */
	@ApiModelProperty(value = "配置别名")
	private String value;

}