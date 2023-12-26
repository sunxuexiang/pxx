package com.wanmi.sbc.goods.goodstypeconfig.root;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.wanmi.sbc.goods.info.model.root.GoodsInfo;
import com.wanmi.sbc.goods.storecate.model.root.StoreCate;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;

/**
 * <p>分类推荐分类实体类</p>
 * @author sgy
 * @date  2023-06-07 10:53:36
 */
@Data
@Entity
@Table(name = "merchant_recommend_type")
public class MerchantRecommendType implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 推荐分类主键编号
	 */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "merchant_type_id")
	private String merchantTypeId;

	/**
	 * 店铺id
	 */
	@Column(name = "store_id")
	private Long storeId;
	/**
	 * 商家Id
	 */
	@Column(name = "company_info_id")
	private Long companyInfoId;
	/**
	 * 排序
	 */
	@Column(name = "sort")
	private int sort;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "store_cate_id", insertable = false, updatable = false)
	@NotFound(action= NotFoundAction.IGNORE)
	@JsonBackReference
	private StoreCate storeCate;
}