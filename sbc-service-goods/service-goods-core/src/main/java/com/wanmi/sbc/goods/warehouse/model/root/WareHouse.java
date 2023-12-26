package com.wanmi.sbc.goods.warehouse.model.root;

import com.wanmi.sbc.common.base.BaseEntity;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.PickUpFlag;
import com.wanmi.sbc.common.enums.WareHouseType;
import lombok.Data;

import javax.persistence.*;

/**
 * <p>仓库表实体类</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:21:37
 */
@Data
@Entity
@Table(name = "ware_house")
public class WareHouse extends BaseEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * wareId
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ware_id")
	private Long wareId;

	/**
	 * 店铺标识
	 */
	@Column(name = "store_id")
	private Long storeId;

	/**
	 * 仓库名称
	 */
	@Column(name = "ware_name")
	private String wareName;

	/**
	 * 仓库编号
	 */
	@Column(name = "ware_code")
	private String wareCode;

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
	 * 区
	 */
	@Column(name = "area_id")
	private Long areaId;

	/**
	 * 详细地址
	 */
	@Column(name = "address_detail")
	private String addressDetail;

	/**
	 * 是否默认仓 0：否，1：是
	 */
	@Column(name = "default_flag")
	private DefaultFlag defaultFlag;

	/**
	 * 是否删除标志 0：否，1：是
	 */
	@Column(name = "del_flag")
	@Enumerated
	private DeleteFlag delFlag;


	/**
	 * 线上仓:0 ,门店仓:1
	 */
	@Column(name = "ware_house_type")
	@Enumerated
	private WareHouseType wareHouseType;


	/**
	 * 是否支持自提 0:否  , 1:是
	 */
	@Column(name = "pick_up_flag")
	@Enumerated
	private PickUpFlag pickUpFlag;


	/**
	 * 仓库地区id(逗号分隔)
	 */
	@Column(name = "destination_area")
	private String destinationArea;

	/**
	 * 仓库地区名称(逗号分隔)
	 */
	@Column(name = "destination_area_name")
	private String destinationAreaName;

	/**
	 * 特价仓id
	 */
	@Column(name = "sp_price_id")
	private String spPriceId;

	/**
	 * 纬度
	 */
	@Column(name = "lat")
	private Double lat;

	/**
	 * 经度
	 */
	@Column(name = "lng")
	private Double lng;

	/**
	 * 类型 0:线下仓 1：线上仓
	 */
	@Column(name = "type")
	private DefaultFlag type;

	/**
	 * 配送范围（KM）
	 */
	@Column(name = "distance")
	private Long distance;

	/**
	 * 仓库在erp中的Id
	 */
	@Column(name = "self_erp_id")
	private String selfErpId;

}