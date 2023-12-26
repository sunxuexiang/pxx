package com.wanmi.sbc.returnorder.api.provider.refund;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.returnorder.api.request.manualrefund.RefundForClaimsApplyPageRequest;
import com.wanmi.sbc.returnorder.api.request.manualrefund.RefundForClaimsRequest;
import com.wanmi.sbc.returnorder.api.response.manualrefund.RefundForClaimsApplyPageResponse;
import com.wanmi.sbc.returnorder.bean.vo.RefundForClaimsApplyVO;
import com.wanmi.sbc.returnorder.api.request.refund.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author: wanggang
 * @createDate: 2018/12/3 13:46
 * @version: 1.0
 */
@FeignClient(value = "${application.returnOrder.name}", url="${feign.url.returnOrder:#{null}}", contextId = "ReturnRefundOrderProvider")
public interface RefundOrderProvider {

    /**
     * 作废退款单
     *
     * @param refundOrderDeleteByIdRequest {@link RefundOrderDeleteByIdRequest }
     * @return {@link BaseResponse }
     */
    @PostMapping("/returnOrder/${application.order.version}/refund/delete-by-id")
    BaseResponse deleteById(@RequestBody @Valid RefundOrderDeleteByIdRequest refundOrderDeleteByIdRequest);

    /**
     * 销毁
     *
     * @param refundOrderDestoryRequest {@link RefundOrderDestoryRequest }
     * @return {@link BaseResponse }
     */
    @PostMapping("/returnOrder/${application.order.version}/refund/destory")
    BaseResponse destory(@RequestBody @Valid RefundOrderDestoryRequest refundOrderDestoryRequest);

    /**
     * 拒绝退款
     *
     * @param refundOrderRefuseRequest {@link RefundOrderRefuseRequest }
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/refund/refuse")
    BaseResponse refuse(@RequestBody @Valid RefundOrderRefuseRequest refundOrderRefuseRequest);

    /**
     * 退款失败
     *
     * @param refundOrderRefundRequest
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/refund/failed")
    BaseResponse refundFailed(@RequestBody @Valid RefundOrderRefundRequest refundOrderRefundRequest);

    @PostMapping("/returnOrder/${application.order.version}/refund/refundForNewPickNotAudit")
    BaseResponse refundForNewPickNotAudit(@RequestBody @Valid RefundForNewPickNotAudit request);

    @PostMapping("/returnOrder/${application.order.version}/refund/refundForClaims")
    BaseResponse refundForClaims(@RequestBody @Valid RefundForClaimsRequest request);

    @PostMapping("/returnOrder/${application.order.version}/refund/getRefundForClaimsApplyPage")
    BaseResponse<RefundForClaimsApplyPageResponse> getRefundForClaimsApplyPage(@RequestBody @Valid RefundForClaimsApplyPageRequest request);

    @PostMapping("/returnOrder/${application.order.version}/refund/export-chaims-apply")
    BaseResponse<List<RefundForClaimsApplyVO>> exportChaimsApply(@RequestBody @Valid RefundForClaimsApplyPageRequest request);

    @PostMapping("/returnOrder/${application.order.version}/refund/getApplyDetail/{applyNo}")
    BaseResponse<RefundForClaimsApplyVO> getApplyDetail(@PathVariable(value = "applyNo") String applyNo);

    @GetMapping("/returnOrder/${application.order.version}/refund/refundOrderSuccess")
    BaseResponse refundOrderSuccess(@RequestParam("businessId") String businessId, @RequestParam("rid") String rid, @RequestParam("refunded") Boolean refunded, @RequestParam(value = "msg",required = false) String msg);
}