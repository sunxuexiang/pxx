package com.wanmi.sbc.order.provider.impl.ares;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.order.api.provider.ares.AresProvider;
import com.wanmi.sbc.order.api.request.areas.*;
import com.wanmi.sbc.order.ares.service.OrderAresService;
import com.wanmi.sbc.order.returnorder.model.root.ReturnOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@Validated
@RestController
public class AresController implements AresProvider {


    @Autowired
    private OrderAresService areasService;

    @Override
    public BaseResponse addOrder(@RequestBody @Valid OrderAddRequest request) {

        areasService.addOrder(request.getTradeDTO());

        return BaseResponse.success(true);
    }

    @Override
    public BaseResponse addOrderList(@RequestBody @Valid OrderListAddRequest request) {

        areasService.addOrderList(request.getTradeDTOS());

        return BaseResponse.success(true);
    }

    @Override
    public BaseResponse payOrder(@RequestBody @Valid PayOrderRequest request) {

        areasService.payOrder(request.getPayOrderVO());

        return BaseResponse.success(true);
    }

    @Override
    public BaseResponse  offlinePayOrder(@RequestBody @Valid OfflinePayOrderRequest request) {

        areasService.offlinePayOrder(request.getPayOrderVOS());

        return BaseResponse.success(true);

    }

    @Override
    public BaseResponse payOrderInit(@RequestBody @Valid PayOrderInitRequest request) {

        areasService.payOrderInit(request.getPayOrderVOS());

        return BaseResponse.success(true);
    }

    @Override
    public BaseResponse  returnOrder(@RequestBody @Valid ReturnOrderRequest request) {

        areasService.returnOrder(KsBeanUtil.convert(request.getReturnOrderDTO(),ReturnOrder.class) );

        return BaseResponse.success(true);
    }

    @Override
    public BaseResponse initOrderES() {
        areasService.initOrderES();
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse initReturnOrderES() {
        areasService.initReturnOrderES();
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse initPayOrderES() {
        areasService.initPayOrderES();
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse  returnOrderInit(@RequestBody @Valid ReturnOrderInitRequest request) {

        areasService.returnOrderInit(request.getReturnOrderVO());

        return BaseResponse.success(true);
    }
}
