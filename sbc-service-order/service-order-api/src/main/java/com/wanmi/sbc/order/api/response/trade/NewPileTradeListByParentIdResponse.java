package com.wanmi.sbc.order.api.response.trade;

import com.wanmi.sbc.order.bean.vo.NewPileTradeVO;
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
public class NewPileTradeListByParentIdResponse implements Serializable {

    private static final long serialVersionUID = -4124020395904412846L;

    /**
     * 订单对象集合
     */
    @ApiModelProperty(value = "订单对象结合")
    private List<NewPileTradeVO> tradeVOList;
}
