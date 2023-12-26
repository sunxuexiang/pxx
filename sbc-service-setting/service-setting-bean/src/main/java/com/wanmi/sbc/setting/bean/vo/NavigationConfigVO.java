package com.wanmi.sbc.setting.bean.vo;

import lombok.Data;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>导航配置VO</p>
 * @author lvheng
 * @date 2021-04-12 14:46:18
 */
@ApiModel
@Data
public class NavigationConfigVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@ApiModelProperty(value = "id")
	private Integer id;

	/**
	 * 导航名称
	 */
	@ApiModelProperty(value = "导航名称")
	private String navName;

	/**
	 * 导航图标-未点击状态
	 */
	@ApiModelProperty(value = "导航图标-未点击状态")
	private String iconShow;

	/**
	 * 导航图标-点击状态
	 */
	@ApiModelProperty(value = "导航图标-点击状态")
	private String iconClick;

}