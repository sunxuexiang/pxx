package com.wanmi.sbc.marketing.api.request.marketingpurchaselimit;

import com.wanmi.sbc.common.enums.BoolFlag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
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
public class MarketingPurchaseLimitRequest implements Serializable {
    /**
     * 活动名称
     */
    @ApiModelProperty(value = "促销Id")
    private Long marketingId;

    @ApiModelProperty(value = "商品ID")
    private List<String> goodsInfoIds;

}
