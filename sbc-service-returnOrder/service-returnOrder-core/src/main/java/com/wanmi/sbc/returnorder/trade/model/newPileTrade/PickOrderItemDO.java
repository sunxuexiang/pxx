package com.wanmi.sbc.returnorder.trade.model.newPileTrade;

import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 囤货订单： 提货条目
 */
@Data
public class PickOrderItemDO {
    /**
     * 提货订单号
     */
    private String pickOrderNo;

    /**
     * 提货时间
     */
    private String pickTime;

    /**
     * 商品名称
     */
    private String skuName;

    /**
     * 商品数量
     */
    private Long num;

    /**
     * 订单商品金额
     */
    private BigDecimal orderGoodsPrice;

    /**
     * 配送方式
     */
    private DeliverWay deliverWay;

    /**
     * 配送运费
     */
    private BigDecimal freight;

    /**
     * 运费优惠金额：配送费用，可以从TradePriceInfo获取
     */
    private BigDecimal deliveryDiscountPrice;

    /**
     * 扣款金额
     */
    private BigDecimal payPrice;

    /**
     * 实付金额
     */
    private BigDecimal actualPrice;
}