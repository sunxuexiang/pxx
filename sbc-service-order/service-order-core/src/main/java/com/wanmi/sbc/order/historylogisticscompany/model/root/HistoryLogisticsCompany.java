package com.wanmi.sbc.order.historylogisticscompany.model.root;

import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * <p>物流公司历史记录实体类</p>
 * @author fcq
 * @date 2020-11-09 17:32:23
 */
@Data
@Entity
@Table(name = "history_logistics_company")
public class HistoryLogisticsCompany{
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "id")
	private String id;

	/**
	 * 会员id
	 */
	@Column(name = "customer_id")
	private String customerId;

	/**
	 * 订单id
	 */
	@Column(name = "order_id")
	private String orderId;

	/**
	 * 物流公司名称
	 */
	@Column(name = "logistics_name")
	private String logisticsName;

	/**
	 * 物流公司电话
	 */
	@Column(name = "logistics_phone")
	private String logisticsPhone;

	/**
	 * 物流公司地址
	 */
	@Column(name = "logistics_address")
	private String logisticsAddress;

	/**
	 * 收货站点
	 */
	@Column(name = "receiving_site")
	private String receivingSite;
	/**
	 * 是否是客户自建物流
	 */
	@Column(name = "self_flag")
	@Enumerated
	private DefaultFlag selfFlag;

	/**
	 * 是否是客户自建物流
	 */
	@Column(name = "company_id")
	private Long companyId;

	/**
	 * 删除标志
	 */
	@Column(name = "del_flag")
	@Enumerated
	private DeleteFlag delFlag;

	@CreatedDate
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@Column(name = "create_time")
	private LocalDateTime createTime;

	/**
	 * 物流类型
	 */
	@Column(name="logistics_type")
	private Integer logisticsType;

	/**
	 * 市场Id
	 */
	@Column(name="market_id")
	private Long marketId;
}