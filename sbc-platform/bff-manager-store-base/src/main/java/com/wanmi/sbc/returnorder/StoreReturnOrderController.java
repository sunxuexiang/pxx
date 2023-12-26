package com.wanmi.sbc.returnorder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.account.api.provider.wallet.CustomerWalletQueryProvider;
import com.wanmi.sbc.account.api.request.wallet.WalletByCustomerIdQueryRequest;
import com.wanmi.sbc.account.bean.enums.PayOrderStatus;
import com.wanmi.sbc.account.bean.vo.CustomerWalletVO;
import com.wanmi.sbc.aop.EmployeeCheck;
import com.wanmi.sbc.common.annotation.MultiSubmit;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.enums.Platform;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.company.CompanyInfoQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.company.CompanyInfoByIdRequest;
import com.wanmi.sbc.customer.api.request.store.NoDeleteStoreByIdRequest;
import com.wanmi.sbc.customer.bean.vo.CompanyInfoVO;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseQueryProvider;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseListRequest;
import com.wanmi.sbc.goods.api.response.warehouse.WareHouseListResponse;
import com.wanmi.sbc.order.api.provider.payorder.PayOrderQueryProvider;
import com.wanmi.sbc.order.api.provider.refund.RefundOrderQueryProvider;
import com.wanmi.sbc.order.api.provider.returnorder.ReturnOrderProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.order.api.request.payorder.FindPayOrderRequest;
import com.wanmi.sbc.order.api.request.refund.RefundOrderByReturnOrderNoRequest;
import com.wanmi.sbc.order.api.request.returnorder.ReturnOrderAddRequest;
import com.wanmi.sbc.order.api.request.returnorder.ReturnOrderOfflineRefundForSupplierRequest;
import com.wanmi.sbc.order.api.request.returnorder.ReturnOrderOnlineModifyPriceRequest;
import com.wanmi.sbc.order.api.request.trade.TradeGetByIdRequest;
import com.wanmi.sbc.order.api.response.payorder.FindPayOrderResponse;
import com.wanmi.sbc.order.api.response.refund.RefundOrderByReturnOrderNoResponse;
import com.wanmi.sbc.order.api.response.returnorder.ReturnOrderAddResponse;
import com.wanmi.sbc.order.bean.dto.CompanyDTO;
import com.wanmi.sbc.order.bean.dto.RefundBillDTO;
import com.wanmi.sbc.order.bean.dto.ReturnCustomerAccountDTO;
import com.wanmi.sbc.order.bean.dto.ReturnOrderDTO;
import com.wanmi.sbc.order.bean.enums.ReturnFlowState;
import com.wanmi.sbc.returnorder.bean.enums.ReturnType;
import com.wanmi.sbc.order.bean.vo.TradeItemVO;
import com.wanmi.sbc.order.bean.vo.TradeVO;
import com.wanmi.sbc.returnorder.api.provider.returnorder.ReturnOrderQueryProvider;
import com.wanmi.sbc.returnorder.api.request.returnorder.ReturnOrderByIdRequest;
import com.wanmi.sbc.returnorder.api.request.returnorder.ReturnOrderListByTidRequest;
import com.wanmi.sbc.returnorder.api.request.returnorder.ReturnOrderPageRequest;
import com.wanmi.sbc.returnorder.api.request.returnorder.ReturnOrderQueryRefundPriceRequest;
import com.wanmi.sbc.returnorder.bean.vo.ReturnItemVO;
import com.wanmi.sbc.returnorder.bean.vo.ReturnOrderVO;
import com.wanmi.sbc.returnorder.request.ReturnOfflineRefundRequest;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 退货
 * Created by sunkun on 2017/11/23.
 */
@Api(tags = "StoreReturnOrderController", description = "退货服务API")
@RestController
@RequestMapping("/return")
@Validated
@Slf4j
public class StoreReturnOrderController {

    @Autowired
    private ReturnOrderProvider returnOrderProvider;

    @Autowired
    private ReturnOrderQueryProvider returnOrderQueryProvider;

    @Autowired
    private CompanyInfoQueryProvider companyInfoQueryProvider;

    @Resource
    private StoreQueryProvider storeQueryProvider;

    @Resource
    private CommonUtil commonUtil;

    @Autowired
    private TradeQueryProvider tradeQueryProvider;

    @Resource
    private PayOrderQueryProvider payOrderQueryProvider;

    @Resource
    private RefundOrderQueryProvider refundOrderQueryProvider;

    @Autowired
    private AuditQueryProvider auditQueryProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @Autowired
    private WareHouseQueryProvider wareHouseQueryProvider;


    @Autowired
    private CustomerWalletQueryProvider walletQueryProvider;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 分页查询 from ES
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "分页查询 from ES")
    @EmployeeCheck
    @RequestMapping(method = RequestMethod.POST)
    public BaseResponse<MicroServicePage<ReturnOrderVO>> page(@RequestBody ReturnOrderPageRequest request) {
        request.setCompanyInfoId(commonUtil.getCompanyInfoId());
        MicroServicePage<ReturnOrderVO> page = returnOrderQueryProvider.page(request).getContext().getReturnOrderPage();
        BaseResponse<WareHouseListResponse> list = wareHouseQueryProvider.list(new WareHouseListRequest());

        page.getContent().forEach(returnOrder -> {
            RefundOrderByReturnOrderNoResponse refundOrderByReturnCodeResponse = refundOrderQueryProvider.getByReturnOrderNo(new RefundOrderByReturnOrderNoRequest(returnOrder.getId())).getContext();
            if (Objects.nonNull(refundOrderByReturnCodeResponse)) {
                returnOrder.setRefundStatus(refundOrderByReturnCodeResponse.getRefundStatus());
            }
            if(Objects.nonNull(returnOrder.getWareId())){
                returnOrder.setWareName(list.getContext().getWareHouseVOList().stream().filter(wareHouse -> wareHouse.getWareId().equals(returnOrder.getWareId())).findFirst().get().getWareName());
            }
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
        });

        page.setContent(page.getContent().stream().filter(v->{
            return  !(StringUtils.isNotBlank(v.getSourceChannel())&&v.getSourceChannel().contains("chains"));
        }).collect(Collectors.toList()));


        return BaseResponse.success(page);
    }

    /**
     * 创建退单
     *
     * @param returnOrder
     * @return
     */
    @ApiOperation(value = "创建退单")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @LcnTransaction
    @MultiSubmit
    public BaseResponse<ReturnOrderAddResponse> create(@RequestBody ReturnOrderDTO returnOrder) {
        log.info("StoreReturnOrderController.create tid:{}",returnOrder.getTid());
        CompanyInfoVO companyInfo = companyInfoQueryProvider.getCompanyInfoById(
                CompanyInfoByIdRequest.builder().companyInfoId(commonUtil.getCompanyInfoId()).build()
        ).getContext();
        if (Objects.isNull(companyInfo)) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        Operator operator = commonUtil.getOperator();
        StoreVO store = storeQueryProvider.getNoDeleteStoreById(new NoDeleteStoreByIdRequest(commonUtil.getStoreId())
        ).getContext().getStoreVO();
        if (Objects.isNull(store)) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        returnOrder.setCompany(CompanyDTO.builder().companyInfoId(companyInfo.getCompanyInfoId())
                .companyCode(companyInfo.getCompanyCode()).supplierName(companyInfo.getSupplierName())
                .storeId(commonUtil.getStoreId()).storeName(store.getStoreName()).companyType(store.getCompanyType()).build());
        TradeVO trade = tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(returnOrder.getTid()).build()).getContext().getTradeVO();
        returnOrder.setChannelType(trade.getChannelType());
        returnOrder.setDistributorId(trade.getDistributorId());
        returnOrder.setInviteeId(trade.getInviteeId());
        returnOrder.setShopName(trade.getShopName());
        returnOrder.setDistributorName(trade.getDistributorName());
        returnOrder.setDistributeItems(trade.getDistributeItems());
        //设置提货单类型
        returnOrder.setActivityType(trade.getActivityType());

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

        //forceRefund为1，是代客退单权限最高，拣货也可以退单
        BaseResponse<ReturnOrderAddResponse> response = returnOrderProvider.add(
                ReturnOrderAddRequest.builder().returnOrder(returnOrder).operator(operator).forceRefund(1).build());
        operateLogMQUtil.convertAndSend(
                "订单", "代客退单", "退单号" + response.getContext().getReturnOrderId());
        return response;
    }


    /**
     * 线下退款
     *
     * @param rid
     * @param request
     * @return
     */
    @ApiOperation(value = "线下退款")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "rid", value = "退单Id", required = true)
    @RequestMapping(value = "/refund/{rid}/offline", method = RequestMethod.POST)
    @LcnTransaction
    @MultiSubmit
    public BaseResponse refundOffline(@PathVariable String rid,
                                      @RequestBody @Valid ReturnOfflineRefundRequest request) {
        //合并退款
        if(Objects.nonNull(request.getTids())){
            request.getTids().forEach(tid->{
                List<ReturnOrderVO> returnOrderVOS= returnOrderQueryProvider.listByTid(ReturnOrderListByTidRequest.builder().tid(tid)
                        .build()).getContext().getReturnOrderList();
                /*returnOrderVOS.forEach(returnOrderVO -> {
                    this.refundOfflineAll(returnOrderVO.getId(),request);
                });*/
                if(returnOrderVOS.size()>0){
                    log.info("退单---"+returnOrderVOS.get(0).getId());
                    this.refundOfflineAll(returnOrderVOS.get(0).getId(),request);
                }
            });
            return BaseResponse.SUCCESSFUL();
        }
        
        
        //客户账号
        ReturnCustomerAccountDTO customerAccount = null;
        ReturnOrderVO returnOrder = returnOrderQueryProvider.getById(ReturnOrderByIdRequest.builder().rid(rid)
                .build()).getContext();
        if (returnOrder.getReturnPrice().getTotalPrice().compareTo(request.getActualReturnPrice()) == -1) {
            throw new SbcRuntimeException("K-050132", new Object[]{returnOrder.getReturnPrice().getTotalPrice()});
        }

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
            }
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
        operateLogMQUtil.convertAndSend(
                "订单", "线下退款", "退单Id" + rid);
        return returnOrderProvider.offlineRefundForSupplier(ReturnOrderOfflineRefundForSupplierRequest.builder().rid(rid)
                .customerAccount(customerAccount).refundBill(refundBill).operator(commonUtil.getOperator()).build());
    }


    public void refundOfflineAll(String rid, ReturnOfflineRefundRequest request){


        //客户账号
        ReturnCustomerAccountDTO customerAccount = null;
        ReturnOrderVO returnOrder = returnOrderQueryProvider.getById(ReturnOrderByIdRequest.builder().rid(rid)
                .build()).getContext();
        if (returnOrder.getReturnPrice().getTotalPrice().compareTo(request.getActualReturnPrice()) == -1) {
            throw new SbcRuntimeException("K-050132", new Object[]{returnOrder.getReturnPrice().getTotalPrice()});
        }

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
            }
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
         returnOrderProvider.offlineRefundForSupplier(ReturnOrderOfflineRefundForSupplierRequest.builder().rid(rid)
                .customerAccount(customerAccount).refundBill(refundBill).operator(commonUtil.getOperator()).build());
    }

    /**
     * 商家退款申请(修改退单价格新增流水)
     *
     * @param rid
     * @param request
     * @return
     */
    @ApiOperation(value = "商家退款申请(修改退单价格新增流水)")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "rid", value = "退单Id", required = true)
    @RequestMapping(value = "/edit/price/{rid}", method = RequestMethod.POST)
    @LcnTransaction
    @MultiSubmit
    public BaseResponse onlineEditPrice(@PathVariable String rid, @RequestBody @Valid ReturnOfflineRefundRequest request) {
        String returnRedisKey = "onlineEditPrice:"+rid;
        RLock rLock = redissonClient.getLock(returnRedisKey);
        try {
            boolean lockResult= rLock.tryLock(0,30, TimeUnit.SECONDS);
            if(!lockResult){
                return BaseResponse.error("处理中，请稍后操作");
            }
        BigDecimal refundPrice = returnOrderQueryProvider.queryRefundPrice(ReturnOrderQueryRefundPriceRequest.builder()
                .rid(rid).build()).getContext().getRefundPrice();
        if (refundPrice.compareTo(request.getActualReturnPrice()) == -1) {
            throw new SbcRuntimeException("K-050132", new Object[]{refundPrice});
        }
        ReturnOrderVO returnOrder = returnOrderQueryProvider.getById(ReturnOrderByIdRequest.builder().rid(rid)
                .build()).getContext();
        operateLogMQUtil.convertAndSend(
                "订单", "商家退款申请(修改退单价格新增流水)", "退单Id" + rid);

        BaseResponse response = returnOrderProvider.onlineModifyPrice(ReturnOrderOnlineModifyPriceRequest.builder()
                .returnOrder(KsBeanUtil.convert(returnOrder, ReturnOrderDTO.class))
                .refundComment(request.getRefundComment())
                .actualReturnPrice(request.getActualReturnPrice())
                .actualReturnPoints(request.getActualReturnPoints())
                .operator(commonUtil.getOperator()).build());

        if (Objects.equals(returnOrder.getReturnType(), ReturnType.REFUND)) {
            // 营运后台退款
            /*BaseResponse<Object> res = returnOrderProvider.refundOnlineByTid(ReturnOrderOnlineRefundByTidRequest.builder().returnOrderCode(rid)
                    .operator(null).wmsStats(false).build());
            Object data = res.getContext();
            if (Objects.isNull(data)) {
                //无返回信息，追踪退单退款状态
                ReturnFlowState state = returnOrderQueryProvider.getById(ReturnOrderByIdRequest.builder().rid(rid).build()).getContext().getReturnFlowState();
                if (state.equals(ReturnFlowState.REFUND_FAILED)) {
                    return BaseResponse.error("运营后台退款失败");
                }
            }*/
            // 延迟任务 自动退款
            returnOrderProvider.supplierAutoRefund(rid);
        }

        return response;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if(rLock.isLocked()) {
                rLock.unlock();
            }
        }
    }

    /**
     * 是否可创建退单
     *
     * @return
     */
    @ApiOperation(value = "是否可创建退单")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String",
                    name = "tid", value = "退单Id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Boolean",
                    name = "isRefund", value = "是否可以退货", required = true)
    })
    @RequestMapping(value = "/returnable/{tid}/{isRefund}", method = RequestMethod.GET)
    public BaseResponse isReturnable(@PathVariable String tid, @PathVariable Boolean isRefund) {
        BaseResponse<FindPayOrderResponse> findPayOrderResponseBaseResponse = payOrderQueryProvider.findPayOrder(FindPayOrderRequest.builder().value(tid).build());
        FindPayOrderResponse payOrder = findPayOrderResponseBaseResponse.getContext();
        if (Objects.isNull(payOrder) || Objects.isNull(payOrder.getPayOrderStatus()) || payOrder.getPayOrderStatus() != PayOrderStatus.PAYED) {
            throw new SbcRuntimeException("K-050105");
        }
//        verifyIsReturnable(tid, isRefund);
        return BaseResponse.SUCCESSFUL();
    }


    /**
     * 校验是否可退
     *
     * @param tid
     */
    private void verifyIsReturnable(String tid, boolean isRefund) {
        TradeConfigGetByTypeRequest request = new TradeConfigGetByTypeRequest();
        request.setConfigType(ConfigType.ORDER_SETTING_APPLY_REFUND);

        TradeConfigGetByTypeResponse config = auditQueryProvider.getTradeConfigByType(request).getContext();
        TradeVO trade = tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO();
        if (!isRefund) {
            if (config.getStatus() == 0) {
                throw new SbcRuntimeException("K-050208");
            }
            JSONObject content = JSON.parseObject(config.getContext());
            Integer day = content.getObject("day", Integer.class);

            if (Objects.isNull(trade.getTradeState().getEndTime())) {
                throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
            }

            if (trade.getTradeState().getEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() < LocalDateTime.now().minusDays(day).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()) {
                throw new SbcRuntimeException("K-050208");
            }
        }
    }

    /**
     * 商家退款申请(修改退单价格新增流水)
     *
     * @param rid
     * @return
     */
    @ApiOperation(value = "商家退款申请(修改退单价格新增流水)")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "rid", value = "退单Id", required = true)
    @RequestMapping(value = "/edit/test/{rid}", method = RequestMethod.POST)
    @LcnTransaction
    @MultiSubmit
    public BaseResponse testOnlineEditPrice(@PathVariable String rid) {
        log.info("1111111111111111111111111111========={}",rid);
        return BaseResponse.SUCCESSFUL();
    }

}
