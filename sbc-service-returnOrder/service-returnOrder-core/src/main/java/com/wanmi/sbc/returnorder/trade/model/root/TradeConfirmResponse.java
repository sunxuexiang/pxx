package com.wanmi.sbc.returnorder.trade.model.root;

import com.wanmi.sbc.returnorder.trade.model.entity.Discounts;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>订单确认返回结构</p>
 * Created by of628-wenzhi on 2018-03-08-下午6:09.
 */
@Data
@Builder
public class TradeConfirmResponse {

    /**
     * 按商家拆分后的订单项
     */
    private List<TradeConfirmItem> tradeConfirmItems;

    /**
     * 订单总额
     */
    private BigDecimal totalPrice = BigDecimal.ZERO;

    /**
     * 商品总额
     */
    private BigDecimal goodsTotalPrice = BigDecimal.ZERO;

    /**
     * 优惠总额
     */
    private BigDecimal discountsTotalPrice = BigDecimal.ZERO;

    public BigDecimal getTotalPrice() {
        return tradeConfirmItems.stream().map(i -> i.getTradePrice().getTotalPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getGoodsTotalPrice() {
        return tradeConfirmItems.stream().map(i -> i.getTradePrice().getGoodsPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getDiscountsTotalPrice() {
        return tradeConfirmItems.stream().flatMap(i -> i.getDiscountsPrice().stream()).map(Discounts::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
