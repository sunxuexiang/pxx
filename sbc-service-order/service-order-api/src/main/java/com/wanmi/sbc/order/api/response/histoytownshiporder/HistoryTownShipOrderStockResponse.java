package com.wanmi.sbc.order.api.response.histoytownshiporder;

import com.wanmi.sbc.order.bean.vo.HistoryLogisticsCompanyVO;
import com.wanmi.sbc.order.bean.vo.TrueStockVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryTownShipOrderStockResponse implements Serializable {


    private static final long serialVersionUID = 6413376762800592091L;

    @ApiModelProperty(value = "真实库存")
    private List<TrueStockVO> trueStockVO;
}
