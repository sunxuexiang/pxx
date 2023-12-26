package com.wanmi.sbc.wallet.api.response.wallet;

import com.wanmi.sbc.wallet.bean.vo.WalletSettingVO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class WalletSettingResPonse implements Serializable {

    private List<WalletSettingVO> response;
}
