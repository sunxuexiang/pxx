package com.wanmi.sbc.wallet.api.request.wallet;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.extern.java.Log;
import org.checkerframework.checker.units.qual.A;


@Data
@ApiModel
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WalletInfoRequest extends BaseQueryRequest {

    private static final long serialVersionUID = 9094629374388797324L;

    @ApiModelProperty(value = "钱包ID")
    private Long walletId;
    @ApiModelProperty(value = "是否商家")
    private Boolean storeFlag;
    @ApiModelProperty(value = "商家ID")
    private String storeId;
}
