package com.wanmi.sbc.advertising.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.wanmi.sbc.advertising.bean.enums.PayType;

import lombok.Data;

@Data
@Entity
@Table(name = "ad_pay_record")
public class AdPayRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/**
	 * 广告活动id
	 */
	private String activityId;

	/**
	 * 支付订单号
	 */
	private String payOrderId;

	/**
	 * 支付类型 0.线上 1.鲸币
	 */
	private PayType payType;

	/**
	 * 状态 0.失败 1.成功
	 */
	private Integer state;

	/**
	 * 支付金额
	 */
	private BigDecimal amount;


	private Date createTime;

}
