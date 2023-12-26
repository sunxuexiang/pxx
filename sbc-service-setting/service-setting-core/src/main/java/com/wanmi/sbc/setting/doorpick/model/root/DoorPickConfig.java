package com.wanmi.sbc.setting.doorpick.model.root;


import lombok.Data;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 上门自提配置
 */
@Data
@Entity
@Table(name = "doorPickConfig")
public class DoorPickConfig implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "network_id")
	private Long networkId;

	/**
	 * 网点联系人
	 */
	@Column(name = "contacts")
	private String contacts;

	/**
	 * 网点名字
	 */
	@Column(name = "network_name")
	private String networkName;

	/**
	 * 网点手机号码
	 */
	@Column(name = "phone")
	private String phone;


	/**
	 * 网点座机号码
	 */
	@Column(name = "Landline")
	private String landline;


	/**
	 * 网点地址
	 */
	@Column(name = "network_address")
	private String networkAddress;

	/**
	 * 省
	 */
	@Column(name = "province")
	private String province;


	/**
	 * 市
	 */
	@Column(name = "city")
	private String city;

	/**
	 * 区
	 */
	@Column(name = "area")
	private String area;


	/**
	 * 镇
	 */
	@Column(name = "town")
	private String town;


	@Column(name = "province_name")
	private String provinceName;


	@Column(name = "city_name")
	private String cityName;

	@Column(name = "area_name")
	private String areaName;

	@Column(name = "town_name")
	private String townName;

	@Column(name = "specific_adress")
	private String specificAdress;


	/**
	 * 纬度值
	 */
	@Column(name = "lat")
	private BigDecimal lat;

	/**
	 * 经度值
	 */
	@Column(name = "lng")
	private BigDecimal lng;

	/**
	 * 可配送距离 米
	 */
	@Column(name = "distance")
	private int distance;

	/**
	 * 是否删除
	 */
	@Column(name = "del_flag")
	private Integer delFlag = 0;



	/**
	 * 创建时间
	 */
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@Column(name = "create_time")
	private LocalDateTime createTime;

	/**
	 * 删除时间
	 */
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@Column(name = "delete_time")
	private LocalDateTime deleteTime;

	/**
	 * 店铺id
	 */
	@Column(name="store_id")
	private Long storeId;
}