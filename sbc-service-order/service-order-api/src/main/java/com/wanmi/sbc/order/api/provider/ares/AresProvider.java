package com.wanmi.sbc.order.api.provider.ares;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.order.api.request.areas.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;


/**
 * <p>订单操作接口</p>
 * author: sunpeng
 * Date: 2018-12-5
 */
@FeignClient(value = "${application.order.name}", url="${feign.url.order:#{null}}", contextId = "AresProvider")
public interface AresProvider {


    /**
     *  创建订单
     * @param request {@link OrderAddRequest}
     * @return   {@link BaseResponse}
     */
    @PostMapping("/order/${application.order.version}/ares/add-order")
    BaseResponse addOrder(@RequestBody @Valid OrderAddRequest request);

    /**
     * 创建订单(用户下单/带客下单,拆单,多笔订单方式)
     * @param request {@link OrderListAddRequest}
     * @return   {@link BaseResponse}
     */
    @PostMapping("/order/${application.order.version}/ares/add-orderList")
    BaseResponse addOrderList(@RequestBody @Valid OrderListAddRequest request);

    /**
     * 订单支付-线上支付成功/boss后台添加线下收款单
     * @param request  {@link PayOrderRequest}
     * @return  {@link BaseResponse}
     */
    @PostMapping("/order/${application.order.version}/ares/pay-order")
   BaseResponse  payOrder(@RequestBody @Valid PayOrderRequest request);

    /**
     * 订单支付-线下支付,商家确认(审核)
     * @param request  [付款单List] {@link OfflinePayOrderRequest}
     * @return  {@link BaseResponse}
     */
    @PostMapping("/order/${application.order.version}/ares/offline-payorder")
    BaseResponse  offlinePayOrder(@RequestBody @Valid OfflinePayOrderRequest request);

    /**
     * ES初始化支付单信息(不需要判断是否有作废记录)
     * @param request  [付款单List] {@link PayOrderInitRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/order/${application.order.version}/ares/pay-orderinit")
     BaseResponse   payOrderInit(@RequestBody @Valid PayOrderInitRequest request);


    /**
     * 埋点退单(以当前时间为准)
     * @param request 退单 {@link ReturnOrderRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/order/${application.order.version}/ares/return-order")
     BaseResponse  returnOrder(@RequestBody @Valid ReturnOrderRequest request);

    /**
     * ES初始化退单信息(以退单创建时间为准)
     * @param request  退单 {@link ReturnOrderInitRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/order/${application.order.version}/ares/return-orderinit")
     BaseResponse  returnOrderInit(@RequestBody @Valid ReturnOrderInitRequest request);


    /**
     * 初始化订单
     * @return  {@link BaseResponse}
     */
    @PostMapping("/order/${application.order.version}/ares/init-orderes")
     BaseResponse initOrderES();

    /**
     *  初始化退单
     * @return  {@link BaseResponse}
     */
    @PostMapping("/order/${application.order.version}/ares/init-returnorderes")
     BaseResponse initReturnOrderES();

    /**
     *  初始化付款单
     * @return  {@link BaseResponse}
     */
    @PostMapping("/order/${application.order.version}/ares/init-payordereS")
     BaseResponse initPayOrderES();

}
