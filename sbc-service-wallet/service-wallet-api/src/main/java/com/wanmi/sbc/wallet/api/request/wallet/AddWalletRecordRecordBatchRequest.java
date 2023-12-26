package com.wanmi.sbc.wallet.api.request.wallet;

import com.wanmi.sbc.wallet.bean.vo.WalletRecordVO;
import io.swagger.annotations.ApiModel;
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
public class AddWalletRecordRecordBatchRequest implements Serializable {

    private static final long serialVersionUID = -9046163909631386663L;

    private List<WalletRecordVO> walletRecordVOList;
}
