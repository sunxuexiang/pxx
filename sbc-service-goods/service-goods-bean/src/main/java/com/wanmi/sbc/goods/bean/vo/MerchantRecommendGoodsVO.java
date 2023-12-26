package com.wanmi.sbc.goods.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>商品推荐商品VO</p>
 * @author sgy
 * @date 2019-06-07 10:53:36
 */
@ApiModel
@Data
public class MerchantRecommendGoodsVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 推荐商品主键编号
	 */
	@ApiModelProperty(value = "推荐商品主键编号")
	private String merchantRecommendId;

	/**
	 * 推荐的商品编号
	 */
	@ApiModelProperty(value = "推荐的商品编号")
	private String goodsInfoId;

	/**
	 * 店铺id
	 */
	@ApiModelProperty(value = "店铺id")
	private Long storeId;
	/**
	 *商家ID
	 */
	@ApiModelProperty(value = "商家ID")
	private Long companyInfoId;
	/**
	 *商品明细
	 */
	@ApiModelProperty(value = "商品明细")
	private GoodsInfoVO goodsInfo;

	/**
	 * 排序字段
	 */
	@ApiModelProperty(value = "排序字段")
	private int sort;

}