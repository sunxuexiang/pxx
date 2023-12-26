package com.wanmi.sbc.returnorder.api.provider.orderinvoice;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.returnorder.api.request.orderinvoice.*;
import com.wanmi.sbc.returnorder.api.response.orderinvoice.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2018-11-30 11:23
 */
@FeignClient(value = "${application.returnOrder.name}", url="${feign.url.returnOrder:#{null}}", contextId = "ReturnOrderInvoiceQueryProvider")
public interface OrderInvoiceQueryProvider {


    @PostMapping("/returnOrder/${application.order.version}/invoice/find-byorderinvoiceidanddelflag")
    BaseResponse<OrderInvoiceFindByOrderInvoiceIdAndDelFlagResponse> findByOrderInvoiceIdAndDelFlag(@RequestBody @Valid OrderInvoiceFindByOrderInvoiceIdAndDelFlagRequest request);

    /**
     * 根据id查询开票信息
     * @param request
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/invoice/find-byorderinvoiceid")
    BaseResponse<OrderInvoiceFindByOrderInvoiceIdResponse> findByOrderInvoiceId(OrderInvoiceFindByOrderInvoiceIdRequest request);

    /**
     * 查询所有
     * @param request  {@link OrderInvoiceFindAllRequest}
     * @return  开票信息 {@link OrderInvoiceFindAllResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/invoice/findall")
    BaseResponse<OrderInvoiceFindAllResponse> findAll(@RequestBody @Valid  OrderInvoiceFindAllRequest request);

    /**
     * 生成开票
     * @param getByOrderNoRequest 订单开票查询参数 {@link OrderInvoiceGetByOrderNoRequest}
     * @return  开票信息 {@link OrderInvoiceGetByOrderNoResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/invoice/get-by-orderNo")
    BaseResponse<OrderInvoiceGetByOrderNoResponse> getByOrderNo(@RequestBody @Valid OrderInvoiceGetByOrderNoRequest getByOrderNoRequest);

    /**
     * 开票导出
     * @param exportRequest 开票导出查询参数 {@link OrderInvoiceExportRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/invoice/export")
    BaseResponse export(@RequestBody @Valid OrderInvoiceExportRequest exportRequest);

    /**
     * 统计
     * @param countByStateRequest 统计参数 {@link OrderInvoiceCountByStateRequest}
     * @return 统计数量 {@link OrderInvoiceCountByStateResponse }
     */
    @PostMapping("/returnOrder/${application.order.version}/invoice/count-by-state")
    BaseResponse<OrderInvoiceCountByStateResponse> countByState(@RequestBody @Valid OrderInvoiceCountByStateRequest countByStateRequest);



}
