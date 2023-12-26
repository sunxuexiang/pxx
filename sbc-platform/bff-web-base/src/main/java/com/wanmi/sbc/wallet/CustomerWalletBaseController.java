package com.wanmi.sbc.wallet;

import com.wanmi.sbc.account.api.provider.wallet.CustomerWalletProvider;
import com.wanmi.sbc.account.api.request.wallet.CustomerWalletTradePriceRequest;
import com.wanmi.sbc.account.api.response.wallet.CustomerWalletTradePriceResponse;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponCodeQueryProvider;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.wallet.request.CustomerWalletBaseRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api(tags = "CustomerWalletBaseController", description = "钱包API")
@RestController
@RequestMapping("/customerWallet")
@Slf4j
@Validated
public class CustomerWalletBaseController {

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private CustomerWalletProvider customerWalletProvider;

    @Autowired
    private CouponCodeQueryProvider couponCodeQueryProvider;

    @ApiOperation(value = "用户支付, 使用余额预扣减")
    @RequestMapping(value = "/useCustomerWallet", method = RequestMethod.POST)
    public BaseResponse<CustomerWalletTradePriceResponse> useCustomerWallet(@Valid @RequestBody CustomerWalletBaseRequest customerWalletBaseRequest) {
        CustomerVO customer = commonUtil.getCustomer();

        CustomerWalletTradePriceRequest customerWalletTradePriceRequest = new CustomerWalletTradePriceRequest();
        customerWalletTradePriceRequest.setCustomerId(customer.getCustomerId());
        KsBeanUtil.copyProperties(customerWalletBaseRequest, customerWalletTradePriceRequest);

        return customerWalletProvider.useCustomerWallet(customerWalletTradePriceRequest);
    }
}
