package com.wanmi.sbc.pay.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/11/19 15:39
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CcbRefundAdRequest implements Serializable {

    private static final long serialVersionUID = -4998365572017490695L;

    /**
     * 交易流水号
     */
    @NotBlank
    private String pyTrnNo;

    /**
     * 退单编号
     */
    @NotBlank
    private String refundNo;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount;
}
