package com.wanmi.sbc.wallet.api.response.wallet;

import com.wanmi.sbc.wallet.bean.enums.JingBiState;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletStatusResponse implements Serializable {
    private static final long serialVersionUID = 1273365272648963201L;

    private JingBiState jingBiState;
}
