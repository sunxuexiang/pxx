package com.wanmi.sbc.order.trade.model.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 描述
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
@Table(name = "stock_record")
public class PileStockRecord implements Serializable {
    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_record_id")
    private Long stockRecordId;

    /**
     * 会员Id
     */
    @Column(name = "customer_id")
    private String customerId;

    /**
     * 商品id
     */
    @Column(name = "goods_id")
    private String goodsId;

    /**
     * SKU编号
     */
    @Column(name = "goods_info_id")
    private String goodsInfoId;

    /**
     * 囤货剩余数量（已使用了多少数量）
     */
    @Column(name = "stock_record_remaining_num")
    private Long stockRecordRemainingNum;

    /**
     * 囤货数量
     */
    @Column(name = "stock_record_num")
    private Long stockRecordNum;

    /**
     * 囤货价格
     */
    @Column(name = "stock_record_price")
    private BigDecimal stockRecordPrice;

    /**
     * 是否已使用  0：未使用完  1：已使用完
     */
    @Column(name = "is_use")
    private Long isUse;

    /**
     * 囤货订单id
     */
    @Column(name = "order_code")
    private String orderCode;

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
