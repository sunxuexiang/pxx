package com.wanmi.sbc.order.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@ApiModel("保存 乡镇件订单")
public class OrderVillageAddDeliveryVO implements Serializable {
    private static final long serialVersionUID = 1L;


    /**
     * 主键
     */
    @ApiModelProperty("主键")
    private Long id;


    /**
     * 订单号
     */
    @ApiModelProperty("订单号")
    private String tradeId;


    /**
     * 商家批发市场ID
     */
    @ApiModelProperty("商家批发市场ID")
    private Long storeMarketId;
    /**
     * 用户ID
     */
    @ApiModelProperty("用户ID")
    private String buyerId;

    /**
     * 收货地址ID
     */
    private String consigneeId;


    /**
     * 收货省份
     */
    @ApiModelProperty("收货省份")
    private Long consigneeProvinceId;

    /**
     * 收货乡镇ID
     */
    private Long consigneeTownId;


    /**
     * 配送方式
     */
    @ApiModelProperty("配送方式")
    private Integer deliveryWayId;
    /**
     * 加收费用
     */
    @ApiModelProperty("加收费用")
    private BigDecimal deliveryPrice;


    /**
     * 加收费用
     */
    @ApiModelProperty("加收费用")
    private BigDecimal addDeliveryPrice;
    /**
     * 下单时间
     */
    @ApiModelProperty("下单时间")
    private LocalDateTime orderTime;


    /**
     * 付款时间
     */
    @NotNull(message = "payTime can not null")
    @ApiModelProperty("付款时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime payTime;


    /**
     * 退单id
     */
    @ApiModelProperty("退单id")
    private String returnOrderId;


    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;


    /**
     * 修改时间
     */
    @ApiModelProperty("修改时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;


    /**
     * 删除状态
     */
    @ApiModelProperty("删除状态")
    private Integer delFlag;
    /**
     * 是否仅退款
     */
    @ApiModelProperty("是否仅退款")
    private Integer refundFlag;
    /**
     * 订单号
     */
    @ApiModelProperty("订单号")
    private String addDeliveryTradeId;

}
