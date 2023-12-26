package com.wanmi.sbc.returnorder.refundfreight.model.root;

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
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/11/30 11:54
 */
@Data
@Entity
@Table(name = "refund_freight_record")
public class RefundFreightRecord implements Serializable {

    /**
     * ID
     */
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 订单ID
     */
    @Column(name= "tid")
    private String tid;

    /**
     * 退单ID
     */
    @Column(name= "rid")
    private String rid;

    /**
     * 退运费金额
     */
    @Column(name= "freight_price")
    private BigDecimal freightPrice;


    /**
     * 状态：1成功，2退款中，3失败
     */
    @Column(name = "refund_status")
    private Integer refundStatus;

    /**
     * 备注
     */
    @Column(name = "remark")
    private String remarks;


    /**
     * 删除时间
     */
    @Column(name = "create_time")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    @Column(name = "create_person")
    private String createPerson;

    /**
     * 修改时间
     */
    @Column(name = "update_time")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;
}
