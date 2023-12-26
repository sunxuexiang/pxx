package com.wanmi.sbc.returnorder.provider.impl.manualrefund;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.account.bean.enums.PayWay;
import com.wanmi.sbc.account.bean.enums.RefundStatus;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.bean.vo.CustomerDetailVO;
import com.wanmi.sbc.returnorder.api.provider.manualrefund.ManualRefundQueryProvider;
import com.wanmi.sbc.returnorder.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.returnorder.api.request.manualrefund.ManualRefundResponseByOrderCodeRequest;
import com.wanmi.sbc.returnorder.api.request.trade.TradeGetByIdRequest;
import com.wanmi.sbc.returnorder.api.response.manualrefund.ManualRefundResponse;
import com.wanmi.sbc.returnorder.bean.vo.TradeVO;
import com.wanmi.sbc.returnorder.common.OperationLogMq;
import com.wanmi.sbc.returnorder.customer.service.CustomerCommonService;
import com.wanmi.sbc.returnorder.manualrefund.model.root.ManualRefund;
import com.wanmi.sbc.returnorder.manualrefund.service.ManualRefundService;
import com.wanmi.sbc.returnorder.returnorder.model.root.NewPileReturnOrder;
import com.wanmi.sbc.returnorder.trade.model.newPileTrade.NewPileTrade;
import com.wanmi.sbc.returnorder.trade.service.TradeService;
import com.wanmi.sbc.returnorder.trade.service.newPileTrade.NewPileTradeService;
import com.wanmi.sbc.pay.api.provider.CupsPayProvider;
import com.wanmi.sbc.pay.api.provider.PayQueryProvider;
import com.wanmi.sbc.pay.api.request.CupsPayRefundDataRequest;
import com.wanmi.sbc.pay.api.request.GatewayConfigByGatewayRequest;
import com.wanmi.sbc.pay.api.request.PayTradeRecordRequest;
import com.wanmi.sbc.pay.api.response.CupsPayRefundResponse;
import com.wanmi.sbc.pay.api.response.PayGatewayConfigResponse;
import com.wanmi.sbc.pay.api.response.PayTradeRecordResponse;
import com.wanmi.sbc.pay.bean.enums.PayGatewayEnum;
import com.wanmi.sbc.pay.bean.enums.TradeStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Validated
@RestController
@Slf4j
public class ManualRefundQueryController implements ManualRefundQueryProvider {

    @Autowired
    private ManualRefundService manualRefundService;

    @Autowired
    private TradeQueryProvider tradeQueryProvider;

    @Autowired
    private PayQueryProvider payQueryProvider;

    @Autowired
    private CustomerCommonService customerCommonService;

    @Autowired
    private CupsPayProvider cupsPayProvider;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private NewPileTradeService newPileTradeService;

    @Autowired
    private OperationLogMq operationLogMq;

    @Autowired
    private MongoTemplate mongoTemplate;

    private  static String sourceCode = "31YT";

    /**
     * 根据订单ID查询退款列表
     * @param manualRefundResponseByOrderCodeRequest {@link ManualRefundResponseByOrderCodeRequest }
     * @return
     */
    @Override
    public BaseResponse<List<ManualRefundResponse>> getManualRefundRespByOrderCode(@Valid ManualRefundResponseByOrderCodeRequest manualRefundResponseByOrderCodeRequest) {
        List<ManualRefundResponse> manualRefundResponses = manualRefundService.findManualRefundOrdersRespByOrderCode(manualRefundResponseByOrderCodeRequest.getOrderCode());
        return BaseResponse.success(KsBeanUtil.convert(manualRefundResponses,ManualRefundResponse.class));
    }

    @Override
    public BaseResponse<List<ManualRefundResponse>> manualRefundByOrderCode(ManualRefundResponseByOrderCodeRequest manualRefundResponseByOrderCodeRequest) {
        TradeVO trade = tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(manualRefundResponseByOrderCodeRequest.getOrderCode()).build()).getContext().getTradeVO();
        if(!(trade.getPayWay().name().equals(PayWay.CUPSALI.name()) || trade.getPayWay().name().equals(PayWay.CUPSWECHAT.name()) )){
            throw new SbcRuntimeException("K-999999","该订单不支持手动退款");
        }
        List<ManualRefundResponse> manualRefundResponses = manualRefundService.findManualRefundOrdersRespByOrderCode(manualRefundResponseByOrderCodeRequest.getOrderCode());
        BigDecimal totalPrice =  trade.getTradePrice().getTotalPrice();
        BigDecimal applyPrice = new BigDecimal(manualRefundResponseByOrderCodeRequest.getRefundPrice());
        if(totalPrice.compareTo(applyPrice) == -1){
            throw new SbcRuntimeException("K-999999","申请金额大于订单金额");
        }
        //如果没 有退款记录，则直接退款，先加记录，在走银联退款
        if(!CollectionUtils.isEmpty(manualRefundResponses)){
            BigDecimal refundPrice = new BigDecimal(0);
            for (ManualRefundResponse vo: manualRefundResponses) {
                refundPrice = refundPrice.add(vo.getApplyPrice());
            }
            refundPrice = refundPrice.add(applyPrice);
            if(totalPrice.compareTo(refundPrice) == -1){
                throw new SbcRuntimeException("K-999999","可退金额不足");
            }
        }

        PayTradeRecordRequest recordRequest  = new PayTradeRecordRequest();
        recordRequest.setBusinessId(trade.getParentId());
        PayTradeRecordResponse record = payQueryProvider.findByBusinessId(recordRequest).getContext();
        if(Objects.isNull(record) || !record.getStatus().equals(TradeStatus.SUCCEED)){
            recordRequest.setBusinessId(trade.getId());
            record = payQueryProvider.findByBusinessId(recordRequest).getContext();
        }
        if(Objects.isNull(record) || !record.getStatus().equals(TradeStatus.SUCCEED)){
            throw new SbcRuntimeException("K-999999","该订单有异常");
        }
        CustomerDetailVO customerDetail = customerCommonService.getCustomerDetailByCustomerId(trade.getBuyer().getId());
        ManualRefund refund =  manualRefundService.save(trade,applyPrice,sourceCode + record.getPayOrderNo(),
                manualRefundResponseByOrderCodeRequest.getRefuseReason(),customerDetail.getCustomerDetailId()).get();
        if(Objects.isNull(refund)){
            throw new SbcRuntimeException("K-999999","退款失败");
        }
        String desc = "支付流水号["+refund.getPayOrderNo()+"]已退款：" + applyPrice + "元" + "[退款流水号："+refund.getRefundCode()+"]";
        log.info("===手动退款===================");
        //走银联退款接口
        GatewayConfigByGatewayRequest gatewayConfigByGatewayRequest = new GatewayConfigByGatewayRequest();
        PayGatewayEnum payGatewayEnum = trade.getPayWay().name().equals(PayWay.CUPSALI.name())  ? PayGatewayEnum.CUPSALI : PayGatewayEnum.CUPSWECHAT;
        gatewayConfigByGatewayRequest.setGatewayEnum(payGatewayEnum);
        gatewayConfigByGatewayRequest.setStoreId(Constants.BOSS_DEFAULT_STORE_ID);
        //查询银联配置信息
        PayGatewayConfigResponse payGatewayConfigResponse =
                payQueryProvider.getGatewayConfigByGateway(gatewayConfigByGatewayRequest).getContext();
        CupsPayRefundDataRequest refundRequest = CupsPayRefundDataRequest.builder().build();
        refundRequest.setOrderNo(record.getBusinessId());
        refundRequest.setRefundOrderId(refund.getRefundCode());
        refundRequest.setAmount(String.valueOf(applyPrice));
        refundRequest.setPayOrderNo(record.getPayOrderNo());
        refundRequest.setChannelId(trade.getPayWay().name().equals(PayWay.CUPSALI.name()) ? 29L : 30L);
        refundRequest.setAppId(payGatewayConfigResponse.getAppId());
        refundRequest.setApiKey(payGatewayConfigResponse.getApiKey());
        CupsPayRefundResponse response = cupsPayProvider.cupsPayRefund(refundRequest).getContext();
        if(response.getRefundStatus().indexOf("SUCCESS") == -1){
            desc = "支付流水号["+refund.getPayOrderNo()+"]退款失败：" + applyPrice + "元" + "[退款流水号："+refund.getRefundCode()+"]";
            tradeService.addTradeEventLog(trade.getId(),"手动退款",desc,manualRefundResponseByOrderCodeRequest.getOperator());
            return BaseResponse.FAILED();
        }
        tradeService.addTradeEventLog(trade.getId(),"手动退款",desc,manualRefundResponseByOrderCodeRequest.getOperator());
        refund.setRefundStatus(RefundStatus.FINISH);
        manualRefundService.update(refund);
        return BaseResponse.SUCCESSFUL();
    }

    @LcnTransaction
    @Transactional
    @Override
    public BaseResponse newPileTradeRefundByOrderCode(ManualRefundResponseByOrderCodeRequest request) {
        NewPileTrade newPileTrade = newPileTradeService.getNewPileTradeById(request.getOrderCode());
        if(newPileTrade.getPayWay() != PayWay.CUPSALI && newPileTrade.getPayWay() != PayWay.CUPSWECHAT){
            throw new SbcRuntimeException("K-999999","该订单不支持手动退款");
        }
        BigDecimal totalPrice =  newPileTrade.getTradePrice().getTotalPrice();
        BigDecimal applyPrice = new BigDecimal(request.getRefundPrice());
        if(totalPrice.compareTo(applyPrice) == -1){
            throw new SbcRuntimeException("K-999999","申请金额大于订单金额");
        }
        // 查询囤货退款单
        Criteria criteria = Criteria.where("tid").is(request.getOrderCode());
        NewPileReturnOrder newPileReturnOrder = mongoTemplate.findOne(Query.query(criteria), NewPileReturnOrder.class);
        // 查询退款单
        //如果没 有退款记录，则直接退款，先加记录，在走银联退款
        if(Objects.nonNull(newPileReturnOrder)){
            if(totalPrice.compareTo(newPileReturnOrder.getReturnPrice().getActualReturnPrice()) < 0){
                throw new SbcRuntimeException("K-999999","可退金额不足");
            }
        }
        PayTradeRecordRequest recordRequest  = new PayTradeRecordRequest();
        recordRequest.setBusinessId(newPileTrade.getParentId());
        PayTradeRecordResponse record = payQueryProvider.findByBusinessId(recordRequest).getContext();
        if(Objects.isNull(record) || record.getStatus() != TradeStatus.SUCCEED){
            recordRequest.setBusinessId(newPileTrade.getId());
            record = payQueryProvider.findByBusinessId(recordRequest).getContext();
        }
        if(Objects.isNull(record) || record.getStatus() != TradeStatus.SUCCEED){
            throw new SbcRuntimeException("K-999999","该订单有异常");
        }
        CustomerDetailVO customerDetail = customerCommonService.getCustomerDetailByCustomerId(newPileTrade.getBuyer().getId());
        ManualRefund refund =  manualRefundService.saveNewPileTrade(newPileTrade,applyPrice,sourceCode + record.getPayOrderNo(),
                request.getRefuseReason(),customerDetail.getCustomerDetailId()).get();
        if(Objects.isNull(refund)){
            throw new SbcRuntimeException("K-999999","退款失败");
        }
        String desc = "支付流水号["+refund.getPayOrderNo()+"]已退款：" + applyPrice + "元" + "[退款流水号："+refund.getRefundCode()+"]";
        log.info("===手动退款===================");
        //走银联退款接口
        GatewayConfigByGatewayRequest gatewayConfigByGatewayRequest = new GatewayConfigByGatewayRequest();
        PayGatewayEnum payGatewayEnum = newPileTrade.getPayWay() == PayWay.CUPSALI  ? PayGatewayEnum.CUPSALI : PayGatewayEnum.CUPSWECHAT;
        gatewayConfigByGatewayRequest.setGatewayEnum(payGatewayEnum);
        gatewayConfigByGatewayRequest.setStoreId(Constants.BOSS_DEFAULT_STORE_ID);
        //查询银联配置信息
        PayGatewayConfigResponse payGatewayConfigResponse =
                payQueryProvider.getGatewayConfigByGateway(gatewayConfigByGatewayRequest).getContext();
        CupsPayRefundDataRequest refundRequest = CupsPayRefundDataRequest.builder().build();
        refundRequest.setOrderNo(record.getBusinessId());
        refundRequest.setRefundOrderId(refund.getRefundCode());
        refundRequest.setAmount(String.valueOf(applyPrice));
        refundRequest.setPayOrderNo(record.getPayOrderNo());
        refundRequest.setChannelId(newPileTrade.getPayWay() == PayWay.CUPSALI ? 29L : 30L);
        refundRequest.setAppId(payGatewayConfigResponse.getAppId());
        refundRequest.setApiKey(payGatewayConfigResponse.getApiKey());
        CupsPayRefundResponse response = cupsPayProvider.cupsPayRefund(refundRequest).getContext();
        if(response.getRefundStatus().indexOf("SUCCESS") == -1){
            desc = "支付流水号["+refund.getPayOrderNo()+"]退款失败：" + applyPrice + "元" + "[退款流水号："+refund.getRefundCode()+"]";
            newPileTradeService.addTradeEventLog(newPileTrade,"新囤货手动退款",desc,request.getOperator());
            return BaseResponse.FAILED();
        }
        newPileTradeService.addTradeEventLog(newPileTrade,"新囤货手动退款",desc,request.getOperator());
        refund.setRefundStatus(RefundStatus.FINISH);
        manualRefundService.update(refund);
        return BaseResponse.SUCCESSFUL();
    }
}
