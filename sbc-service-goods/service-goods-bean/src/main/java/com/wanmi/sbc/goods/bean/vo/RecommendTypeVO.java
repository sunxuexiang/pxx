package com.wanmi.sbc.goods.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>分类推荐分类VO</p>
 * @author sgy
 * @date 2019-09-07 10:53:36
 */
@ApiModel
@Data
public class RecommendTypeVO implements Serializable {
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
	private String goodsTypeId;
	/**
	 * 店铺id
	 */
	@ApiModelProperty(value = "店铺Id")
	private Long storeId;

	/**
	 * 推荐的分类编号
	 */
	@ApiModelProperty(value = "推荐的分类编号")
	private Long storeCateId;

}