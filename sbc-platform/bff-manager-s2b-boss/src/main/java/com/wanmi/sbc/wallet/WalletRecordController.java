package com.wanmi.sbc.wallet;

import com.wanmi.sbc.account.api.provider.wallet.WalletRecordProvider;
import com.wanmi.sbc.account.api.request.wallet.WalletRecordRequest;
import com.wanmi.sbc.common.base.BaseResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Api(description = "钱包交易流水信息", tags = "WalletRecordController")
@RestController
@RequestMapping(value = "/boss/walletRecord")
public class WalletRecordController {

    @Autowired
    private WalletRecordProvider walletRecordProvider;
    /**
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "交易流水列表")
    @RequestMapping(value = "walletRecord",method = RequestMethod.POST)
    public BaseResponse walletRecord(@RequestBody WalletRecordRequest request){
        return walletRecordProvider.getQueryWalletRecord(request);
    }

    @ApiOperation(value = "交易流水列表")
    @RequestMapping(value = "queryPgWalletRecord",method = RequestMethod.POST)
    public BaseResponse queryPgWalletRecord(@RequestBody WalletRecordRequest request){
        return walletRecordProvider.queryPgWalletRecord(request);
    }
}
