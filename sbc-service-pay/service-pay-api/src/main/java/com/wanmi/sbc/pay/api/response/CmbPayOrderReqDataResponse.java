package com.wanmi.sbc.pay.api.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class CmbPayOrderReqDataResponse implements Serializable {

    /**
     *请求时间
     */
    private String dateTime;
    /**
     *分行号
     */
    private String branchNo;
    /**
     *商户号
     */
    private String merchantNo;
    /**
     *订单日期
     */
    private String date;
    /**
     *订单号
     */
    private String orderNo;
    /**
     *订单金额
     */
    private String amount;
    /**
     *过期时间跨度
     */
    private String expireTimeSpan;
    /**
     *成功支付结果通知地址
     */
    private String payNoticeUrl;

}
