package com.wanmi.sbc.marketing.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoPurchaseResponseVO;
import com.wanmi.sbc.marketing.bean.enums.MarketingScopeType;
import com.wanmi.sbc.marketing.bean.enums.MarketingSubType;
import com.wanmi.sbc.marketing.bean.enums.MarketingType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@ApiModel
@Data
public class MarketingPurchaseLimitVO implements Serializable {


    private static final long serialVersionUID = 2104678484558317735L;


    @ApiModelProperty(value = "营销Id")
    private Long id;


    @ApiModelProperty(value = "customerId")
    private String customerId;


    @ApiModelProperty(value = "marketingId")
    private Long marketingId;



    @ApiModelProperty(value = "goodsInfoId")
    private String goodsInfoId;


    @ApiModelProperty(value = "tradeId")
    private String tradeId;


    @ApiModelProperty(value = "num")
    private BigDecimal num;



    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;



}
