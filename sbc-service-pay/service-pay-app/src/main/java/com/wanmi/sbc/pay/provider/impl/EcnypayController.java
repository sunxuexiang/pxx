package com.wanmi.sbc.pay.provider.impl;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.pay.api.provider.EcnyPayProvider;
import com.wanmi.sbc.pay.api.request.PayExtraRequest;
import com.wanmi.sbc.pay.api.request.PayTradeRecordRequest;
import com.wanmi.sbc.pay.api.response.EcnyPayFormResponse;
import com.wanmi.sbc.pay.service.EcnyPayService;

/**
 * 数据字人民币
 * @author dkq
 *
 */
@RestController
@Validated
@Slf4j
public class EcnypayController implements EcnyPayProvider {

    @Autowired
    EcnyPayService ecnypayService;

    @Override
    public BaseResponse<EcnyPayFormResponse> getPayForm(@RequestBody @Valid PayExtraRequest request) {
        return BaseResponse.success(new EcnyPayFormResponse(ecnypayService.pay(request)));
    }

	 

    
}
