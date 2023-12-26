package com.wanmi.sbc.setting.logisticscompany.model.root;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.UUIDUtil;
import lombok.Data;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import java.time.LocalDateTime;

import javax.persistence.*;
import com.wanmi.sbc.common.base.BaseEntity;

/**
 * <p>配送到家实体类</p>
 * @author fcq
 * @date 2020-11-06 13:37:51
 */
@Data
@Entity
@Table(name = "logistics_company")
public class LogisticsCompany extends BaseEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	/**
	 * 公司编号
	 */
	@Column(name = "company_number")
	@Access(value=AccessType.PROPERTY)
	private String companyNumber;

	/**
	 * 公司名称
	 */
	@Column(name = "logistics_name")
	@Access(value=AccessType.PROPERTY)
	private String logisticsName;

	/**
	 * 公司电话
	 */
	@Column(name = "logistics_phone")
	@Access(value=AccessType.PROPERTY)
	private String logisticsPhone;

	/**
	 * 物流公司地址
	 */
	@Column(name = "logistics_address")
	@Access(value=AccessType.PROPERTY)
	private String logisticsAddress;

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

	/**
	 * 店铺标识
	 */
	@Column(name="store_id")
	private Long storeId;

	/**
	 * 物流类型
	 */
	@Column(name="logistics_type")
	private Integer logisticsType;

	/**
	 * 市场Id
	 */
	@Column(name="market_id")
	@Access(value=AccessType.PROPERTY)
	private Long marketId;


	public String getCompanyNumber() {
		if(this.companyNumber==null){
			this.companyNumber = UUIDUtil.getUUID19();
		}
		return companyNumber;
	}

	public String getLogisticsName() {
		if(this.logisticsName!=null){
			this.logisticsName = this.logisticsName.trim();
		}
		return logisticsName;
	}

	public String getLogisticsPhone() {
		if(this.logisticsPhone!=null){
			this.logisticsPhone =this.logisticsPhone.trim();
		}
		return logisticsPhone;
	}

	public String getLogisticsAddress() {
		if(this.logisticsAddress!=null){
			this.logisticsAddress=this.logisticsAddress.trim();
		}
		return logisticsAddress;
	}

	public Long getMarketId() {
		if(null== marketId){
			this.marketId=Constants.BOSS_DEFAULT_MARKET_ID;
		}
		return marketId;
	}
}