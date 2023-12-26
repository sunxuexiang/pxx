package com.wanmi.sbc.pay.service;

import com.wanmi.sbc.advertising.api.provider.AdActivityProvider;
import com.wanmi.sbc.advertising.api.request.activity.ActPayCallBackRequest;
import com.wanmi.sbc.advertising.bean.constant.AdConstants;
import com.wanmi.sbc.common.constant.ErrorCodeConstant;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.order.api.provider.paycallbackresult.PayCallBackResultProvider;
import com.wanmi.sbc.order.api.request.paycallbackresult.PayCallBackResultAddRequest;
import com.wanmi.sbc.order.api.request.paycallbackresult.PayCallBackResultModifyResultStatusRequest;
import com.wanmi.sbc.order.api.request.trade.TradePayOnlineCallBackRequest;
import com.wanmi.sbc.order.bean.enums.PayCallBackResultStatus;
import com.wanmi.sbc.order.bean.enums.PayCallBackType;
import com.wanmi.sbc.pay.api.provider.CcbPayProvider;
import com.wanmi.sbc.wallet.api.request.wallet.TradePayWalletOnlineCallBackRequest;
import com.wanmi.sbc.wallet.bean.enums.PayWalletCallBackType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/12/20 14:56
 */
@Service
@Slf4j
public class CcbPayCallbackService {

    @Autowired
    private CcbPayProvider ccbPayProvider;

    @Autowired
    private PayCallBackTaskService payCallBackTaskService;

    @Autowired
    private AdActivityProvider adActivityProvider;

    @Autowired
    private PayCallBackResultProvider payCallBackResultProvider;

    public void ccbPayStatusSuccess(String request, String pyTrnNo) {
        String businessId = ccbPayProvider.ccbPayRecordSuccess(pyTrnNo).getContext();
        //  建行支付通知
        addPayCallBackResult(PayCallBackResultAddRequest.builder()
                .businessId(businessId)
                .resultXml(request)
                .resultContext(request)
                .resultStatus(PayCallBackResultStatus.HANDLING)
                .errorNum(0)
                .payType(PayCallBackType.CCB)
                .build());

        if (businessId.startsWith("W")) {
            log.info("鲸币充值回调开始");
            payCallBackTaskService.payMerchantCallBack(TradePayWalletOnlineCallBackRequest.builder()
                    .payCallBackType(PayWalletCallBackType.CCB)
                    .ccbPayCallBackResultStr(request)
                    .businessId(businessId)
                    .build());

        }
        if (businessId.startsWith(AdConstants.AD_ACTIVITY_ID_PREFIX)) {
            log.info("广告充值回调开始");
            adActivityProvider.payCallBack(ActPayCallBackRequest.builder().adActId(businessId).bizId(pyTrnNo).build());
            payCallBackResultProvider.modifyResultStatusByBusinessId(PayCallBackResultModifyResultStatusRequest.builder()
                    .businessId(businessId)
                    .resultStatus(PayCallBackResultStatus.SUCCESS)
                    .build());
        } else {
            payCallBackTaskService.payCallBack(TradePayOnlineCallBackRequest.builder().payCallBackType(PayCallBackType.CCB)
                    .ccbPayCallBackResultStr(request)
                    .businessId(businessId)
                    .build());
        }
    }

    private void addPayCallBackResult(PayCallBackResultAddRequest resultAddRequest){
        try{
            payCallBackResultProvider.add(resultAddRequest);
        } catch (SbcRuntimeException e) {
            //business_id唯一索引报错捕获，不影响流程处理
            if(!e.getErrorCode().equals(ErrorCodeConstant.PAY_CALL_BACK_RESULT_EXIT)){
                throw e;
            }
            e.printStackTrace();
        }
    }
}
