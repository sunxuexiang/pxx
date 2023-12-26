package com.wanmi.sbc.wallet;

import com.wanmi.sbc.account.api.provider.wallet.WalletConfigProvider;
import com.wanmi.sbc.account.api.request.wallet.BalaceSettingRequest;
import com.wanmi.sbc.account.api.request.wallet.WalletConfigAddRequest;
import com.wanmi.sbc.account.api.request.wallet.WalletConfigRequest;
import com.wanmi.sbc.account.api.request.wallet.WalletSettingRequest;
import com.wanmi.sbc.account.api.response.wallet.WalletConfigAddResponse;
import com.wanmi.sbc.account.api.response.wallet.WalletConfigResponse;
import com.wanmi.sbc.account.api.response.wallet.WalletSettingResPonse;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(description = "钱包限制消费配置API",tags = "WalletBalanceQueryController")
@RestController
@RequestMapping(value = "/wallet/config")
@Slf4j
public class WalletConfigController {

    @Autowired
    WalletConfigProvider walletConfigProvider;

    @Autowired
    OperateLogMQUtil operateLogMQUtil;


    @ApiOperation(value = "新增修改配置")
    @PostMapping(value = "/editConfig")
    public BaseResponse<WalletConfigAddResponse> editConfig(@RequestBody WalletConfigAddRequest request){
            operateLogMQUtil.convertAndSend("钱包", "钱包限制消费配置", "新增修改配置");
            return walletConfigProvider.editConfig(request);
    }

    @ApiOperation(value = "查询配置信息")
    @PostMapping(value = "/getWalletConfig")
    public BaseResponse<WalletConfigResponse> getWalletConfig(@RequestBody WalletConfigRequest request){
        return walletConfigProvider.getWalletConfig(request);
    }


    @ApiOperation(value = "保存说明配置信息")
    @PostMapping(value = "/saveWalletSetting")
    public BaseResponse saveWalletSetting(@RequestBody WalletSettingRequest request){
        operateLogMQUtil.convertAndSend("钱包", "钱包限制消费配置", "保存说明配置信息");
        return walletConfigProvider.saveWalletSetting(request);
    }

//    @ApiOperation(value = "查询说明配置信息")
//    @PostMapping(value = "/getWalletSetting")
//    public BaseResponse getWalletSetting(){
//        return walletConfigProvider.getWalletSetting();
//    }
    @ApiOperation(value = "获取配置协议")
    @RequestMapping(value = "/getWalletSettingByKey",method = RequestMethod.POST)
    BaseResponse<WalletSettingResPonse> getWalletSettingByKey(@RequestBody BalaceSettingRequest request){
        return walletConfigProvider.getWalletSettingByKey(request);
    }
}
