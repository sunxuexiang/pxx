package com.wanmi.sbc.goods.merchantconfig.root;


import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * <p>商品推荐商品实体类</p>
 * @author sgy
 * @date  2023-06-07 10:53:36
 */
@Data
@Entity
@Table(name = "merchant_recommend_goods")
public class MerchantRecommendGoods implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 推荐商品主键编号
	 */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "merchant_recommend_id")
	private String recommendId;

	/**
	 * 推荐的商品编号
	 */
	@Column(name = "goods_info_id")
	private String goodsInfoId;

	/**
	 * 店铺id
	 */
	@Column(name = "store_id")
	private Long storeId;
	/**
	 *商家ID
	 */
	@Column(name = "company_info_id")
	private Long companyInfoId;


	/**
	 *排序字段
	 */
	@Column(name = "sort")
	private int  sort;


}