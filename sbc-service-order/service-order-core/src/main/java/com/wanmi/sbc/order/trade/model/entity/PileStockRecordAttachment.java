package com.wanmi.sbc.order.trade.model.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.*;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 提货成功副表
 *
 * @author yitang
 * @version 1.0
 */
@Builder
@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "stock_record_trade_item_attachment")
public class PileStockRecordAttachment implements Serializable {

    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 订单id
     */
    @Column(name = "tid")
    private String tid;

    /**
     * 囤货id
     */
    @Column(name = "stock_record_id")
    private Long stockRecordId;

    /**
     * 囤货订单id
     */
    @Column(name = "order_code")
    private String orderCode;


    @Column(name = "sku_id")
    private String skuId;

    /**
     * 购买数量
     */
    @Column(name = "num")
    private Long num;

    /**
     * 成交价格
     */
    @Column(name = "price")
    private BigDecimal price;

    /**
     * 会员id
     */
    @Column(name = "customer_id")
    private String customerId;


    /**
     * 创建时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
