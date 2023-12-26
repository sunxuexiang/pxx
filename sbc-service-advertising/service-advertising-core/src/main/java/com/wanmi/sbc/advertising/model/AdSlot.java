package com.wanmi.sbc.advertising.model;

import java.util.Date;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.wanmi.sbc.advertising.bean.enums.SlotState;
import com.wanmi.sbc.advertising.bean.enums.SlotType;
import com.wanmi.sbc.advertising.enumconverter.SlotTypeConverter;

import lombok.Data;

/**
 * @author zc
 *
 */
@Data
@Entity
@Table(name = "ad_slot")
public class AdSlot {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/**
	 * 广告位名称
	 */
	private String slotName;

	/**
	 * 广告位描述
	 */
	private String slotDesc;

	/**
	 * 广告位类型
	 */
	@Convert(converter = SlotTypeConverter.class)
	private SlotType slotType;

	/**
	 * 广告位在同一组广告位中的序号
	 */
	private Integer slotGroupSeq;


	
	/**
	 * 批发市场省id
	 */
	private Integer provinceId;

	/**
	 * 批发市场id
	 */
	private Integer marketId;
	
	/**
	 * 批发市场名称
	 */
	private String marketName;

	/**
	 * 商城id
	 */
	private Integer mallTabId;
	
	/**
	 * 商城名称
	 */
	private String mallTabName;
	
	/**
	 * 商品类目id
	 */
	private Integer goodsCateId;

	/**
	 * 商品类目名称
	 */
	private String goodsCateName;
	
	

	/**
	 * 角标图片地址
	 */
	private String cornerMarkUrl;

	/**
	 * 角标图片名
	 */
	private String cornerMarkKey;

	/**
	 * 状态
	 */
	private SlotState slotState;
	
	/**
	 * 上架时间
	 */
	private Date addedTime;

	private String updateUser;

	private String createUser;

	private Date updateTime;

	private Date createTime;

}
