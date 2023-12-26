package com.wanmi.sbc.marketing.api.request.coupon;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.marketing.bean.dto.CoinActivityStoreDTO;
import com.wanmi.sbc.marketing.bean.dto.GoodsInfoDTO;
import com.wanmi.sbc.marketing.bean.enums.CoinActivityFullType;
import com.wanmi.sbc.marketing.bean.enums.CoinActivityType;
import com.wanmi.sbc.marketing.bean.enums.MarketingJoinLevel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@ApiModel
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoinActivityAddRequest extends BaseRequest {

    private static final long serialVersionUID = -1144976258555148289L;

    /**
     * 优惠券活动名称
     */
    @ApiModelProperty(value = "优惠券活动名称")
    @NotBlank(message = "优惠券名称不能为空")
    private String activityName;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;

    /**
     * 是否暂停 ，1 暂停
     */
    private BoolFlag pauseFlag = BoolFlag.NO;


    /**
     * 商户id
     */
    @ApiModelProperty(value = "店铺id")
    private Long storeId;

    /**
     * 仓库id
     */
    @ApiModelProperty(value = "仓库id")
    @NotNull
    private Long wareId;


    /**
     * 关联的客户等级   -2：指定客户 -1:全部客户 0:全部等级 other:其他等级
     * {@link MarketingJoinLevel}
     */
    @ApiModelProperty(value = "关联的客户等级", dataType = "com.wanmi.sbc.marketing.bean.enums.MarketingJoinLevel")
    @NotBlank
    private String joinLevel;

    @ApiModelProperty(value = "操作人")
    private String createPerson;

    /**
     * 购买指定商品赠券商品信息id列表
     */
    @ApiModelProperty(value = "购买指定商品赠券商品信息id列表 单个活动最多配置100个商品")
    @Size(max = 100)
    //@NotEmpty(message = "指定商品不能为空")
    private List<GoodsInfoDTO> goodsInfos;


    /**
     * 0 任买返
     */
    @ApiModelProperty(value = "0 任买返")
    @NotNull
    private CoinActivityFullType coinActivityFullType;

    /**
     * 是否叠加（0：否，1：是）
     */
    @ApiModelProperty(value = "是否叠加（0：否，1：是）")
    @NotNull(message = "是否叠加不能为空")
    private DefaultFlag isOverlap = DefaultFlag.NO;

    /**
     * 是否叠加（0：否，1：是）
     */
    @ApiModelProperty(value = "鲸币数量")
    @NotNull(message = "鲸币数量不能为空")
    private BigDecimal coinNum;

    @ApiModelProperty(value = "订单赠金币配置的商家")
    private List<CoinActivityStoreDTO> coinActivityStore;
    
    @ApiModelProperty(value = "金币活动类型")
    private CoinActivityType activityType;


}
