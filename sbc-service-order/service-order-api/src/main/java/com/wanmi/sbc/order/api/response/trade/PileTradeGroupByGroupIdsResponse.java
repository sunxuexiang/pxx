package com.wanmi.sbc.order.api.response.trade;

import com.wanmi.sbc.order.bean.vo.PileTradeGroupVO;
import com.wanmi.sbc.order.bean.vo.TradeGroupVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class PileTradeGroupByGroupIdsResponse implements Serializable {
    private static final long serialVersionUID = 2703472207617430180L;

    /**
     * 分页数据
     */
    @ApiModelProperty(value = "分页数据")
    private List<PileTradeGroupVO> tradeGroupVOS;
}
