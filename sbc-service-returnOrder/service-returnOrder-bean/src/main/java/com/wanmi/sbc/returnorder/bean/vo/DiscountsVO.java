package com.wanmi.sbc.returnorder.bean.vo;

import com.wanmi.sbc.marketing.bean.enums.MarketingSubType;
import com.wanmi.sbc.marketing.bean.enums.MarketingType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * <p>订单优惠金额</p>
 * Created by of628-wenzhi on 2018-02-26-下午6:19.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class DiscountsVO {
    /**
     * 营销类型
     */
    @ApiModelProperty(value = "营销类型")
    private MarketingType type;

    /**
     * 优惠金额
     */
    @ApiModelProperty(value = "优惠金额")
    private BigDecimal amount;

    /**
     * 营销子类型
     */
    @ApiModelProperty(value = "营销子类型")
    private MarketingSubType subType;

}
