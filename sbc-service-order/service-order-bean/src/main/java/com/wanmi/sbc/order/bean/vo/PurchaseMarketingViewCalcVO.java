package com.wanmi.sbc.order.bean.vo;

import com.wanmi.sbc.marketing.bean.enums.MarketingSubType;
import com.wanmi.sbc.marketing.bean.enums.MarketingType;
import com.wanmi.sbc.marketing.bean.vo.MarketingFullDiscountLevelVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingFullGiftLevelVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingFullGiftLevelViewVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingFullReductionLevelVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p></p>
 * author: sunkun
 * Date: 2018-12-03
 */
@Data
@ApiModel
public class PurchaseMarketingViewCalcVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 采购单参与营销活动的商品skuId
     */
    @ApiModelProperty(value = "采购单参与营销活动的商品skuId")
    private List<String> goodsInfoIds;

    /**
     * 采购单达到营销级别的差额，满金额是缺少的金额，满数量是缺少的数量
     */
    @ApiModelProperty(value = "采购单达到营销级别的差额")
    private BigDecimal lack;

    /**
     * 营销id
     */
    @ApiModelProperty(value = "营销id")
    private Long marketingId;

    /**
     * 促销类型 0：满减 1:满折 2:满赠
     */
    @ApiModelProperty(value = "促销类型")
    private MarketingType marketingType;

    /**
     * 促销子类型 0：满金额减 1：满数量减 2:满金额折 3:满数量折 4:满金额赠 5:满数量赠
     */
    @ApiModelProperty(value = "促销子类型")
    private MarketingSubType subType;

    /**
     * 满减等级，没有满足等级的则为最小等级，满足等级的则为满足等级里的最大一个
     */
    @ApiModelProperty(value = "满减等级")
    private MarketingFullReductionLevelVO fullReductionLevel;

    /**
     * 满折等级，没有满足等级的则为最小等级，满足等级的则为满足等级里的最大一个
     */
    @ApiModelProperty(value = "满折等级")
    private MarketingFullDiscountLevelVO fullDiscountLevel;

    /**
     * 满赠等级，没有满足等级的则为最小等级，满足等级的则为满足等级里的最大一个
     */
    @ApiModelProperty(value = "满赠等级")
    private MarketingFullGiftLevelViewVO fullGiftLevel;

    /**
     * 商家Id  0：boss,  other:其他商家
     */
    @ApiModelProperty(value = "商家Id")
    private Long storeId;

    /**
     * 满赠营销下所有等级列表
     */
    @ApiModelProperty(value = "满赠营销下所有等级列表")
    private List<MarketingFullGiftLevelVO> fullGiftLevelList;

    /**
     * 采购单参与营销活动的商品总数
     */
    @ApiModelProperty(value = "采购单参与营销活动的商品总数")
    private Long totalCount;

    /**
     * 采购单参与营销活动的商品总金额
     */
    @ApiModelProperty(value = "采购单参与营销活动的商品总金额")
    private BigDecimal totalAmount = BigDecimal.ZERO;

    /**
     * 优惠力度，满折是折扣，满减是减多少
     */
    @ApiModelProperty(value = "优惠力度，满折是折扣，满减是减多少")
    private BigDecimal discount;

    /**
     * 满减等级
     */
    @ApiModelProperty(value = "营销满减多级优惠列表")
    private List<MarketingFullReductionLevelVO> fullReductionLevelList;

    /**
     * 满折等级
     */
    @ApiModelProperty(value = "营销满折多级优惠列表")
    private List<MarketingFullDiscountLevelVO> fullDiscountLevelList;
}
