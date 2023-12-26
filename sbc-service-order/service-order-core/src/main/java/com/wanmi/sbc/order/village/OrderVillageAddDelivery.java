package com.wanmi.sbc.order.village;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 乡镇件订单
 */
@Data
@Entity
@Accessors(chain = true)
@Table(name = "order_village_add_delivery")
public class OrderVillageAddDelivery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 订单号
     */
    @Column(name = "trade_id")
    private String tradeId;


    /**
     * 商家批发市场ID
     */
    @Column(name = "store_market_id")
    private Long storeMarketId;
    /**
     * 用户ID
     */
    @Column(name = "buyer_id")
    private String buyerId;

    /**
     * 收货地址ID
     */
    @Column(name = "consignee_id")
    private String consigneeId;

    /**
     * 收货省份
     */
    @Column(name = "consignee_province_id")
    private Long consigneeProvinceId;

    /**
     * 收货乡镇ID
     */
    @Column(name = "consignee_town_id")
    private Long consigneeTownId;

    /**
     * 配送方式
     */
    @Column(name = "delivery_way_id")
    private Integer deliveryWayId;
    /**
     * 加收费用
     */
    @Column(name = "delivery_price", nullable = false)
    private BigDecimal deliveryPrice;

    /**
     * 加收费用
     */
    @Column(name = "add_delivery_price")
    private BigDecimal addDeliveryPrice;
    /**
     * 下单时间
     */
    @Column(name = "order_time", nullable = false)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime orderTime;

    /**
     * 付款时间
     */
    @Column(name = "pay_time")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime payTime;

    /**
     * 退单id
     */
    @Column(name = "return_order_id")
    private String returnOrderId;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @Column(name = "update_time")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;

    /**
     * 删除状态
     */
    @Column(name = "del_flag")
    private Integer delFlag;
    /**
     * 是否仅退款
     */
    @Column(name = "refund_flag", nullable = false)
    private Integer refundFlag;
    /**
     * 订单号
     */
    @Column(name = "add_delivery_trade_id", nullable = false)
    private String addDeliveryTradeId;

}
