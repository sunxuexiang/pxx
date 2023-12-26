package com.wanmi.sbc.order.api.provider.trade;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.order.api.request.trade.*;
import com.wanmi.sbc.order.api.response.trade.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @Description: 供应商订单查询
 * @Autho qiaokang
 * @Date：2020-03-27 09:08
 */
@FeignClient(value = "${application.order.name}", url="${feign.url.order:#{null}}", contextId = "ProviderTradeQueryProvider")
public interface ProviderTradeQueryProvider {

    /**
     * 条件分页
     *
     * @param tradePageCriteriaRequest 带参分页参数
     * @return
     */
    @PostMapping("/order/${application.order.version}/trade/provider-page-criteria")
    BaseResponse<TradePageCriteriaResponse> providerPageCriteria(@RequestBody @Valid ProviderTradePageCriteriaRequest tradePageCriteriaRequest);

    /**
     * 通过id获取交易单信息
     *
     * @param tradeGetByIdRequest 交易单id {@link TradeGetByIdRequest}
     * @return 交易单信息 {@link TradeGetByIdResponse}
     */
    @PostMapping("/order/${application.order.version}/trade/provider-get-by-id")
    BaseResponse<TradeGetByIdResponse> providerGetById(@RequestBody @Valid TradeGetByIdRequest tradeGetByIdRequest);


    /**
     * 通过父订单号获取交易单集合
     *
     * @param tradeListByParentIdRequest 父交易单id {@link TradeListByParentIdRequest}
     * @return 交易单信息 {@link TradeListByParentIdResponse}
     */
    @PostMapping("/order/${application.order.version}/trade/get-provider-list-by-parent-id")
    BaseResponse<TradeListByParentIdResponse> getProviderListByParentId(@RequestBody @Valid TradeListByParentIdRequest tradeListByParentIdRequest);

    /**
     * 查询导出数据
     *
     * @param tradeListExportRequest 查询条件 {@link TradeListExportRequest}
     * @return 验证结果 {@link TradeListExportResponse}
     */
    @PostMapping("/order/${application.order.version}/trade/provider-list-export")
    BaseResponse<TradeListExportResponse> providerListTradeExport(@RequestBody @Valid TradeListExportRequest tradeListExportRequest);

    /**
     * 查询导出订单数据(需求是:导出T-1天的新用户首单数据)
     * 1. 查询T-2天所有的订单数据
     * 2. 最终要排队掉这些已下单成功的老用户
     */
    @PostMapping("/order/${application.order.version}/trade/getBeforeYesterdayData")
    BaseResponse<TradeListExportResponse> getBeforeYesterdayData();

    /**
     * 1. 查询T-1天所有的订单数据
     * 2. 传新用户的条件进来
     */
    @PostMapping("/order/${application.order.version}/trade/getYesterdayData")
    BaseResponse<TradeListExportResponse> getYesterdayData();

    @PostMapping("/order/${application.order.version}/trade/sendNewUserOrder")
    BaseResponse sendNewUserOrder(@RequestBody TradeListExportResponse tradeListExportResponse);


}
