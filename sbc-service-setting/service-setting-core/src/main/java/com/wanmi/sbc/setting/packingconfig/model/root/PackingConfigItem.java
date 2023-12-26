package com.wanmi.sbc.setting.packingconfig.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DeleteFlag;
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
@Table(name = "packing_config")
@EntityListeners(AuditingEntityListener.class)
public class PackingConfigItem implements Serializable {


	private static final long serialVersionUID = 7598561242795370702L;
	/**
	 * 包装配置ID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "packing_id")
	private Long packingId;

	/**
	 * 包装标志0满金额，1满数量
	 */
	@Column(name = "packing_type")
	private Integer packingType;

	/**
	 * 如果是满金额存的就是金额字段，如果是满数量那就存的是数量
	 */
	@Column(name = "packing_amount_num")
	private BigDecimal packingAmountNum;

	/**
	 * 如果是满金额存的就是金额字段，如果是满数量那就存的是数量
	 */
	@Column(name = "packing_amount")
	private BigDecimal packingAmount;

	/**
	 * sku_id
	 */
	@Column(name = "goods_info_id")
	private String goodsInfoId;

	/**
	 * sku_id
	 */
	@Column(name = "goods_info_name")
	private String goodsInfoName;

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