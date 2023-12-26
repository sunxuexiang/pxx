package com.wanmi.sbc.order.stockupaction.model.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.Data;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @description: 提货明细实体类
 * @author: XinJiang
 * @time: 2021/12/17 15:41
 */
@Data
@Entity
@Table(name = "stockup_action")
public class StockupAction {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 父订单号
     */
    @Column(name = "pid")
    private String pid;

    /**
     * 提货订单号
     */
    @Column(name = "order_code")
    private String orderCode;

    /**
     * 订单总金额
     */
    @Column(name = "order_total_price")
    private BigDecimal orderTotalPrice = BigDecimal.ZERO;

    /**
     * 客户id
     */
    @Column(name = "customer_id")
    private String customerId;

    /**
     * 商品spuId
     */
    @Column(name = "goods_id")
    private String goodsId;

    /**
     * 商品skuId
     */
    @Column(name = "goods_info_id")
    private String goodsInfoId;

    /**
     * 商品单价
     */
    @Column(name = "goods_split_price")
    private BigDecimal goodsSplitPrice = BigDecimal.ZERO;

    /**
     * 购买数量
     */
    @Column(name = "goods_num")
    private Long goodsNum;

    /**
     * 创建时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "create_time")
    private LocalDateTime createTime;


}
