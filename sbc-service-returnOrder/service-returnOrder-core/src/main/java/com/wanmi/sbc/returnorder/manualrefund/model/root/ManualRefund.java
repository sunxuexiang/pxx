package com.wanmi.sbc.returnorder.manualrefund.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.account.bean.enums.RefundStatus;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "manual_refund")
public class ManualRefund implements Serializable{
    /**
     * 主键
     */
    @Column(name = "refund_id", nullable = false, length = 45)
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String refundId;

    /**
     * 会员详情ID外键
     */
    @Column(name= "customer_detail_id")
    private String customerDetailId;

    /**
     * 退款平台：1-银联
     */
    @Column(name = "pay_type")
    private Integer payType;

    /**
     * 订单总金额（实付）
     */
    @Column(name= "total_price")
    private BigDecimal totalPrice = BigDecimal.ZERO;

    /**
     * 申请退款金额
     */
    @Column(name= "apply_price")
    private BigDecimal applyPrice = BigDecimal.ZERO;

    /**
     * 订单编号
     */
    @Column(name = "order_code")
    private String orderCode;

    /**
     * 支付单号
     */
    @Column(name = "pay_order_no")
    private String payOrderNo;

    /**
     * 退款单号
     */
    @Column(name = "refund_code")
    private String refundCode;

    /**
     * 退款单状态
     */
    @Column(name = "refund_status")
    @Enumerated
    private RefundStatus refundStatus;

    /**
     * 拒绝原因
     */
    @Column(name = "refuse_reason")
    private String refuseReason;

    /**
     * del
     */
    @Column(name = "del_flag")
    private DeleteFlag delFlag;

    /**
     * 删除时间
     */
    @Column(name = "del_time")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime delTime;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

}
