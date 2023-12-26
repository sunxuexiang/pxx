package com.wanmi.sbc.order.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wanmi.sbc.common.annotation.ApiEnumProperty;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.marketing.bean.enums.MarketingSubType;
import com.wanmi.sbc.marketing.bean.vo.CouponCodeVO;
import com.wanmi.sbc.marketing.bean.vo.StoreCouponCodeVO;
import com.wanmi.sbc.order.bean.vo.DiscountsVO;
import com.wanmi.sbc.order.bean.vo.TradeConfirmItemVO;
import com.wanmi.sbc.order.bean.vo.TradeItemVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Author: gaomuwei
 * @Date: Created In 下午5:58 2018/9/28
 * @Description: 订单确认返回结构
 */
@ApiModel
@Data
@JsonIgnoreProperties
public class TradeConfirmResponse {

    /**
     * 按商家拆分后的订单项
     */
    @ApiModelProperty(value = "按商家拆分后的订单项")
    private List<TradeConfirmItemVO> tradeConfirmItems;

    /**
     * 包装费
     */
    @ApiModelProperty(value = "包装费")
    private BigDecimal packingPrice = BigDecimal.ZERO;

    /**
     * 订单总额
     */
    @ApiModelProperty(value = "订单总额")
    private BigDecimal totalPrice = BigDecimal.ZERO;

    /**
     * 商品总额
     */
    @ApiModelProperty(value = "商品总额")
    private BigDecimal goodsTotalPrice = BigDecimal.ZERO;

    /**
     * 囤货已支付金额
     */
    @ApiModelProperty(value = "囤货已支付金额")
    private BigDecimal paidPrice = BigDecimal.ZERO;

    /**
     * 优惠总额
     */
    @ApiModelProperty(value = "优惠总额")
    private BigDecimal discountsTotalPrice = BigDecimal.ZERO;

    /**
     * 满订单优惠
     */
    @ApiModelProperty(value = "满订单优惠")
    private BigDecimal discountsTotalOrderPrice = BigDecimal.ZERO;

    /**
     * 满订单优惠
     */
    @ApiModelProperty(value = "满订单类型(7:满订单减,8：满订单折)")
    private MarketingSubType subType;

    /**
     * 未使用的优惠券
     */
    @ApiModelProperty(value = "未使用的优惠券")
    private List<CouponCodeVO> couponCodes;
    
    /**
     * 按店铺分组的可用优惠券集合
     */
    @ApiModelProperty(value = "按店铺分组的可用优惠券集合")
    private List<StoreCouponCodeVO> availableCouponCodeVOList;
    
    /**
     * 按店铺分组的不可用优惠券集合
     */
    @ApiModelProperty(value = "按店铺分组的不可用优惠券集合")
    private List<StoreCouponCodeVO> unavailableCouponCodeVOList;
    

    /**
     * 小店名称
     */
    @ApiModelProperty(value = "小店名称")
    private String shopName;

    /**
     * 邀请人名称
     */
    @ApiModelProperty(value = "邀请人名称")
    private String inviteeName;

    /**
     * 返利总额
     */
    @ApiModelProperty(value = "返利总额")
    private BigDecimal totalCommission;

    /**
     * 是否开店礼包
     */
    @ApiModelProperty(value = "是否开店礼包")
    private DefaultFlag storeBagsFlag = DefaultFlag.NO;

    /**
     * 是否开团购买(true:开团 false:参团 null:非拼团购买)
     */
    @ApiModelProperty(value = "是否开团购买")
    private Boolean openGroupon;

    /**
     * 商品总件数
     */
    @ApiModelProperty(value = "商品总件数")
    private Long goodsTotalNum;

    /**
     * 拼团活动是否包邮
     */
    @ApiModelProperty(value = "拼团活动是否包邮")
    private boolean grouponFreeDelivery;

    public BigDecimal getPackingPrice() {
        return tradeConfirmItems.stream().map(i -> i.getTradePrice().getPackingPrice())
                .reduce(BigDecimal.ZERO,BigDecimal::add);
    }

    public BigDecimal getTotalPrice() {
        return tradeConfirmItems.stream().map(i -> i.getTradePrice().getTotalPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getGoodsTotalPrice() {
        return tradeConfirmItems.stream().map(i -> i.getTradePrice().getGoodsPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getDiscountsTotalPrice() {
        return tradeConfirmItems.stream().flatMap(i -> i.getDiscountsPrice().stream()).map(DiscountsVO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

//    public BigDecimal getDiscountsTotalOrderPrice() {
//        return tradeConfirmItems.stream().flatMap(i -> i.getDiscountsPrice().stream()
//                .filter(discountsVO -> MarketingSubType.REDUCTION_FULL_ORDER.equals(discountsVO.getSubType()) || MarketingSubType.DISCOUNT_FULL_ORDER.equals(discountsVO.getSubType()))).map(DiscountsVO::getAmount)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//    }
//    public MarketingSubType getSubType(){
//        List<MarketingSubType> collect = tradeConfirmItems.stream().flatMap(i -> i.getDiscountsPrice().stream().filter(discountsVO -> MarketingSubType.REDUCTION_FULL_ORDER.equals(discountsVO.getSubType()) || MarketingSubType.DISCOUNT_FULL_ORDER.equals(discountsVO.getSubType())))
//                .map(DiscountsVO::getSubType).collect(Collectors.toList());
//        if(CollectionUtils.isNotEmpty(collect)){
//            return collect.get(0);
//        }
//        return null;
//    }

    public Long getGoodsTotalNum() {
        Long goodsTotalNum = 0L;
        Optional<Long> _goodsTotalNum = tradeConfirmItems.stream().flatMap(i -> i.getTradeItems().stream())
                .map(TradeItemVO::getNum).reduce((sum, item) -> {
                    sum += item;
                    return sum;
                });
        if (_goodsTotalNum.isPresent()) {
            goodsTotalNum += _goodsTotalNum.get();
        }
        Optional<Long> _giftNum = tradeConfirmItems.stream()
                .filter(v->{
                    if (CollectionUtils.isNotEmpty(v.getGifts())){
                        return true;
                    }
                    return false;
                })
                .flatMap(i -> i.getGifts().stream())
                .map(TradeItemVO::getNum).reduce((sum, item) -> {
                    sum += item;
                    return sum;
                });
        if (_giftNum.isPresent()) {
            goodsTotalNum += _giftNum.get();
        }
        return goodsTotalNum;
    }

}