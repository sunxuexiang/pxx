package com.wanmi.sbc.marketing.api.request.market.latest;

import com.wanmi.sbc.marketing.bean.dto.TradeItemInfoDTO;
import com.wanmi.sbc.marketing.bean.dto.TradeMarketingDTO;
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
@AllArgsConstructor
@NoArgsConstructor
public class MarketingEffectiveRequest implements Serializable {

    private static final long serialVersionUID = -494737949098158484L;
    private List<TradeMarketingDTO> tradeMarketingList;
    private List<TradeItemInfoDTO> tradeItems;
    private Long wareId;
    private String type ="redis"; //校验从redis校验还是mysql校验默认redis

}
