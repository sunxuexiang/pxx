package com.wanmi.sbc.goods.bean.vo;

import com.wanmi.sbc.common.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * <p>商品库位关联表</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:21:37
 */
@Data
@ApiModel
public class WareHouseStockVO extends BaseEntity {
	private static final long serialVersionUID = 1L;



	/**
     * 仓库ID
	 */
	@ApiModelProperty(value = "wareHouseId")
	private String wareHouseId;


	/**
	 * 客户名称
	 */
	@ApiModelProperty(value = "custom_name")
	private String customName;



	/**
	 * skuId
	 */
	@ApiModelProperty(value =  "sku_id")
	private String skuId;

	/**
	 * 中文描述
	 */
	@ApiModelProperty(value =  "chinese_name")
	private String chineseName;

	/**
	 * 英文描述
	 */
	@ApiModelProperty(value =  "english_name")
	private String englishName;

	/**
	 * 库位名称
	 */
	@ApiModelProperty(value =  "stock_name")
	private String stockName;

	/**
	 * 单位
	 */
	@ApiModelProperty(value =  "unit")
	private String unit;

	/**
	 * 库存数
	 */
	@ApiModelProperty(value =  "stock_num")
	private BigDecimal stockNum;

	/**
	 * 排序库位
	 */
	@ApiModelProperty(value =  "sort_stock_name")
	private String sortStockName;
}