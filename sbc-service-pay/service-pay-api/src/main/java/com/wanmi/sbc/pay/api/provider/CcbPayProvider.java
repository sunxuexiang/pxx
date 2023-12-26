package com.wanmi.sbc.pay.api.provider;

import com.alipay.api.response.AlipayTradeQueryResponse;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.pay.api.request.*;
import com.wanmi.sbc.pay.api.response.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/6/13 10:56
 */
@FeignClient(value = "${application.pay.name}", contextId = "CcbPayProvider", url = "${feign.url.pay:#{null}}")
public interface CcbPayProvider {

    @PostMapping("/pay/${application.pay.version}/ccb/verifySign")
    BaseResponse<Boolean> verifySign(@RequestBody String request);

    @PostMapping("/pay/${application.pay.version}/ccb/businessNotify")
    BaseResponse businessNotify(@RequestBody String request);

    @GetMapping("/pay/${application.pay.version}/ccb/business/query")
    BaseResponse businessQuery();

    @PostMapping("/pay/${application.pay.version}/ccb/ruleNotify")
    BaseResponse ruleNotify(@RequestBody String request);

    @GetMapping("/pay/${application.pay.version}/ccb/rule/query")
    BaseResponse ruleQuery();

    @PostMapping("/pay/${application.pay.version}/ccb/order")
    BaseResponse<CcbPayOrderResponse> ccbPayOrder(@RequestBody CcbPayOrderRequest request);

    @PostMapping("/pay/${application.pay.version}/ccb/refund")
    BaseResponse<String> ccbPayRefund(@RequestBody CcbRefundRequest request);

    @GetMapping("/pay/${application.pay.version}/ccb/ccbPayRecordSuccess")
    BaseResponse<String> ccbPayRecordSuccess(@RequestParam("pyTrnNo") String pyTrnNo);

    @GetMapping("/pay/${application.pay.version}/ccb/ccbRefundSuccess")
    BaseResponse ccbRefundSuccess(@RequestParam("custRfndTrcno") String custRfndTrcno);

    @GetMapping("/pay/${application.pay.version}/ccb/syncCcbSubAccountStatus")
    BaseResponse syncCcbSubAccountStatus();

    @GetMapping("/pay/${application.pay.version}/ccb/verifySign2")
    BaseResponse<Boolean> verifySign2(@RequestParam("oriString") String oriString, @RequestParam("signInf") String signInf);

    @GetMapping("/pay/${application.pay.version}/ccb/validCcbMerchantNo")
    BaseResponse<Boolean> validCcbMerchantNo(@RequestParam("ccbMerchantNo") String ccbMerchantNo);

    @GetMapping("/pay/${application.pay.version}/ccb/queryCcbPayOrderRecord")
    BaseResponse<CcbPayOrderRecordResponse> queryCcbPayOrderRecord(@RequestParam("tradeId") String tradeId, @RequestParam("payOrderNo") String payOrderNo);

    @GetMapping("/pay/${application.pay.version}/ccb/queryBusinessIdByPyTrnNo")
    BaseResponse<String> queryBusinessIdByPyTrnNo(@RequestParam("pyTrnNo") String pyTrnNo);

    @GetMapping("/pay/${application.pay.version}/ccb/addRefundAmt")
    BaseResponse addRefundAmt(@RequestParam("tid") String tid, @RequestParam("payOrderNo") String payOrderNo, @RequestParam("rid") String rid);

    @GetMapping("/pay/${application.pay.version}/ccb/ccbAlipayTradeQuery/{tradeNo}")
    BaseResponse<AlipayTradeQueryResponse> ccbAlipayTradeQuery(@PathVariable("tradeNo") String tradeNo);

    @GetMapping("/pay/${application.pay.version}/ccb/queryCcbPayRecordByPayOrderNo")
    BaseResponse<CcbPayRecordResponse> queryCcbPayRecordByPayOrderNo(@RequestParam("payOrderNo") String payOrderNo);

    @GetMapping("/pay/${application.pay.version}/ccb/queryCcbPayRecordByPyTrnNo")
    BaseResponse<CcbPayRecordResponse> queryCcbPayRecordByPyTrnNo(@RequestParam("pyTrnNo") String pyTrnNo);

    @PostMapping("/pay/${application.pay.version}/ccb/saveCcbPayImg")
    BaseResponse saveCcbPayImg(@RequestBody @Valid CcbPayImgRequest request);

    @GetMapping("/pay/${application.pay.version}/ccb/queryCcbRefundRecordByRid")
    BaseResponse<CcbRefundRecordResponse> queryCcbRefundRecordByRid(@RequestParam("rid") String rid);

    @GetMapping("/pay/${application.pay.version}/ccb/queryCcbBusinessByName")
    BaseResponse<CcbBusinessResponse> queryCcbBusinessByName(@RequestParam("name") String name);

    @PostMapping("/pay/${application.pay.version}/ccb/adRefund")
    BaseResponse<CcbRefundAdResponse> adRefund(@RequestBody @Valid CcbRefundAdRequest request);

    @PostMapping("/pay/${application.pay.version}/ccb/freightRefund")
    BaseResponse<CcbRefundFreightResponse> freightRefund(@RequestBody @Valid CcbRefundFreightRequest request);

    @PostMapping("/pay/${application.pay.version}/ccb/extraRefund")
    BaseResponse<CcbRefundFreightResponse> extraRefund(@RequestBody @Valid CcbRefundExtraRequest extraRequest);

    @GetMapping("/pay/${application.pay.version}/ccb/queryCcbPayStatusByPyTrnNo")
    BaseResponse<CcbPayStatusQueryResponse> queryCcbPayStatusByPyTrnNo(@RequestParam("pyTrnNo") String pyTrnNo);

    @GetMapping("/pay/${application.pay.version}/ccb/delayCcbConfirm")
    BaseResponse delayCcbConfirm(@RequestParam("pyTrnNo") String pyTrnNo, @RequestParam("millis") long millis);
}
