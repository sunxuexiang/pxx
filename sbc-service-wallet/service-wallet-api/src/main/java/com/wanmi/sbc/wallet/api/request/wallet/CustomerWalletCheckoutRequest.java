package com.wanmi.sbc.wallet.api.request.wallet;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.math.BigDecimal;

/**
 * @Description: TODO
 * @author: jiangxin
 * @create: 2021-08-24 14:06
 */
@Data
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerWalletCheckoutRequest extends BaseQueryRequest {

    private static final long serialVersionUID = 9094629374388797324L;

    /**
     * 使用余额
     */
    @ApiModelProperty(value = "使用余额")
    private BigDecimal walletBalance;

    @ApiModelProperty("客户信息")
    private CustomerVO customer;

}
