package com.wanmi.sbc.marketing.coupon.model.root;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;

import lombok.Data;

/**
 * 指定商品赠金币
 * 
 * @author Administrator
 */
@Entity
@Table(name = "coin_activity_store")
@Data
public class CoinActivityStore {

	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	/**
	 * 活动id
	 */
	@Column(name = "activity_id")
	private String activityId;

	/**
	 * 店铺id
	 */
	@Column(name = "store_id")
	private Long storeId;
	
	/**
	 * 店铺name
	 */
	@Column(name = "store_name")
	private String storeName;
	
	/**
	 * 商家账号
	 */
	@Column(name = "account_name")
	private String accountName;
	
	/**
	 * 批发市场名称
	 */
	@Column(name = "market_name")
	private String marketName;

	/**
	 * 商家类型
	 */
	@Column(name = "company_type")
	private CompanyType companyType;

	/**
	 * 是否终止
	 */
	@Column(name = "termination_flag")
	@Enumerated
	private BoolFlag terminationFlag;

	/**
	 * 终止时间
	 */
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	@Column(name = "termination_time")
	private LocalDateTime terminationTime;


}
