package com.wanmi.sbc.pay.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CupsPayRefundDataRequest implements Serializable {



    private String appId;


    private String apiKey;



    /**
     *商户订单号，支付时的订单号
     */
    private String orderNo;

    /**
     *退款流水号,商户生成，同一笔订单内，同一退款流水号只能退款一次。可用于防重复退款。
     */
    private String refundOrderId;

    /**
     *退款金额,格式xxxx.xx
     */
    private String amount;

    private String desc;

    /**
     * 支付单号
     */
    private String payOrderNo;

    private Long channelId;

}
