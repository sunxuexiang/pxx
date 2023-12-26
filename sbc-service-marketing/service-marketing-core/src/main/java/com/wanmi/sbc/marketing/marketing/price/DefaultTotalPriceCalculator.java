package com.wanmi.sbc.marketing.marketing.price;

import com.wanmi.ares.enums.DefaultFlag;
import com.wanmi.sbc.goods.bean.vo.DevanningGoodsInfoVO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 默认的总金额计算组件
 */
public class DefaultTotalPriceCalculator implements TotalPriceCalculator {

    @Override
    public BigDecimal calculate(List<DevanningGoodsInfoVO> goods) {

        BigDecimal totalPrice = BigDecimal.ZERO;
        for (DevanningGoodsInfoVO good : goods) {
            if(DefaultFlag.NO.equals(good.getIsCheck())){
                continue;
            }
            BigDecimal marketPrice = good.getMarketPrice();
            BigDecimal buyCount = BigDecimal.valueOf(good.getBuyCount());
            BigDecimal multiply = marketPrice.multiply(buyCount);
            totalPrice = totalPrice.add(multiply);
        }
        return totalPrice;
    }
}
