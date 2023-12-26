package com.wanmi.sbc.goods.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>配送到家VO</p>
 * @author lh
 * @date 2020-12-05 18:16:34
 */
@ApiModel
@Data
public class IcitemVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * sku
	 */
	@ApiModelProperty(value = "sku")
	private String sku;

	/**
	 * name
	 */
	@ApiModelProperty(value = "name")
	private String name;

	/**
	 * tiji
	 */
	@ApiModelProperty(value = "tiji")
	private BigDecimal tiji;

	/**
	 * weight
	 */
	@ApiModelProperty(value = "weight")
	private BigDecimal weight;

}