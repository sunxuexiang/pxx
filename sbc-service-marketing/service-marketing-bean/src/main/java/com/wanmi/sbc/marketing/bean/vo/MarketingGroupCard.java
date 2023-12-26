package com.wanmi.sbc.marketing.bean.vo;

import com.wanmi.sbc.goods.bean.vo.DevanningGoodsInfoVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 营销组下商品列表信息
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketingGroupCard implements Serializable {

    private static final long serialVersionUID = -2644568383561560425L;

    /**
     * 优惠前的商品总价格
     */
    private BigDecimal beforeAmount;
    /**
     * 优惠后的商品总价格
     */
    private BigDecimal payableAmount;
    /**
     * 优惠金额
     */
    private BigDecimal profitAmount;

    /**
     * 是否已达到活动门槛
     */
    private Boolean reachLevel = Boolean.FALSE;

    /**
     * 活动叠加倍数
     */
    private BigDecimal reMarketingFold;

    /**
     * 离下一门槛的差值
     *  满数量：差多少件
     *  满金额：差多少钱
     */
    private BigDecimal diffNextLevel;

    /**
     * 当前使用的营销活动
     */
    private MarketingVO marketingVO;

    /**
     * 商品列表
     */
    public List<DevanningGoodsInfoVO> devanningGoodsInfoVOList;

    /**
     * 超过营销限购数量，按照原价购买的商品列表
     */
    public List<DevanningGoodsInfoVO> overPurchuseLimitDevanningGoodsInfoVOList;

    /*--------------------------------------------------当前门槛 -----------------------------------------------------------------------------------*/
    /**
     * 满减
     */
    @ApiModelProperty(value = "满减-当前门槛")
    private MarketingFullReductionLevelVO currentFullReductionLevel;

    /**
     * 满折
     */
    @ApiModelProperty(value = "满折-当前门槛")
    private MarketingFullDiscountLevelVO currentFullDiscountLevel;

    /**
     * 满赠
     */
    @ApiModelProperty(value = "满赠-当前门槛")
    private MarketingFullGiftLevelVO currentFullGiftLevel;

    /*--------------------------------------------------下一门槛 -----------------------------------------------------------------------------------*/
    /**
     * 满减
     */
    @ApiModelProperty(value = "满减-下一门槛")
    private MarketingFullReductionLevelVO nextFullReductionLevel;

    /**
     * 满折
     */
    @ApiModelProperty(value = "满折-下一门槛")
    private MarketingFullDiscountLevelVO nextFullDiscountLevel;

    /**
     * 满赠
     */
    @ApiModelProperty(value = "满赠-下一门槛")
    private MarketingFullGiftLevelVO nextFullGiftLevel;

}
