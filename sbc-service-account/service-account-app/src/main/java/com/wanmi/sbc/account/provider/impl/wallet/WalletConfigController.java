package com.wanmi.sbc.account.provider.impl.wallet;

import com.wanmi.sbc.account.api.provider.wallet.WalletConfigProvider;
import com.wanmi.sbc.account.api.request.wallet.BalaceSettingRequest;
import com.wanmi.sbc.account.api.request.wallet.WalletConfigAddRequest;
import com.wanmi.sbc.account.api.request.wallet.WalletConfigRequest;
import com.wanmi.sbc.account.api.request.wallet.WalletSettingRequest;
import com.wanmi.sbc.account.api.response.wallet.WalletConfigAddResponse;
import com.wanmi.sbc.account.api.response.wallet.WalletConfigResponse;
import com.wanmi.sbc.account.api.response.wallet.WalletSettingResPonse;
import com.wanmi.sbc.account.wallet.service.WalletConfigService;
import com.wanmi.sbc.common.base.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class WalletConfigController implements WalletConfigProvider {

    @Autowired
    WalletConfigService walletConfigService;

    @Override
    public BaseResponse<WalletConfigAddResponse> editConfig(WalletConfigAddRequest request) {
        WalletConfigAddResponse walletConfigAddResponse = walletConfigService.editConfig(request);
        return BaseResponse.success(walletConfigAddResponse);
    }

    /**
     * 查询配置信息
     * @param request
     * @return
     */
    @Override
    public BaseResponse<WalletConfigResponse> getWalletConfig(WalletConfigRequest request) {
        WalletConfigResponse walletConfig = walletConfigService.getWalletConfig(request);
        return BaseResponse.success(walletConfig);
    }

    @Override
    public BaseResponse<WalletSettingResPonse> getWalletSettingByKey(BalaceSettingRequest request) {
        return walletConfigService.getWalletSettingByKey(request);
    }

    @Override
    public BaseResponse saveWalletSetting(WalletSettingRequest request) {

        return walletConfigService.saveWalletSetting(request);
    }
}
