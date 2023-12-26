package com.wanmi.sbc.account.api.provider.wallet;

import com.wanmi.sbc.account.api.request.wallet.BalaceSettingRequest;
import com.wanmi.sbc.account.api.request.wallet.WalletConfigAddRequest;
import com.wanmi.sbc.account.api.request.wallet.WalletConfigRequest;
import com.wanmi.sbc.account.api.request.wallet.WalletSettingRequest;
import com.wanmi.sbc.account.api.response.wallet.WalletConfigAddResponse;
import com.wanmi.sbc.account.api.response.wallet.WalletConfigResponse;
import com.wanmi.sbc.account.api.response.wallet.WalletSettingResPonse;
import com.wanmi.sbc.common.base.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 钱包消费配置接口
 */
@FeignClient(value = "${application.account.name}", url="${feign.url.account:#{null}}", contextId = "WalletConfigProvider")
public interface WalletConfigProvider {

    @PostMapping("/account/${application.account.version}/wallet/walletConfig/editConfig")
    BaseResponse<WalletConfigAddResponse> editConfig(@RequestBody WalletConfigAddRequest request);

    @PostMapping("/account/${application.account.version}/wallet/walletConfig/getWalletConfig")
    BaseResponse<WalletConfigResponse> getWalletConfig(@RequestBody WalletConfigRequest request);

    @PostMapping("/account/${application.account.version}/wallet/walletConfig/getWalletSettingByKey")
    BaseResponse<WalletSettingResPonse> getWalletSettingByKey(@RequestBody BalaceSettingRequest request);

    @PostMapping("/account/${application.account.version}/wallet/walletConfig/saveWalletSetting")
    BaseResponse saveWalletSetting(@RequestBody WalletSettingRequest request);

}
