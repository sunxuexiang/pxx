package com.wanmi.sbc.goods.warehouse.model.root;

import com.wanmi.sbc.common.base.BaseEntity;
import com.wanmi.sbc.common.enums.DeleteFlag;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>商品库位关联表</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:21:37
 */
@Data
@Entity
@Table(name = "goods_stock_rel")
public class WareHouseStock extends BaseEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@Id
	@Column(name = "id")
	private Integer id;


	/**
	 * 仓库ID
	 */
	@Column(name = "ware_house_id")
	private String wareHouseId;


	/**
	 * 客户名称
	 */
	@Column(name = "custom_name")
	private String customName;



	/**
	 * skuId
	 */
	@Column(name = "sku_id")
	private String skuId;

	/**
	 * 中文描述
	 */
	@Column(name = "chinese_name")
	private String chineseName;

	/**
	 * 英文描述
	 */
	@Column(name = "english_name")
	private String englishName;

	/**
	 * 库位名称
	 */
	@Column(name = "stock_name")
	private String stockName;

	/**
	 * 单位
	 */
	@Column(name = "unit")
	private String unit;

	/**
	 * 库存数
	 */
	@Column(name = "stock_num")
	private BigDecimal stockNum;

	/**
	 * 排序库位
	 */
	@Column(name = "sort_stock_name")
	private String sortStockName;


}