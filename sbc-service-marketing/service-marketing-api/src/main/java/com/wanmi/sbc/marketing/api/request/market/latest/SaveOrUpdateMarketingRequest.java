package com.wanmi.sbc.marketing.api.request.market.latest;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SaveOrUpdateMarketingRequest implements Serializable {

    private static final long serialVersionUID = -3237443832349336201L;

    /**
     * 营销Id
     */
    @ApiModelProperty(value = "营销Id")
    private Long marketingId;

    /**
     * 营销名称
     */
    @ApiModelProperty(value = "营销名称")
    @NotBlank
    @Length(max=40)
    private String marketingName;

    /**
     * 营销类型 0：满减 1:满折 2:满赠
     */
    @ApiModelProperty(value = "营销活动类型")
    @NotNull
    private MarketingType marketingType;

    /**
     * 营销子类型 0：满金额减 1：满数量减 2:满金额折 3:满数量折 4:满金额赠 5:满数量赠
     */
    @ApiModelProperty(value = "营销子类型")
    @NotNull
    private MarketingSubType subType;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间")
    @NotNull
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime beginTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间")
    @NotNull
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime endTime;

    /**
     * 是否叠加, 0：否， 1：是
     */
    @ApiModelProperty(value = "是否叠加")
    @NotNull
    private BoolFlag isOverlap;

    /**
     * 是否可跨单品（0：否，1：是）
     */
    @ApiModelProperty(value = "是否可跨单品")
    private BoolFlag isAddMarketingName;

    /**
     * 仓库id
     */
    @ApiModelProperty(value = "仓库id")
    private Long wareId;

    /**
     * 促销目标客户范围Ids
     */
    @ApiModelProperty(value = "促销目标客户范围Id列表,不填或-1为全平台用户")
    private List<String> customerScopeIds;

    /**
     * 商品的范围
     */
    @ApiModelProperty(value = "商品范围")
    private List<MarketingGoodsItemRequest> goodsItemRequest;


    /**
     * 参加会员 0:全部等级  other:其他等级
     */
    @ApiModelProperty(value = "参加会员", dataType = "com.wanmi.sbc.marketing.bean.enums.MarketingJoinLevel")
    @NotBlank
    private String joinLevel;

    /**
     * 是否是商家，0：boss，1：商家
     */
    @ApiModelProperty(value = "是否是商家")
    private BoolFlag isBoss;

    /**
     * 店铺id  0：boss,  other:其他商家
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
     * 参加营销范围 0：全部货品 1：货品 2：品牌 3：分类
     */
    @ApiModelProperty(value = "参加营销范围")
    private MarketingScopeType scopeType;

    /**
     * 草稿
     */
    @ApiModelProperty(value = "草稿")
    private BoolFlag isDraft;

    /**
     * 满减阶梯
     */
    @ApiModelProperty(value = "满减阶梯")
    private List<ReachAmountLevelRequest> reachAmountLevelRequest;

    /**
     * 满折阶梯
     */
    @ApiModelProperty(value = "满折阶梯")
    private List<ReachDiscountLevelRequest> reachDiscountLevelRequests;

    /**
     * 满赠阶梯
     */
    @ApiModelProperty(value = "满赠阶梯")
    private List<ReachGiftLevelRequest> reachGiftLevelRequests;
}
