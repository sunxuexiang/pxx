package com.wanmi.sbc.returnorder.api.response.payorder;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel
public class FindPickTradeResponse implements Serializable {
    /***
     * 提货单列表
     */
    @ApiModelProperty(value = "提货单列表")
    private List<PickOrderItem> pickOrderItems;

    @Data
    public static class PickOrderItem {
        /**
         * 提货订单号
         */
        @ApiModelProperty(value = "提货订单号")
        private String pickOrderNo;

        /**
         * 提货时间
         */
        @ApiModelProperty(value = "提货时间")
        private String pickTime;

        /**
         * 商品数量
         */
        @ApiModelProperty(value = "商品数量")
        private Long num;

        /**
         * 订单商品金额
         */
        @ApiModelProperty(value = "订单商品金额")
        private BigDecimal orderGoodsPrice;

        /**
         * 配送方式
         */
        @ApiModelProperty(value = "配送方式(文本)")
        private String deliverWayText;

        /**
         * 配送运费
         */
        @ApiModelProperty(value = "配送运费")
        private BigDecimal freight;

        /**
         * 运费优惠金额：配送费用，可以从TradePriceInfo获取
         */
        @ApiModelProperty(value = "运费优惠金额")
        private BigDecimal deliveryDiscountPrice;

        /**
         * 扣款金额
         */
        @ApiModelProperty(value = "扣款金额")
        private BigDecimal payPrice;

        /**
         * 实付金额
         */
        @ApiModelProperty(value = "实付金额")
        private BigDecimal actualPrice;
    }

}
