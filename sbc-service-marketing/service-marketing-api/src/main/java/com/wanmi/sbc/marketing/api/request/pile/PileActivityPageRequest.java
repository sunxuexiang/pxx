package com.wanmi.sbc.marketing.api.request.pile;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.marketing.bean.enums.MarketingStatus;
import com.wanmi.sbc.marketing.bean.enums.PileActivityType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * <p></p>
 * author: chenchang
 * Date: 2022-09-06
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel
@Data
public class PileActivityPageRequest extends BaseQueryRequest {
    /**
     * 场次状态
     */
    private static final long serialVersionUID = -1015004949280219152L;


    @ApiModelProperty(value = "店铺id", hidden = true)
    private Long storeId;

    @ApiModelProperty(value = "店铺名称", hidden = true)
    private String storeName;

    /**
     * 囤货活动名称
     */
    @ApiModelProperty(value = "囤货活动名称")
    private String activityName;


    /**
     * 囤货活动类型
     */
    @ApiModelProperty(value = "囤货活动类型")
    private PileActivityType pileActivityType;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime endTime;

    @ApiModelProperty(value = "查询类型")
    private MarketingStatus queryTab;
}
