package com.wanmi.sbc.wallet.api.response.wallet;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description: 账户余额总数响应类
 * @author: jiangxin
 * @create: 2021-08-24 16:40
 */
@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BalanceCountResponse implements Serializable {

    private static final long serialVersionUID = -3394514815739042016L;

    /**
     * 累计余额
     */
    @ApiModelProperty(value = "累计余额")
    private BigDecimal balanceSum;
}
