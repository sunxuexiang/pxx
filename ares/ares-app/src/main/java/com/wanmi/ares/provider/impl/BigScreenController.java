package com.wanmi.ares.provider.impl;

import com.wanmi.ares.base.BaseResponse;
import com.wanmi.ares.provider.BigScreenProvider;
import com.wanmi.ares.request.screen.GenerateOrderRequest;
import com.wanmi.ares.request.screen.RealTimeOrderRequest;
import com.wanmi.ares.request.screen.model.ScreenOrder;
import com.wanmi.ares.request.screen.model.ScreenOrderDetail;
import com.wanmi.ares.response.screen.*;
import com.wanmi.ares.screen.service.BigScreenServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author lm
 * @date 15:48
 */
@RestController
public class BigScreenController implements BigScreenProvider {

    @Autowired
    private BigScreenServiceImpl bigScreenService;


    @Override
    public BaseResponse<?> initOrderData() {
        bigScreenService.generateScreenOrderInTenMin();
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<PreSaleDataResponse> queryTodayPreSaleData() {
        PreSaleDataResponse preSaleDataResponse = bigScreenService.queryTodayPreSaleData();
        return BaseResponse.success(preSaleDataResponse);
    }

    @Override
    public BaseResponse<List<RealTimeOrderResponse>> queryRealTimeOrder() {
        List<RealTimeOrderResponse> realTimeOrder = bigScreenService.queryRealTimeOrder();
        return BaseResponse.success(realTimeOrder);
    }

    @Override
    public BaseResponse<AreaPreSaleTotalMoneyResponse> queryAreaPreSaleTotalMoney() {
        AreaPreSaleTotalMoneyResponse data = bigScreenService.queryAreaPreSaleTotalMoney();
        return BaseResponse.success(data);
    }

    @Override
    public BaseResponse<List<CateSaleResponse>> queryCateSale() {
        List<CateSaleResponse> result = bigScreenService.queryCateSale();
        return BaseResponse.success(result);
    }

    @Override
    public ScreenLastTimeResponse queryScreenLastTime() {
        ScreenLastTimeResponse screenLastTimeResponse = bigScreenService.queryScreenLastTime();
        return screenLastTimeResponse;
    }

    @Override
    public void saveScreenOrderAndDetail(GenerateOrderRequest orderRequest) {
        bigScreenService.saveScreenOrderAndDetail(orderRequest);
    }

    @Override
    public void deleteScreenOrderAndDetail() {
        bigScreenService.deleteScreenOrderAndDetail();
    }
}
