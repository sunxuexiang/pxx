package com.wanmi.sbc.goods.goodslabelrela.model.root;


import com.wanmi.sbc.common.base.BaseEntity;
import lombok.Data;

import javax.persistence.*;

/**
 * <p>邀新统计实体类</p>
 * @author lvheng
 * @date 2021-04-23 14:20:19
 */
@Data
@Entity
@Table(name = "goods_label_rela")
public class GoodsLabelRela extends BaseEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 商品标签关联主键ID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	/**
	 * 商品id
	 */
	@Column(name = "goods_id")
	private String goodsId;

	/**
	 * 标签id
	 */
	@Column(name = "label_id")
	private Long labelId;

}