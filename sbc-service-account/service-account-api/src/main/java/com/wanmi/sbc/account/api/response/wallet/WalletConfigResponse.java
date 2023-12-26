package com.wanmi.sbc.account.api.response.wallet;

import com.wanmi.sbc.account.bean.vo.WalletConfigVO;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletConfigResponse implements Serializable {
    private static final long serialVersionUID = 123656320104594296L;

    @ApiModelProperty(value = "钱包限制消费配置信息")
    private List<WalletConfigVO> walletConfigVO;
}
