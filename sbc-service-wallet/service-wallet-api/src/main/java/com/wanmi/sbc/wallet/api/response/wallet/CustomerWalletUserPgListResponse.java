package com.wanmi.sbc.wallet.api.response.wallet;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.wallet.bean.vo.WalletListVO;
import com.wanmi.sbc.wallet.bean.vo.WalletRecordVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerWalletUserPgListResponse {

    @ApiModelProperty(value = "带分页的钱包账户余额信息")
    private MicroServicePage<WalletRecordVO> customerWalletVOList;
}
