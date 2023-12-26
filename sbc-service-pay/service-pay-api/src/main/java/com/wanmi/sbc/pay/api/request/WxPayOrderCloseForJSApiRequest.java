package com.wanmi.sbc.pay.api.request;

import com.wanmi.sbc.pay.bean.enums.WxPayTradeType;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName WxPayOrderDetailRequest
 * @Description TODO
 * @Author lvzhenwei
 * @Date 2020/9/17 13:56
 **/
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WxPayOrderCloseForJSApiRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 订单号
     */
    private String businessId;

    /**
     * 支付单号
     */
    private String payOrderNo;

}
