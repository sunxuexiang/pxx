package com.wanmi.sbc.setting.bean.vo;

import lombok.Data;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>天气设置VO</p>
 * @author 费传奇
 * @date 2021-04-16 09:54:37
 */
@ApiModel
@Data
public class WeatherSwitchVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 开关id
	 */
	@ApiModelProperty(value = "开关id")
	private Long id;

	/**
	 * 顶部背景图状态(0.关闭，1开启)
	 */
	@ApiModelProperty(value = "顶部背景图状态(0.关闭，1开启)")
	private Integer topImgStatus;

	/**
	 * 顶部背景图
	 */
	@ApiModelProperty(value = "顶部背景图")
	private String topImg;

	/**
	 * slogan图图状态(0.关闭，1开启)
	 */
	@ApiModelProperty(value = "slogan图图状态(0.关闭，1开启)")
	private Integer sloganImgStatus;

	/**
	 * slogan图
	 */
	@ApiModelProperty(value = "slogan图")
	private String sloganImg;

	/**
	 * 组件开关状态 (0：关闭 1：开启)
	 */
	@ApiModelProperty(value = "组件开关状态 (0：关闭 1：开启)")
	private Integer componentStatus;

	/**
	 * 是否设置 (0：关闭 1：开启)
	 */
	@ApiModelProperty(value = "是否设置 (0：关闭 1：开启)")
	private Integer searchBackStatus;

	/**
	 * 搜索背景色
	 */
	@ApiModelProperty(value = "搜索背景色")
	private String searchBackColor;

}