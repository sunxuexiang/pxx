  package com.wanmi.sbc.advertising.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "ad_click")
public class AdClick {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/**
	 * 广告活动id
	 */
	private String activityId;

	/**
	 * 点击时间
	 */
	private Date clickTime;

	/**
	 * 地理位置
	 */
	private String geoPosition;

	/**
	 * 设备型号
	 */
	private String phoneModel;

	/**
	 * 用户账号，登陆后有
	 */
	private String userAccount;

	private Date createTime;

}
