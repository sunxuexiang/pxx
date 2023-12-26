package com.wanmi.sbc.marketing.api.response.market;

import com.wanmi.sbc.marketing.bean.dto.TradeItemInfoDTO;
import com.wanmi.sbc.marketing.bean.dto.TradeMarketingDTO;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketingEffectiveRespose implements Serializable {

    private static final long serialVersionUID = 8864708056352488707L;
    private List<TradeMarketingDTO> tradeMarketingList;


}
