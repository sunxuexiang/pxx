package com.wanmi.sbc.account;

import com.wanmi.sbc.account.api.provider.cashBack.CashBackProvider;
import com.wanmi.sbc.account.api.provider.cashBack.CashBackQueryProvider;
import com.wanmi.sbc.account.api.request.cashBack.CashBackModifyRequest;
import com.wanmi.sbc.account.api.request.cashBack.CashBackPageRequest;
import com.wanmi.sbc.account.api.response.cashBack.CashBackPageResponse;
import com.wanmi.sbc.common.base.BaseResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/account")
@Api(tags = "CashBackController", description = "S2B 公用-返现管理-返现打款表单API")
public class CashBackController {
    @Autowired
    private CashBackQueryProvider cashBackQueryProvider;
    @Autowired
    private CashBackProvider cashBackProvider;
    @ApiOperation(value = "查询返现打款")
    @RequestMapping(value = "/cashBackPageList", method = RequestMethod.POST)
    public BaseResponse<CashBackPageResponse> findPageCashBackList(@RequestBody CashBackPageRequest cashBackPageRequest) {
        return cashBackQueryProvider.page(cashBackPageRequest);
    }

    @ApiOperation(value = "查询返现打款")
    @RequestMapping(value = "/cashBackModify", method = RequestMethod.POST)
    public  BaseResponse cashBackModify(@RequestBody CashBackModifyRequest cashBackModifyRequest){
        cashBackProvider.modify(cashBackModifyRequest);
        return BaseResponse.SUCCESSFUL();
    }
}
