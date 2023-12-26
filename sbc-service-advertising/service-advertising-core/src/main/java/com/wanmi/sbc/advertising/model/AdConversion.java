package com.wanmi.sbc.advertising.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "ad_conversion")
public class AdConversion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/**
	 * 广告活动id
	 */
	private String activityId;

	/**
	 * 费用
	 */
	private BigDecimal cost;

	/**
	 * 转化类型
	 */
	private Integer conversionType;

	/**
	 * 转化时间
	 */
	private Date conversionTime;

	/**
	 * 转化量
	 */
	private Integer conversionCount;

	/**
	 * 转化金额
	 */
	private BigDecimal conversionAmount;

	private Date createTime;

}
