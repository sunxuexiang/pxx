package com.wanmi.sbc.goods.goodstypeconfig.root;


import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.goods.api.request.goodstypeconfig.MerchantTypeConfigBatchAddRequest;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * <p>分类推荐分类实体类</p>
 * @author sgy
 * @date  2023-06-07 10:53:36
 */
@Data
@Entity
@Table(name = "merchant_recommend_type")
public class RecommendType implements Serializable {
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
	 * 推荐的分类编号
	 */
	@Column(name = "store_cate_id")
	private String storeCateId;

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


}