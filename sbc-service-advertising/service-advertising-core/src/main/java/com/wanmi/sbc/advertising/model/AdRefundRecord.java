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
@Table(name = "ad_refund_record")
public class AdRefundRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/**
	 * 广告活动id
	 */
	private String activityId;

	/**
	 * 退款单号
	 */
	private String refundOrderId;

	/**
	 * 退款类型 0.线上 1.鲸币 2.线上小程序
	 */
	private Integer refundType;

	/**
	 * 状态 0.失败 1.成功 2.退款中（退款时正好处于建行分账时间会有这个状态）
	 */
	private Integer state;

	/**
	 * 退款金额
	 */
	private BigDecimal amount;

	/**
	 * 
	 */
	private Date createTime;
}
