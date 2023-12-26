package com.wanmi.sbc.marketing.api.request.coupon;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.marketing.bean.enums.CoinActivityType;
import com.wanmi.sbc.marketing.bean.enums.MarketingStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@ApiModel
@Data
public class CoinActivityPageRequest extends BaseQueryRequest {

    private static final long serialVersionUID = 4243718077145628609L;


    @ApiModelProperty(value = "店铺id")
    private Long storeId;

    /**
     *优惠券活动名称
     */
    @ApiModelProperty(value = "活动名称")
    private String activityName;

    /**
     *开始时间
     */
    @ApiModelProperty(value = "开始时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime startTime;

    /**
     *结束时间
     */
    @ApiModelProperty(value = "结束时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime endTime;

    /**
     * 仓库iD
     */
    @ApiModelProperty(value = "仓库iD")
    private Long wareId;

    /**
     * 查询页面
     */
    @ApiModelProperty(value = "查询类型")
    private MarketingStatus queryTab = MarketingStatus.ALL;

    @ApiModelProperty(value = "erp编码")
    private String erpGoodsInfoNo;

    @ApiModelProperty(value = "goodsInfoIds")
    private List<String> goodsInfoIds;

    @ApiModelProperty(value = "营销Ids")
    private List<String> activityIds;
    
    @ApiModelProperty(value = "金币活动类型")
    private CoinActivityType activityType;
    
    @ApiModelProperty(value = "店铺名称")
    private String storeName;

}
