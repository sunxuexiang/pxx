package com.wanmi.sbc.setting.logisticscompany.model.root;

import com.wanmi.sbc.common.base.BaseEntity;
import com.wanmi.sbc.common.enums.DeleteFlag;
import lombok.Data;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @desc  
 * @author shiy  2023/11/16 14:50
*/
@Data
@Entity
@Table(name = "logistics_base_address")
public class LogisticsBaseAddress extends BaseEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;

	/**
	 * 出发省
	 */
	@Column(name="province_code")
	private String provinceCode;

	/**
	 * 出发省
	 */
	@Column(name="province_name")
	private String provinceName;

	/**
	 * 出发城
	 */
	@Column(name="city_code")
	private String cityCode;

	/**
	 * 出发城
	 */
	@Column(name="city_name")
	private String cityName;

	/**
	 * 出发区
	 */
	@Column(name="area_code")
	private String areaCode;

	/**
	 * 出发区
	 */
	@Column(name="area_name")
	private String areaName;

	/**
	 * 出发街
	 */
	@Column(name="town_code")
	private String townCode;

	/**
	 * 出发街
	 */
	@Column(name="town_name")
	private String townName;

	/**
	 * 备注
	 */
	@Column(name="remark")
	private String remark;

	/**
	 * 删除标志
	 */
	@Column(name = "del_flag")
	@Enumerated
	private DeleteFlag delFlag;



	/**
	 * 删除时间
	 */
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@Column(name = "delete_time")
	private LocalDateTime deleteTime;
}