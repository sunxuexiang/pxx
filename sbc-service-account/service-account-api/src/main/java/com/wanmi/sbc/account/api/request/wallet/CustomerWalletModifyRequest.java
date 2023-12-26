package com.wanmi.sbc.account.api.request.wallet;

import com.wanmi.sbc.account.api.request.AccountBaseRequest;
import com.wanmi.sbc.account.bean.vo.CustomerWalletVO;
import io.swagger.annotations.ApiModel;
import lombok.*;

@Data
@ApiModel
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerWalletModifyRequest  extends AccountBaseRequest {

    private static final long serialVersionUID = -5864259709984682317L;

    private CustomerWalletVO customerWalletVO;
}
