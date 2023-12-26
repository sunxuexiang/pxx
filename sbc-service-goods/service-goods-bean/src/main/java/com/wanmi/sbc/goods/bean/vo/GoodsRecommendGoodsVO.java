package com.wanmi.sbc.goods.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>商品推荐商品VO</p>
 * @author chenyufei
 * @date 2019-09-07 10:53:36
 */
@ApiModel
@Data
public class GoodsRecommendGoodsVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 推荐商品主键编号
	 */
	@ApiModelProperty(value = "推荐商品主键编号")
	private String recommendId;

	/**
	 * 推荐的商品编号
	 */
	@ApiModelProperty(value = "推荐的商品编号")
	private String goodsInfoId;

}