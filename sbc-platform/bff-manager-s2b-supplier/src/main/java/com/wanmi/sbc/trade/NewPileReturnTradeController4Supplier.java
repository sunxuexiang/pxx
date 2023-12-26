package com.wanmi.sbc.trade;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.aop.EmployeeCheck;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.enums.Platform;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.request.employee.EmployeeByCompanyIdRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeListRequest;
import com.wanmi.sbc.customer.bean.vo.EmployeeListVO;
import com.wanmi.sbc.goods.api.provider.goodstobeevaluate.GoodsTobeEvaluateSaveProvider;
import com.wanmi.sbc.goods.api.provider.storetobeevaluate.StoreTobeEvaluateSaveProvider;
import com.wanmi.sbc.order.api.provider.ares.AresProvider;
import com.wanmi.sbc.order.api.provider.payorder.PayOrderQueryProvider;
import com.wanmi.sbc.order.api.provider.refund.RefundOrderQueryProvider;
import com.wanmi.sbc.order.api.provider.trade.newPileTrade.NewPileReturnOrderProvider;
import com.wanmi.sbc.order.api.provider.trade.newPileTrade.NewPileTradeProvider;
import com.wanmi.sbc.order.api.request.refund.RefundOrderByReturnOrderCodeRequest;
import com.wanmi.sbc.order.api.request.refund.RefundOrderByReturnOrderNoRequest;
import com.wanmi.sbc.order.api.request.returnorder.*;
import com.wanmi.sbc.order.api.request.trade.TradeCancelRequest;
import com.wanmi.sbc.order.api.request.trade.TradeGetByIdRequest;
import com.wanmi.sbc.order.api.request.trade.newpile.RefreshReturnedOrderRequest;
import com.wanmi.sbc.order.api.response.refund.RefundOrderByReturnCodeResponse;
import com.wanmi.sbc.order.api.response.refund.RefundOrderByReturnOrderNoResponse;
import com.wanmi.sbc.order.api.response.returnorder.NewPileReturnOrderByIdResponse;
import com.wanmi.sbc.order.api.response.trade.NewPileTradeGetByIdResponse;
import com.wanmi.sbc.order.bean.dto.*;
import com.wanmi.sbc.order.bean.enums.DeliverStatus;
import com.wanmi.sbc.order.bean.enums.NewPileReturnFlowState;
import com.wanmi.sbc.order.bean.vo.*;
import com.wanmi.sbc.pay.api.provider.PayQueryProvider;
import com.wanmi.sbc.returnorder.request.ReturnExportRequest;
import com.wanmi.sbc.returnorder.request.ReturnOfflineRefundRequest;
import com.wanmi.sbc.returnorder.service.ReturnExportService;
import com.wanmi.sbc.setting.api.provider.AuditQueryProvider;
import com.wanmi.sbc.setting.api.request.TradeConfigGetByTypeRequest;
import com.wanmi.sbc.setting.api.response.TradeConfigGetByTypeResponse;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
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
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description: 囤货退单api
 * @author: jiangxin
 * @create: 2021-09-29 10:04
 */
@Api(tags = "NewPileReturnTradeController", description = "囤货退单api")
@RestController
@RequestMapping("/supplier/newPileReturnTrade")
public class NewPileReturnTradeController4Supplier {

    @Autowired
    private NewPileReturnOrderProvider newPileReturnOrderProvider;

    @Autowired
    private NewPileTradeProvider newPileTradeProvider;

    @Autowired
    private ReturnExportService returnExportService;

    @Autowired
    private PayQueryProvider payQueryProvider;

    @Autowired
    private RefundOrderQueryProvider refundOrderQueryProvider;

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
    private PayOrderQueryProvider payOrderQueryProvider;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AresProvider aresProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    /**
     * 导出文件名后的时间后缀
     */
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

    /**
     * 分页查询 from ES
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "分页查询 from ES")
    @EmployeeCheck
    @RequestMapping(value = "/page",method = RequestMethod.POST)
    public BaseResponse<MicroServicePage<ReturnOrderVO>> page(@RequestBody NewPileReturnOrderPageRequest request) {
        request.setCompanyInfoId(commonUtil.getCompanyInfoId());
        MicroServicePage<ReturnOrderVO> page = newPileReturnOrderProvider.page(request).getContext().getReturnOrderPage();
        page.getContent().forEach(returnOrder -> {
            RefundOrderByReturnOrderNoResponse refundOrderByReturnCodeResponse = refundOrderQueryProvider.getByReturnOrderNo(new RefundOrderByReturnOrderNoRequest(returnOrder.getId())).getContext();
            if (Objects.nonNull(refundOrderByReturnCodeResponse)) {
                returnOrder.setRefundStatus(refundOrderByReturnCodeResponse.getRefundStatus());
            }
        });
        return BaseResponse.success(page);
    }

    @ApiOperation(value = "查询退单")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "rid", value = "退单Id", required = true)
    @RequestMapping(value = "/{rid}/detail", method = RequestMethod.GET)
    public BaseResponse<NewPileReturnOrderByIdResponse> findById(@PathVariable String rid) {
        NewPileReturnOrderByIdResponse returnOrder = newPileReturnOrderProvider.getById(ReturnOrderByIdRequest.builder().rid(rid)
                .build()).getContext();
        String accountName = employeeQueryProvider.getByCompanyId(
                EmployeeByCompanyIdRequest.builder().companyInfoId(returnOrder.getCompany().getCompanyInfoId()).build()
        ).getContext().getAccountName();
        returnOrder.getCompany().setAccountName(accountName);
        // 根据订单号查询下单数量
        BaseResponse<NewPileTradeGetByIdResponse> newPileTradeProviderById = newPileTradeProvider.getById(TradeGetByIdRequest.builder().tid(returnOrder.getTid()).build());
        if(Objects.nonNull(newPileTradeProviderById) && Objects.nonNull(newPileTradeProviderById.getContext())){
            NewPileTradeVO tradeVO = newPileTradeProviderById.getContext().getTradeVO();
            Map<String,List<TradeItemVO>> tradeItemsMap = tradeVO.getTradeItems().stream().collect(Collectors.groupingBy(TradeItemVO::getSkuId));
            returnOrder.getReturnItems().forEach(returnItemVO -> {
                Long reduce = tradeItemsMap.get(returnItemVO.getSkuId()).stream().map(item -> item.getNum()).reduce(0l, (a, b) -> a + b);
                returnItemVO.setByNum(reduce);
            });
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
                newPileReturnOrderProvider.listByCondition(conditionRequest).getContext().getReturnOrderList();
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
        //操作日志记录
        operateLogMQUtil.convertAndSend("囤货退单", "导出退单", "操作成功");
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
                                                      @RequestBody @Valid ReturnOfflineRefundRequest request) {
        NewPileReturnOrderByIdResponse returnOrder = newPileReturnOrderProvider.getById(ReturnOrderByIdRequest.builder().rid(rid)
                .build()).getContext();
        if (!NewPileReturnFlowState.AUDIT.equals(returnOrder.getReturnFlowState())) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "囤货退单状态已变化，请核实！");
        }
        if (returnOrder.getFinancialRefundFlag()) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "囤货退单已进入账务退款阶段，请核实！");
        }

        //客户账号
        ReturnCustomerAccountDTO customerAccount = null;
        if (returnOrder.getReturnPrice().getTotalPrice().compareTo(request.getActualReturnPrice()) == -1) {
            throw new SbcRuntimeException("K-050132", new Object[]{returnOrder.getReturnPrice().getTotalPrice()});
        }
        if (Objects.equals(request.getCustomerAccountId(), "0")) {
            customerAccount = new ReturnCustomerAccountDTO();
            customerAccount.setCustomerAccountName(request.getCustomerAccountName());
            customerAccount.setCustomerBankName(request.getCustomerBankName());
            customerAccount.setCustomerAccountNo(request.getCustomerAccountNo());
            customerAccount.setCustomerId(request.getCustomerId());
        }
        //退款流水
        RefundBillDTO refundBill = new RefundBillDTO();
        refundBill.setActualReturnPrice(request.getActualReturnPrice());
        refundBill.setActualReturnPoints(request.getActualReturnPoints());
        refundBill.setRefundComment(request.getRefundComment());
        // 客户账号
        refundBill.setCustomerAccountId(request.getCustomerAccountId());
        refundBill.setCreateTime(StringUtils.isNotEmpty(request.getCreateTime()) ? DateUtil.parseDate(request.getCreateTime()) :
                LocalDateTime.now());

        newPileReturnOrderProvider.offlineRefundForSupplier(
                ReturnOrderOfflineRefundForSupplierRequest.builder()
                        .rid(rid)
                        .customerAccount(customerAccount)
                        .refundBill(KsBeanUtil.convert(refundBill, RefundBillDTO.class))
                        .operator(commonUtil.getOperator())
                        .build());

        //操作日志记录
        operateLogMQUtil.convertAndSend("客服", "确认退款", "确认退款：退单编号" + returnOrder.getId());

        return ResponseEntity.ok(BaseResponse.SUCCESSFUL());
    }

    //TODO:
    //
    /**
     * 商家端取消订单
     */
    @ApiOperation(value = "取消订单")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单ID", required = true)
    @RequestMapping(value = "/cancel/{tid}", method = RequestMethod.GET)
    @LcnTransaction
    public BaseResponse cancel(@PathVariable(value = "tid") String tid) {
        Operator operator = commonUtil.getOperator();
        TradeCancelRequest tradeCancelRequest = TradeCancelRequest.builder()
                .tid(tid).operator(operator).build();
        try {
            newPileTradeProvider.cancel(tradeCancelRequest);
            //操作日志记录
            operateLogMQUtil.convertAndSend("囤货退单", "商家端取消订单", "操作成功");
            return BaseResponse.SUCCESSFUL();
        } catch (SbcRuntimeException e) {
            // TODO 待定状态码 ，如果是已支付的情况下走退货退款流程，且调用wms取消订单接口
            if("K-050202".equals(e.getErrorCode())){
                //检验定点是否支持退货退款
                verifyIsReturnable(tid);
                ReturnOrderDTO returnOrder = new ReturnOrderDTO();
                NewPileTradeVO trade =
                        newPileTradeProvider.getById(TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO();
                returnOrder.setCompany(CompanyDTO.builder().companyInfoId(trade.getSupplier().getSupplierId())
                        .companyCode(trade.getSupplier().getSupplierCode()).supplierName(trade.getSupplier().getSupplierName())
                        .storeId(trade.getSupplier().getStoreId()).storeName(trade.getSupplier().getStoreName())
                        .companyType(trade.getSupplier().getIsSelf() ? CompanyType.SUPPLIER : CompanyType.PLATFORM)
                        .build());

                //封装退货退款基本信息
                returnOrder.setTid(tid);
                returnOrder.setChannelType(trade.getChannelType());
                returnOrder.setDistributorId(trade.getDistributorId());
                returnOrder.setInviteeId(trade.getInviteeId());
                returnOrder.setShopName(trade.getShopName());
                returnOrder.setDistributorName(trade.getDistributorName());
                returnOrder.setDistributeItems(trade.getDistributeItems());

                List<ReturnItemDTO> returnItems = KsBeanUtil.convert(trade.getTradeItems(), ReturnItemDTO.class);
                returnOrder.setReturnItems(returnItems);
                TradePriceVO tradePrice = trade.getTradePrice();
                ReturnPriceDTO returnPriceDTO = new ReturnPriceDTO();
                //金额相关
                returnPriceDTO.setTotalPrice(tradePrice.getTotalPrice());
                returnPriceDTO.setApplyStatus(false);
                returnPriceDTO.setApplyPrice(BigDecimal.ZERO);

                returnOrder.setReturnPrice(returnPriceDTO);
                returnOrder.setDescription("供应商商家取消订单");
                returnOrder.setRejectReason("{4: 0}");
                returnOrder.setWareId(trade.getWareId());

                newPileReturnOrderProvider.add(ReturnOrderAddRequest.builder().returnOrder(returnOrder)
                        .operator(commonUtil.getOperator()).build()).getContext().getReturnOrderId();
                //操作日志记录
                operateLogMQUtil.convertAndSend("商家端取消订单", "异常块处理逻辑", "操作成功");

            }
            return BaseResponse.SUCCESSFUL();
        }

    }

    /**
     * 取消订单退货退款的数据校验
     *
     * @param tid
     */
    private void verifyIsReturnable(String tid) {
        NewPileTradeVO trade =
                newPileTradeProvider.getById(TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO();
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
       /* //校验wms是否符合要求取消订单
        tradeProvider.checkWms(TradeCheckWmsRequest.builder().tradeVO(trade).build());*/
    }

    @RequestMapping(value = "/refreshReturnedOrder", method = RequestMethod.POST)
    public BaseResponse refreshReturnedOrder(@RequestBody RefreshReturnedOrderRequest request) {
        String token = HttpUtil.getRequest().getHeader("token");
        if (Objects.equals(token, "ae456f35-9519-4ccb-8808-d281123871e2")) {
            newPileReturnOrderProvider.doRefreshReturnOrder(request);
            //操作日志记录
            operateLogMQUtil.convertAndSend("商家端取消订单", "刷新操作", "操作成功");
            return BaseResponse.SUCCESSFUL();
        }
        return BaseResponse.success("false");
    }
}
