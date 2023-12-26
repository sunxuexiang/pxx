package com.wanmi.sbc.returnorder.provider.impl.payorder;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.GeneratorService;
import com.wanmi.sbc.returnorder.api.provider.payorder.PayOrderProvider;
import com.wanmi.sbc.returnorder.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.returnorder.api.provider.trade.newPileTrade.NewPileTradeProvider;
import com.wanmi.sbc.returnorder.api.request.payorder.DestoryPayOrderRequest;
import com.wanmi.sbc.returnorder.api.request.payorder.GeneratePayOrderByOrderCodeRequest;
import com.wanmi.sbc.returnorder.api.request.payorder.UpdatePayOrderRequest;
import com.wanmi.sbc.returnorder.api.response.payorder.DeleteByPayOrderIdResponse;
import com.wanmi.sbc.returnorder.api.response.payorder.GeneratePayOrderByOrderCodeResponse;
import com.wanmi.sbc.returnorder.api.response.payorder.PayOrderUpdatePayOrderResponse;
import com.wanmi.sbc.returnorder.bean.dto.PayOrderDTO;
import com.wanmi.sbc.returnorder.bean.dto.StringRequest;
import com.wanmi.sbc.returnorder.bean.vo.PayOrderVO;
import com.wanmi.sbc.returnorder.payorder.model.root.PayOrder;
import com.wanmi.sbc.returnorder.payorder.request.PayOrderGenerateRequest;
import com.wanmi.sbc.returnorder.payorder.service.PayOrderService;
import com.wanmi.sbc.pay.api.provider.CupsPayProvider;
import com.wanmi.sbc.pay.api.provider.PayProvider;
import com.wanmi.sbc.pay.api.provider.PayQueryProvider;
import com.wanmi.sbc.pay.api.provider.WxPayProvider;
import com.wanmi.sbc.pay.api.request.CmbPayOrderRequest;
import com.wanmi.sbc.pay.api.request.PayTradeRecordRequest;
import com.wanmi.sbc.pay.api.response.PayTradeRecordResponse;
import com.wanmi.sbc.pay.bean.enums.TradeStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.*;

@Validated
@RestController
@Slf4j
public class PayOrderController implements PayOrderProvider {


    @Autowired
    private PayOrderService payOrderService;

    @Autowired
    private CupsPayProvider cupsPayProvider;

    @Autowired
    private WxPayProvider wxPayProvider;

    @Autowired
    private GeneratorService generatorService;

    @Autowired
    private PayQueryProvider payQueryProvider;

    @Autowired
    private PayProvider payProvider;

    @Autowired
    private TradeQueryProvider tradeQueryProvider;

    @Autowired
    private NewPileTradeProvider newPileTradeProvider;

    @Override
    public BaseResponse<GeneratePayOrderByOrderCodeResponse> generatePayOrderByOrderCode(@RequestBody @Valid GeneratePayOrderByOrderCodeRequest request) {

        PayOrderGenerateRequest payOrderGenerateRequest = PayOrderGenerateRequest.builder().payType(request.getPayType())
                .payOrderPrice(request.getPayOrderPrice())
                .orderTime(request.getOrderTime())
                .customerId(request.getCustomerId())
                .orderCode(request.getOrderCode())
                .companyInfoId(request.getCompanyInfoId())
                .build();


        Optional<PayOrder> rawPayOrder = payOrderService.generatePayOrderByOrderCode(payOrderGenerateRequest);

        if(rawPayOrder.isPresent()){

            PayOrderVO payOrder = new PayOrderVO();

            BeanUtils.copyProperties(rawPayOrder.get(), payOrder);

            return    BaseResponse.success(GeneratePayOrderByOrderCodeResponse.builder().payOrder(payOrder).build());

        }

        return  BaseResponse.FAILED();
    }

    private  List<PayOrder> toRawList(List<PayOrderDTO> voList){

        List<PayOrder> result = new ArrayList<>();

        voList.forEach( e ->{ PayOrder target = new PayOrder();BeanUtils.copyProperties(e,target);result.add(target) ;});

        return  result;
    }


    @Override
    public BaseResponse destoryPayOrder(@RequestBody @Valid DestoryPayOrderRequest request) {
        payOrderService.destoryPayOrder(toRawList(request.getPayOrders()), request.getOperator());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<PayOrderUpdatePayOrderResponse> updatePayOrder(@RequestBody @Valid UpdatePayOrderRequest request) {

        payOrderService.updatePayOrder(request.getPayOrderId(),request.getPayOrderStatus());

        return BaseResponse.success(PayOrderUpdatePayOrderResponse.builder().value(request.getPayOrderId()).build());
    }

    @Override
    public BaseResponse<DeleteByPayOrderIdResponse>  deleteByPayOrderId(@RequestBody @Valid StringRequest id) {

        payOrderService.deleteByPayOrderId(id.getValue());

        return BaseResponse.success(DeleteByPayOrderIdResponse.builder().value(id.getValue()).build());

    }

    private boolean isMergePayOrder(String businessId) {
        return businessId.contains(GeneratorService._PREFIX_PARENT_TRADE_ID);
    }

    @Override
    public BaseResponse<String> generateNewPayOrderByOrderCode(StringRequest request) {
        //查看是否有父级ID的订单已经已经支付或者是未支付，未关单
        /*if(!isMergePayOrder(request.getValue())){
            checkDuplicatePay(request, request.getValue());
        } else {
            TradeVO trade = tradeQueryProvider.getById(TradeGetByIdRequest.builder()
                    .tid(request.getValue()).build()).getContext().getTradeVO();
            String pid  = trade.getParentId();
            checkDuplicatePay(request, pid);
        }*/
        checkDuplicatePay(request, request.getValue());

        String payOrderNo = generatorService.generateOid();
        log.info("====generatorService.generateOid():{}",payOrderNo);
        return BaseResponse.success(payOrderNo);

    }

    private void checkDuplicatePay(StringRequest request, String pid) {
        if(!StringUtils.isEmpty(pid)){
            PayTradeRecordRequest recordRequest  = new PayTradeRecordRequest();
            recordRequest.setBusinessId(pid);
            PayTradeRecordResponse record = payQueryProvider.findByBusinessId(recordRequest).getContext();
            if (!Objects.isNull(record) && record.getStatus() == TradeStatus.SUCCEED) {
                //如果重复支付，判断状态，已成功状态则做异常提示
                throw new SbcRuntimeException("K-100203");
            }

            //检查银联重复支付
            List<Long> cpusPayChannels = Arrays.asList(29L,30L);
            if(!Objects.isNull(record) && cpusPayChannels.contains(request.getChannelId())){
                CmbPayOrderRequest cmbPayOrderRequest = new CmbPayOrderRequest();
                cmbPayOrderRequest.setPayOrderNo(record.getPayOrderNo());
                cmbPayOrderRequest.setStoreId(request.getStoreId());
                cmbPayOrderRequest.setChannelId(request.getChannelId());
                Boolean notCompleted = cupsPayProvider.isPayCompleted(cmbPayOrderRequest).getContext();
                if(!notCompleted){
                    //如果重复支付，判断状态，已成功状态则做异常提示
                    throw new SbcRuntimeException("K-100203");
                }
            }

            //检查微信重复支付
            List<Long> wechatPayChannels = Arrays.asList(16L);
            if(!Objects.isNull(record) && wechatPayChannels.contains(request.getChannelId())){
                CmbPayOrderRequest cmbPayOrderRequest = new CmbPayOrderRequest();
                cmbPayOrderRequest.setPayOrderNo(record.getPayOrderNo());
                cmbPayOrderRequest.setStoreId(request.getStoreId());
                cmbPayOrderRequest.setChannelId(request.getChannelId());
                Boolean paid = wxPayProvider.isPayCompleted(cmbPayOrderRequest).getContext();
                if(paid){
                    //如果重复支付，判断状态，已成功状态则做异常提示
                    throw new SbcRuntimeException("K-100203");
                }
            }
        }
    }

    @Override
    public BaseResponse<String> generateNewPayOrderByOrderCodeNewPile(@Valid StringRequest request) {
        //查看是否有父级ID的订单已经已经支付或者是未支付，未关单
       /* if(!request.getValue().startsWith(GeneratorService._NEW_PILE_PARENT_PREFIX_TRADE_ID)){
            checkDuplicatePay(request, request.getValue());
        } else {
            NewPileTradeVO trade = newPileTradeProvider.getById(TradeGetByIdRequest.builder()
                    .tid(request.getValue()).build()).getContext().getTradeVO();
            String pid  = trade.getParentId();
            checkDuplicatePay(request, pid);
        }*/
        checkDuplicatePay(request, request.getValue());

        String payOrderNo = generatorService.generateOid();
        log.info("====generatorService.generateOid():{}",payOrderNo);
        return BaseResponse.success(payOrderNo);

    }
}
