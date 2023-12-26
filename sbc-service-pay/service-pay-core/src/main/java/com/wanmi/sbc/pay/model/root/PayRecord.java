package com.wanmi.sbc.pay.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.Data;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交易记录
 */
@Data
@Entity
@Table(name = "pay_record")
public class PayRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 业务id(订单或退单号)
     */
    @Column(name = "business_id", nullable = false)
    private String businessId;

    /**
     * 申请价格
     */
    @Column(name = "apply_price", nullable = false)
    private BigDecimal applyPrice;

    /**
     * 支付渠道项id
     */
    @Column(name = "channel_item_id", nullable = false)
    private Long channelItemId;

    /**
     * 创建时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    /**
     * 支付订单号
     */
    @Column(name = "pay_order_no")
    private String payOrderNo;

    /**
     * 0：未支付，1：已支付
     */
    @Column(name = "status")
    private Integer status;

}
