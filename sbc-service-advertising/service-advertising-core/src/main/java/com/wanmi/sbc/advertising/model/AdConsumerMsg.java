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
@Table(name = "ad_consumer_msg")
public class AdConsumerMsg {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/**
	 * 消息id
	 */
	private String msgId;

	/**
	 * 消息内容
	 */
	private String payload;

	/**
	 * 
	 */
	private Date createTime;
}
