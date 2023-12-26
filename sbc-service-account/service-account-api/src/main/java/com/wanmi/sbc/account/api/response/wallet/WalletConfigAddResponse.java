package com.wanmi.sbc.account.api.response.wallet;

import com.wanmi.sbc.account.bean.vo.WalletConfigVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletConfigAddResponse implements Serializable {
    private static final long serialVersionUID = 1038166320104594296L;

    @ApiModelProperty(value = "钱包限制消费配置信息")
    private WalletConfigVO walletConfigVO;
}
