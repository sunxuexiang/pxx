package com.wanmi.sbc.pay.provider.impl;

import com.alipay.api.response.AlipayTradeQueryResponse;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.pay.api.provider.CcbPayProvider;
import com.wanmi.sbc.pay.api.request.*;
import com.wanmi.sbc.pay.api.response.*;
import com.wanmi.sbc.pay.model.root.CcbBusiness;
import com.wanmi.sbc.pay.model.root.CcbPayRecord;
import com.wanmi.sbc.pay.model.root.CcbRefundRecord;
import com.wanmi.sbc.pay.mq.CcbPayProducerService;
import com.wanmi.sbc.pay.service.CcbService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/6/13 10:57
 */
@RestController
@Validated
@Slf4j
public class CcbPayController implements CcbPayProvider {

    @Autowired
    private CcbService ccbService;

    @Autowired
    private CcbPayProducerService ccbPayProducerService;

    @Override
    public BaseResponse<Boolean> verifySign(String request) {
        return BaseResponse.success(ccbService.verifySign(request));
    }

    @Override
    public BaseResponse businessNotify(String request) {
        ccbService.businessNotify(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse businessQuery() {
        ccbService.businessQuery();
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse ruleNotify(String request) {
        ccbService.ruleNotify(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse ruleQuery() {
        ccbService.ruleQuery();
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<CcbPayOrderResponse> ccbPayOrder(CcbPayOrderRequest request) {
        return BaseResponse.success(ccbService.ccbPayOrder(request));
    }

    @Override
    public BaseResponse<String> ccbPayRefund(CcbRefundRequest request) {
        return BaseResponse.success(ccbService.refundOrder(request));
    }

    @Override
    public BaseResponse<String> ccbPayRecordSuccess(String pyTrnNo) {
        return BaseResponse.success(ccbService.ccbPayRecordSuccess(pyTrnNo));
    }

    @Override
    public BaseResponse ccbRefundSuccess(String custRfndTrcno) {
        ccbService.ccbRefundSuccess(custRfndTrcno);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse syncCcbSubAccountStatus() {
        ccbService.syncCcbSubAccountStatus();
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<Boolean> verifySign2(String oriString, String signInf) {
        return BaseResponse.success(ccbService.verifySign2(oriString, signInf));
    }

    @Override
    public BaseResponse<Boolean> validCcbMerchantNo(String ccbMerchantNo) {
        return BaseResponse.success(ccbService.validCcbMerchantNo(ccbMerchantNo));
    }

    @Override
    public BaseResponse<CcbPayOrderRecordResponse> queryCcbPayOrderRecord(String tradeId, String payOrderNo) {
        return BaseResponse.success(KsBeanUtil.convert(ccbService.queryCcbPayOrderRecord(tradeId, payOrderNo), CcbPayOrderRecordResponse.class));
    }

    @Override
    public BaseResponse<String> queryBusinessIdByPyTrnNo(String pyTrnNo) {
        return BaseResponse.success(ccbService.queryBusinessIdByPyTrnNo(pyTrnNo));
    }

    @Override
    public BaseResponse addRefundAmt(String tid, String payOrderNo, String rid) {
        CcbPayRecord payRecord = ccbService.queryCcbPayRecordByPayOrderNo(payOrderNo);
        ccbService.addRefundAmt(tid, payRecord.getPyTrnNo(), rid);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<AlipayTradeQueryResponse> ccbAlipayTradeQuery(String tradeNo) {
        return BaseResponse.success(ccbService.alipayTradeQuery(tradeNo));
    }

    @Override
    public BaseResponse<CcbPayRecordResponse> queryCcbPayRecordByPayOrderNo(String payOrderNo) {
        CcbPayRecord ccbPayRecord = ccbService.queryCcbPayRecordByPayOrderNo(payOrderNo);
        if (Objects.isNull(ccbPayRecord)) {
            return BaseResponse.success(null);
        }
        return BaseResponse.success(KsBeanUtil.convert(ccbPayRecord, CcbPayRecordResponse.class));
    }

    @Override
    public BaseResponse<CcbPayRecordResponse> queryCcbPayRecordByPyTrnNo(String pyTrnNo) {
        CcbPayRecord ccbPayRecord = ccbService.queryCcbPayRecordByPyTrnNo(pyTrnNo);
        if (Objects.isNull(ccbPayRecord)) {
            return BaseResponse.success(null);
        }
        return BaseResponse.success(KsBeanUtil.convert(ccbPayRecord, CcbPayRecordResponse.class));
    }

    @Override
    public BaseResponse saveCcbPayImg(CcbPayImgRequest request) {
        ccbService.saveCcbPayImg(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<CcbRefundRecordResponse> queryCcbRefundRecordByRid(String rid) {
        CcbRefundRecord ccbRefundRecord = ccbService.queryCcbRefundRecordByRid(rid);
        if (Objects.isNull(ccbRefundRecord)) {
            return BaseResponse.success(null);
        }
        return BaseResponse.success(KsBeanUtil.convert(ccbRefundRecord, CcbRefundRecordResponse.class));
    }

    @Override
    public BaseResponse<CcbBusinessResponse> queryCcbBusinessByName(String name) {
        CcbBusiness ccbBusiness = ccbService.queryCcbBusinessByName(name);
        if (Objects.isNull(ccbBusiness)) {
            return BaseResponse.success(null);
        }
        return BaseResponse.success(KsBeanUtil.convert(ccbBusiness, CcbBusinessResponse.class));
    }

    @Override
    public BaseResponse<CcbRefundAdResponse> adRefund(CcbRefundAdRequest request) {

        return BaseResponse.success(ccbService.adRefund(request));
    }

    @Override
    public BaseResponse<CcbRefundFreightResponse> freightRefund(CcbRefundFreightRequest request) {
        return BaseResponse.success(ccbService.freightRefund(request));
    }

    @Override
    public BaseResponse<CcbRefundFreightResponse> extraRefund(CcbRefundExtraRequest extraRequest) {
        return BaseResponse.success(ccbService.extraRefund(extraRequest));
    }

    @Override
    public BaseResponse<CcbPayStatusQueryResponse> queryCcbPayStatusByPyTrnNo(String pyTrnNo) {
        return BaseResponse.success(ccbService.ccbPayStatusQuery(pyTrnNo));
    }

    @Override
    public BaseResponse delayCcbConfirm(String pyTrnNo, long millis) {
        ccbPayProducerService.delayCcbConfirm(pyTrnNo, millis);
        return BaseResponse.SUCCESSFUL();
    }

}
