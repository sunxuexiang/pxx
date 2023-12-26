package com.wanmi.sbc.account;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.order.api.provider.refund.RefundOrderQueryProvider;
import com.wanmi.sbc.order.api.request.refund.RefundOrderResponseByReturnOrderCodeRequest;
import com.wanmi.sbc.order.api.response.refund.RefundOrderResponse;
import com.wanmi.sbc.saas.bean.vo.DomainStoreRelaVO;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * 退款单
 * Created by zhangjin on 2017/4/21.
 */
@RestController
@RequestMapping("/account")
@Api(tags = "RefundOrderController", description = "S2B web公用-退款单管理API")
public class RefundOrderController {

    @Autowired
    private RefundOrderQueryProvider refundOrderQueryProvider;

    @Autowired
    private CommonUtil commonUtil;

    /**
     * 根据退单号查询
     *
     * @return BaseResponse<RefundBill>
     */
    @ApiOperation(value = "根据退单编号查询退款单")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "returnOrderNo", value = "退单编号", required = true)
    @RequestMapping(value = "/refundOrders/{returnOrderNo}", method = RequestMethod.GET)
    public ResponseEntity<BaseResponse<RefundOrderResponse>> queryRefundByReturnOrderNo(@PathVariable("returnOrderNo") String returnOrderNo) {
        BaseResponse<RefundOrderResponse> response =
                refundOrderQueryProvider.getRefundOrderRespByReturnOrderCode(new RefundOrderResponseByReturnOrderCodeRequest(returnOrderNo));

        DomainStoreRelaVO domainStoreRelaVO = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainStoreRelaVO)) {
            if (Objects.isNull(response.getContext().getStoreId()) ||
                    !Objects.equals(response.getContext().getStoreId(), domainStoreRelaVO.getStoreId())) {
                throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
            }
        }
        return ResponseEntity.ok(response);
    }
}
