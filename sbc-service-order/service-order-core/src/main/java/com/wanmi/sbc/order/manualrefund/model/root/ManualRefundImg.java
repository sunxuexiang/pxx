package com.wanmi.sbc.order.manualrefund.model.root;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "manual_refund_img")
public class ManualRefundImg implements Serializable {

    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "manual_refund_img_id")
    private Long manualRefundImgId;

    /**
     * 退款单id
     */
    @Column(name = "refund_id")
    private String refundId;

    /**
     * 打款凭证图片地址
     */
    @Column(name = "manual_refund_payment_voucher_img")
    private String manualRefundPaymentVoucherImg;

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

    /**
     * 退款所属单据类型1订单2退单
     */
    @Column(name = "refund_bill_type")
    private Integer refundBillType;
}

