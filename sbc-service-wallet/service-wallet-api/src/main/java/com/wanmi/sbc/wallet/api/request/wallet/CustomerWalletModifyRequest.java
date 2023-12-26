package com.wanmi.sbc.wallet.api.request.wallet;

import com.wanmi.sbc.wallet.api.request.BalanceBaseRequest;
import com.wanmi.sbc.wallet.bean.vo.CusWalletVO;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerWalletModifyRequest extends BalanceBaseRequest {

    private static final long serialVersionUID = -5864259709984682317L;

    private CusWalletVO cusWalletVO;
}
