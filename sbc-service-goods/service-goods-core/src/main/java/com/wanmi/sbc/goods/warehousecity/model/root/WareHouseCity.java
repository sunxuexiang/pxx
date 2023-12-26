package com.wanmi.sbc.goods.warehousecity.model.root;


import lombok.Data;

import javax.persistence.*;

/**
 * <p> 仓库地区表实体类</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:28:33
 */
@Data
@Entity
@Table(name = "ware_house_city")
public class WareHouseCity {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键ID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	/**
	 * 仓库iD
	 */
	@Column(name = "ware_id")
	private Long wareId;

	/**
	 * 省份
	 */
	@Column(name = "province_id")
	private Long provinceId;

	/**
	 * 市
	 */
	@Column(name = "city_id")
	private Long cityId;

	/**
	 * 区县ID
	 */
	@Column(name = "area_id")
	private Long areaId;

}