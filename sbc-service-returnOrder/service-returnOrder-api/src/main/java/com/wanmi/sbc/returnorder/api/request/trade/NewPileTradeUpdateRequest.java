package com.wanmi.sbc.returnorder.api.request.trade;

import com.wanmi.sbc.returnorder.bean.dto.NewPileTradeUpdateDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class NewPileTradeUpdateRequest implements Serializable {
    /**
     * 交易单
     */
    @ApiModelProperty(value = "交易单")
    private NewPileTradeUpdateDTO trade;
}
