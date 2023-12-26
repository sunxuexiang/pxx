package com.wanmi.sbc.returnorder.returnorder.model.root;

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
 * 退货物流信息
 */
@Data
@Entity
@Table(name = "return_order_logistics")
public class ReturnOrderLogistics implements Serializable {

    private static final long serialVersionUID = 1L;

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
    @Column(name = "tid")
    private String tid;

    /**
     * 退单ID
     */
    @Column(name = "rid")
    private String rid;

    /**
     * 店铺ID
     */
    @Column(name = "store_id")
    private Long storeId;

    /**
     * 目的地
     */
    @Column(name = "desc_addr")
    private String descAddr;

    /**
     * 提货地点
     */
    @Column(name = "pick_addr")
    private String pickAddr;

    /**
     * 提货地点电话
     */
    @Column(name = "pick_phone")
    private String pickPhone;

    /**
     * 物流功能名称
     */
    @Column(name = "company_name")
    private String companyName;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;


    /**
     * 用户ID
     */
    @Column(name = "customer_id")
    private String customerId;

}
