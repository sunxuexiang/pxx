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
public class CcbRefundParRequest implements Serializable {


    private static final long serialVersionUID = -2346800247931020665L;

    /**
     * 子订单ID
     */
    private String subOrdrId;

    /**
     * 商家ID
     */
    private String mktMrchId;

    /**
     * 退单金额
     */
    private BigDecimal mktMrchRfndAmt;


}
