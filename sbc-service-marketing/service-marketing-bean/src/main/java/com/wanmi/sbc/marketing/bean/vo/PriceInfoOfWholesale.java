package com.wanmi.sbc.marketing.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PriceInfoOfWholesale implements Serializable {

    private static final long serialVersionUID = -3166482406533470793L;

    /**
     * 商品总价
     */
    public BigDecimal totalAmount = BigDecimal.ZERO;

    /**
     * 满减优惠
     */
    public BigDecimal profitAmountOfReduce = BigDecimal.ZERO;

    /**
     * 满折优惠
     */
    public BigDecimal profitAmountOfDiscount = BigDecimal.ZERO;

    /**
     * 共减
     */
    public BigDecimal profitAmount = BigDecimal.ZERO;
    /**
     * 合计
     */
    public BigDecimal payableAmount = BigDecimal.ZERO;
}
