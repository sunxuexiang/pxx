package com.wanmi.sbc.goods.bean.vo;

import lombok.Data;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>类目品牌排序表VO</p>
 * @author lvheng
 * @date 2021-04-10 15:09:50
 */
@ApiModel
@Data
public class SoldOutGoodsVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 将要被下架的商品
	 */
	@ApiModelProperty(value = "将要被下架的商品")
	private String goodsId;

}