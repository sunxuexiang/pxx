package com.wanmi.sbc.marketing.api.response.market;

import com.wanmi.sbc.marketing.bean.dto.TradeMarketingDTO;
import com.wanmi.sbc.marketing.bean.vo.MarketingVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 商品对应营销列表
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketingByGoodsInfoIdAndIdResponse implements Serializable {
    private static final long serialVersionUID = 7293672850773285107L;


    @ApiModelProperty(value = "订单最新营销")
    private List<TradeMarketingDTO> tradeMarketingList;

    @ApiModelProperty(value = "失效营销")
    private List<TradeMarketingDTO> removemarketinglist;
    @ApiModelProperty(value = "失效的赠品id")
    private List<String> removelist;
}
