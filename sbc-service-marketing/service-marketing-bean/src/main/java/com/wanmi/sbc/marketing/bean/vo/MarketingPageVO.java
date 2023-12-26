package com.wanmi.sbc.marketing.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.marketing.bean.enums.MarketingJoinLevel;
import com.wanmi.sbc.marketing.bean.enums.MarketingStatus;
import com.wanmi.sbc.marketing.bean.enums.MarketingSubType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Transient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2018-11-19 11:17
 */
@ApiModel
@Data
public class MarketingPageVO implements Serializable {

    private static final long serialVersionUID = -6883628601339940084L;
    /**
     * 营销Id
     */
    @ApiModelProperty(value = "营销Id")
    private Long marketingId;

    /**
     * 活动名称
     */
    @ApiModelProperty(value = "活动名称")
    private String marketingName;

    /**
     * 活动类型
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
     * 目标客户
     */
    @ApiModelProperty(value = "参加会员", dataType = "com.wanmi.sbc.marketing.bean.enums.MarketingJoinLevel")
    private String joinLevel;

    /**
     * 是否暂停
     */
    @ApiModelProperty(value = "是否暂停")
    private BoolFlag isPause;

    /**
     * 活动状态
     */
    @ApiModelProperty(value = "活动状态")
    private MarketingStatus marketingStatus;

    /**
     * 关联商品
     */
    @ApiModelProperty(value = "关联商品列表")
    private List<MarketingScopeVO> marketingScopeList;

    /**
     * 套装购买商品
     */
    @ApiModelProperty(value = "套装购买商品")
    private List<MarketingSuitDetialVO> marketingSuitDetialList;

    /**
     * joinLevel的衍射属性，获取枚举
     */
    @ApiModelProperty(value = "关联客户等级")
    @Transient
    private MarketingJoinLevel marketingJoinLevel;

    /**
     * joinLevel的衍射属性，marketingJoinLevel == LEVEL_LIST 时，可以获取对应的等级集合
     */
    @ApiModelProperty(value = "关联其他等级的等级id集合")
    @Transient
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
     * 是否为草稿 0：否，1：是
     */
    @ApiModelProperty(value = "是否为草稿 0：否，1：是")
    private BoolFlag isDraft;

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
     * 购买数量
     */
    @ApiModelProperty("购买数量")
    private Integer suitBuyNum;

    /**
     * 每套每人限制购买数量
     */
    @ApiModelProperty(value = "每套每人限制购买数量")
    private Integer suitLimitNum;

    /**
     * 最多购买套数
     */
    @ApiModelProperty("最多购买套数")
    private Integer maxBuyCount;

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
     * 活动商品skuId（后台选择活动时使用）
     */
    @ApiModelProperty("活动商品skuId")
    private String scopeId;

    /**
     * 商品名称（后台选择活动时使用）
     */
    private String goodsInfoName;

    /**
     * 仓库id
     */
    private Long wareId;

    /**
     * 仓库名称
     */
    private String wareName;
    
    /**
     * 店铺Id 
     */
    private Long storeId;
    
    /**
     * 商家名称
     */
    private String supplierName;
    
    /**
     * 店铺名称
     */
    private String storeName;


    /**
     * 获取活动状态
     * @return
     */
    public MarketingStatus getMarketingStatus(){
        if(beginTime != null && endTime != null){
            // 前端格式化使用
            if(BoolFlag.YES.equals(isDraft)){
                return MarketingStatus.NOT_START;
            }
            if(LocalDateTime.now().isBefore(beginTime)){
                return MarketingStatus.NOT_START;
            }else if(LocalDateTime.now().isAfter(endTime)){
                return MarketingStatus.ENDED;
            }else if(isPause == BoolFlag.YES){
                return MarketingStatus.PAUSED;
            }else{
                return MarketingStatus.STARTED;
            }
        }
        return null;
    }
}
