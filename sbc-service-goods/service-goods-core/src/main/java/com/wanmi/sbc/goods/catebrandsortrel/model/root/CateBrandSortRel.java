package com.wanmi.sbc.goods.catebrandsortrel.model.root;

import com.wanmi.sbc.common.enums.DeleteFlag;

import lombok.Data;
import javax.persistence.*;
import com.wanmi.sbc.common.base.BaseEntity;

/**
 * <p>类目品牌排序表实体类</p>
 * @author lvheng
 * @date 2021-04-08 11:24:32
 */
@Data
@Entity
@Table(name = "cate_brand_sort_rel")
@IdClass(CateBrandRelId.class)
public class CateBrandSortRel extends BaseEntity {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "cate_id")
	private Long cateId;

	@Id
	@Column(name = "brand_id")
	private Long brandId;
	/**
	 * 品牌名称
	 */
	@Column(name = "name")
	private String name;

	/**
	 * 品牌别名
	 */
	@Column(name = "alias")
	private String alias;

	/**
	 * 排序序号
	 */
	@Column(name = "serial_no")
	private Integer serialNo;

	/**
	 * 删除标识，0：未删除 1：已删除
	 */
	@Column(name = "del_flag")
	@Enumerated
	private DeleteFlag delFlag;

}