package com.wanmi.sbc.marketing.bean.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.marketing.bean.enums.MarketingJoinLevel;
import com.wanmi.sbc.marketing.bean.enums.MarketingScopeType;
import com.wanmi.sbc.marketing.bean.enums.MarketingSubType;
import com.wanmi.sbc.marketing.bean.enums.MarketingType;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2018-11-19 14:13
 */
@ApiModel
@Data
public class MarketingVO implements Serializable {

	private static final long serialVersionUID = 5114164129691782553L;
	/**
	 * 营销Id
	 */
	@ApiModelProperty(value = "营销Id")
	private Long marketingId;

	/**
	 * 营销名称
	 */
	@ApiModelProperty(value = "营销名称")
	private String marketingName;

	/**
	 * 营销类型 0：满减 1:满折 2:满赠
	 */
	@ApiModelProperty(value = "营销活动类型")
	private MarketingType marketingType;

	/**
	 * 营销子类型 0：满金额减 1：满数量减 2:满金额折 3:满数量折 4:满金额赠 5:满数量赠
	 */
	@ApiModelProperty(value = "营销子类型")
	private MarketingSubType subType;

	/**
	 * 开始时间
	 */
	@ApiModelProperty(value = "开始时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime beginTime;

	/**
	 * 结束时间
	 */
	@ApiModelProperty(value = "结束时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime endTime;

	/**
	 * 参加营销类型 0：全部货品 1：货品 2：品牌 3：分类
	 */
	@ApiModelProperty(value = "参加营销类型")
	private MarketingScopeType scopeType;

	/**
	 * 参加会员 0:全部等级 other:其他等级
	 */
	@ApiModelProperty(value = "参加会员", dataType = "com.wanmi.sbc.marketing.bean.enums.MarketingJoinLevel")
	private String joinLevel;

	/**
	 * 是否是商家，0：商家，1：boss
	 */
	@ApiModelProperty(value = "是否是商家")
	private BoolFlag isBoss;

	/**
	 * 商家Id 0：boss, other:其他商家
	 */
	@ApiModelProperty(value = "商家Id，0：boss, other:其他商家")
	private Long storeId;

	/**
	 * 删除标记 0：正常，1：删除
	 */
	@ApiModelProperty(value = "是否已删除")
	private DeleteFlag delFlag;

	/**
	 * 是否暂停 0：正常，1：暂停
	 */
	@ApiModelProperty(value = "是否暂停")
	private BoolFlag isPause;

	/**
	 * 是否为草稿 0：不是草稿，1：是草稿
	 */
	@ApiModelProperty(value = "是否为草稿")
	private BoolFlag isDraft;

	/**
	 * 是否叠加, 0：否， 1：是
	 */
	@ApiModelProperty(value = "是否叠加")
	private BoolFlag isOverlap;

	/**
	 * 是否可跨单品（0：否，1：是）
	 */
	@ApiModelProperty(value = "是否可跨单品")
	private BoolFlag isAddMarketingName;

	/**
	 * 创建时间
	 */
	@ApiModelProperty(value = "创建时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime createTime;

	/**
	 * 创建人
	 */
	@ApiModelProperty(value = "创建人")
	private String createPerson;

	/**
	 * 修改时间
	 */
	@ApiModelProperty(value = "修改时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime updateTime;

	/**
	 * 修改人
	 */
	@ApiModelProperty(value = "修改人")
	private String updatePerson;

	/**
	 * 删除时间
	 */
	@ApiModelProperty(value = "删除时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime deleteTime;

	/**
	 * 删除人
	 */
	@ApiModelProperty(value = "删除人")
	private String deletePerson;

	/**
	 * 关联商品
	 */
	@ApiModelProperty(value = "营销关联商品列表")
	private List<MarketingScopeVO> marketingScopeList;

	/**
	 * 套装商品id
	 */
	@ApiModelProperty(value = "套装商品id")
	private List<MarketingSuitDetialVO> marketingSuitDetialVOList;

	/**
	 * joinLevel的衍射属性，获取枚举
	 */
	@ApiModelProperty(value = "关联客户等级")
	private MarketingJoinLevel marketingJoinLevel;

	/**
	 * joinLevel的衍射属性，marketingJoinLevel == LEVEL_LIST 时，可以获取对应的等级集合
	 */
	@ApiModelProperty(value = "关联其他等级的等级id集合")
	private List<Long> joinLevelList;

	/**
	 * 真正的终止时间
	 */
	@ApiModelProperty(value = "真正的终止时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime realEndTime;

	/**
	 * 是否终止 0：未终止，1：已终止
	 */
	@ApiModelProperty(value = "是否终止 0：未终止，1：已终止")
	private BoolFlag terminationFlag;

	/**
	 * 当前活动是否有必选商品
	 */
	@ApiModelProperty(value = "当前活动是否有必选商品 0:没有 1:有")
	private BoolFlag whetherChoice;

	/**
	 * 下单数量
	 */
	@ApiModelProperty(value = "下单数量")
	private Integer suitBuyNum;

	/**
	 * 每套每人限制购买数量
	 */
	@ApiModelProperty(value = "每套每人限制购买数量")
	private Integer suitLimitNum;

	/**
	 * 套装价格
	 */
	@ApiModelProperty("套装价格")
	private BigDecimal suitPrice;

	/**
	 * 套装原价
	 */
	@ApiModelProperty("套装原价")
	private BigDecimal suitInitPrice;

	/**
	 * 优惠标签
	 */
	@ApiModelProperty("优惠标签")
	private String suitCouponLabel;

	/**
	 * 优惠文案
	 */
	@ApiModelProperty("优惠文案")
	private String suitCouponDesc;

	/**
	 * 营销头图
	 */
	@ApiModelProperty("营销头图")
	private String suitMarketingBanner;

	/**
	 * 顶部头图
	 */
	@ApiModelProperty("顶部头图")
	private String suitTopImage;

	/**
	 * 仓库id
	 */
	@ApiModelProperty("仓库id")
	private Long wareId;

	@ApiModelProperty("满折")
	private List<MarketingFullDiscountLevelVO> marketingFullDiscountLevels;

	@ApiModelProperty("满赠")
	private List<MarketingFullGiftLevelVO> marketingFullGiftLevels;

	@ApiModelProperty("满减")
	private List<MarketingFullReductionLevelVO> marketingFullReductionLevels;

	/**
	 * 获取活动状态
	 * 
	 * @return
	 */
	public String getMarketingStatus() {
		if (beginTime != null && endTime != null) {
			// 前端格式化使用
			if (BoolFlag.YES.equals(isDraft)) {
				return "未开始"; // MarketingStatus.NOT_START;
			}
			if (LocalDateTime.now().isBefore(beginTime)) {
				return "未开始"; // MarketingStatus.NOT_START;
			} else if (LocalDateTime.now().isAfter(endTime)) {
				return "已结束"; // MarketingStatus.ENDED;
			} else if (isPause == BoolFlag.YES) {
				return "暂停中"; // MarketingStatus.PAUSED;
			} else {
				return "进行中"; // MarketingStatus.STARTED;
			}
		}
		return null;
	}

}
