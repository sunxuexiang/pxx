package com.wanmi.sbc.pay.provider.impl;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.pay.api.provider.AliPayProvider;
import com.wanmi.sbc.pay.api.request.AliPayPaymentSlipRequest;
import com.wanmi.sbc.pay.api.request.AliPayRefundRequest;
import com.wanmi.sbc.pay.api.request.PayExtraRequest;
import com.wanmi.sbc.pay.api.request.PayRequest;
import com.wanmi.sbc.pay.api.response.AliPayFormResponse;
import com.wanmi.sbc.pay.api.response.AliPayRefundResponse;
import com.wanmi.sbc.pay.bean.enums.PayGatewayEnum;
import com.wanmi.sbc.pay.model.root.PayChannelItem;
import com.wanmi.sbc.pay.model.root.PayGateway;
import com.wanmi.sbc.pay.model.root.PayGatewayConfig;
import com.wanmi.sbc.pay.service.AlipayService;
import com.wanmi.sbc.pay.service.PayDataService;
import com.wanmi.sbc.pay.utils.PayValidates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Objects;

/**
 * @program: service-pay
 * @description: 支付宝
 * @create: 2019-01-28 16:30
 **/
@RestController
@Validated
@Slf4j
public class AlipayController implements AliPayProvider {

    @Autowired
    AlipayService alipayService;
    @Autowired
    PayDataService payDataService;

    @Override
    public BaseResponse<AliPayFormResponse> getPayForm(@RequestBody @Valid PayExtraRequest request) {
        return BaseResponse.success(new AliPayFormResponse(alipayService.pay(request)));
    }

    @Override
    public BaseResponse<AliPayFormResponse> getPilePayForm(PayExtraRequest request) {
        return BaseResponse.success(new AliPayFormResponse(alipayService.pilePay(request)));
    }


    @Override
    public BaseResponse<AliPayFormResponse> getPayRechargeForm(PayExtraRequest request) {
        return BaseResponse.success(new AliPayFormResponse(alipayService.payRecharge(request)));
    }

    @Override
    public BaseResponse<AliPayFormResponse> getPayTakeGoodForm(PayExtraRequest request) {
        return BaseResponse.success(new AliPayFormResponse(alipayService.payTakeGood(request)));
    }

    @Override
    public BaseResponse<AliPayRefundResponse> aliPayRefund(@RequestBody @Valid AliPayRefundRequest refundRequest) {
        // Todo Saas独立收款
        PayGatewayConfig payGatewayConfig = payDataService.queryConfigByNameAndStoreId(PayGatewayEnum.ALIPAY,0L);
        if (Objects.nonNull(payGatewayConfig)) {
            refundRequest.setAppid(payGatewayConfig.getAppId());
            refundRequest.setAliPayPublicKey(payGatewayConfig.getPublicKey());
            refundRequest.setAppPrivateKey(payGatewayConfig.getPrivateKey());
        } else {
            throw new SbcRuntimeException("K-100205");
        }
        AliPayRefundResponse aliPayRefundResponse = new AliPayRefundResponse();
        aliPayRefundResponse.setAlipayTradeRefundResponse(alipayService.tradeRefund(refundRequest));
        return BaseResponse.success(aliPayRefundResponse);
    }

    @Override
    public BaseResponse<AlipayTradeQueryResponse> queryAlipayPaymentSlip(AliPayPaymentSlipRequest request) {
        return BaseResponse.success(alipayService.queryAlipayPaymentSlip(request.getAlipayClient(),request.getBusinessId()));
    }

}
