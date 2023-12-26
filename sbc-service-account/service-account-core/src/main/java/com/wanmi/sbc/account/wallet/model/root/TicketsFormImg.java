package com.wanmi.sbc.account.wallet.model.root;


import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tickets_form_img")
public class TicketsFormImg implements Serializable {

    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tickets_form_img_id")
    private Long ticketsFormImgId;

    /**
     * 申请工单号
     */
    @Column(name = "form_id")
    private Long formId;

    /**
     * 打款凭证图片地址
     */
    @Column(name = "tickets_form_payment_voucher_img")
    private String ticketsFormPaymentVoucherImg;

    /**
     * 删除标识,0:未删除1:已删除
     */
    @Column(name = "del_flag")
    private Integer delFlag;

    /**
     * 创建人
     */
    @Column(name = "create_by")
    private String createBy;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private LocalDateTime createTime;

    /**
     * 修改人
     */
    @Column(name = "update_by")
    private String updateBy;


    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
