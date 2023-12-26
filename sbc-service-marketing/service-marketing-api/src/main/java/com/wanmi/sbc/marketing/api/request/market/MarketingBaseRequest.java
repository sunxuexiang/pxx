package com.wanmi.sbc.marketing.api.request.market;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.marketing.bean.dto.FullReductionActivitiesDTO;
import com.wanmi.sbc.marketing.bean.enums.MarketingScopeType;
import com.wanmi.sbc.marketing.bean.enums.MarketingSubType;
import com.wanmi.sbc.marketing.bean.enums.MarketingType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2018-11-16 16:39
 */
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MarketingBaseRequest implements Serializable {

    private static final long serialVersionUID = -2868696387510782396L;
    /**
     * 营销Id
     */
    @ApiModelProperty(value = "营销Id")
    private Long marketingId;

    /**
     * 营销名称
     */
    @ApiModelProperty(value = "营销名称")
    @Length(max=40)
    private String marketingName;

    /**
     * 营销类型 0：满减 1:满折 2:满赠
     */
    @ApiModelProperty(value = "营销活动类型")
    private MarketingType marketingType;

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
     * 参加营销范围 0：全部货品 1：货品 2：品牌 3：分类
     */
    @ApiModelProperty(value = "参加营销范围")
    private MarketingScopeType scopeType;

    /**
     * 参加会员 0:全部等级  other:其他等级
     */
    @ApiModelProperty(value = "参加会员", dataType = "com.wanmi.sbc.marketing.bean.enums.MarketingJoinLevel")
    private String joinLevel;

    /**
     * 是否是商家，0：boss，1：商家
     */
    @ApiModelProperty(value = "是否是商家")
    private BoolFlag isBoss;

    /**
     * 店铺Id  0：boss,  other:其他商家
     */
    @ApiModelProperty(value = "店铺id，0：boss, other:其他商家")
    private Long storeId;

    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String createPerson;

    /**
     * 修改人
     */
    @ApiModelProperty(value = "修改人")
    private String updatePerson;

    /**
     * 删除人
     */
    @ApiModelProperty(value = "删除人")
    private String deletePerson;

    /**
     * 营销子类型 0：满金额减 1：满数量减 2:满金额折 3:满数量折 4:满金额赠 5:满数量赠
     */
    @ApiModelProperty(value = "营销子类型")
    private MarketingSubType subType;

    /**
     * 营销范围Id
     */
    @ApiModelProperty(value = "营销范围Id列表")
    private List<String> skuIds;

    /**
     * 捆绑销售，促销范围Id
     */
    private List<FullReductionActivitiesDTO> bundleSalesSkuIds;

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
     * 顶部头图
     */
    @ApiModelProperty("顶部头图")
    private String suitTopImage;


    /**
     * 仓库id
     */
    @ApiModelProperty("仓库id")
    private Long wareId;
}
