package com.wanmi.sbc.wallet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.internal.util.StringUtils;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.pay.api.provider.AliPayProvider;
import com.wanmi.sbc.pay.api.provider.WxPayProvider;
import com.wanmi.sbc.pay.api.request.PayExtraRequest;
import com.wanmi.sbc.pay.api.request.WxPayForNativeRequest;
import com.wanmi.sbc.pay.api.response.AliPayFormResponse;
import com.wanmi.sbc.pay.api.response.WxPayForNativeResponse;
import com.wanmi.sbc.pay.bean.enums.TerminalType;
import com.wanmi.sbc.pay.bean.enums.WxPayTradeType;
import com.wanmi.sbc.pay.reponse.PayQRCodeReponse;
import com.wanmi.sbc.setting.api.provider.AuditQueryProvider;
import com.wanmi.sbc.setting.bean.vo.ConfigVO;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.wallet.api.provider.wallet.CustomerWalletProvider;
import com.wanmi.sbc.wallet.api.provider.wallet.CustomerWalletQueryProvider;
import com.wanmi.sbc.wallet.api.provider.wallet.WalletMerchantProvider;
import com.wanmi.sbc.wallet.api.request.wallet.*;
import com.wanmi.sbc.wallet.api.response.wallet.BalanceByCustomerIdResponse;
import com.wanmi.sbc.wallet.api.response.wallet.CustomerWalletStorePgResponse;
import com.wanmi.sbc.wallet.bean.vo.TicketsFormQueryVO;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

@Api(tags = "WalletController", description = "钱包")
@RestController
@RequestMapping("/wallet")
@Slf4j
public class WalletController {

    @Autowired
    private CustomerWalletQueryProvider customerWalletQueryProvider;

    @ApiOperation(value = "用户鲸币列表查询")
    @RequestMapping(value = "/userStoreList", method = RequestMethod.POST)
    public BaseResponse userStoreList (@RequestBody WalletUserPageQueryRequest request) throws Exception {
        return customerWalletQueryProvider.userAndStoredList(request);
    }

}
