package com.wanmi.sbc.pay.provider.impl;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.pay.api.provider.BalancePayProvider;
import com.wanmi.sbc.pay.api.provider.CmbPayProvider;
import com.wanmi.sbc.pay.api.request.CmbPayOrderRequest;
import com.wanmi.sbc.pay.api.request.CmbPayRefundDataRequest;
import com.wanmi.sbc.pay.api.response.CmbPayOrderResponse;
import com.wanmi.sbc.pay.api.response.CmbPayRefundResponse;
import com.wanmi.sbc.pay.service.CmbService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: service-pay
 * @description: 余额
 * @create: 2019-01-28 16:30
 **/
@RestController
@Validated
@Slf4j
public class BalancePayController implements BalancePayProvider {


    @Autowired
    private CmbService cmsService;


//    @Override
//    public BaseResponse cmbPayRefund() {
//        return null;
//    }
//
//    @Override
//    public BaseResponse cmbCallback(CmbCallBackRequest appRequest) {
//      return cmsService.cmbCallback(appRequest);
//    }

    @Override
    public BaseResponse<CmbPayOrderResponse> balancePayOrder(@RequestBody CmbPayOrderRequest cmbPayOrderRequest) {
        return BaseResponse.success(cmsService.cmbPayOrder(cmbPayOrderRequest));
    }

    @Override
    public BaseResponse<CmbPayRefundResponse> balancePayRefund(@RequestBody CmbPayRefundDataRequest cmbPayRefundDataRequest) {
        return BaseResponse.success(cmsService.cmbPayRefund(cmbPayRefundDataRequest));
    }
}
