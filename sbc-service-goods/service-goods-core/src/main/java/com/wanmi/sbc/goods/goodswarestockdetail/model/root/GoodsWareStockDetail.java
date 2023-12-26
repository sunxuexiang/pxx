package com.wanmi.sbc.goods.goodswarestockdetail.model.root;

import com.wanmi.sbc.common.base.BaseEntity;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.goods.bean.enums.GoodsWareStockImportType;
import lombok.Data;

import javax.persistence.*;

/**
 * <p> 库存明细表实体类</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:24:37
 */
@Data
@Entity
@Table(name = "goods_ware_stock_detail")
public class GoodsWareStockDetail extends BaseEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	/**
	 * 商品库存关联表ID
	 */
	@Column(name = "goods_ware_stock_id")
	private Long goodsWareStockId;

	/**
	 * 导入编号
	 */
	@Column(name = "stock_import_no")
	private String stockImportNo;

	/**
	 * 商品id
	 */
	@Column(name = "goods_info_id")
	private String goodsInfoId;

	/**
	 * sku编码
	 */
	@Column(name = "goods_info_no")
	private String goodsInfoNo;

	/**
	 * 仓库ID
	 */
	@Column(name = "ware_id")
	private Long wareId;

	/**
	 * 导入类型 0：导入，1：编辑，2：返还，3：下单扣减
	 */
	@Column(name = "import_type")
	@Enumerated
	private GoodsWareStockImportType importType;

	/**
	 * 操作库存
	 */
	@Column(name = "operate_stock")
	private Long operateStock;

	/**
	 * 库存数量
	 */
	@Column(name = "stock")
	private Long stock;

	/**
	 * 是否删除标志 0：否，1：是
	 */
	@Column(name = "del_flag")
	@Enumerated
	private DeleteFlag delFlag;

}