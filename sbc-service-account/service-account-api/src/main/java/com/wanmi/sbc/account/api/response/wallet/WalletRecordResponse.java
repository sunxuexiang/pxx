package com.wanmi.sbc.account.api.response.wallet;

import com.wanmi.sbc.account.bean.vo.BalanceInfoVO;
import com.wanmi.sbc.account.bean.vo.ExtractInfoVO;
import com.wanmi.sbc.account.bean.vo.WalletRecordVO;
import com.wanmi.sbc.common.base.MicroServicePage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletRecordResponse  implements Serializable {
    private static final long serialVersionUID = 1273365272689369513L;

    @ApiModelProperty(value = "单个交易记录实体")
    private WalletRecordVO walletRecordVO;

    @ApiModelProperty(value = "多个交易记录实体")
    private List<WalletRecordVO> walletRecordVOs;

    @ApiModelProperty(value = "多个交易记录实体--分页")
    private MicroServicePage<WalletRecordVO> pageList;

    @ApiModelProperty(value = "多个提现记录详情实体--分页")
    private MicroServicePage<ExtractInfoVO> extractInfoPageList;

    @ApiModelProperty(value = "余额明细实体 -- 分页")
    private MicroServicePage<BalanceInfoVO> balanceInfoPageList;

}
