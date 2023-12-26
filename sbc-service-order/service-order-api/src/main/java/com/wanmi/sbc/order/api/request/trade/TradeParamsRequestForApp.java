package com.wanmi.sbc.order.api.request.trade;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class TradeParamsRequestForApp {

    private List<TradeParamsRequest> tradeParams;
    private Long wareId;
    private Boolean matchWareHouseFlag;
}
