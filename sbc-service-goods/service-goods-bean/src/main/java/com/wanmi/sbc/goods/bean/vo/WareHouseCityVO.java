package com.wanmi.sbc.goods.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p> 仓库地区表VO</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:28:33
 */
@ApiModel
@Data
public class WareHouseCityVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键ID
	 */
	@ApiModelProperty(value = "主键ID")
	private Long id;

	/**
	 * 仓库iD
	 */
	@ApiModelProperty(value = "仓库iD")
	private Long wareId;

	/**
	 * 省份
	 */
	@ApiModelProperty(value = "省份")
	private Long provinceId;

	/**
	 * 市
	 */
	@ApiModelProperty(value = "市")
	private Long cityId;

	/**
	 * 区县ID
	 */
	@ApiModelProperty(value = "区县ID")
	private Long areaId;

}