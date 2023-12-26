package com.wanmi.sbc.order.api.response.trade;

import com.wanmi.sbc.order.bean.vo.NewPileTradeVO;
import com.wanmi.sbc.order.bean.vo.ProviderTradeVO;
import com.wanmi.sbc.order.bean.vo.TradeVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class NewPileTradeGetByIdResponse implements Serializable {

    /**
     * 订单对象
     */
    @ApiModelProperty(value = "订单对象")
    private NewPileTradeVO tradeVO;

    /**
     * 子订单对象
     */
    @ApiModelProperty(value = "子订单对象")
    private List<ProviderTradeVO> providerTradeVOs;
}
