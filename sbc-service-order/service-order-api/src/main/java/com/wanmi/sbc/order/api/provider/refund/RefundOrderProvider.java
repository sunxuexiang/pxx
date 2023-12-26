package com.wanmi.sbc.order.api.provider.refund;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.order.api.request.manualrefund.RefundForClaimsApplyPageRequest;
import com.wanmi.sbc.order.api.request.manualrefund.RefundForClaimsRequest;
import com.wanmi.sbc.order.api.request.refund.*;
import com.wanmi.sbc.order.api.response.manualrefund.RefundForClaimsApplyPageResponse;
import com.wanmi.sbc.order.bean.vo.RefundForClaimsApplyVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author: wanggang
 * @createDate: 2018/12/3 13:46
 * @version: 1.0
 */
@FeignClient(value = "${application.order.name}", url="${feign.url.order:#{null}}", contextId = "RefundOrderProvider")
public interface RefundOrderProvider {

    /**
     * 作废退款单
     *
     * @param refundOrderDeleteByIdRequest {@link RefundOrderDeleteByIdRequest }
     * @return {@link BaseResponse }
     */
    @PostMapping("/order/${application.order.version}/refund/delete-by-id")
    BaseResponse deleteById(@RequestBody @Valid RefundOrderDeleteByIdRequest refundOrderDeleteByIdRequest);

    /**
     * 销毁
     *
     * @param refundOrderDestoryRequest {@link RefundOrderDestoryRequest }
     * @return {@link BaseResponse }
     */
    @PostMapping("/order/${application.order.version}/refund/destory")
    BaseResponse destory(@RequestBody @Valid RefundOrderDestoryRequest refundOrderDestoryRequest);

    /**
     * 拒绝退款
     *
     * @param refundOrderRefuseRequest {@link RefundOrderRefuseRequest }
     * @return
     */
    @PostMapping("/order/${application.order.version}/refund/refuse")
    BaseResponse refuse(@RequestBody @Valid RefundOrderRefuseRequest refundOrderRefuseRequest);

    /**
     * 退款失败
     *
     * @param refundOrderRefundRequest
     * @return
     */
    @PostMapping("/order/${application.order.version}/refund/failed")
    BaseResponse refundFailed(@RequestBody @Valid RefundOrderRefundRequest refundOrderRefundRequest);

    @PostMapping("/order/${application.order.version}/refund/refundForNewPickNotAudit")
    BaseResponse refundForNewPickNotAudit(@RequestBody @Valid RefundForNewPickNotAudit request);

    @PostMapping("/order/${application.order.version}/refund/refundForClaims")
    BaseResponse refundForClaims(@RequestBody @Valid RefundForClaimsRequest request);

    @PostMapping("/order/${application.order.version}/refund/getRefundForClaimsApplyPage")
    BaseResponse<RefundForClaimsApplyPageResponse> getRefundForClaimsApplyPage(@RequestBody @Valid RefundForClaimsApplyPageRequest request);

    @PostMapping("/order/${application.order.version}/refund/export-chaims-apply")
    BaseResponse<List<RefundForClaimsApplyVO>> exportChaimsApply(@RequestBody @Valid RefundForClaimsApplyPageRequest request);

    @PostMapping("/order/${application.order.version}/refund/getApplyDetail/{applyNo}")
    BaseResponse<RefundForClaimsApplyVO> getApplyDetail(@PathVariable(value = "applyNo") String applyNo);

    @GetMapping("/order/${application.order.version}/refund/refundOrderSuccess")
    BaseResponse refundOrderSuccess(@RequestParam("businessId") String businessId, @RequestParam("rid") String rid, @RequestParam("refunded") Boolean refunded, @RequestParam(value = "msg",required = false) String msg);
}