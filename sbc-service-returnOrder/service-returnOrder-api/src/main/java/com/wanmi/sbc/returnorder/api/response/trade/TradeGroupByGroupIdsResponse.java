package com.wanmi.sbc.returnorder.api.response.trade;

import com.wanmi.sbc.returnorder.bean.vo.TradeGroupVO;
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
public class TradeGroupByGroupIdsResponse implements Serializable {
    private static final long serialVersionUID = 2703472207617430180L;

    /**
     * 分页数据
     */
    @ApiModelProperty(value = "分页数据")
    private List<TradeGroupVO> tradeGroupVOS;
}
