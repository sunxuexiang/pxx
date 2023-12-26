package com.wanmi.sbc.returnorder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.account.bean.enums.PayOrderStatus;
import com.wanmi.sbc.common.annotation.MultiSubmit;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.enums.Platform;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.request.employee.EmployeeByCompanyIdRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeListRequest;
import com.wanmi.sbc.customer.bean.vo.EmployeeListVO;
import com.wanmi.sbc.goods.api.provider.goodstobeevaluate.GoodsTobeEvaluateSaveProvider;
import com.wanmi.sbc.goods.api.provider.storetobeevaluate.StoreTobeEvaluateSaveProvider;
import com.wanmi.sbc.goods.api.request.goodstobeevaluate.GoodsTobeEvaluateQueryRequest;
import com.wanmi.sbc.goods.api.request.storetobeevaluate.StoreTobeEvaluateQueryRequest;
import com.wanmi.sbc.order.api.provider.payorder.PayOrderQueryProvider;
import com.wanmi.sbc.order.api.provider.refund.RefundOrderQueryProvider;
import com.wanmi.sbc.order.api.provider.returnorder.ReturnPileOrderProvider;
import com.wanmi.sbc.order.api.provider.returnorder.ReturnPileOrderQueryProvider;
import com.wanmi.sbc.order.api.provider.trade.PileTradeQueryProvider;
import com.wanmi.sbc.order.api.request.payorder.FindPayOrderRequest;
import com.wanmi.sbc.order.api.request.refund.RefundOrderByReturnOrderCodeRequest;
import com.wanmi.sbc.order.api.request.refund.RefundOrderResponseByReturnOrderCodeRequest;
import com.wanmi.sbc.order.api.request.returnorder.*;
import com.wanmi.sbc.order.api.request.trade.TradeGetByIdRequest;
import com.wanmi.sbc.order.api.response.payorder.FindPayOrderResponse;
import com.wanmi.sbc.order.api.response.refund.RefundOrderByReturnCodeResponse;
import com.wanmi.sbc.order.api.response.refund.RefundOrderResponse;
import com.wanmi.sbc.order.api.response.returnorder.ReturnOrderByIdResponse;
import com.wanmi.sbc.order.api.response.returnorder.ReturnOrderTransferByUserIdResponse;
import com.wanmi.sbc.order.bean.dto.CompanyDTO;
import com.wanmi.sbc.order.bean.dto.RefundOrderDTO;
import com.wanmi.sbc.order.bean.dto.ReturnLogisticsDTO;
import com.wanmi.sbc.order.bean.dto.ReturnOrderDTO;
import com.wanmi.sbc.order.bean.enums.DeliverStatus;
import com.wanmi.sbc.order.bean.enums.ReturnFlowState;
import com.wanmi.sbc.order.bean.enums.ReturnReason;
import com.wanmi.sbc.order.bean.enums.ReturnWay;
import com.wanmi.sbc.order.bean.vo.ReturnOrderVO;
import com.wanmi.sbc.order.bean.vo.TradeVO;
import com.wanmi.sbc.pay.api.provider.PayQueryProvider;
import com.wanmi.sbc.pay.api.request.RefundResultByOrdercodeRequest;
import com.wanmi.sbc.pay.bean.enums.TradeStatus;
import com.wanmi.sbc.returnorder.convert.Remedy2ReturnOrder;
import com.wanmi.sbc.returnorder.request.RejectRequest;
import com.wanmi.sbc.returnorder.request.RemedyReturnRequest;
import com.wanmi.sbc.returnorder.request.ReturnExportRequest;
import com.wanmi.sbc.returnorder.request.ReturnRequest;
import com.wanmi.sbc.returnorder.service.ReturnExportService;
import com.wanmi.sbc.setting.api.provider.AuditQueryProvider;
import com.wanmi.sbc.setting.api.request.TradeConfigGetByTypeRequest;
import com.wanmi.sbc.setting.api.response.TradeConfigGetByTypeResponse;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description: 囤货退单api
 * @author: jiangxin
 * @create: 2021-09-29 10:04
 */
@Api(tags = "ReturnPileOrderController", description = "囤货退单api")
@RestController
@RequestMapping("/return/pile")
public class ReturnPileOrderController {


    @Autowired
    private ReturnPileOrderProvider returnOrderProvider;

    @Autowired
    private ReturnPileOrderQueryProvider returnOrderQueryProvider;

    @Autowired
    private ReturnExportService returnExportService;

    @Autowired
    private PayQueryProvider payQueryProvider;

    @Autowired
    private RefundOrderQueryProvider refundOrderQueryProvider;

    @Autowired
    private PileTradeQueryProvider tradeQueryProvider;

    @Autowired
    private EmployeeQueryProvider employeeQueryProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private GoodsTobeEvaluateSaveProvider goodsTobeEvaluateSaveProvider;

    @Autowired
    private StoreTobeEvaluateSaveProvider storeTobeEvaluateSaveProvider;

    @Autowired
    private AuditQueryProvider auditQueryProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @Autowired
    private PayOrderQueryProvider payOrderQueryProvider;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 导出文件名后的时间后缀
     */
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

    @ApiOperation(value = "查询退单")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "rid", value = "退单Id", required = true)
    @RequestMapping(value = "/{rid}")
    public BaseResponse<ReturnOrderVO> findById(@PathVariable String rid) {
        ReturnOrderVO returnOrder = returnOrderQueryProvider.getById(ReturnOrderByIdRequest.builder().rid(rid)
                .build()).getContext();
        String accountName = employeeQueryProvider.getByCompanyId(
                EmployeeByCompanyIdRequest.builder().companyInfoId(returnOrder.getCompany().getCompanyInfoId()).build()
        ).getContext().getAccountName();
        returnOrder.getCompany().setAccountName(accountName);
        return BaseResponse.success(returnOrder);
    }

    /**
     * 导出退单
     *
     * @param encrypted
     * @param response
     */
    @ApiOperation(value = "导出退单")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "encrypted", value = "解密", required = true)
    @RequestMapping(value = "/export/params/{encrypted}", method = RequestMethod.GET)
    public void exportByParams(@PathVariable String encrypted, HttpServletResponse response) {
        String decrypted = new String(Base64.getUrlDecoder().decode(encrypted));
        ReturnExportRequest returnExportRequest = JSON.parseObject(decrypted, ReturnExportRequest.class);

        Operator operator = commonUtil.getOperator();
        logger.debug("/return/export/params, employeeId=" + operator.getUserId());
        Platform platform = operator.getPlatform();
        if (platform == Platform.SUPPLIER) {
            returnExportRequest.setCompanyInfoId(Long.parseLong(operator.getAdminId()));
        }

        ReturnOrderByConditionRequest conditionRequest = new ReturnOrderByConditionRequest();
        KsBeanUtil.copyPropertiesThird(returnExportRequest, conditionRequest);
        List<ReturnOrderVO> returnOrders =
                returnOrderQueryProvider.listByCondition(conditionRequest).getContext().getReturnOrderList();
        List<EmployeeListVO> employeeList = employeeQueryProvider.list(new EmployeeListRequest()).getContext().getEmployeeList();
        if(CollectionUtils.isNotEmpty(returnOrders) && CollectionUtils.isNotEmpty(employeeList)){
            returnOrders = returnOrders.stream().map(returnOrderVO -> {
                for (EmployeeListVO employeeListVO : employeeList) {
                    if (returnOrderVO.getBuyer().getEmployeeId().equals(employeeListVO.getEmployeeId())) {
                        returnOrderVO.setEmployeeName(employeeListVO.getEmployeeName());
                        break;
                    }
                }
                return returnOrderVO;
            }).collect(Collectors.toList());
        }
        String headerKey = "Content-Disposition";
        LocalDateTime dateTime = LocalDateTime.now();
        String fileName = String.format("批量导出退单_%s.xls", dateTime.format(formatter));
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("/return/export/params, fileName={}, error={}", fileName, e);
        }
        String headerValue = String.format("attachment;filename=\"%s\";filename*=\"utf-8''%s\"", fileName, fileName);
        response.setHeader(headerKey, headerValue);
        try {

            returnExportService.export(returnOrders, response.getOutputStream(),
                    platform == Platform.PLATFORM);
            response.flushBuffer();
        } catch (IOException e) {
            throw new SbcRuntimeException(e);
        }
        operateLogMQUtil.convertAndSend("囤货退单", "导出退单", "操作成功");
    }

    @ApiOperation(value = "退单方式查询")
    @RequestMapping(value = "/ways", method = RequestMethod.GET)
    public BaseResponse<List<ReturnWay>> findReturnWay() {
        return BaseResponse.success(returnOrderQueryProvider.listReturnWay().getContext().getReturnWayList());
    }

    @ApiOperation(value = "退单原因查询")
    @RequestMapping(value = "/reasons", method = RequestMethod.GET)
    public BaseResponse<List<ReturnReason>> findReturnReason() {
        return BaseResponse.success(returnOrderQueryProvider.listReturnReason().getContext().getReturnReasonList());
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
        operateLogMQUtil.convertAndSend("囤货退单", "审核", "审核:退单id" + (StringUtils.isNotEmpty(rid) ? rid : ""));
        return returnOrderProvider.audit(ReturnOrderAuditRequest.builder().rid(rid).operator(commonUtil.getOperator()).build());
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
        operateLogMQUtil.convertAndSend("囤货退单", "批量审核", "操作成功");
        return ResponseEntity.ok(BaseResponse.SUCCESSFUL());
    }

    /**
     * 校验退单退款状态
     *
     * @param rid
     * @return
     */
    @ApiOperation(value = "校验退单退款状态")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "rid", value = "退单Id", required = true)
    @RequestMapping("/verifyRefundStatus/{rid}")
    @LcnTransaction
    public BaseResponse verifyRefundStatus(@PathVariable String rid) {
        ReturnOrderVO returnOrder = returnOrderQueryProvider.getById(ReturnOrderByIdRequest.builder().rid(rid)
                .build()).getContext();
        TradeStatus tradeStatus = payQueryProvider.getRefundResponseByOrdercode(new RefundResultByOrdercodeRequest
                (returnOrder.getTid(), returnOrder.getId())).getContext().getTradeStatus();
        if (tradeStatus != null) {
            if (tradeStatus == TradeStatus.PROCESSING) {
                throw new SbcRuntimeException("K-100105");
            } else if (tradeStatus == TradeStatus.SUCCEED) {
                RefundOrderByReturnCodeResponse refundOrder =
                        refundOrderQueryProvider.getByReturnOrderCode(new RefundOrderByReturnOrderCodeRequest(rid)).getContext();
                Operator operator = Operator.builder().ip(HttpUtil.getIpAddr()).adminId("1").name("system")
                        .account("system").platform(Platform.BOSS).build();
                returnOrderProvider.onlineRefund(ReturnOrderOnlineRefundRequest.builder().operator(operator)
                        .returnOrder(KsBeanUtil.convert(returnOrder, ReturnOrderDTO.class))
                        .refundOrder(KsBeanUtil.convert(refundOrder, RefundOrderDTO.class)).build());
                throw new SbcRuntimeException("K-100104");
            }
        }
        operateLogMQUtil.convertAndSend("囤货退单", "校验退单退款状态", "操作成功：退单id" + (StringUtils.isNotEmpty(rid) ? rid : ""));
        return BaseResponse.SUCCESSFUL();
    }


    @ApiOperation(value = "退单派送")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "rid", value = "退单Id", required = true)
    @RequestMapping(value = "/deliver/{rid}", method = RequestMethod.POST)
    @LcnTransaction
    public BaseResponse deliver(@PathVariable String rid, @RequestBody ReturnLogisticsDTO logistics) {
        operateLogMQUtil.convertAndSend("囤货退单", "退单派送", "退单派送：退单id" + (StringUtils.isNotEmpty(rid) ? rid : ""));
        return returnOrderProvider.deliver(ReturnOrderDeliverRequest.builder().rid(rid).logistics(logistics)
                .operator(commonUtil.getOperator()).build());
    }

    /**
     * 收货
     *
     * @param rid
     * @return
     */
    @ApiOperation(value = "收货")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "rid", value = "退单Id", required = true)
    @RequestMapping(value = "/receive/{rid}", method = RequestMethod.POST)
    @LcnTransaction
    public BaseResponse receive(@PathVariable String rid) {
        ReturnOrderByIdResponse returnOrder =
                returnOrderQueryProvider.getById(ReturnOrderByIdRequest.builder().rid(rid).build()).getContext();
        returnOrder.getReturnItems().forEach(goodsInfo -> {
            goodsTobeEvaluateSaveProvider.deleteByOrderAndSku(GoodsTobeEvaluateQueryRequest.builder()
                    .orderNo(returnOrder.getTid()).goodsInfoId(goodsInfo.getSkuId()).build());
        });
        storeTobeEvaluateSaveProvider.deleteByOrderAndStoreId(StoreTobeEvaluateQueryRequest.builder()
                .storeId(returnOrder.getCompany().getStoreId()).orderNo(returnOrder.getTid()).build());
        operateLogMQUtil.convertAndSend("囤货退单", "收货", "收货：退单id" + (StringUtils.isNotEmpty(rid) ? rid : ""));
        return returnOrderProvider.receive(ReturnOrderReceiveRequest.builder().operator(commonUtil.getOperator())
                .rid(rid).build());
    }

    /**
     * 批量收货
     *
     * @param returnRequest
     * @return
     */
    @ApiOperation(value = "批量收货")
    @RequestMapping(value = "/receive", method = RequestMethod.POST)
    public BaseResponse batchReceive(@RequestBody ReturnRequest returnRequest) {
        returnRequest.getRids().forEach(this::receive);
        operateLogMQUtil.convertAndSend("囤货退单", "批量收货", "批量收货" );
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "退单拒绝收货")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "rid", value = "退单Id", required = true)
    @RequestMapping(value = "/receive/{rid}/reject", method = RequestMethod.POST)
    @LcnTransaction
    public BaseResponse rejectReceive(@PathVariable String rid, @RequestBody RejectRequest request) {
        returnOrderProvider.rejectReceive(ReturnOrderRejectReceiveRequest.builder().rid(rid).reason(request.getReason())
                .operator(commonUtil.getOperator()).build());
        operateLogMQUtil.convertAndSend("囤货退单", "退单拒绝收货", "退单拒绝收货：退单id" + (StringUtils.isNotEmpty(rid) ? rid : ""));
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 批量拒绝收货
     *
     * @param returnRequest
     * @return
     */
    @ApiOperation(value = "批量拒绝收货")
    @RequestMapping(value = "/receive/reject", method = RequestMethod.POST)
    public BaseResponse rejectReceive(@RequestBody ReturnRequest returnRequest) {
        RejectRequest request = new RejectRequest();
        request.setReason(StringUtils.EMPTY);
        returnRequest.getRids().forEach(rid -> this.rejectReceive(rid, request));
        operateLogMQUtil.convertAndSend("囤货退单", "批量拒绝收货", "操作成功");
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 在线退款
     *
     * @param rid
     * @return
     */
    @ApiOperation(value = "在线退款")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "rid", value = "退单Id", required = true)
    @RequestMapping(value = "/refund/{rid}/online", method = RequestMethod.POST)
    @LcnTransaction
    @MultiSubmit
    public BaseResponse<Object> refundOnline(@PathVariable String rid) {
        Operator operator = commonUtil.getOperatorWithNull();
        BaseResponse<Object> res = returnOrderProvider.refundOnlineByTid(ReturnOrderOnlineRefundByTidRequest.builder().returnOrderCode(rid)
                .operator(operator).build());
        Object data = res.getContext();
        if (Objects.isNull(data)) {
            //无返回信息，追踪退单退款状态
            ReturnFlowState state = returnOrderQueryProvider.getById(ReturnOrderByIdRequest.builder().rid(rid).build()).getContext().getReturnFlowState();
            if (state.equals(ReturnFlowState.REFUND_FAILED)) {
                return BaseResponse.FAILED();
            }
        }
        operateLogMQUtil.convertAndSend("囤货退单", "在线退款", "在线退款：退单id" + (StringUtils.isNotEmpty(rid) ? rid : ""));
        return res;
    }

    /**
     * 根据订单id查询退单(过滤拒绝退款、拒绝收货、已作废)
     *
     * @param tid
     * @return
     */
    @ApiOperation(value = "根据订单id查询退单(过滤拒绝退款、拒绝收货、已作废)")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "退单Id", required = true)
    @RequestMapping(value = "/findByTid/{tid}", method = RequestMethod.GET)
    public BaseResponse<List<ReturnOrderVO>> findByTid(@PathVariable String tid) {
        List<ReturnOrderVO> returnOrders = returnOrderQueryProvider.listByTid(ReturnOrderListByTidRequest.builder()
                .tid(tid).build()).getContext().getReturnOrderList();
        return BaseResponse.success(returnOrders.stream().filter(o -> o.getReturnFlowState() != ReturnFlowState
                .REJECT_REFUND &&
                o.getReturnFlowState() != ReturnFlowState.REJECT_RECEIVE && o.getReturnFlowState() != ReturnFlowState
                .VOID).collect(Collectors.toList()));
    }

    /**
     * 根据订单id查询已完成的退单
     *
     * @param tid
     * @return
     */
    @ApiOperation(value = "根据订单id查询已完成的退单")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "退单Id", required = true)
    @RequestMapping(value = "/findCompletedByTid/{tid}", method = RequestMethod.GET)
    public BaseResponse<List<ReturnOrderVO>> findCompletedByTid(@PathVariable String tid) {
        List<ReturnOrderVO> returnOrders = returnOrderQueryProvider.listNotVoidByTid(ReturnOrderNotVoidByTidRequest
                .builder().tid(tid).build()).getContext().getReturnOrderList();
        return BaseResponse.success(returnOrders);
    }

    /**
     * 根据订单id查询全量退单
     *
     * @param tid
     * @return
     */
    @ApiOperation(value = "根据订单id查询全量退单")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "退单Id", required = true)
    @RequestMapping(value = "/find-all/{tid}", method = RequestMethod.GET)
    public BaseResponse<List<ReturnOrderVO>> findAllByTid(@PathVariable String tid) {
        //判断订单是否计入了账期，如果计入了账期，不允许作废
        Boolean settled =
                tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO().getHasBeanSettled();
        if (settled != null && settled) {
            throw new SbcRuntimeException("K-050131");
        }
        return BaseResponse.success(returnOrderQueryProvider.listByTid(ReturnOrderListByTidRequest.builder().tid(tid)
                .build()).getContext().getReturnOrderList());
    }

    /**
     * 拒绝收款
     *
     * @param rid
     * @return
     */
    @ApiOperation(value = "拒绝收款")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "rid", value = "退单Id", required = true)
    @RequestMapping(value = "/refund/{rid}/reject", method = RequestMethod.POST)
    @LcnTransaction
    public BaseResponse refundReject(@PathVariable String rid, @RequestBody RejectRequest request) {
        operateLogMQUtil.convertAndSend("囤货退单", "拒绝收款", "拒绝收款：退单id" + (StringUtils.isNotEmpty(rid) ? rid : ""));
        return returnOrderProvider.rejectRefund(ReturnOrderRejectRefundRequest.builder().operator(commonUtil.getOperator())
                .rid(rid).reason(request.getReason()).build());
    }

    /**
     * 批量拒绝退款
     *
     * @param returnRequest
     * @return
     */
    @ApiOperation(value = "批量拒绝退款")
    @RequestMapping(value = "/refund/reject", method = RequestMethod.POST)
    public ResponseEntity<BaseResponse> refundReject(@RequestBody ReturnRequest returnRequest) {
        RejectRequest rejectRequest = new RejectRequest();
        rejectRequest.setReason(StringUtils.EMPTY);
        returnRequest.getRids().forEach(rid -> this.refundReject(rid, rejectRequest));
        operateLogMQUtil.convertAndSend("囤货退单", "批量拒绝退款", "操作成功");
        return ResponseEntity.ok(BaseResponse.SUCCESSFUL());
    }

    @ApiOperation(value = "取消退单")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "rid", value = "退单Id", required = true)
    @RequestMapping(value = "/cancel/{rid}", method = RequestMethod.POST)
    @LcnTransaction
    public BaseResponse cancel(@PathVariable String rid, @RequestParam("reason") String reason) {
        operateLogMQUtil.convertAndSend("囤货退单", "取消退单", "取消退单：退单id" + (StringUtils.isNotEmpty(rid) ? rid : ""));
        return returnOrderProvider.cancel(ReturnOrderCancelRequest.builder().operator(commonUtil.getOperator()).rid(rid)
                .remark(reason).build());
    }

    @ApiOperation(value = "修改退单")
    @RequestMapping(value = "/remedy", method = RequestMethod.POST)
    @LcnTransaction
    public BaseResponse remedy(@RequestBody RemedyReturnRequest request) {
        operateLogMQUtil.convertAndSend("囤货退单", "修改退单", "修改退单：退单id" + (Objects.nonNull(request) ? request.getRid() : ""));
        return returnOrderProvider.remedy(ReturnOrderRemedyRequest.builder().operator(commonUtil.getOperator())
                .newReturnOrder(Remedy2ReturnOrder.convert(request)).build());
    }

    /**
     * 查看退货订单详情和可退商品数
     *
     * @param tid
     * @return
     */
    @ApiOperation(value = "查看退货订单详情和可退商品数")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "退单Id", required = true)
    @RequestMapping(value = "/trade/{tid}", method = RequestMethod.GET)
    public BaseResponse<TradeVO> tradeDetails(@PathVariable String tid) {
        TradeVO trade = returnOrderQueryProvider.queryCanReturnItemNumByTid(CanReturnItemNumByTidRequest.builder()
                .tid(tid).build()).getContext();
        return BaseResponse.success(trade);
    }

    /**
     * 查询退货退单及可退商品数
     *
     * @param rid
     * @return
     */
    @ApiOperation(value = "查询退货退单及可退商品数")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "rid", value = "退单Id", required = true)
    @RequestMapping(value = "/detail/{rid}")
    public ResponseEntity<ReturnOrderVO> returnDetail(@PathVariable String rid) {
        ReturnOrderVO returnOrder = returnOrderQueryProvider.queryCanReturnItemNumById(CanReturnItemNumByIdRequest
                .builder().rid(rid).build()).getContext();
        return ResponseEntity.ok(returnOrder);
    }


    /**
     * 查询退单退款记录
     *
     * @param rid
     * @return
     */
    @ApiOperation(value = "查询退单退款记录")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "rid", value = "退单Id", required = true)
    @RequestMapping(value = "/refundOrder/{rid}", method = RequestMethod.GET)
    public BaseResponse<RefundOrderResponse> refundOrder(@PathVariable String rid) {
        RefundOrderResponse refundOrderResponse =
                refundOrderQueryProvider.getRefundOrderRespByReturnOrderCode(new RefundOrderResponseByReturnOrderCodeRequest(rid)).getContext();
        return BaseResponse.success(refundOrderResponse);
    }

    /**
     * 关闭退款
     *
     * @param rid
     * @return
     */
    @RequestMapping(value = "/refund/{rid}/closeRefund", method = RequestMethod.POST)
    @LcnTransaction
    public BaseResponse closeRefund(@PathVariable String rid) {
        operateLogMQUtil.convertAndSend("囤货退单", "关闭退款", "关闭退款：退单id" + (StringUtils.isNotEmpty(rid) ? rid : ""));
        return returnOrderProvider.closeRefund(ReturnOrderCloseRequest.builder()
                .operator(commonUtil.getOperator())
                .rid(rid).build());
    }

    /**
     * 创建退单
     *
     * @param returnOrder
     * @return
     */
    @ApiOperation(value = "创建退单")
    @RequestMapping(value = "/base/add", method = RequestMethod.POST)
    @LcnTransaction
    public BaseResponse<String> create(@RequestBody @Valid ReturnOrderDTO returnOrder) {
        verifyIsReturnable(returnOrder.getTid());
        String userId = commonUtil.getOperatorId();
        ReturnOrderVO oldReturnOrderTemp = returnOrderQueryProvider.getTransferByUserId(
                ReturnOrderTransferByUserIdRequest.builder().userId(userId).build()).getContext();
        if (oldReturnOrderTemp == null) {
            throw new SbcRuntimeException("K-000001");
        }
        ReturnOrderDTO oldReturnOrder = KsBeanUtil.convert(oldReturnOrderTemp, ReturnOrderDTO.class);

        oldReturnOrder.setReturnReason(returnOrder.getReturnReason());
        oldReturnOrder.setDescription(returnOrder.getDescription());
        oldReturnOrder.setImages(returnOrder.getImages());
        oldReturnOrder.setReturnLogistics(returnOrder.getReturnLogistics());
        oldReturnOrder.setReturnWay(returnOrder.getReturnWay());
        oldReturnOrder.setReturnPrice(returnOrder.getReturnPrice());
        oldReturnOrder.setPushNeeded(returnOrder.getPushNeeded());

        TradeVO trade =
                tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(returnOrder.getTid()).build()).getContext().getTradeVO();
        oldReturnOrder.setCompany(CompanyDTO.builder().companyInfoId(trade.getSupplier().getSupplierId())
                .companyCode(trade.getSupplier().getSupplierCode()).supplierName(trade.getSupplier().getSupplierName())
                .storeId(trade.getSupplier().getStoreId()).storeName(trade.getSupplier().getStoreName()).companyType(trade.getSupplier().getCompanyType()).build());
        //
        oldReturnOrder.setChannelType(trade.getChannelType());
        oldReturnOrder.setDistributorId(trade.getDistributorId());
        oldReturnOrder.setInviteeId(trade.getInviteeId());
        oldReturnOrder.setShopName(trade.getShopName());
        oldReturnOrder.setDistributorName(trade.getDistributorName());
        oldReturnOrder.setDistributeItems(trade.getDistributeItems());
        oldReturnOrder.setActivityType(trade.getActivityType());
        String rid = returnOrderProvider.add(ReturnOrderAddRequest.builder().returnOrder(oldReturnOrder)
                .operator(commonUtil.getOperator()).build()).getContext().getReturnOrderId();
        returnOrderProvider.deleteTransfer(ReturnOrderTransferDeleteRequest.builder().userId(userId).build());
        operateLogMQUtil.convertAndSend("囤货退单", "创建退单", "创建退单：退单id" + (StringUtils.isNotEmpty(rid) ? rid : ""));
        return BaseResponse.success(rid);
    }

    /**
     * 创建退款单
     *
     * @param returnOrder
     * @return
     */
    @ApiOperation(value = "创建退款单")
    @RequestMapping(value = "/addRefund", method = RequestMethod.POST)
    @LcnTransaction
    public BaseResponse<String> createRefund(@RequestBody @Valid ReturnOrderDTO returnOrder) {
        verifyIsReturnable(returnOrder.getTid());
        TradeVO trade =
                tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(returnOrder.getTid()).build()).getContext().getTradeVO();
        returnOrder.setCompany(CompanyDTO.builder().companyInfoId(trade.getSupplier().getSupplierId())
                .companyCode(trade.getSupplier().getSupplierCode()).supplierName(trade.getSupplier().getSupplierName())
                .storeId(trade.getSupplier().getStoreId()).storeName(trade.getSupplier().getStoreName())
                .companyType(trade.getSupplier().getCompanyType())
                .build());
        returnOrder.setChannelType(trade.getChannelType());
        returnOrder.setDistributorId(trade.getDistributorId());
        returnOrder.setInviteeId(trade.getInviteeId());
        returnOrder.setShopName(trade.getShopName());
        returnOrder.setDistributorName(trade.getDistributorName());
        returnOrder.setDistributeItems(trade.getDistributeItems());
        returnOrder.setActivityType(trade.getActivityType());
        String rid = returnOrderProvider.add(ReturnOrderAddRequest.builder().returnOrder(returnOrder)
                .operator(commonUtil.getOperator()).build()).getContext().getReturnOrderId();
        operateLogMQUtil.convertAndSend("囤货退单", "创建退款单", "创建退款单：退单id" + (StringUtils.isNotEmpty(rid) ? rid : ""));
        return BaseResponse.success(rid);
    }

    /**
     * 创建退单快照
     *
     * @param returnOrder
     * @return
     */
    @ApiOperation(value = "创建退单快照")
    @RequestMapping(value = "/transfer", method = RequestMethod.POST)
    @LcnTransaction
    public BaseResponse transfer(@RequestBody @Valid ReturnOrderDTO returnOrder) {
        BaseResponse<FindPayOrderResponse> response =
                payOrderQueryProvider.findPayOrder(FindPayOrderRequest.builder().value(returnOrder.getTid()).build());

        FindPayOrderResponse payOrderResponse = response.getContext();
        if (Objects.isNull(payOrderResponse) || Objects.isNull(payOrderResponse.getPayOrderStatus())
                || (payOrderResponse.getPayOrderStatus() != PayOrderStatus.PAYED && payOrderResponse.getPayOrderStatus() != PayOrderStatus.NOTPAY)) {
            throw new SbcRuntimeException("K-050103");
        }
        verifyIsReturnable(returnOrder.getTid());
        returnOrderProvider.addTransfer(ReturnOrderTransferAddRequest.builder().returnOrder(returnOrder)
                .operator(commonUtil.getOperator()).build());
        operateLogMQUtil.convertAndSend("囤货退单", "创建退单快照", "创建退单快照：退单id" + (Objects.nonNull(returnOrder) ? returnOrder.getId() : ""));
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 查询退单快照
     *
     * @return
     */
    @ApiOperation(value = "查询退单快照")
    @RequestMapping(value = "/findTransfer", method = RequestMethod.GET)
    public BaseResponse<ReturnOrderTransferByUserIdResponse> transfer() {
        ReturnOrderTransferByUserIdResponse response = returnOrderQueryProvider.getTransferByUserId(
                ReturnOrderTransferByUserIdRequest.builder()
                        .userId(commonUtil.getOperatorId()).build()).getContext();
        return BaseResponse.success(response);
    }

    /**
     * 校验是否可退
     *
     * @param tid
     */
    private void verifyIsReturnable(String tid) {
        TradeVO trade =
                tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO();
        if (Objects.nonNull(trade.getTradeState().getDeliverStatus()) && (trade.getTradeState().getDeliverStatus() == DeliverStatus.SHIPPED || trade.getTradeState().getDeliverStatus() == DeliverStatus.PART_SHIPPED)) {
            TradeConfigGetByTypeRequest request = new TradeConfigGetByTypeRequest();
            request.setConfigType(ConfigType.ORDER_SETTING_APPLY_REFUND);
            TradeConfigGetByTypeResponse config = auditQueryProvider.getTradeConfigByType(request).getContext();
            if (config.getStatus() == 0) {
                throw new SbcRuntimeException("K-050208");
            }
            JSONObject content = JSON.parseObject(config.getContext());
            Integer day = content.getObject("day", Integer.class);

            if (Objects.isNull(trade.getTradeState().getEndTime())) {
                throw new SbcRuntimeException("K-050002");
            }
            if (trade.getTradeState().getEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() < LocalDateTime.now().minusDays(day).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()) {
                throw new SbcRuntimeException("K-050208");
            }
        }
    }
}
