package com.wanmi.sbc.pay.api.request;

import com.alipay.api.AlipayClient;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

/**
 * @description: 查询支付宝订单支付状态
 * @author: XinJiang
 * @time: 2022/1/24 16:52
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AliPayPaymentSlipRequest extends PayBaseRequest {

    private static final long serialVersionUID = -7807625113893584760L;

    /**
     * 订单业务id
     */
    @ApiModelProperty(value = "订单业务id")
    private String businessId;

    /**
     * 订单业务id集合
     */
    @ApiModelProperty(value = "订单业务id集合")
    private List<String> businessIds;

    /**
     * 阿里openApi封装类
     */
    @ApiModelProperty(value = "阿里openApi封装类")
    private AlipayClient alipayClient;
}
