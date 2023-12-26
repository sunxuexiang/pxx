package com.wanmi.sbc.wallet.api.response.wallet;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.wallet.bean.vo.WalletListVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerWalletPgListResponse {

    @ApiModelProperty(value = "带分页的钱包账户余额信息")
    private MicroServicePage<WalletListVO> customerWalletVOList;
}
