package com.wanmi.sbc.setting.logisticscompany.model.root;

import com.wanmi.sbc.common.base.BaseEntity;
import com.wanmi.sbc.common.enums.DeleteFlag;
import lombok.Data;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @desc 物流线路
 * @author shiy  2023/11/7 9:08
*/
@Data
@Entity
@Table(name = "logistics_company_line")
public class LogisticsCompanyLine extends BaseEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="line_id")
	private Long lineId;

	/**
	 * 物流公司编号
	 */
	@Column(name="logistics_id")
	private Long logisticsId;

	/**
	 * 出发站点
	 */
	@Column(name="from_site_id")
	private Long fromSiteId;

	/**
	 * 出发站点
	 */
	@Column(name="to_site_id")
	private Long toSiteId;

	/**
	 * 到达点
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