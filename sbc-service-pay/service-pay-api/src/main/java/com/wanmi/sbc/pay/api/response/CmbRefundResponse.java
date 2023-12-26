package com.wanmi.sbc.pay.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CmbRefundResponse implements Serializable {
    //退款接口返回的参数
    private CmbPayRefundResponse cmbPayRefundResponse;
    //
    private String payType;

}
