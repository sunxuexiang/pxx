package com.wanmi.sbc.advertising.bean.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.wanmi.sbc.advertising.bean.enums.ActivityState;
import com.wanmi.sbc.advertising.bean.enums.ActivityType;
import com.wanmi.sbc.advertising.bean.enums.ClickJumpType;
import com.wanmi.sbc.advertising.bean.enums.MaterialType;
import com.wanmi.sbc.advertising.bean.enums.PayType;
import com.wanmi.sbc.advertising.bean.enums.SlotType;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class AdActivityDTO {

	@ApiModelProperty("广告活动id")
	private String id;
	
	@ApiModelProperty("父id")
	private String pid;
	
	@ApiModelProperty("广告位id")
	@NotNull
	private Integer slotId;

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
	@NotNull
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

	/**
	 * 商城名称
	 */
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
	 * 商品id
	 */
	private String spuId;
	
	/**
	 * 商品名称
	 */
	private String spuName;

	/**
	 */
	@ApiModelProperty("广告活动类型")
	@NotNull
	private ActivityType activityType;

	/**
	 * 广告活动投放顺序
	 */
	@ApiModelProperty("广告活动投放顺序")
	private Integer advertiseSeq;

	/**
	 * 广告活动开始时间
	 */
	@ApiModelProperty("广告活动开始时间")
	@NotNull
	private Date startTime;

	/**
	 * 广告活动结束时间
	 */
	@ApiModelProperty("广告活动结束时间")
	@NotNull
	private Date endTime;

	/**
	 * 广告活动天数，最小1天
	 */
	@ApiModelProperty("广告活动天数，最小1天")
	@NotNull
	private Integer days;

	/**
	 * 广告创意点击跳转类型
	 */
	@ApiModelProperty("广告创意点击跳转类型")
	private ClickJumpType clickJumpType;

	/**
	 * 广告创意点击跳转信息，与click_jump_type有关
	 */
	@ApiModelProperty("广告创意点击跳转信息，与click_jump_type有关")
	private String clickJumpInfo;

	/**
	 * 总价
	 */
	@ApiModelProperty("总价")
	@NotNull
	private BigDecimal totalPrice;

	/**
	 * 实际价格
	 */
	@ApiModelProperty("实际价格")
	@NotNull
	private BigDecimal realPrice;

	/**
	 * 所属商家id
	 */
	@ApiModelProperty("所属商家id")
	@NotNull
	private Long storeId;

	/**
	 * 所属商家名称
	 */
	@ApiModelProperty("所属商家名称")
	@NotBlank
	private String storeName;

	/**
	 * 购买人名称
	 */
	@ApiModelProperty("购买人名称")
	private String buyUser;

	/**
	 * 购买人id
	 */
	@ApiModelProperty("购买人id")
	private String buyUserId;

	/**
	 * 业务id
	 */
	@ApiModelProperty("业务id")
	private String bizId;

	/**
	 * 广告活动状态
	 */
	@ApiModelProperty("广告活动状态")
	private ActivityState activityState;

	@ApiModelProperty("提交审核时间")
	private Date submitTime;

	/**
	 * 审核时间
	 */
	@ApiModelProperty("审核时间")
	private Date auditTime;

	/**
	 * 审批意见
	 */
	@ApiModelProperty("审批意见")
	private String auditComment;

	/**
	 * 素材url
	 */
	@ApiModelProperty("素材url")
	private String materialUrl;
	/**
	 * 素材文件key
	 */
	@ApiModelProperty("素材文件key")
	private String materialKey;
	
	/**
	 * 素材类型
	 */
	@ApiModelProperty("素材类型")
	private MaterialType materialType;

	/**
	 * 支付类型
	 */
	@ApiModelProperty("支付类型")
	@NotNull
	private PayType payType;
	
	
	/**
	 * 持续时间，秒
	 */
	@ApiModelProperty("持续时间，秒")
	private Integer duration;

	private String updateUser;

	private String createUser;

	private Date updateTime;

	private Date createTime;

	
	@ApiModelProperty("价格详情")
	@Valid
	private List<AdSlotPriceDTO> priceList;

}
