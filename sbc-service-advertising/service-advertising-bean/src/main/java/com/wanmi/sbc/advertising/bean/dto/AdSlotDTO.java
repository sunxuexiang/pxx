package com.wanmi.sbc.advertising.bean.dto;

import java.util.Date;

import javax.validation.constraints.NotNull;

import com.wanmi.sbc.advertising.bean.enums.SlotState;
import com.wanmi.sbc.advertising.bean.enums.SlotType;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zc
 *
 */
@ApiModel
@Data
public class AdSlotDTO {

	@ApiModelProperty("广告位id")
	private Integer id;

	/**
	 * 广告位名称
	 */
	@ApiModelProperty("广告位名称")
	private String slotName;

	/**
	 * 广告位描述
	 */
	@ApiModelProperty("广告位描述")
	private String slotDesc;

	/**
	 * 广告位类型
	 */
	@ApiModelProperty("广告位类型")
	@NotNull
	private SlotType slotType;

	/**
	 * 广告位在同一组广告位中的序号
	 */
	@ApiModelProperty("广告位在同一组广告位中的序号")
	private Integer slotGroupSeq;

	@ApiModelProperty("批发市场省id")
	private Integer provinceId;

	/**
	 * 批发市场id
	 */
	@ApiModelProperty("批发市场id")
	private Integer marketId;

	/**
	 * 批发市场名称
	 */
	@ApiModelProperty("批发市场名称")
	private String marketName;

	/**
	 * 商城id
	 */
	@ApiModelProperty("商城id")
	private Integer mallTabId;

	@ApiModelProperty("商城名称")
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
	@ApiModelProperty("角标图片地址")
	private String cornerMarkUrl;

	/**
	 * 角标图片名
	 */
	@ApiModelProperty("角标图片名")
	private String cornerMarkKey;

	/**
	 * 广告位状态
	 */
	@ApiModelProperty("广告位状态")
	private SlotState slotState;

	/**
	 * 上架时间
	 */
	@ApiModelProperty("上架时间")
	private Date addedTime;

	private String updateUser;

	private String createUser;

	private Date updateTime;

	private Date createTime;

	@ApiModelProperty("可选天数")
	private Long availableDaysCount;
	
	@ApiModelProperty("已占天数")
	private Long occupiedDaysCount;

}
