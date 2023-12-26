package com.wanmi.sbc.account.api.request.wallet;

import com.wanmi.sbc.account.bean.vo.WalletSettingVO;
import lombok.Data;

import java.util.List;

@Data
public class WalletSettingRequest {

    private List<WalletSettingVO> response;
}
