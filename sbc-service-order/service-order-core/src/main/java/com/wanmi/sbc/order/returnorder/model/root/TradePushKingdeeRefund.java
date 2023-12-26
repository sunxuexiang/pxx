package com.wanmi.sbc.order.returnorder.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.Data;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * push金蝶退款单记录
 *
 * @author yitang
 * @version 1.0
 */
@Data
@Entity
@Table(name = "push_kingdee_refund")
public class TradePushKingdeeRefund implements Serializable{

    private static final long serialVersionUID = -3449299497313158492L;

    /**
     * 推送金蝶支付订单id
     */
    @Id
    @Column(name = "push_kingdee_refund_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pushKingdeeRefundId;

    /**
     * 退款单编号
     */
    @Column(name = "refund_code")
    private String refundCode;

    /**
     * 推送给金蝶状态，0：创建 1：推送成功 2推送失败
     */
    @Column(name = "push_status")
    private Integer pushStatus;

    /**
     * 销售订单
     */
    @Column(name = "order_code")
    private String orderCode;

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
     * 客户账户
     */
    @Column(name = "customer_account")
    private String customerAccount;

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
