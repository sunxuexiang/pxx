package com.wanmi.sbc.marketing.marketing.price;

import com.wanmi.sbc.goods.bean.vo.DevanningGoodsInfoVO;
import com.wanmi.sbc.marketing.bean.dto.TradeMarketingDTO;
import com.wanmi.sbc.marketing.common.model.root.Marketing;
import com.wanmi.sbc.marketing.common.request.TradeItemInfo;
import com.wanmi.sbc.marketing.common.request.TradeMarketingRequest;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MarketingCaculatorData {

    /**
     * 参与的营销活动
     */
    private Marketing marketing;

    /**
     * 参与营销活动的商品
     */
    private List<DevanningGoodsInfoVO> devanningGoodsInfoVOList;

    private TradeMarketingDTO tradeMarketingDTO;

    private List<TradeItemInfo> tradeItems;

}
