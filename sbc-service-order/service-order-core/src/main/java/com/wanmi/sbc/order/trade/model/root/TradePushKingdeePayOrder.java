package com.wanmi.sbc.order.trade.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * push金蝶支付单记录
 *
 * @author yitang
 * @version 1.0
 */
@Getter
@Setter
@Entity
@Table(name = "push_kingdee_pay")
public class TradePushKingdeePayOrder implements Serializable{
    private static final long serialVersionUID = -7756394526376964965L;
    /**
     * 推送金蝶订单id
     */
    @Id
    @Column(name = "push_kingdee_pay_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pushKingdeePayId;

    /**
     * 销售订单编号
     */
    @Column(name = "order_code")
    private String orderCode;

    /**
     * 支付单编号
     */
    @Column(name = "pay_code")
    private String payCode;

    /**
     * 推送给金蝶状态，0：创建 1：推送成功 2推送失败
     */
    @Column(name = "push_status")
    private Integer pushStatus;

    /**
     * 支付类型
     */
    @Column(name = "pay_type")
    private String payType;

    /**
     * 说明
     */
    @Column(name = "instructions")
    private String instructions;

    /**
     * 实际支付金额
     */
    @Column(name = "practical_price")
    private BigDecimal practicalPrice;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;
}
