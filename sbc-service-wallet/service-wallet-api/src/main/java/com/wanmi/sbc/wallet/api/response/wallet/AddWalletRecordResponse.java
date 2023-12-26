package com.wanmi.sbc.wallet.api.response.wallet;

import com.wanmi.sbc.wallet.bean.vo.WalletRecordVO;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddWalletRecordResponse implements Serializable {
    private static final long serialVersionUID = 1273365272689562313L;

    private WalletRecordVO walletRecord;
}
