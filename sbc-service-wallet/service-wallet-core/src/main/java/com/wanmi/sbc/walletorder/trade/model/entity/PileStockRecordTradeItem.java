package com.wanmi.sbc.walletorder.trade.model.entity;

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
 * 描述
 *
 * @author yitang
 * @version 1.0
 */
@Builder
@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "stock_record_trade_item")
public class PileStockRecordTradeItem implements Serializable {
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
     * 店铺id
     */
    @Column(name = "store_id")
    private String storeId;

    /**
     * spuId
     */
    @Column(name = "spu_id")
    private String spuId;

    /**
     * spuName
     */
    @Column(name = "spu_name")
    private String spuName;

    @Column(name = "sku_id")
    private String skuId;

    @Column(name = "sku_name")
    private String skuName;

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
