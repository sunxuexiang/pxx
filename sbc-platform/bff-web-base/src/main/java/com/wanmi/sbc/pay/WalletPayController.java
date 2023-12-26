package com.wanmi.sbc.pay;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.constant.CustomerErrorCode;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.loginregister.CustomerSiteProvider;
import com.wanmi.sbc.customer.api.request.customer.CustomerGetByIdRequest;
import com.wanmi.sbc.customer.api.request.loginregister.CustomerCheckPayPasswordRequest;
import com.wanmi.sbc.customer.api.response.customer.CustomerGetByIdResponse;
import com.wanmi.sbc.order.api.provider.trade.PileTradeProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeProvider;
import com.wanmi.sbc.order.api.request.trade.WalletPayRequest;
import com.wanmi.sbc.pay.request.BalancePayRequest;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * <p>订单公共Controller</p>
 * Created by of628-wenzhi on 2017-07-10-下午4:12.
 */
@Api(tags = "WalletPayController", description = "余额支付API")
@RestController
@RequestMapping("/wallet")
@Slf4j
@Validated
public class WalletPayController {

    @Autowired
    private PileTradeProvider pileTradeProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private CustomerQueryProvider customerQueryProvider;

    @Autowired
    private CustomerSiteProvider customerSiteProvider;

    /**
     * 余额支付
     * @param request
     */
    @ApiOperation(value = "余额支付")
    @RequestMapping(value = "/walletPay", method = {RequestMethod.POST, RequestMethod.GET})
    @LcnTransaction
    public BaseResponse walletPay(@RequestBody @Valid BalancePayRequest request){
        WalletPayRequest convert = KsBeanUtil.convert(request, WalletPayRequest.class);
        convert.setCustomerId(commonUtil.getOperatorId());

        //校验密码是否可用
        CustomerGetByIdResponse customerGetByIdResponse = customerQueryProvider.getCustomerById(new
                CustomerGetByIdRequest(commonUtil.getCustomer().getCustomerId())).getContext();
        if (StringUtils.isBlank(customerGetByIdResponse.getCustomerPayPassword())) {
            throw new SbcRuntimeException(CustomerErrorCode.NO_CUSTOMER_PAY_PASSWORD);
        }
        if (customerGetByIdResponse.getPayErrorTime() != null && customerGetByIdResponse.getPayErrorTime() == 3) {
            Duration duration = Duration.between(customerGetByIdResponse.getPayLockTime(), LocalDateTime.now());
            if (duration.toMinutes() < 30) {
                //支付密码输错三次，并且锁定时间还未超过30分钟，返回账户冻结错误信息
                throw new SbcRuntimeException(CustomerErrorCode.CUSTOMER_PAY_LOCK_TIME_ERROR, new Object[]{30 - duration.toMinutes()});
            }
        }

        //校验密码是否正确
        CustomerCheckPayPasswordRequest customerCheckPayPasswordRequest = new CustomerCheckPayPasswordRequest();
        customerCheckPayPasswordRequest.setPayPassword(request.getPayPassword());
        customerCheckPayPasswordRequest.setCustomerId(commonUtil.getCustomer().getCustomerId());
        customerSiteProvider.checkCustomerPayPwd(customerCheckPayPasswordRequest);

        pileTradeProvider.walletPay(convert);
        return BaseResponse.SUCCESSFUL();
    }
}
