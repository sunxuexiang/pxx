package com.wanmi.sbc.marketing.api.request.market.latest;

import com.wanmi.sbc.common.enums.BoolFlag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketingCalculatorRequest implements Serializable {

    private static final long serialVersionUID = -494737949098158474L;

    /**
     * 商品的ID
     */
    private String goodsInfoId;
    /**
     * 购买数量
     */
    private Long buyCount = 0L;

    /**
     * 客户收货地址省
     */
    @ApiModelProperty(value = "省")
    private Long provinceId;

    /**
     * 客户收货地址市
     */
    @ApiModelProperty(value = "市")
    private Long cityId;

    private BoolFlag isPileShopcart;
}
