package com.wanmi.sbc.account.api.response.wallet;

import com.wanmi.sbc.account.bean.vo.CustomerWalletVO;
import com.wanmi.sbc.common.base.MicroServicePage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerWalletPgResponse {

    @ApiModelProperty(value = "带分页的钱包账户余额信息")
    private MicroServicePage<CustomerWalletVO> customerWalletVOList;
}
