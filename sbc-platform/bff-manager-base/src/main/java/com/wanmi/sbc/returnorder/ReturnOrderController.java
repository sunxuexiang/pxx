package com.wanmi.sbc.returnorder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.account.api.provider.funds.CustomerFundsDetailProvider;
import com.wanmi.sbc.account.api.provider.wallet.CustomerWalletQueryProvider;
import com.wanmi.sbc.account.api.request.wallet.WalletByCustomerIdQueryRequest;
import com.wanmi.sbc.account.bean.enums.PayOrderStatus;
import com.wanmi.sbc.account.bean.enums.WalletDetailsType;
import com.wanmi.sbc.account.bean.vo.CustomerWalletVO;
import com.wanmi.sbc.common.annotation.MultiSubmit;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.Platform;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.request.employee.EmployeeByCompanyIdRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeListRequest;
import com.wanmi.sbc.customer.bean.vo.EmployeeListVO;
import com.wanmi.sbc.goods.api.provider.goodstobeevaluate.GoodsTobeEvaluateQueryProvider;
import com.wanmi.sbc.goods.api.provider.goodstobeevaluate.GoodsTobeEvaluateSaveProvider;
import com.wanmi.sbc.goods.api.provider.storetobeevaluate.StoreTobeEvaluateQueryProvider;
import com.wanmi.sbc.goods.api.provider.storetobeevaluate.StoreTobeEvaluateSaveProvider;
import com.wanmi.sbc.goods.api.request.goodstobeevaluate.GoodsTobeEvaluateQueryRequest;
import com.wanmi.sbc.goods.api.request.storetobeevaluate.StoreTobeEvaluateQueryRequest;
import com.wanmi.sbc.marketing.api.provider.coupon.CoinActivityProvider;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponCodeProvider;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponCodeQueryProvider;
import com.wanmi.sbc.marketing.api.request.coupon.CouponCodeDeleteRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponsByCodeIdsRequest;
import com.wanmi.sbc.marketing.bean.dto.CoinActivityRecordDetailDto;
import com.wanmi.sbc.marketing.bean.dto.CoinActivityRecordDto;
import com.wanmi.sbc.marketing.bean.dto.CouponCodeDTO;
import com.wanmi.sbc.order.api.provider.payorder.PayOrderQueryProvider;
import com.wanmi.sbc.order.api.provider.refund.RefundOrderProvider;
import com.wanmi.sbc.order.api.provider.refund.RefundOrderQueryProvider;
import com.wanmi.sbc.order.api.provider.returnorder.ReturnOrderProvider;
import com.wanmi.sbc.order.api.provider.returnorder.ReturnOrderQueryProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeQueryProvider;
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
import com.wanmi.sbc.order.bean.vo.ReturnItemVO;
import com.wanmi.sbc.order.bean.vo.ReturnOrderVO;
import com.wanmi.sbc.order.bean.vo.TradeItemVO;
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
import com.wanmi.sbc.wallet.api.request.wallet.CustomerWalletOrderByRequest;
import com.wanmi.sbc.wallet.bean.enums.WalletRecordTradeType;
import com.wanmi.sbc.wallet.bean.vo.WalletRecordVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 退货
 * Created by jinwei on 20/4/2017.
 */
@Slf4j
@Api(tags = "ReturnOrderController", description = "退货 Api")
@RestController
@RequestMapping("/return")
public class ReturnOrderController {

    @Autowired
    private ReturnOrderProvider returnOrderProvider;

    @Autowired
    private ReturnOrderQueryProvider returnOrderQueryProvider;

    @Autowired
    private ReturnExportService returnExportService;

    @Autowired
    private CouponCodeQueryProvider couponCodeQueryProvider;

    @Autowired
    private PayQueryProvider payQueryProvider;

    @Autowired
    private RefundOrderQueryProvider refundOrderQueryProvider;

    @Autowired
    private TradeQueryProvider tradeQueryProvider;

    @Autowired
    private EmployeeQueryProvider employeeQueryProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @Autowired
    private RefundOrderProvider refundOrderProvider;

    @Autowired
    private GoodsTobeEvaluateSaveProvider goodsTobeEvaluateSaveProvider;

    @Autowired
    private StoreTobeEvaluateSaveProvider storeTobeEvaluateSaveProvider;

    @Autowired
    private CouponCodeProvider couponCodeProvider;

    @Autowired
    private CustomerFundsDetailProvider customerFundsDetailProvider;

    @Autowired
    private AuditQueryProvider auditQueryProvider;

    @Autowired
    private PayOrderQueryProvider payOrderQueryProvider;

    @Autowired
    private StoreTobeEvaluateQueryProvider storeTobeEvaluateQueryProvider;
    @Autowired
    private GoodsTobeEvaluateQueryProvider goodsTobeEvaluateQueryProvider;

    @Autowired
    private CustomerWalletQueryProvider walletQueryProvider;
    
    @Autowired
    private CoinActivityProvider coinActivityProvider;
    
    @Autowired
    private com.wanmi.sbc.wallet.api.provider.wallet.CustomerWalletProvider customerWalletProvider;

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
        // 只处理客户发起的订单
        if (Objects.equals(returnOrder.getPlatform(), Platform.CUSTOMER) && Objects.equals(returnOrder.getReturnFlowState(), ReturnFlowState.RECEIVED)) {
            List<ReturnItemVO> returnItems = returnOrder.getReturnItems();
            boolean partialReceipt = returnItems.stream().
                    anyMatch(item ->
                            Objects.nonNull(item.getReceivedQty()) &&
                                    new BigDecimal(item.getReceivedQty()).compareTo(BigDecimal.ZERO) > 0 &&
                                    new BigDecimal(item.getReceivedQty()).compareTo(new BigDecimal(item.getNum())) < 0
                    );
            if (partialReceipt) {
                BigDecimal modifyPrice = BigDecimal.ZERO;
                String modifyComment = "";
                for (ReturnItemVO item : returnItems) {
                    if (Objects.nonNull(item.getReceivedQty()) &&
                            new BigDecimal(item.getReceivedQty()).compareTo(BigDecimal.ZERO) > 0 &&
                            new BigDecimal(item.getReceivedQty()).compareTo(new BigDecimal(item.getNum())) < 0) {
                        modifyPrice = modifyPrice.add(new BigDecimal(item.getReceivedQty()).multiply(item.getPrice()).setScale(2, RoundingMode.HALF_UP));
                        modifyComment = modifyComment + "[" + item.getSkuName() + ":实收" + item.getReceivedQty() + item.getUnit() + " ] \n";
                    } else {
                        modifyPrice = modifyPrice.add(new BigDecimal(item.getNum()).multiply(item.getPrice()).setScale(2, RoundingMode.HALF_UP));
                    }
                }
                returnOrder.setModifyPrice(modifyPrice);
                returnOrder.setModifyComment(modifyComment);
            }

        }
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
        operateLogMQUtil.convertAndSend("退货", "导出退单", "操作成功");
    }

    @ApiOperation(value = "导出退单明细")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "encrypted", value = "解密", required = true)
    @RequestMapping(value = "/export/detail/params/{encrypted}", method = RequestMethod.GET)
    public void exportDetailByParams(@PathVariable String encrypted, HttpServletResponse response) {
        String decrypted = new String(Base64.getUrlDecoder().decode(encrypted));
        ReturnExportRequest returnExportRequest = JSON.parseObject(decrypted, ReturnExportRequest.class);

        Operator operator = commonUtil.getOperator();
        logger.debug("/return/export/detail/params, employeeId=" + operator.getUserId());
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
        String fileName = String.format("批量导出退单明细_%s.xls", dateTime.format(formatter));
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("/return/export/detail/params, fileName={}, error={}", fileName, e);
        }
        String headerValue = String.format("attachment;filename=\"%s\";filename*=\"utf-8''%s\"", fileName, fileName);
        response.setHeader(headerKey, headerValue);
        try {

            returnExportService.exportDetail(returnOrders, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            throw new SbcRuntimeException(e);
        }
        operateLogMQUtil.convertAndSend("退货", "导出退单明细", "操作成功");
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
        operateLogMQUtil.convertAndSend("退货", "审核", "操作成功：退单id" + (StringUtils.isNotEmpty(rid) ? rid : ""));
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
        operateLogMQUtil.convertAndSend("退货", "批量审核", "操作成功");
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
        log.info("====后台退款returnOrder===={}",JSONObject.toJSONString(returnOrder));
        TradeStatus tradeStatus = payQueryProvider.getRefundResponseByOrdercode(new RefundResultByOrdercodeRequest
                (returnOrder.getTid(), returnOrder.getId())).getContext().getTradeStatus();
        log.info("====后台退款tradeStatus===={}",JSONObject.toJSONString(tradeStatus));
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
        operateLogMQUtil.convertAndSend("退货", "校验退单退款状态", "操作成功:退单id" + (StringUtils.isNotEmpty(rid) ? rid : ""));
        return BaseResponse.SUCCESSFUL();
    }


    @ApiOperation(value = "退单派送")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "rid", value = "退单Id", required = true)
    @RequestMapping(value = "/deliver/{rid}", method = RequestMethod.POST)
    @LcnTransaction
    public BaseResponse deliver(@PathVariable String rid, @RequestBody ReturnLogisticsDTO logistics) {
        operateLogMQUtil.convertAndSend("退货", "退单派送", "退单派送:退单id" + (StringUtils.isNotEmpty(rid) ? rid : ""));
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
        operateLogMQUtil.convertAndSend("退货", "收货", "收货:退单id" + (StringUtils.isNotEmpty(rid) ? rid : ""));
        ReturnOrderByIdResponse returnOrder =
                returnOrderQueryProvider.getById(ReturnOrderByIdRequest.builder().rid(rid).build()).getContext();
        returnOrder.getReturnItems().forEach(goodsInfo -> {
            goodsTobeEvaluateSaveProvider.deleteByOrderAndSku(GoodsTobeEvaluateQueryRequest.builder()
                    .orderNo(returnOrder.getTid()).goodsInfoId(goodsInfo.getSkuId()).build());
        });
        storeTobeEvaluateSaveProvider.deleteByOrderAndStoreId(StoreTobeEvaluateQueryRequest.builder()
                .storeId(returnOrder.getCompany().getStoreId()).orderNo(returnOrder.getTid()).build());
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
        operateLogMQUtil.convertAndSend("退货", "批量收货", "批量收货");
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "退单拒绝收货")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "rid", value = "退单Id", required = true)
    @RequestMapping(value = "/receive/{rid}/reject", method = RequestMethod.POST)
    @LcnTransaction
    public BaseResponse rejectReceive(@PathVariable String rid, @RequestBody RejectRequest request) {
        returnOrderProvider.rejectReceive(ReturnOrderRejectReceiveRequest.builder().rid(rid).reason(request.getReason())
                .operator(commonUtil.getOperator()).build());
        operateLogMQUtil.convertAndSend("退货", "退单拒绝收货", "操作成功:退单id" + (StringUtils.isNotEmpty(rid) ? rid : ""));
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
        operateLogMQUtil.convertAndSend("退货", "批量拒绝收货", "操作成功");
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
        
        // 收回用户赠送的鲸币 
        takeBackCustomerCoin(rid);
        
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
        operateLogMQUtil.convertAndSend("退货", "在线退款", "操作成功：退单id" + (StringUtils.isNotEmpty(rid) ? rid : ""));
        return res;
    }
    
	/**
	 * 收回用户赠送的鲸币 
	 * @param returnOrder
	 * @param trade
	 */
    @Transactional
	private void takeBackCustomerCoin(String rid) {
		logger.info("退单[{}]准备收回用户赠送的鲸币", rid);
		
		// 存在收回用户赠送的鲸币成功，MQ里退款失败的情况，此时不需要再次收回用户鲸币
		BaseResponse<List<CoinActivityRecordDto>> record = coinActivityProvider.queryCoinActivityRecordByOrderId(rid);
		if (CollectionUtils.isNotEmpty( record.getContext())) {
			logger.info("退单[{}]已经存在收回用户赠送的鲸币的记录", rid);
			return;
		}
		
        ReturnOrderVO returnOrder = returnOrderQueryProvider.getById(ReturnOrderByIdRequest.builder().rid(rid)
                .build()).getContext();

        // 检验退货单是否参加鲸币活动
        TradeVO tradeVO = tradeQueryProvider.getOrderById(TradeGetByIdRequest.builder().tid(returnOrder.getTid()).build()).getContext().getTradeVO();
        if (Objects.nonNull(tradeVO.getReturnCoin())) {
            Map<String, BigDecimal> returnCoinMap = tradeVO.getTradeItems().stream().filter(o -> Objects.nonNull(o.getReturnCoin())).collect(Collectors.toMap(TradeItemVO::getSkuId, TradeItemVO::getReturnCoin, (o1, o2) -> o1));
            BigDecimal returnCoin = returnOrder.getReturnItems().stream().map(o -> {
                if (Objects.nonNull(returnCoinMap.get(o.getSkuId()))) {
                    return returnCoinMap.get(o.getSkuId());
                } else {
                    return BigDecimal.ZERO;
                }
            }).reduce(BigDecimal.ZERO, BigDecimal::add);
            if (returnCoin.compareTo(BigDecimal.ZERO) > 0) {
                CustomerWalletVO customerWalletVO = walletQueryProvider.getBalanceByCustomerId(WalletByCustomerIdQueryRequest.builder().customerId(tradeVO.getBuyer().getId()).build())
                        .getContext().getCustomerWalletVO();
                if (returnCoin.compareTo(customerWalletVO.getBalance()) > 0) {
                    throw new SbcRuntimeException("K-050601");
                }
                
                logger.info("用户[{}]鲸币余额[{}]，需要扣除[{}]", tradeVO.getBuyer().getId(), customerWalletVO.getBalance(), returnCoin);
                
            	// 取消返鲸币商家鲸币余额增加，用户鲸币余额减少
                Long storeId = tradeVO.getSupplier().getStoreId();
				String tradeRemark = WalletDetailsType.CANCEL_GOODS_RECHARGE.getDesc() + "-" + rid;
				String customerId = tradeVO.getBuyer().getId();
				CustomerWalletOrderByRequest orderByRequest = CustomerWalletOrderByRequest.builder()
						.customerId(customerId).storeId(storeId.toString()).balance(returnCoin)
						.relationOrderId(rid).tradeRemark(tradeRemark).remark(tradeRemark)
						.walletRecordTradeType(WalletRecordTradeType.ORDER_CASH_BACK)
						.build();
				BaseResponse<WalletRecordVO> orderByGiveStore = customerWalletProvider.orderByGiveStore(orderByRequest);

		        String sendNo = orderByGiveStore.getContext().getSendNo();

                //  保存退回记录
                String customerAccount = tradeVO.getBuyer().getAccount();
                LocalDateTime returnTime = returnOrder.getCreateTime();
                BigDecimal returnPrice = returnOrder.getReturnPrice().getTotalPrice();
                LocalDateTime now = LocalDateTime.now();
                List<String> skuIds = returnOrder.getReturnItems().stream().map(ReturnItemVO::getSkuId).collect(Collectors.toList());
                Map<String, List<CoinActivityRecordDetailDto>> detailMap = coinActivityProvider.queryCoinActivityRecordByOrderIdAndSkuIds(tradeVO.getId(), skuIds).getContext()
                        .stream().collect(Collectors.groupingBy(CoinActivityRecordDetailDto::getActivityId));

                List<CoinActivityRecordDto> saveRequest = new ArrayList<>();
                detailMap.forEach((k, v) -> {
                    CoinActivityRecordDto recordDto = new CoinActivityRecordDto();
                    recordDto.setSendNo(sendNo);
                    recordDto.setActivityId(k);
                    recordDto.setCustomerAccount(customerAccount);
                    recordDto.setOrderNo(rid);
                    recordDto.setOrderTime(returnTime);
                    recordDto.setOrderPrice(returnPrice);
                    recordDto.setRecordType(2);
                    recordDto.setRecordTime(now);

                    List<CoinActivityRecordDetailDto> detailDtoList = new ArrayList<>();
                    for (CoinActivityRecordDetailDto detailDto : v) {
                        detailDto.setDetailId(null);
                        detailDto.setRecordId(null);
                        detailDto.setOrderNo(rid);
                        detailDto.setRecordType(2);
                        detailDto.setRecordTime(now);

                        detailDtoList.add(detailDto);
                    }
                    BigDecimal totalCoinNum = detailDtoList.stream().map(CoinActivityRecordDetailDto::getCoinNum).reduce(BigDecimal.ZERO, BigDecimal::add);

                    recordDto.setCoinNum(totalCoinNum);
                    recordDto.setDetailList(detailDtoList);
                    saveRequest.add(recordDto);

                });
                coinActivityProvider.saveCoinRecord(saveRequest);
            }

        }
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
        operateLogMQUtil.convertAndSend("退货", "拒绝收款", "拒绝收款：退单id" + (StringUtils.isNotEmpty(rid) ? rid : ""));
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
        operateLogMQUtil.convertAndSend("退货", "批量拒绝退款", "操作成功");
        return ResponseEntity.ok(BaseResponse.SUCCESSFUL());
    }

    @ApiOperation(value = "取消退单")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "rid", value = "退单Id", required = true)
    @RequestMapping(value = "/cancel/{rid}", method = RequestMethod.POST)
    @LcnTransaction
    public BaseResponse cancel(@PathVariable String rid, @RequestParam("reason") String reason) {
        operateLogMQUtil.convertAndSend("退货", "取消退单", "取消退单：退单id" + (StringUtils.isNotEmpty(rid) ? rid : ""));
        return returnOrderProvider.cancel(ReturnOrderCancelRequest.builder().operator(commonUtil.getOperator()).rid(rid)
                .remark(reason).build());
    }

    @ApiOperation(value = "修改退单")
    @RequestMapping(value = "/remedy", method = RequestMethod.POST)
    @LcnTransaction
    public BaseResponse remedy(@RequestBody RemedyReturnRequest request) {
        operateLogMQUtil.convertAndSend("退货", "修改退单", "修改退单：退单id" + (Objects.nonNull(request) ? request.getRid() : ""));
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
        TradeVO trade = returnOrderQueryProvider.queryCanReturnDevanningItemNumByTid(CanReturnItemNumByTidRequest.builder()
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
        operateLogMQUtil.convertAndSend("退货", "关闭退款", "关闭退款：退单id" + (StringUtils.isNotEmpty(rid) ? rid : ""));
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
        oldReturnOrder.setWareId(trade.getWareId());
        oldReturnOrder.setWareName(trade.getWareName());
        if (CollectionUtils.isNotEmpty(trade.getSendCouponCodeIds())) {
            oldReturnOrder.setSendCouponCodeIds(trade.getSendCouponCodeIds());
        }

        //退单：填充订单类型
        oldReturnOrder.setActivityType(trade.getActivityType());
        String rid = returnOrderProvider.add(ReturnOrderAddRequest.builder().returnOrder(oldReturnOrder)
                .operator(commonUtil.getOperator()).build()).getContext().getReturnOrderId();
        returnOrderProvider.deleteTransfer(ReturnOrderTransferDeleteRequest.builder().userId(userId).build());

        if (CollectionUtils.isNotEmpty(trade.getSendCouponCodeIds())) {
            couponCodeProvider.delByCustomerIdAndCouponIds(CouponCodeDeleteRequest.builder()
                    .customerId(trade.getBuyer().getId()).couponCodeIds(trade.getSendCouponCodeIds()).build());
        }
        operateLogMQUtil.convertAndSend("退货", "创建退单", "创建退单：退单id" + (StringUtils.isNotEmpty(rid) ? rid : ""));
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
        if (CollectionUtils.isNotEmpty(trade.getSendCouponCodeIds())) {
            returnOrder.setSendCouponCodeIds(trade.getSendCouponCodeIds());
        }


        if (Objects.nonNull(trade.getReturnCoin())) {
            Map<String, BigDecimal> returnCoinMap = trade.getTradeItems().stream().filter(o -> Objects.nonNull(o.getReturnCoin())).collect(Collectors.toMap(TradeItemVO::getSkuId, TradeItemVO::getReturnCoin, (o1, o2) -> o1));
            BigDecimal returnCoin = returnOrder.getReturnItems().stream().map(o -> {
                if (Objects.nonNull(returnCoinMap.get(o.getSkuId()))) {
                    return returnCoinMap.get(o.getSkuId());
                } else {
                    return BigDecimal.ZERO;
                }
            }).reduce(BigDecimal.ZERO, BigDecimal::add);
            if (returnCoin.compareTo(BigDecimal.ZERO) > 0) {
                CustomerWalletVO customerWalletVO = walletQueryProvider.getBalanceByCustomerId(WalletByCustomerIdQueryRequest.builder().customerId(trade.getBuyer().getId()).build())
                        .getContext().getCustomerWalletVO();
                if (returnCoin.compareTo(customerWalletVO.getBalance()) > 0) {
                    throw new SbcRuntimeException("K-050601");
                }
            }

        }

        //退单：填充订单类型
        returnOrder.setActivityType(trade.getActivityType());
        String rid = returnOrderProvider.add(ReturnOrderAddRequest.builder().returnOrder(returnOrder)
                    .operator(commonUtil.getOperator()).build()).getContext().getReturnOrderId();

        if (CollectionUtils.isNotEmpty(trade.getSendCouponCodeIds())) {
            couponCodeProvider.delByCustomerIdAndCouponIds(CouponCodeDeleteRequest.builder()
                    .customerId(trade.getBuyer().getId()).couponCodeIds(trade.getSendCouponCodeIds()).build());
        }
        operateLogMQUtil.convertAndSend("退货", "创建退款单", "创建退款单：退单id" + (StringUtils.isNotEmpty(rid) ? rid : ""));
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
        operateLogMQUtil.convertAndSend("退货", "创建退单快照", "创建退单快照：退单号" + (Objects.nonNull(returnOrder) ? returnOrder.getId() : ""));
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

        if(CollectionUtils.isNotEmpty(trade.getSendCouponCodeIds())) {
            List<CouponCodeDTO> couponCodeList = couponCodeQueryProvider.findCouponsByCouponCodeIds(CouponsByCodeIdsRequest
                    .builder().couponCodeIds(trade.getSendCouponCodeIds()).build()).getContext().getCouponCodeList();
            log.info("=================》退单校验是否赠券，couponCodeList：：：{}",couponCodeList);
            boolean useStatus = couponCodeList.stream().anyMatch(coupon -> coupon.getUseStatus().equals(DefaultFlag.YES));
            log.info("=================》使用状态，useStatus：：：{}",useStatus);
            if (useStatus) {
                throw new SbcRuntimeException("K-050320");
            }
        }

        /*if (Objects.nonNull(trade.getReturnCoin())) {
            CustomerWalletVO customerWalletVO = walletQueryProvider.getBalanceByCustomerId(WalletByCustomerIdQueryRequest.builder().customerId(trade.getBuyer().getId()).build())
                    .getContext().getCustomerWalletVO();
            if (trade.getReturnCoin().compareTo(customerWalletVO.getBalance()) > 0) {
                throw new SbcRuntimeException("K-050601");
            }
        }*/

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
