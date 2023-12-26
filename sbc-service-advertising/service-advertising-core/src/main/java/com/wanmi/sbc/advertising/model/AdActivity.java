package com.wanmi.sbc.advertising.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.wanmi.sbc.advertising.bean.enums.ActivityState;
import com.wanmi.sbc.advertising.bean.enums.ActivityType;
import com.wanmi.sbc.advertising.bean.enums.ClickJumpType;
import com.wanmi.sbc.advertising.bean.enums.MaterialType;
import com.wanmi.sbc.advertising.bean.enums.PayType;
import com.wanmi.sbc.advertising.bean.enums.SlotType;
import com.wanmi.sbc.advertising.enumconverter.ActivityStateConverter;
import com.wanmi.sbc.advertising.enumconverter.SlotTypeConverter;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zc
 *
 */
@Data
@Entity
@Table(name = "ad_activity")
public class AdActivity {

	@Id
	private String id;

	private String pid;

	/**
	 * 广告位id
	 */
	private Integer slotId;

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
	 * 商品id
	 */
	private String spuId;

	/**
	 * 商品名称
	 */
	private String spuName;

	/**
	 * 广告活动类型
	 */
	private ActivityType activityType;

	/**
	 * 广告活动投放顺序
	 */
	private Integer advertiseSeq;

	/**
	 * 广告活动开始时间
	 */
	private Date startTime;

	/**
	 * 广告活动结束时间
	 */
	private Date endTime;

	/**
	 * 广告活动天数，最小1天
	 */
	private Integer days;

	/**
	 * 跳转类型
	 */
	private ClickJumpType clickJumpType;

	/**
	 * 跳转信息，与click_jump_type有关
	 */
	private String clickJumpInfo;

	/**
	 * 总价
	 */
	private BigDecimal totalPrice;

	/**
	 * 实际价格
	 */
	private BigDecimal realPrice;

	/**
	 * 所属商家id
	 */
	private Long storeId;

	/**
	 * 所属商家名称
	 */
	private String storeName;

	/**
	 * 购买人名称
	 */
	private String buyUser;

	/**
	 * 购买人id
	 */
	private String buyUserId;

	/**
	 * 业务id
	 */
	private String bizId;

	/**
	 * 广告活动状态
	 */
	@Convert(converter = ActivityStateConverter.class)
	private ActivityState activityState;

	/**
	 * 提交审核时间
	 */
	private Date submitTime;

	/**
	 * 审核时间
	 */
	private Date auditTime;

	/**
	 * 审批意见
	 */
	private String auditComment;

	/**
	 * 素材url
	 */
	private String materialUrl;
	/**
	 * 素材文件key
	 */
	private String materialKey;

	/**
	 * 素材类型
	 */
	private MaterialType materialType;

	/**
	 * 支付类型 0.线上 1.鲸币
	 */
	private PayType payType;

	/**
	 * 持续时间，秒
	 */
	private Integer duration;

	private String updateUser;

	private String createUser;

	private Date updateTime;

	private Date createTime;

}
