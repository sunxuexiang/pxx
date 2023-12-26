package com.wanmi.sbc.pay.api.request;

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
 * @create 2023/6/17 15:15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CcbRefundSubRequest implements Serializable {


    private static final long serialVersionUID = 3943794051298969216L;

    /**
     * 惠市宝生成的子订单ID
     */
    private String subOrderId;

    /**
     * 子订单退单金额
     */
    private BigDecimal subOrderRfndAmt;


}
