package com.wanmi.sbc.wallet.wallet.model.root;


import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tickets_form_log")
public class TicketsFormLog implements Serializable {

    /**
     * 提现申请日志id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tickets_form_log_id")
    private Long ticketsFormLogId;

    /**
     * 业务id/表主键
     */
    @Column(name = "business_id")
    private Long businessId;

    /**
     * 审核人员类型:1客户 2财务
     */
    @Column(name = "audit_staff_type")
    private Integer auditStaffType;

    /**
     * 审核状态:1通过 2不通过 3打款失败
     */
    @Column(name = "audit_status")
    private Integer auditStatus;

    /**
     * 审核人员
     */
    @Column(name = "audit_staff")
    private String auditStaff;

    /**
     * 审核时间
     */
    @Column(name = "audit_time")
    private LocalDateTime auditTime;

    /**
     * 备注
     */
    @Column(name = "remark")
    private String remark;
}
