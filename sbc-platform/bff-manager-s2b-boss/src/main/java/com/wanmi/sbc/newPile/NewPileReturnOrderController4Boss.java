package com.wanmi.sbc.newPile;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.aop.EmployeeCheck;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.request.employee.EmployeeByCompanyIdRequest;
import com.wanmi.sbc.order.api.provider.refund.RefundOrderQueryProvider;
import com.wanmi.sbc.order.api.provider.trade.newPileTrade.NewPileReturnOrderProvider;
import com.wanmi.sbc.order.api.request.refund.RefundOrderByReturnOrderCodeRequest;
import com.wanmi.sbc.order.api.request.refund.RefundOrderByReturnOrderNoRequest;
import com.wanmi.sbc.order.api.request.refund.RefundOrderResponseByReturnOrderCodeRequest;
import com.wanmi.sbc.order.api.request.returnorder.*;
import com.wanmi.sbc.order.api.response.refund.RefundOrderByReturnCodeResponse;
import com.wanmi.sbc.order.api.response.refund.RefundOrderByReturnOrderNoResponse;
import com.wanmi.sbc.order.api.response.refund.RefundOrderResponse;
import com.wanmi.sbc.order.api.response.returnorder.NewPileReturnOrderByIdResponse;
import com.wanmi.sbc.order.bean.dto.RefundBillDTO;
import com.wanmi.sbc.order.bean.enums.NewPileReturnFlowState;
import com.wanmi.sbc.order.bean.enums.ReturnFlowState;
import com.wanmi.sbc.order.bean.vo.NewPileReturnOrderVO;
import com.wanmi.sbc.order.bean.vo.RefundBillVO;
import com.wanmi.sbc.order.bean.vo.ReturnOrderVO;
import com.wanmi.sbc.returnorder.request.ReturnOfflineRefundRequest;
import com.wanmi.sbc.returnorder.request.ReturnRequest;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Created by Administrator on 2017/4/19.
 */
@Api(tags = "TradeController", description = "订单服务 Api")
@RestController
@RequestMapping("/boss/newPileReturnTrade")
@Slf4j
@Validated
public class NewPileReturnOrderController4Boss {

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private NewPileReturnOrderProvider newPileReturnOrderProvider;

    @Autowired
    private EmployeeQueryProvider employeeQueryProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @Autowired
    private RefundOrderQueryProvider refundOrderQueryProvider;

    /**
     * 分页查询 from ES
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "囤货退单列表")
    @EmployeeCheck
    @RequestMapping(value = "page", method = RequestMethod.POST)
    public BaseResponse<MicroServicePage<ReturnOrderVO>> page(@RequestBody NewPileReturnOrderPageRequest request) {
        request.setCompanyInfoId(commonUtil.getCompanyInfoId());
        MicroServicePage<ReturnOrderVO> page = newPileReturnOrderProvider.page(request).getContext().getReturnOrderPage();
        page.getContent().forEach(returnOrder -> {
            RefundOrderByReturnOrderNoResponse refundOrderByReturnCodeResponse = newPileReturnOrderProvider.getByReturnOrderNo(new RefundOrderByReturnOrderNoRequest(returnOrder.getId())).getContext();
            if (Objects.nonNull(refundOrderByReturnCodeResponse)) {
                returnOrder.setRefundStatus(refundOrderByReturnCodeResponse.getRefundStatus());
            }
        });
        return BaseResponse.success(page);
    }

    /**
     * 线下退款
     *
     * @param rid
     * @param request
     * @return
     */
    @ApiOperation(value = "线下退款")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "rid",
                    value = "退单Id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "ReturnOfflineRefundRequest", name = "request",
                    value = "线下退款", required = true),
    })
    @RequestMapping(value = "/refund/{rid}/offline", method = RequestMethod.POST)
    @LcnTransaction
    public ResponseEntity<BaseResponse> refundOffline(@PathVariable String rid,
                                                      @RequestBody ReturnOfflineRefundRequest request) {

        NewPileReturnOrderByIdResponse returnOrder = newPileReturnOrderProvider.getById(ReturnOrderByIdRequest.builder().rid(rid)
                .build()).getContext();

        if (!NewPileReturnFlowState.AUDIT.equals(returnOrder.getReturnFlowState())) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "囤货退单状态已变化，请核实！");
        }
        if (!returnOrder.getFinancialRefundFlag()) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "囤货退单未进入账务退款阶段，请核实！");
        }

        RefundOrderByReturnCodeResponse refundOrder = refundOrderQueryProvider.getByReturnOrderCode(new RefundOrderByReturnOrderCodeRequest(returnOrder.getId())).getContext();

        //退款流水
        RefundBillVO refundBill = refundOrder.getRefundBill();
        if (Objects.isNull(refundBill)) {
            refundBill = new RefundBillVO();
            refundBill.setRefundId(refundOrder.getRefundId());
            refundBill.setActualReturnPrice(refundOrder.getReturnPrice());
            refundBill.setActualReturnPoints(refundOrder.getReturnPoints());
        }
        refundBill.setOfflineAccountId(request.getOfflineAccountId());
        refundBill.setCreateTime(DateUtil.parseDate(request.getCreateTime()));
        newPileReturnOrderProvider.offlineRefundForBoss(ReturnOrderOfflineRefundForBossRequest.builder().rid(rid)
                .refundBill(KsBeanUtil.convert(refundBill, RefundBillDTO.class))
                .operator(commonUtil.getOperator()).tid(returnOrder.getTid()).build());

//        ReturnOrderByIdResponse returnOrderByIdResponse = newPileReturnOrderProvider.getById(ReturnOrderByIdRequest.builder().rid(rid).build()).getContext();

//        ReturnOrderDTO requestDto = KsBeanUtil.convert(returnOrderByIdResponse, ReturnOrderDTO.class);

//        ReturnOrderRequest providerrequest = new ReturnOrderRequest();
//
//        providerrequest.setReturnOrderDTO(requestDto);
//
//        aresProvider.returnOrder(providerrequest);

        //操作日志记录
        operateLogMQUtil.convertAndSend("财务", "确认退款", "确认退款：退单编号" + returnOrder.getId());

        return ResponseEntity.ok(BaseResponse.SUCCESSFUL());
    }

    @ApiOperation(value = "在线退款")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "rid", value = "退单Id", required = true)
    @RequestMapping(value = "/refund/{rid}/online", method = RequestMethod.POST)
    @LcnTransaction
    public BaseResponse<Object> refundOnline(@PathVariable String rid) {
        NewPileReturnOrderByIdResponse returnOrder = newPileReturnOrderProvider.getById(ReturnOrderByIdRequest.builder().rid(rid)
                .build()).getContext();

        if (!NewPileReturnFlowState.AUDIT.equals(returnOrder.getReturnFlowState())) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "囤货退单状态已变化，请核实！");
        }
        if (!returnOrder.getFinancialRefundFlag()) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "囤货退单未进入账务退款阶段，请核实！");
        }

        newPileReturnOrderProvider.refundOnlineByTid(ReturnOrderOnlineRefundByTidRequest.builder().returnOrderCode(rid)
                .operator(commonUtil.getOperator()).build());

        //操作日志记录
        operateLogMQUtil.convertAndSend("财务", "确认退款", "确认退款：退单编号" + returnOrder.getId());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 根据退单号查询囤货退款单
     *
     * @return BaseResponse<RefundBill>
     */
    @ApiOperation(value = "根据退单号查询囤货退款单")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "returnOrderNo", value = "退单号", required = true)
    @RequestMapping(value = "/detail/{returnOrderNo}")
    public BaseResponse<RefundOrderResponse> queryPileRefundByReturnOrderNo(@PathVariable("returnOrderNo") String returnOrderNo) {
        return newPileReturnOrderProvider.getPileRefundOrderRespByReturnOrderCode(new RefundOrderResponseByReturnOrderCodeRequest(returnOrderNo));
    }

    @ApiOperation(value = "查询囤貨退单")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "rid", value = "退单Id", required = true)
    @RequestMapping(value = "/{rid}/detail")
    public BaseResponse<NewPileReturnOrderVO> findById(@PathVariable String rid) {
        NewPileReturnOrderByIdResponse returnOrder = newPileReturnOrderProvider.getById(ReturnOrderByIdRequest.builder().rid(rid)
                .build()).getContext();
        String accountName = employeeQueryProvider.getByCompanyId(
                EmployeeByCompanyIdRequest.builder().companyInfoId(returnOrder.getCompany().getCompanyInfoId()).build()
        ).getContext().getAccountName();
        returnOrder.getCompany().setAccountName(accountName);
        return BaseResponse.success(returnOrder);
    }


    /**
     * 审核
     *
     * @param rid
     * @return
     */
    @ApiOperation(value = "审核")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "rid", value = "退单Id", required = true)
    @RequestMapping(value = "/audit/{rid}", method = RequestMethod.POST)
    @LcnTransaction
    public BaseResponse audit(@PathVariable String rid) {
        //操作日志记录
        operateLogMQUtil.convertAndSend("财务", "审核", "审核：id" + rid);
        return newPileReturnOrderProvider.audit(ReturnOrderAuditRequest.builder().rid(rid).operator(commonUtil.getOperator()).build());
    }

    /**
     * 批量审核
     *
     * @param returnRequest
     * @return
     */
    @ApiOperation(value = "批量审核")
    @RequestMapping(value = "/audit", method = RequestMethod.POST)
    public ResponseEntity<BaseResponse> batchAudit(@RequestBody ReturnRequest returnRequest) {
        returnRequest.getRids().forEach(this::audit);
        //操作日志记录
        operateLogMQUtil.convertAndSend("财务", "批量审核", "审核成功");
        return ResponseEntity.ok(BaseResponse.SUCCESSFUL());
    }

}
