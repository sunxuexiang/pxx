package com.wanmi.sbc.goods.bean.vo;

import com.wanmi.sbc.common.enums.DefaultFlag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>导航配置VO</p>
 * @author lvheng
 * @date 2021-04-19 11:09:28
 */
@ApiModel
@Data
public class GoodsLabelVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 标签id
	 */
	@ApiModelProperty(value = "标签id")
	private Long id;

	/**
	 * 标签名称
	 */
	@ApiModelProperty(value = "标签名称")
	private String name;

	/**
	 * 商品列表展示开关 0: 关闭 1:开启
	 */
	@ApiModelProperty(value = "商品列表展示开关 0: 关闭 1:开启")
	private DefaultFlag visible;

	/**
	 * 排序
	 */
	@ApiModelProperty(value = "排序")
	private Integer sort;

	/**
	 * 标签图片
	 */
	@ApiModelProperty(value = "标签图片")
	private String image;

	/**
	 * 店铺id
	 */
	@ApiModelProperty(value = "店铺id")
	private Long storeId;

}