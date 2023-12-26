package com.wanmi.sbc.wallet.api.response.wallet;

import com.wanmi.sbc.wallet.bean.vo.BindBankCardVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WithdrawalDetailsResponse implements Serializable {
    private static final long serialVersionUID = 1273365272689369513L;

    @ApiModelProperty(value = "可提现金额")
    private BigDecimal withdrawalAmount;

    @ApiModelProperty(value = "绑定的所有银行卡")
    private List<BindBankCardVo> banks;
}
