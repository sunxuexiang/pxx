package com.wanmi.sbc.order.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.order.bean.enums.PaymentOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class NewPileTradeCommitResultVO {

    /**
     * 订单编号
     */
    @ApiModelProperty(value = "订单编号")
    private String tid;

    /**
     * 父订单号，用于不同商家订单合并支付场景
     */
    @ApiModelProperty(value = "父订单号")
    private String parentTid;

    /**
     * 订单状态
     */
    @ApiModelProperty(value = "订单状态")
    private NewPileTradeStateVO tradeState;

    /**
     * 订单支付顺序
     */
    @ApiModelProperty(value = "订单支付顺序")
    private PaymentOrder paymentOrder;

    /**
     * 交易金额
     */
    @ApiModelProperty(value = "交易金额")
    private BigDecimal price;

    /**
     * 订单取消时间
     */
    @ApiModelProperty(value = "订单取消时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime orderTimeOut;

    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * 是否平台自营
     */
    private Boolean isSelf;

    /**
     * 商品图片
     */
    private String img;
}

