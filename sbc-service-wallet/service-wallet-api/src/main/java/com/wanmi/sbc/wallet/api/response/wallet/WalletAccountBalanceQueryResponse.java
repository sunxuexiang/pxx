package com.wanmi.sbc.wallet.api.response.wallet;

import com.wanmi.sbc.wallet.bean.vo.CusWalletVO;
import com.wanmi.sbc.common.base.MicroServicePage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description: 钱包账户余额分页查询响应
 * @author: jiangxin
 * @create: 2021-08-24 11:42
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletAccountBalanceQueryResponse implements Serializable {

    private static final long serialVersionUID = 1038164588304594296L;

    /**
     * 钱包账户信息分页
     */
    @ApiModelProperty(value = "带分页的钱包账户余额信息")
    private MicroServicePage<CusWalletVO> customerWalletVO;
}
