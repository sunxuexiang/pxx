package com.wanmi.sbc.order.historydeliver;

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
@Table(name = "history_order_view_rule_desc")
public class HistoryOrderViewRuleDesc implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
     * 接口id
     */
    @Column(name = "api_id")
    private Integer apiId;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private LocalDateTime createTime;

}
