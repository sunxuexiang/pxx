package com.wanmi.sbc.returnorder.provider.impl.payorder;

import com.wanmi.sbc.account.bean.enums.RefundStatus;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.returnorder.api.provider.payorder.PayOrderQueryProvider;
import com.wanmi.sbc.returnorder.bean.vo.PayOrderDetailVO;
import com.wanmi.sbc.returnorder.bean.vo.PayOrderVO;
import com.wanmi.sbc.returnorder.manualrefund.model.root.ManualRefund;
import com.wanmi.sbc.returnorder.manualrefund.repository.ManualRefundRepository;
import com.wanmi.sbc.returnorder.payorder.model.root.PayOrder;
import com.wanmi.sbc.returnorder.payorder.request.PayOrderRequest;
import com.wanmi.sbc.returnorder.payorder.response.PayOrderPageResponse;
import com.wanmi.sbc.returnorder.payorder.response.PayOrderResponse;
import com.wanmi.sbc.returnorder.payorder.service.PayOrderService;
import com.wanmi.sbc.returnorder.trade.model.root.Trade;
import com.wanmi.sbc.returnorder.trade.service.ProviderTradeService;
import com.wanmi.sbc.returnorder.trade.service.TradeService;
import com.wanmi.sbc.returnorder.api.request.payorder.*;
import com.wanmi.sbc.returnorder.api.response.payorder.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Validated
@RestController
public class PayOrderQueryController implements PayOrderQueryProvider {

    @Autowired
    private PayOrderService payOrderService;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private ProviderTradeService providerTradeService;

    @Autowired
    private ManualRefundRepository manualRefundRepository;


    private List<PayOrderVO> toVoList(List<PayOrder> voList) {

        List<PayOrderVO> result = new ArrayList<>();

        voList.forEach(e -> {
            PayOrderVO target = new PayOrderVO();
            BeanUtils.copyProperties(e, target);
            result.add(target);
        });

        return result;
    }

    @Override
    public BaseResponse<FindPayOrderByPayOrderIdsResponse> findPayOrderByPayOrderIds(@RequestBody @Valid FindPayOrderByPayOrderIdsRequest request) {


        FindPayOrderByPayOrderIdsResponse response = FindPayOrderByPayOrderIdsResponse.builder()
                .orders(toVoList(payOrderService.findPayOrderByPayOrderIds(request.getPayOrderIds())))
                .build();

        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<FindPayOrderByOrderCodeResponse> findPayOrderByOrderCode(@RequestBody @Valid FindPayOrderByOrderCodeRequest request) {

        PayOrderVO target = new PayOrderVO();

        Optional<PayOrder> raw = payOrderService.findPayOrderByOrderCode(request.getValue());

        if (raw.isPresent()) {

            BeanUtils.copyProperties(raw.get(), target);


        } else {

            target = null;
        }


        FindPayOrderByOrderCodeResponse response = FindPayOrderByOrderCodeResponse.builder().value(target)
                .build();

        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<FindPayOrderResponse> findPayOrder(@RequestBody @Valid FindPayOrderRequest orderNo) {
        PayOrderResponse src = payOrderService.findPayOrder(orderNo.getValue());
        FindPayOrderResponse target = new FindPayOrderResponse();
        BeanUtils.copyProperties(src, target);

        BigDecimal totalPrice =  target.getTotalPrice();//订单总金额
        BigDecimal refundPrice = new BigDecimal(0);//退款总金额
        List<ManualRefund> manualRefunds = manualRefundRepository.findByOrderCodeAndRefundStatus(target.getOrderCode(), RefundStatus.FINISH);
        if(!CollectionUtils.isEmpty(manualRefunds)){
            for (ManualRefund manualRefund: manualRefunds) {
                refundPrice = refundPrice.add(manualRefund.getApplyPrice());
            }
            if(refundPrice.compareTo(totalPrice) == -1){//退款金额小于订单金额
                target.setIsDisplay(true);
            }
        }else{
            target.setIsDisplay(true);
        }
        return BaseResponse.success(target);
    }

    @Override
    public BaseResponse<FindPayOrderResponse> findPayOrderByPayOrderNo(FindPayOrderRequest request) {
        PayOrderResponse src = payOrderService.findPayOrderByPayOrderNo(request.getValue());

        FindPayOrderResponse target = new FindPayOrderResponse();

        BeanUtils.copyProperties(src, target);

        return BaseResponse.success(target);
    }

    /**
     * 查找囤货支付单
     * @param orderNo
     * @return
     */
    @Override
    public BaseResponse<FindPayOrderResponse> findPilePayOrder(@RequestBody @Valid FindPayOrderRequest orderNo) {

        PayOrderResponse src = payOrderService.findPilePayOrder(orderNo.getValue());

        FindPayOrderResponse target = new FindPayOrderResponse();

        BeanUtils.copyProperties(src, target);

        return BaseResponse.success(target);
    }

    @Override
    public BaseResponse<FindPayOrderListResponse> findPayOrderList(@RequestBody @Valid FindPayOrderListRequest request) {
        List<Trade> trades = tradeService.detailsByParentId(request.getParentTid());
        List<PayOrderDetailVO> list = trades.stream().map(i -> {
            PayOrderResponse src = payOrderService.findPayOrder(i.getId());
            PayOrderDetailVO target = new PayOrderDetailVO();
            BeanUtils.copyProperties(src, target);
            target.setIsSelf(i.getSupplier().getIsSelf());
            return target;
        }).collect(Collectors.toList());
        return BaseResponse.success(new FindPayOrderListResponse(list));
    }

    @Override
    public BaseResponse<FindPayOrdersResponse> findPayOrders(@RequestBody @Valid FindPayOrdersRequest srcrequest) {

        PayOrderRequest target = new PayOrderRequest();

        BeanUtils.copyProperties(srcrequest, target);

        PayOrderPageResponse rawresponse = payOrderService.findPayOrders(target);

        FindPayOrdersResponse response = KsBeanUtil.convert(rawresponse, FindPayOrdersResponse.class);

//        //查询主订单编号列表
//        List<PayOrderResponseVO> payOrderVOList=response.getPayOrderResponses();
//        List<String> parentIdList=new ArrayList<>();
//        parentIdList.add(srcrequest.getOrderNo());
//        //根据主订单编号列表查询子订单
//        List<ProviderTrade> result=providerTradeService.findListByParentIdList(parentIdList);
//        List<TradeVO> items=new ArrayList<>();
//        if (CollectionUtils.isNotEmpty(result)) result.forEach(item->{
//            if (srcrequest.getOrderNo().equals(item.getParentId())){
//                items.add(KsBeanUtil.convert(item,TradeVO.class));
//            }
//        });
//        PayOrderResponseVO vo=new PayOrderResponseVO();
//        vo.setTradeVOList(items);
//        payOrderVOList.add(vo);
//        response.setPayOrderResponses(payOrderVOList);
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<FindPayOrdersWithNoPageResponse> findPayOrdersWithNoPage(@RequestBody @Valid FindPayOrdersWithNoPageRequest payOrderRequest) {

        PayOrderRequest request = KsBeanUtil.convert(payOrderRequest, PayOrderRequest.class);

        PayOrderPageResponse rawresponse = payOrderService.findPayOrdersWithNoPage(request);

        FindPayOrdersWithNoPageResponse target = KsBeanUtil.convert(rawresponse, FindPayOrdersWithNoPageResponse.class);

        return BaseResponse.success(target);

    }

    @Override
    public BaseResponse<FindByOrderNosResponse> findByOrderNos(@RequestBody @Valid FindByOrderNosRequest request) {

        List<PayOrder> rawOrders = payOrderService.findByOrderNos(request.getOrderNos(), request.getPayOrderStatus());

        List<PayOrderVO> target = toVoList(rawOrders);

        FindByOrderNosResponse response = FindByOrderNosResponse.builder().orders(target).build();

        return BaseResponse.success(response);

    }

    @Override
    public BaseResponse<SumPayOrderPriceResponse> sumPayOrderPrice(@RequestBody @Valid SumPayOrderPriceRequest payOrderRequest) {

        PayOrderRequest request = KsBeanUtil.convert(payOrderRequest, PayOrderRequest.class);


        SumPayOrderPriceResponse response =
                SumPayOrderPriceResponse.builder().value(payOrderService.sumPayOrderPrice(request)).build();

        return BaseResponse.success(response);
    }
}
