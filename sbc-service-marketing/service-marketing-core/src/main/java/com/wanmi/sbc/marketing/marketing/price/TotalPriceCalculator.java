package com.wanmi.sbc.marketing.marketing.price;

import com.wanmi.sbc.goods.bean.vo.DevanningGoodsInfoVO;
import com.wanmi.sbc.marketing.common.model.root.Marketing;

import java.math.BigDecimal;
import java.util.List;

public interface TotalPriceCalculator {
    BigDecimal calculate(List<DevanningGoodsInfoVO> goods);
}
