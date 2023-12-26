package com.wanmi.sbc.returnorder.api.provider.refund;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.returnorder.api.request.refund.RefundBillAddRequest;
import com.wanmi.sbc.returnorder.api.request.refund.RefundBillDeleteByIdRequest;
import com.wanmi.sbc.returnorder.api.request.refund.RefundBillRequest;
import com.wanmi.sbc.returnorder.api.response.refund.RefundBillAddResponse;
import com.wanmi.sbc.returnorder.api.response.refund.RefundBillDeleteByIdResponse;
import com.wanmi.sbc.returnorder.api.response.refund.RefundBillResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author: wanggang
 * @createDate: 2018/12/3 14:46
 * @version: 1.0
 */
@FeignClient(value = "${application.returnOrder.name}", url="${feign.url.returnOrder:#{null}}", contextId = "ReturnRefundBillProvider")
public interface RefundBillProvider {

    /**
     * 新增流水单
     * @param refundBillAddRequest {@link RefundBillAddRequest }
     * @return {@link RefundBillAddResponse }
    */
    @PostMapping("/returnOrder/${application.order.version}/refund/bill/add")
    void add(@RequestBody @Valid RefundBillAddRequest refundBillAddRequest);

    /**
     * 新增流水单
     * @param refundBillRequest {@link RefundBillRequest }
     * @return {@link RefundBillResponse }
     */
    @PostMapping("/returnOrder/${application.order.version}/refund/bill/add-and-modify-refund-order-reason")
    BaseResponse<RefundBillResponse> addAndModifyRefundOrderReason(@RequestBody @Valid RefundBillRequest refundBillRequest);

    /**
     * 根据退款单ID删除流水
     * @param refundBillDeleteByIdRequest {@link RefundBillDeleteByIdRequest }
     * @return {@link RefundBillDeleteByIdResponse }
    */
    @PostMapping("/returnOrder/${application.order.version}/refund/bill/delete-by-id")
    BaseResponse<RefundBillDeleteByIdResponse> deleteById(@RequestBody @Valid RefundBillDeleteByIdRequest refundBillDeleteByIdRequest);
}
