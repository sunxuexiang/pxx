package com.wanmi.sbc.wallet;

import com.wanmi.sbc.account.api.provider.wallet.WalletConfigProvider;
import com.wanmi.sbc.account.api.request.wallet.BalaceSettingRequest;
import com.wanmi.sbc.account.api.response.wallet.WalletSettingResPonse;
import com.wanmi.sbc.common.base.BaseResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(description = "钱包api", tags = "BalanceSettingController")
@RestController
@Slf4j
@RequestMapping(value = "/balanceSetting")
public class BalanceSettingController {

    @Autowired
    private WalletConfigProvider walletConfigProvider;


    @ApiOperation(value = "获取配置协议")
    @PostMapping(value = "/getWalletSettingByKey")
    BaseResponse<WalletSettingResPonse> getWalletSettingByKey(@RequestBody BalaceSettingRequest request){
        return walletConfigProvider.getWalletSettingByKey(request);
    }
}
