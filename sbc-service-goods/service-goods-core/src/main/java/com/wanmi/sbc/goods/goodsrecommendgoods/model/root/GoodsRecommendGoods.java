package com.wanmi.sbc.goods.goodsrecommendgoods.model.root;


import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * <p>商品推荐商品实体类</p>
 * @author chenyufei
 * @date 2019-09-07 10:53:36
 */
@Data
@Entity
@Table(name = "goods_recommend_goods")
public class GoodsRecommendGoods implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 推荐商品主键编号
	 */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "recommend_id")
	private String recommendId;

	/**
	 * 推荐的商品编号
	 */
	@Column(name = "goods_info_id")
	private String goodsInfoId;

	/**
	 * 仓库id
	 */
	@Column(name = "ware_id")
	private Long wareId;

}