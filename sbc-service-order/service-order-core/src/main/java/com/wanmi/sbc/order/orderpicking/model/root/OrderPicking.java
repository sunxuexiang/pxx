package com.wanmi.sbc.order.orderpicking.model.root;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/4/12 14:26
 */
@Data
@Entity
@Table(name = "order_picking")
public class OrderPicking implements Serializable {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 订单ID
     */
    @Column(name = "trade_id")
    private String tradeId;

    /**
     * 订单号
     */
    @Column(name = "status")
    private Integer status;
}
