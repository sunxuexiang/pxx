package com.wanmi.sbc.wallet.api.response.wallet;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.walletorder.bean.vo.WalletStoreVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerWalletStorePgResponse {

    @ApiModelProperty(value = "商家充值分页查询")
    private MicroServicePage<WalletStoreVO> microServicePage;
}
