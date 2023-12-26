package com.wanmi.sbc.customer.invitationstatistics.model.root;


import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>邀新统计实体类</p>
 *
 * @author lvheng
 * @date 2021-04-23 10:57:45
 */
@Data
@Entity
@Table(name = "invitation_statistics")
@IdClass(InvitationStatistics.class)
public class InvitationStatistics implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 业务员ID
     */
    @Id
    @Column(name = "employee_id")
    private String employeeId;


    @Id
    @Column(name = "date")
    private String date;

    /**
     * 邀新数
     */
    @Column(name = "results_count")
    private Long resultsCount = 0L;

    /**
     * 订单总额
     */
    @Column(name = "trade_price_total")
    private BigDecimal tradePriceTotal = BigDecimal.ZERO;

    /**
     * 总商品数
     */
    @Column(name = "trade_goods_total")
    private Long tradeGoodsTotal = 0L;

    /**
     * 总订单数
     */
    @Column(name = "trade_total")
    private Long tradeTotal = 0L;

}