package com.wanmi.sbc.goods.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>sku分仓库存表VO</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:22:56
 */
@ApiModel
@Data
public class GoodsWareStockGroupVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * sku ID
	 */
	@ApiModelProperty(value = "sku ID")
	private String goodsInfoId;

	@ApiModelProperty(value = "sku 编号")
	private String goodsInfoNo;

	@ApiModelProperty(value = "已选数量")
	private Integer chooseCount;

	/**
	 * 仓库库存
	 */
	@ApiModelProperty(value = "仓库库存")
	private List<GoodsWareStockVO> goodsWareStockVOList;
}