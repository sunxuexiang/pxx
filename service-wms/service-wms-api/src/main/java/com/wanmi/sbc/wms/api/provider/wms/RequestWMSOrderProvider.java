package com.wanmi.sbc.wms.api.provider.wms;


import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.wms.api.request.wms.WMSOrderCancelRequest;
import com.wanmi.sbc.wms.api.request.wms.WMSChargeBackRequest;
import com.wanmi.sbc.wms.api.request.wms.WMSPushOrderRequest;
import com.wanmi.sbc.wms.api.response.wms.ResponseWMSReturnResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(value = "${application.wms.name}", url="${feign.url.wms:#{null}}", contextId = "RequestWMSOrderProvider.class" )
public interface RequestWMSOrderProvider {


    /**
     *
     *平台销售订单取消接口(申请退款接口)
     * @author lihui
     * @param WMSOrderCancelRequest 请求参数结构 {@link WMSOrderCancelRequest}
     */
    @PostMapping("/wms/${application.wms.version}/inventory/cancelOrder")
    BaseResponse<ResponseWMSReturnResponse> cancelOrder(@RequestBody @Valid WMSOrderCancelRequest WMSOrderCancelRequest);

    /**
     *
     *平台退货订单下发接口(退单调用接口)
     * @author lihui
     * @param orderCancelRequest 请求参数结构 {@link WMSChargeBackRequest}
     */
    @PostMapping("/wms/${application.wms.version}/inventory/putASN")
    BaseResponse<ResponseWMSReturnResponse> putASN(@RequestBody @Valid WMSChargeBackRequest orderCancelRequest);

    /**
     *
     *平台销售订单下发接口(订单推送接口)
     * @author lihui
     * @param orderCancelRequest 请求参数结构 {@link WMSPushOrderRequest}
     */
    @PostMapping("/wms/${application.wms.version}/inventory/putSalesOrder")
    BaseResponse<ResponseWMSReturnResponse> putSalesOrder(@RequestBody @Valid WMSPushOrderRequest orderCancelRequest);

    /**
     *
     *平台销售确认发货接口
     * @author lihui
     * @param WMSOrderCancelRequest 请求参数结构 {@link WMSOrderCancelRequest}
     */
    @PostMapping("/wms/${application.wms.version}/inventory/confirmSalesOrder")
    BaseResponse<ResponseWMSReturnResponse> confirmSalesOrder(@RequestBody @Valid WMSOrderCancelRequest WMSOrderCancelRequest);


}
