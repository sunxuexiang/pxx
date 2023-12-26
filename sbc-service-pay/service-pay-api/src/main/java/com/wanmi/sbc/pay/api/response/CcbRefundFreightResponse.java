package com.wanmi.sbc.pay.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/11/19 15:46
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CcbRefundFreightResponse implements Serializable {
    private static final long serialVersionUID = -5176602847738892214L;

    /**
     *  状态（1：成功，2：退款中，3：失败）
     */
    private Integer refundStatus;

    /**
     * 退运费金额
     */
    BigDecimal refundFreightPrice;
}
