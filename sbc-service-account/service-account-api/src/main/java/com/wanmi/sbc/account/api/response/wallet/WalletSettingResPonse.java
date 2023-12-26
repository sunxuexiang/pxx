package com.wanmi.sbc.account.api.response.wallet;

import com.wanmi.sbc.account.bean.vo.WalletSettingVO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class WalletSettingResPonse implements Serializable {

    private List<WalletSettingVO> response;
}
