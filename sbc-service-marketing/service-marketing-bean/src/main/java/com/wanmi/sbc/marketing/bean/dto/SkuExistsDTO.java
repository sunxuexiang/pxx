package com.wanmi.sbc.marketing.bean.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.marketing.bean.enums.MarketingSubType;
import com.wanmi.sbc.marketing.bean.enums.MarketingType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2018-11-19 9:38
 */
@ApiModel
@Data
public class SkuExistsDTO implements Serializable {


    /**
     * skuId集合，逗号分割
     */
    @ApiModelProperty(value = "skuId集合，逗号分割")
    List<String> skuIds;

    /**
     * marketId集合[套装活动]
     */
    @ApiModelProperty(value = "marketId集合[套装活动]")
    List<Long> marketIds;

    /**
     * skuId集合，逗号分割
     */
    @ApiModelProperty(value = "捆绑销售，skuId集合，逗号分割")
    List<FullReductionActivitiesDTO> bundleSalesSkuIds;


    /**
     * 营销类型
     */
    @ApiModelProperty(value = "营销活动类型")
    @NotNull
    MarketingType marketingType;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @NotNull
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @NotNull
    private LocalDateTime endTime;

    /**
     * 需要排除的营销Id，比如编辑时的自己
     */
    @ApiModelProperty(value = "需要排除的营销Id，比如编辑时的自己")
    Long excludeId;

    /**
     * 子类型
     */
    private MarketingSubType marketingSubType;

}
