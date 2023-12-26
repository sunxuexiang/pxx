package com.wanmi.sbc.customer.invitationstatistics.model.root;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>描述<p>
 *
 * @author zhaowei
 * @date 2021/6/10
 */
@Data
@Entity
@Table(name = "invitation_statistics_order")
@IdClass(InvitationStatisticsOrder.class)
public class InvitationStatisticsOrder implements Serializable {

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
     * 订单id
     */
    @Id
    @Column(name = "order_id")
    private String orderId;

}
