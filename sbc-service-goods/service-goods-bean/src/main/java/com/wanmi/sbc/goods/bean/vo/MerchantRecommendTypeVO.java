package com.wanmi.sbc.goods.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.io.Serializable;

/**
 * <p>分类推荐分类VO</p>
 * @author sgy
 * @date 2019-09-07 10:53:36
 */
@ApiModel
@Data
public class MerchantRecommendTypeVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 推荐分类主键编号
	 */
	@ApiModelProperty(value = "推荐分类主键编号")
	private String merchantTypeId;

	/**
	 * 推荐的分类编号
	 */
	@ApiModelProperty(value = "推荐的分类编号")
	private Long goodsTypeId;
	/**
	 * 店铺id
	 */
	@ApiModelProperty(value = "店铺Id")
	private Long storeId;

	/**
	 * 商家Id
	 */
	@ApiModelProperty(value = "商家Id")
	private Long companyInfoId;

	@ApiModelProperty(value = "分类图片")
	private String cateImg;


	@ApiModelProperty(value = "分类名称")
	private String cateName;

	@ApiModelProperty(value = "排序")
	private int sort;


}