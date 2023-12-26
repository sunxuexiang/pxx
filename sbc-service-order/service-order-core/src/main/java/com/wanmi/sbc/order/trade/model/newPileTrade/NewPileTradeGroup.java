package com.wanmi.sbc.order.trade.model.newPileTrade;

import com.wanmi.sbc.marketing.bean.vo.TradeCouponVO;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

@Data
public class NewPileTradeGroup {

    /**
     * 订单组号
     */
    @Id
    private String id;

    /**
     * 订单组中使用的平台优惠券
     */
    private TradeCouponVO commonCoupon;

    /**
     * 使用平台优惠券的商品集合(已作废的商品)
     */
    private List<String> commonSkuIds = new ArrayList<>();

    /**
     * 平台券是否已退
     */
    private Boolean commonCouponIsReturn = Boolean.FALSE;
}
