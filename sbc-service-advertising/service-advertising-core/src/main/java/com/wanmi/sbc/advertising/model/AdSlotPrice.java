package com.wanmi.sbc.advertising.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.wanmi.sbc.advertising.bean.enums.SlotSeqState;
import com.wanmi.sbc.advertising.bean.enums.UnitType;

import lombok.Data;

/**
 * @author zc
 *
 */
@Data
@Entity
@Table(name = "ad_slot_date_price")
public class AdSlotPrice {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/**
	 * 广告位id
	 */
	private Integer slotId;

	/**
	 * 单位价格
	 */
	private BigDecimal unitPrice;

	/**
	 * 单位
	 */
	private UnitType unit;

	/**
	 * 日期
	 */
	private Date effectiveDate;

	/**
	 * 状态
	 */
	private SlotSeqState state;
	
	private String actId;

	private Date createTime;

}
