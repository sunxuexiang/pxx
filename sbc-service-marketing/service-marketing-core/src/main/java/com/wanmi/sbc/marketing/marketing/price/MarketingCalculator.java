package com.wanmi.sbc.marketing.marketing.price;

import com.wanmi.sbc.goods.bean.vo.DevanningGoodsInfoVO;
import com.wanmi.sbc.marketing.bean.dto.TradeMarketingWrapperDTO;
import com.wanmi.sbc.marketing.common.model.entity.TradeMarketing;
import com.wanmi.sbc.marketing.common.model.root.Marketing;
import com.wanmi.sbc.marketing.common.request.TradeItemInfo;
import com.wanmi.sbc.marketing.common.request.TradeMarketingPluginRequest;
import com.wanmi.sbc.marketing.common.request.TradeMarketingRequest;
import com.wanmi.sbc.marketing.common.response.TradeMarketingResponse;

import javax.annotation.PostConstruct;
import java.util.List;

public abstract class MarketingCalculator {

    public abstract String getMarketingTypeEnum();

    @PostConstruct
    public void register() {
        MarketingCalculatorFactory.register(getMarketingTypeEnum(), this);
    }

    public abstract MarketingCalculatorResult calculate(List<DevanningGoodsInfoVO> goods, Marketing marketing);

    public abstract MarketingCalculatorResult calculate(MarketingCaculatorData data);

    /**
     * 忽略营销活动的等级进行价格计算
     * @param devanningGoodsInfoVO
     * @param marketing
     * @return
     */
    public abstract MarketingCalculatorResult calculateIgnoreLevelThreshold(DevanningGoodsInfoVO devanningGoodsInfoVO, Marketing marketing);
}
