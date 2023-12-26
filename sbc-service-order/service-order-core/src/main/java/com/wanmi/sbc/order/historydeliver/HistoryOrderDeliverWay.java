package com.wanmi.sbc.order.historydeliver;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户历史配送方式
 */
@Data
@Entity
@Accessors(chain = true)
@Table(name = "history_order_deliver_way")
public class HistoryOrderDeliverWay implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 店铺id
     */
    @Column(name = "store_id")
    private Long storeId;

    /**
     * 会员id
     */
    @Column(name = "customer_id")
    private String customerId;

    /**
     * 收货地址ID
     */
    @Column(name = "consignee_id")
    private String consigneeId;

    /**
     * 发货方式
     */
    @Column(name = "deliver_way")
    private Integer deliverWay;

    /**
     * 订单id
     */
    @Column(name = "last_trade_id")
    private String lastTradeId;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

}
