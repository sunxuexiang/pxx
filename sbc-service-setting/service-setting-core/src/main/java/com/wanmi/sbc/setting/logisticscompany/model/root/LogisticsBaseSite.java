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
@Table(name = "logistics_base_site")
public class LogisticsBaseSite extends BaseEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="site_id")
	private Long siteId;

	/**
	 * 物流公司编号
	 */
	@Column(name="logistics_id")
	private Long logisticsId;

	/**
	 * 站点名
	 */
	@Column(name="site_name")
	private String siteName;

	/**
	 * 站点人
	 */
	@Column(name="site_person")
	private String sitePerson;

	/**
	 * 站点电话
	 */
	@Column(name="site_phone")
	private String sitePhone;

	@Column(name="site_crt_type")
	private Integer siteCrtType;

	/**
	 * 区域ID
	 */
	@Column(name="base_address_id")
	private Long baseAddressId;

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