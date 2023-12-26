package com.wanmi.sbc.marketing.api.request.market.latest;

import com.wanmi.sbc.marketing.bean.enums.GiftType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReachGiftLevelRequest implements Serializable {

    private static final long serialVersionUID = -5459235811570283242L;

    @ApiModelProperty(value = "满赠梯度阈值")
    @NotBlank
    private BigDecimal threshold;

    /**
     * 满赠类型 0：全赠  1：赠一个
     */
    @ApiModelProperty(value = "满赠类型")
    @NotNull
    private GiftType giftType;

    /**
     * 赠品列表
     */
    @ApiModelProperty(value = "赠品列表")
    private List<MarketingGiveGoodItemRequest> marketingGiveGoodItemRequest;
}
