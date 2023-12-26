package com.wanmi.sbc.order.bean.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 囤货提货详情dto
 */
@Data
@ApiModel
public class PickDetailDTO {

    /**
     * 订单号
     */
    @ApiModelProperty(value = "订单号")
    private String id;

    /**
     * 提货时间
     */
    @ApiModelProperty(value = "提货时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime pickOrderTime;

    /**
     * 提货数量
     */
    @ApiModelProperty(value = "商品数量")
    private Integer goodsNum;

    /**
     * 商品总金额
     */
    @ApiModelProperty(value = "商品总金额")
    private BigDecimal goodsPrice;

    /**
     * 配送费用，可以从TradePriceInfo获取
     */
    @ApiModelProperty(value = "配送费用")
    private BigDecimal deliveryPrice;

    /**
     * 配送方式
     */
    @ApiModelProperty(value = "配送方式")
    private String deliveryType;

    /**
     * 配送费用，可以从TradePriceInfo获取
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
    private BigDecimal receiptsPrice;
}
