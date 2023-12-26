package com.wanmi.sbc.goods.soldoutgoods.model.root;


import lombok.Data;
import javax.persistence.*;
import com.wanmi.sbc.common.base.BaseEntity;
import org.hibernate.annotations.GenericGenerator;

/**
 * <p>类目品牌排序表实体类</p>
 * @author lvheng
 * @date 2021-04-10 15:09:50
 */
@Data
@Entity
@Table(name = "sold_out_goods")
public class SoldOutGoods extends BaseEntity {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String id;
	/**
	 * 将要被下架的商品
	 */

	@Column(name = "goods_id")
	private String goodsId;

}