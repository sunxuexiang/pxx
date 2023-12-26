package com.wanmi.sbc.wallet.api.response.wallet;

import com.wanmi.sbc.wallet.bean.vo.CusWalletVO;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户钱包余额响应
 */
@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BalanceByCustomerIdResponse implements Serializable {
    private static final long serialVersionUID = 1273365272648334543L;

    private CusWalletVO cusWalletVO;
}
