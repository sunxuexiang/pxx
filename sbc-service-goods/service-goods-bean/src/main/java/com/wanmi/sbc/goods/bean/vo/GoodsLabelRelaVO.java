package com.wanmi.sbc.goods.bean.vo;

import lombok.Data;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>邀新统计VO</p>
 * @author lvheng
 * @date 2021-04-23 14:20:19
 */
@ApiModel
@Data
public class GoodsLabelRelaVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 商品标签关联主键ID
	 */
	@ApiModelProperty(value = "商品标签关联主键ID")
	private Long id;

	/**
	 * 商品id
	 */
	@ApiModelProperty(value = "商品id")
	private String goodsId;

	/**
	 * 标签id
	 */
	@ApiModelProperty(value = "标签id")
	private Long labelId;

}