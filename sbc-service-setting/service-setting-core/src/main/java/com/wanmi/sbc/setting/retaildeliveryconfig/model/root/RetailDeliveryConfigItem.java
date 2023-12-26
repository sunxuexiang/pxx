package com.wanmi.sbc.setting.retaildeliveryconfig.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "retail_delivery_config")
@EntityListeners(AuditingEntityListener.class)
public class RetailDeliveryConfigItem implements Serializable {


	private static final long serialVersionUID = 2612935455083693279L;
	/**
	 * id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "retail_delivery_id")
	private Long retailDeliveryId;


	/**
	 * 小于等于1千克多少元
	 */
	@Column(name = "less_money")
	private BigDecimal lessMoney;

	/**
	 * 大于1千克
	 */
	@Column(name = "greater_money")
	private BigDecimal greaterMoney;


	/**
	 * 创建时间
	 */
	@CreatedDate
	@Column(name = "create_time",updatable = false,nullable = false)
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime createTime;


	/**
	 * 修改时间
	 */
	@LastModifiedDate
	@Column(name = "update_time",nullable = false)
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime updateTime;

}