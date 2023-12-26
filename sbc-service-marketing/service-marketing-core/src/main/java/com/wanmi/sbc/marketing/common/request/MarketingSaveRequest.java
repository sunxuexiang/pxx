package com.wanmi.sbc.marketing.common.request;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.marketing.bean.enums.MarketingScopeType;
import com.wanmi.sbc.marketing.bean.enums.MarketingSubType;
import com.wanmi.sbc.marketing.bean.enums.MarketingType;
import com.wanmi.sbc.marketing.common.model.entity.FullReductionActivities;
import com.wanmi.sbc.marketing.common.model.root.MarketingScope;
import com.wanmi.sbc.marketing.common.model.root.Marketing;
import com.wanmi.sbc.marketing.common.model.validgroups.NotMarketingId;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotBlank;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.Convert;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 营销满折规则
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarketingSaveRequest {
    /**
     * 促销Id
     */
    @NotNull(groups = {NotMarketingId.class})
    private Long marketingId;

    /**
     * 促销名称
     */
    @NotBlank
    @Length(max=40)
    private String marketingName;

    /**
     * 促销类型 0：满减 1:满折 2:满赠
     */
    @NotNull
    private MarketingType marketingType;

    /**
     * 开始时间
     */
    @NotNull
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime beginTime;

    /**
     * 结束时间
     */
    @NotNull
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime endTime;

    /**
     * 参加促销类型 0：全部货品 1：货品 2：品牌 3：分类
     */
    @NotNull
    private MarketingScopeType scopeType;

    /**
     * 参加会员 0:全部等级  other:其他等级
     */
    @NotBlank
    private String joinLevel;

    /**
     * 是否是商家，0：boss，1：商家
     */
    private BoolFlag isBoss;

    /**
     * 商家Id  0：boss,  other:其他商家
     */
    private Long storeId;

    /**
     * 是否叠加, 0：否， 1：是
     */
    private BoolFlag isOverlap;

    /**
     * 是否可跨单品（0：否，1：是）
     */
    @ApiModelProperty(value = "是否可跨单品")
    private BoolFlag isAddMarketingName;

    /**
     * 创建人
     */
    private String createPerson;

    /**
     * 修改人
     */
    private String updatePerson;

    /**
     * 删除人
     */
    private String deletePerson;

    /**
     * 促销子类型 0：满金额减 1：满数量减 2:满金额折 3:满数量折 4:满金额赠 5:满数量赠
     */
    @NotNull
    private MarketingSubType subType;

    /**
     * 促销范围Id
     */
    private List<String> skuIds;

    /**
     * 捆绑销售，促销范围Id
     */
    private List<FullReductionActivities> bundleSalesSkuIds;

    /**
     * 套装价格
     */
    @ApiModelProperty("套装价格")
    private BigDecimal suitPrice;

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
     * 仓库id
     */
    @ApiModelProperty("仓库id")
    private Long wareId;

    /**
     * 顶部头图
     */
    @ApiModelProperty("顶部头图")
    private String suitTopImage;

    public Marketing generateMarketing() {
        Marketing marketing = new Marketing();

        marketing.setMarketingName(marketingName);
        marketing.setMarketingType(marketingType);
        marketing.setSubType(subType);
        marketing.setBeginTime(beginTime);
        marketing.setEndTime(endTime);
        marketing.setScopeType(scopeType);
        marketing.setJoinLevel(joinLevel);
        marketing.setIsBoss(isBoss);
        marketing.setStoreId(storeId);
        marketing.setIsOverlap(isOverlap);
        marketing.setIsAddMarketingName(isAddMarketingName);
        marketing.setCreatePerson(createPerson);
        marketing.setUpdatePerson(updatePerson);
        marketing.setDeletePerson(deletePerson);
        //套装活动专属
        marketing.setSuitPrice(suitPrice);
        marketing.setSuitBuyNum(suitBuyNum);
        marketing.setSuitLimitNum(suitLimitNum);
        marketing.setSuitCouponDesc(suitCouponDesc);
        marketing.setSuitCouponLabel(suitCouponLabel);
        marketing.setSuitMarketingBanner(suitMarketingBanner);
        marketing.setSuitTopImage(suitTopImage);
        marketing.setWareId(wareId);

        return marketing;
    }

    public List<MarketingScope> generateMarketingScopeList(Long marketingId) {
        //兼容没有传必选商品的情况
        if (CollectionUtils.isEmpty(bundleSalesSkuIds)) {
            return bundleSalesSkuIds.stream().map((skuId) -> {
                MarketingScope scope = new MarketingScope();
                scope.setMarketingId(marketingId);
                scope.setScopeId(skuId.getSkuIds());
                scope.setWhetherChoice(BoolFlag.NO);
                if(Objects.nonNull(skuId.getPurchaseNum())){
                    if(skuId.getPurchaseNum() > -1){
                        scope.setPurchaseNum(skuId.getPurchaseNum());
                    }
                }
                return scope;
            }).collect(Collectors.toList());
            }else {
                return bundleSalesSkuIds.stream().map((skuId) -> {
                    MarketingScope scope = new MarketingScope();
                    scope.setMarketingId(marketingId);
                    scope.setScopeId(skuId.getSkuIds());
                    scope.setWhetherChoice(skuId.getWhetherChoice());
                    if(Objects.nonNull(skuId.getPurchaseNum())){
                        if(skuId.getPurchaseNum() > -1){
                            scope.setPurchaseNum(skuId.getPurchaseNum());
                        }
                    }
                    return scope;
                }).collect(Collectors.toList());
            }
        }
}
