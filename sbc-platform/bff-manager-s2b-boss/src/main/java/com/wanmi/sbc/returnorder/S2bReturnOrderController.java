package com.wanmi.sbc.returnorder;

import com.alibaba.fastjson.JSONObject;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.google.gson.Gson;
import com.wanmi.sbc.account.api.provider.wallet.CustomerWalletQueryProvider;
import com.wanmi.sbc.account.api.request.wallet.WalletByCustomerIdQueryRequest;
import com.wanmi.sbc.account.bean.enums.WalletDetailsType;
import com.wanmi.sbc.account.bean.vo.CustomerWalletVO;
import com.wanmi.sbc.aop.EmployeeCheck;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.AccountType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.account.CustomerAccountQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.account.CustomerAccountRequest;
import com.wanmi.sbc.customer.api.response.account.CustomerAccountResponse;
import com.wanmi.sbc.customer.bean.vo.CustomerAccountVO;
import com.wanmi.sbc.goods.api.provider.storetobeevaluate.StoreTobeEvaluateQueryProvider;
import com.wanmi.sbc.goods.api.provider.storetobeevaluate.StoreTobeEvaluateSaveProvider;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseQueryProvider;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseListRequest;
import com.wanmi.sbc.goods.api.response.warehouse.WareHouseListResponse;
import com.wanmi.sbc.marketing.api.provider.coupon.CoinActivityProvider;
import com.wanmi.sbc.marketing.bean.dto.CoinActivityRecordDetailDto;
import com.wanmi.sbc.marketing.bean.dto.CoinActivityRecordDto;
import com.wanmi.sbc.order.api.provider.ares.AresProvider;
import com.wanmi.sbc.order.api.provider.refund.RefundOrderQueryProvider;
import com.wanmi.sbc.order.api.provider.returnorder.ReturnOrderProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.order.api.request.areas.ReturnOrderRequest;
import com.wanmi.sbc.order.api.request.refund.RefundOrderByReturnOrderCodeRequest;
import com.wanmi.sbc.returnorder.api.request.returnorder.ReturnOrderListByTidRequest;
import com.wanmi.sbc.order.api.request.returnorder.ReturnOrderOfflineRefundForBossRequest;
import com.wanmi.sbc.order.api.request.trade.TradeGetByIdManagerRequest;
import com.wanmi.sbc.order.api.request.trade.TradeGetByIdRequest;
import com.wanmi.sbc.order.api.response.refund.RefundOrderByReturnCodeResponse;
import com.wanmi.sbc.order.bean.dto.RefundBillDTO;
import com.wanmi.sbc.order.bean.dto.ReturnOrderDTO;
import com.wanmi.sbc.order.bean.vo.RefundBillVO;
import com.wanmi.sbc.order.bean.vo.TradeVO;
import com.wanmi.sbc.order.bean.vo.TradeItemVO;
import com.wanmi.sbc.returnorder.api.provider.returnorder.ReturnOrderQueryProvider;
import com.wanmi.sbc.returnorder.api.request.returnorder.ReturnOrderByIdRequest;
import com.wanmi.sbc.returnorder.api.request.returnorder.ReturnOrderPageRequest;
import com.wanmi.sbc.returnorder.api.response.returnorder.ReturnOrderByIdResponse;
import com.wanmi.sbc.returnorder.bean.vo.ReturnItemVO;
import com.wanmi.sbc.returnorder.bean.vo.ReturnOrderVO;
import com.wanmi.sbc.returnorder.request.ReturnBatchOfflineRefundRequest;
import com.wanmi.sbc.returnorder.request.ReturnOfflineRefundRequest;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.HttpUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import com.wanmi.sbc.wallet.api.provider.wallet.CustomerWalletProvider;
import com.wanmi.sbc.wallet.api.request.wallet.CustomerWalletOrderByRequest;
import com.wanmi.sbc.wallet.bean.enums.WalletRecordTradeType;
import com.wanmi.sbc.wallet.bean.vo.WalletRecordVO;
import io.jsonwebtoken.lang.Assert;
import io.swagger.annotations.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 退单
 * Created by sunkun on 2017/11/17.
 */
@RestController
@RequestMapping("/return")
@Slf4j
@Api(description = "退单", tags = "S2bReturnOrderController")
public class S2bReturnOrderController {
    @Autowired
    private AresProvider aresProvider;

    @Autowired
    private ReturnOrderProvider returnOrderProvider;

    @Autowired
    private ReturnOrderQueryProvider returnOrderQueryProvider;

    @Resource
    private CommonUtil commonUtil;

    @Resource
    private RefundOrderQueryProvider refundOrderQueryProvider;

    @Resource
    private CustomerAccountQueryProvider customerAccountQueryProvider;

    @Autowired
    private StoreTobeEvaluateQueryProvider storeTobeEvaluateQueryProvider;

    @Autowired
    private StoreTobeEvaluateSaveProvider storeTobeEvaluateSaveProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @Autowired
    private TradeQueryProvider tradeQueryProvider;

    @Autowired
    private WareHouseQueryProvider wareHouseQueryProvider;

    @Value("${wms.callback_url}")
    private String wmsCallbackUrl;

    @Value("${wms.api.flag}")
    private boolean wmsAPIFlag;

    @Autowired
    private CustomerWalletQueryProvider walletQueryProvider;

    @Autowired
    private CustomerWalletProvider customerWalletProvider;

    @Autowired
    private TradeProvider tradeProvider;

    @Autowired
    private CoinActivityProvider coinActivityProvider;

    @Autowired
    private StoreQueryProvider storeQueryProvider;

    @ApiOperation(value = "退单收货（调用s2b-wms）")
    @EmployeeCheck
    @RequestMapping(value = "/supplierReceive", method = RequestMethod.POST)
    public BaseResponse<Void> supplierReceive(@RequestBody SupplierReceiveRequest request) {
        log.info("wms启用状态：{}", wmsAPIFlag);
        if (wmsAPIFlag) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "WMS已启用，请从WMS进行退单收货！");
        }

        Assert.notNull(request.getRid(), "rid must not be null");
//        String url = "https://wms.test.7yaya.cn/receive";
//        String url = "http://127.0.0.1:8092/receive";
        log.info("退单收货 wmsCallbackUrl={},rid={}", wmsCallbackUrl, request.getRid());
//        String result = HttpUtil.doPost2(wmsCallbackUrl, JSONObject.parseObject(buildJsonStr(rid)));

        Map<String, String> yourHashMap = new Gson().fromJson(buildJsonStr(request.getRid()), HashMap.class);
        String result = HttpUtil.sendPost(wmsCallbackUrl, yourHashMap);
        log.info("退单收货 result={}", result);
        //{"Response":{"return":{"returnCode":"0001","returnDesc":"接口异常","returnFlag":0}}}

        JSONObject response = JSONObject.parseObject(result,JSONObject.class);
        JSONObject returnJsonObj = response.getJSONObject("Response").getJSONObject("return");
        String returnCode = returnJsonObj.getString("returnCode");
        if (!returnCode.equals("0000")) {
            return BaseResponse.error("请勿重复收货！");
        }
        //操作日志记录
        operateLogMQUtil.convertAndSend("财务", "退单收货", "退单：退单ID" + request.getRid());
        return BaseResponse.success(null);
    }

    private String buildJsonStr(String rid) {
        BaseResponse<ReturnOrderByIdResponse> resp = returnOrderQueryProvider.getById(ReturnOrderByIdRequest.builder().rid(rid).build());
        ReturnOrderByIdResponse context = resp.getContext();
        List<ReturnItemVO> returnItems = context.getReturnItems();

        String itemFormat = "{\\\"sku\\\":\\\"%s\\\",\\\"receivedQty\\\":\\\"%s\\\",\\\"lotatt01\\\":\\\"2021-12-04\\\",\\\"lotatt02\\\":\\\"\\\",\\\"lotatt03\\\":\\\"\\\"," +
                "\\\"lotatt04\\\":\\\"001\\\",\\\"lotatt05\\\":\\\"\\\",\\\"lotatt06\\\":\\\"N\\\",\\\"lotatt07\\\":\\\"\\\",\\\"lotatt08\\\":\\\"\\\"," +
                "\\\"lotatt09\\\":\\\"\\\",\\\"lotatt10\\\":\\\"\\\",\\\"lotatt11\\\":\\\"\\\",\\\"lotatt12\\\":\\\"\\\"," +
                "\\\"userDefine1\\\":\\\"\\\",\\\"userDefine2\\\":\\\"\\\",\\\"userDefine3\\\":\\\"\\\",\\\"userDefine4\\\":\\\"\\\",\\\"userDefine5\\\":\\\"\\\"," +
                "\\\"userDefine6\\\":\\\"\\\",\\\"lotatt13\\\":\\\"\\\",\\\"lotatt14\\\":\\\"\\\",\\\"lotatt15\\\":\\\"\\\",\\\"lotatt16\\\":\\\"\\\",\\\"lineNo\\\":\\\"6\\\"," +
                "\\\"referenceNo\\\":\\\"0\\\",\\\"lotatt17\\\":\\\"\\\",\\\"lotatt18\\\":\\\"\\\",\\\"lotatt19\\\":\\\"\\\",\\\"lotatt20\\\":\\\"\\\",\\\"lotatt21\\\":\\\"\\\"," +
                "\\\"lotatt22\\\":\\\"\\\",\\\"lotatt23\\\":\\\"\\\",\\\"lotatt24\\\":\\\"\\\",\\\"dedi06\\\":\\\"%s\\\"},";

        String dateNowStr = DateUtil.format(new Date(), DateUtil.FMT_TIME_1);

        StringBuilder detailBuilder = new StringBuilder();
        returnItems.forEach(item -> {
            detailBuilder.append(String.format(
                    itemFormat,
                    item.getErpSkuNo(),
                    item.getNum(),
                    Objects.nonNull(item.getDevanningId()) ? item.getDevanningId().toString() : null
            ));
        });

        String detailStr = StringUtils.removeEndIgnoreCase(detailBuilder.toString(), ",");

        String main = "{\n" +
                "\t\"appkey\": \"wm_1TyicBGs\",\n" +
                "\t\"apptoken\": \"e6da28b3c82848e1863d27e9b46c0323\",\n" +
                "\t\"client_customerid\": \"XYY\",\n" +
                "\t\"client_db\": \"json\",\n" +
                "\t\"data\": \"{\\\"xmldata\\\":{\\\"data\\\":{\\\"orderinfo\\\":[" +
                "{\\\"orderNo\\\":\\\"111\\\",\\\"orderType\\\":\\\"XSTHRK\\\",\\\"customerId\\\":\\\"XYY\\\"," +
                "\\\"warehouseId\\\":\\\"WH01\\\",\\\"userDefine1\\\":\\\"101360\\\",\\\"userDefine2\\\":\\\"0\\\",\\\"userDefine3\\\":\\\"\\\"," +
                "\\\"userDefine4\\\":\\\"\\\",\\\"userDefine5\\\":\\\"\\\",\\\"userDefine6\\\":\\\"\\\",\\\"userDefine7\\\":\\\"\\\"," +
                "\\\"userDefine8\\\":\\\"\\\",\\\"userDefine9\\\":\\\"\\\",\\\"userDefine10\\\":\\\"\\\"," +
                "\\\"details\\\":[" +
                "%s" +
                "],\\\"receivedTime\\\":\\\"%s\\\",\\\"docNo\\\":\\\"%s\\\"}]}}}\",\n" +
                "\t\"messageid\": \"1.0.0\",\n" +
                "\t\"method\": \"reception.get.receipt\",\n" +
                "\t\"sign\": \"OGIWMGFLNWNLOWYYZJHJMWQXMGYYYJY1NZJMZWU2MTU=\",\n" +
                "\t\"timestamp\": \"%s\"\n" +
                "}";

        return String.format(main, detailStr, dateNowStr, rid, dateNowStr);
    }


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
        // 是否自由查询
        final List<Long> selfManageStoreIds = mapSelfManageStoreIds();
        if (request.getSelfManage() != null){
            if (CollectionUtils.isEmpty(selfManageStoreIds)) {
                selfManageStoreIds.add(-100L);
            }
            if (Objects.equals(request.getSelfManage(), 1)) {
                request.setStoreIds(selfManageStoreIds);
            } else if (Objects.equals(request.getSelfManage(), 0)) {
                request.setNotStoreIds(selfManageStoreIds);
            }
        }

        MicroServicePage<ReturnOrderVO> returnOrderPage = returnOrderQueryProvider.page(request).getContext().getReturnOrderPage();
        BaseResponse<WareHouseListResponse> list = wareHouseQueryProvider.list(new WareHouseListRequest());

        returnOrderPage.getContent().forEach(returnOrder->{
            if(Objects.nonNull(returnOrder.getWareId())){
                returnOrder.setWareName(list.getContext().getWareHouseVOList().stream().filter(wareHouse -> wareHouse.getWareId().equals(returnOrder.getWareId())).findFirst().get().getWareName());
            }
            if(Objects.nonNull(returnOrder.getTid())) {
                TradeVO trade = tradeQueryProvider.getByIdManager(TradeGetByIdManagerRequest.builder().tid(returnOrder.getTid())
                        .storeId(commonUtil.getStoreId()).requestFrom(AccountType.s2bBoss)
                        .build()).getContext().getTradeVO();
                returnOrder.setTradeVO(KsBeanUtil.convert(trade, com.wanmi.sbc.returnorder.bean.vo.TradeVO.class));
            }
            if (null != returnOrder.getCompany() && selfManageStoreIds.contains(returnOrder.getCompany().getStoreId())) {
                returnOrder.setSelfManage(1);
            } else {
                returnOrder.setSelfManage(0);
            }
        });

        returnOrderPage.setContent(returnOrderPage.getContent().stream().filter(v->{
            return  !(StringUtils.isNotBlank(v.getSourceChannel())&&v.getSourceChannel().contains("chains"));
        }).collect(Collectors.toList()));

        return BaseResponse.success(returnOrderPage);
    }

    private List<Long> mapSelfManageStoreIds() {
        try {
            final BaseResponse<List<Long>> listBaseResponse = storeQueryProvider.listStoreIdsBySelfManage();
            final List<Long> context = listBaseResponse.getContext();
            if (CollectionUtils.isEmpty(context)) return new ArrayList<>();
            return context;
        } catch (Exception e) {
            log.error("查询自营商家异常", e);
            return new ArrayList<>();
        }
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
        //合并退款
        if(Objects.nonNull(request.getTids())){
            request.getTids().forEach(tid->{
                List<ReturnOrderVO> returnOrderVOS= returnOrderQueryProvider.listByTid(ReturnOrderListByTidRequest.builder().tid(tid)
                        .build()).getContext().getReturnOrderList();
                if(returnOrderVOS.size()>0){
                    log.info("退单---"+returnOrderVOS.get(0).getId());
                    this.refundOfflineAll(returnOrderVOS.get(0).getId(),request);
                }
            });
            return ResponseEntity.ok(BaseResponse.SUCCESSFUL());
        }
        ReturnOrderVO returnOrder = returnOrderQueryProvider.getById(ReturnOrderByIdRequest.builder().rid(rid)
                .build()).getContext();
        RefundOrderByReturnCodeResponse refundOrder = refundOrderQueryProvider.getByReturnOrderCode(new RefundOrderByReturnOrderCodeRequest(returnOrder.getId())).getContext();

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

                // 修改钱包
//                String buyerId = tradeVO.getBuyer().getId();
//                ModifyWalletBalanceForCoinActivityRequest modifyBalanceRequest = new ModifyWalletBalanceForCoinActivityRequest();
//                modifyBalanceRequest.setAmount(returnCoin);
//                modifyBalanceRequest.setRelationId(rid);
//                modifyBalanceRequest.setBuyerId(buyerId);
//                modifyBalanceRequest.setCustomerAccount(tradeVO.getBuyer().getAccount());
//                modifyBalanceRequest.setWalletDetailsType(WalletDetailsType.CANCEL_GOODS_RECHARGE);
//                modifyBalanceRequest.setBudgetType(BudgetType.EXPENDITURE);
//                customerWalletProvider.modifyWalletBalanceForCoin(modifyBalanceRequest);
                
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

//                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateUtil.FMT_TIME_3, Locale.UK);
//                String sendNo = "TZS" + LocalDateTime.now().format(dateTimeFormatter) + RandomStringUtils.randomNumeric(4);
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

                // 推送金蝶
//                CoinActivityPushKingdeeRequest kingdeeRequest = CoinActivityPushKingdeeRequest
//                        .builder()
//                        .tid(rid)
//                        .buyerAccount(tradeVO.getBuyer().getAccount())
//                        .applyPrice(returnCoin)
//                        .saleType(tradeVO.getSaleType())
//                        .sendNo(sendNo)
//                        .build();
//
//                tradeProvider.pushRefundOrderKingdeeForCoin(kingdeeRequest);
            }

        }

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
        returnOrderProvider.offlineRefundForBoss(ReturnOrderOfflineRefundForBossRequest.builder().rid(rid)
                .refundBill(KsBeanUtil.convert(refundBill, RefundBillDTO.class))
                .operator(commonUtil.getOperator()).tid(returnOrder.getTid()).build());

        ReturnOrderByIdResponse returnOrderByIdResponse = returnOrderQueryProvider.getById(ReturnOrderByIdRequest.builder().rid(rid).build()).getContext();

        ReturnOrderDTO requestDto = KsBeanUtil.convert(returnOrderByIdResponse, ReturnOrderDTO.class);

        ReturnOrderRequest providerrequest = new ReturnOrderRequest();

        providerrequest.setReturnOrderDTO(requestDto);

        aresProvider.returnOrder(providerrequest);




        //操作日志记录
        operateLogMQUtil.convertAndSend("财务", "确认退款", "确认退款：退单编号" + returnOrder.getId());

        return ResponseEntity.ok(BaseResponse.SUCCESSFUL());
    }

    public void refundOfflineAll(String rid, ReturnOfflineRefundRequest request){
        ReturnOrderVO returnOrder = returnOrderQueryProvider.getById(ReturnOrderByIdRequest.builder().rid(rid)
                .build()).getContext();
        RefundOrderByReturnCodeResponse refundOrder = refundOrderQueryProvider.getByReturnOrderCode(new RefundOrderByReturnOrderCodeRequest(returnOrder.getId())).getContext();

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

                // 修改钱包
//                String buyerId = tradeVO.getBuyer().getId();
//                ModifyWalletBalanceForCoinActivityRequest modifyBalanceRequest = new ModifyWalletBalanceForCoinActivityRequest();
//                modifyBalanceRequest.setAmount(returnCoin);
//                modifyBalanceRequest.setRelationId(rid);
//                modifyBalanceRequest.setBuyerId(buyerId);
//                modifyBalanceRequest.setCustomerAccount(tradeVO.getBuyer().getAccount());
//                modifyBalanceRequest.setWalletDetailsType(WalletDetailsType.CANCEL_GOODS_RECHARGE);
//                modifyBalanceRequest.setBudgetType(BudgetType.EXPENDITURE);
//                customerWalletProvider.modifyWalletBalanceForCoin(modifyBalanceRequest);
                
                Long storeId = tradeVO.getSupplier().getStoreId();
				String tradeRemark = WalletDetailsType.CANCEL_GOODS_RECHARGE.getDesc() + "-" + rid;
				String customerId = tradeVO.getBuyer().getId();
				CustomerWalletOrderByRequest orderByRequest = CustomerWalletOrderByRequest.builder()
						.customerId(customerId).storeId(storeId.toString()).balance(returnCoin)
						.relationOrderId(rid).tradeRemark(tradeRemark).remark(tradeRemark)
						.walletRecordTradeType(WalletRecordTradeType.ORDER_CASH_BACK)
						.build();
				BaseResponse<WalletRecordVO> orderByGiveStore = customerWalletProvider.orderByGiveStore(orderByRequest);

//                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateUtil.FMT_TIME_3, Locale.UK);
//                String sendNo = "TZS" + LocalDateTime.now().format(dateTimeFormatter) + RandomStringUtils.randomNumeric(4);
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

                // 推送金蝶
//                CoinActivityPushKingdeeRequest kingdeeRequest = CoinActivityPushKingdeeRequest
//                        .builder()
//                        .tid(rid)
//                        .buyerAccount(tradeVO.getBuyer().getAccount())
//                        .applyPrice(returnCoin)
//                        .saleType(tradeVO.getSaleType())
//                        .sendNo(sendNo)
//                        .build();
//
//                tradeProvider.pushRefundOrderKingdeeForCoin(kingdeeRequest);
            }

        }

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
        returnOrderProvider.offlineRefundForBoss(ReturnOrderOfflineRefundForBossRequest.builder().rid(rid)
                .refundBill(KsBeanUtil.convert(refundBill, RefundBillDTO.class))
                .operator(commonUtil.getOperator()).tid(returnOrder.getTid()).build());

        ReturnOrderByIdResponse returnOrderByIdResponse = returnOrderQueryProvider.getById(ReturnOrderByIdRequest.builder().rid(rid).build()).getContext();

        ReturnOrderDTO requestDto = KsBeanUtil.convert(returnOrderByIdResponse, ReturnOrderDTO.class);

        ReturnOrderRequest providerrequest = new ReturnOrderRequest();

        providerrequest.setReturnOrderDTO(requestDto);

        aresProvider.returnOrder(providerrequest);

        //操作日志记录
        operateLogMQUtil.convertAndSend("财务", "确认退款", "确认退款：退单编号" + returnOrder.getId());

    }


    /**
     * 线下批量退款
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "线下批量退款")
    @RequestMapping(value = "/refund/batchOffline", method = RequestMethod.POST)
    @LcnTransaction
    public ResponseEntity<BaseResponse> refundBatchOffline(@RequestBody ReturnBatchOfflineRefundRequest request) {
        request.getTids().forEach(rid->{
            ReturnOrderVO returnOrder = returnOrderQueryProvider.getById(ReturnOrderByIdRequest.builder().rid(rid)
                    .build()).getContext();
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
            returnOrderProvider.offlineRefundForBoss(ReturnOrderOfflineRefundForBossRequest.builder().rid(rid)
                    .refundBill(KsBeanUtil.convert(refundBill, RefundBillDTO.class))
                    .operator(commonUtil.getOperator()).tid(returnOrder.getTid()).build());

            ReturnOrderByIdResponse returnOrderByIdResponse = returnOrderQueryProvider.getById(ReturnOrderByIdRequest.builder().rid(rid).build()).getContext();

            ReturnOrderDTO requestDto = KsBeanUtil.convert(returnOrderByIdResponse, ReturnOrderDTO.class);

            ReturnOrderRequest providerrequest = new ReturnOrderRequest();

            providerrequest.setReturnOrderDTO(requestDto);

            aresProvider.returnOrder(providerrequest);



            //操作日志记录
            operateLogMQUtil.convertAndSend("财务", "确认退款", "确认退款：退单编号" + returnOrder.getId());


        });

        return ResponseEntity.ok(BaseResponse.SUCCESSFUL());
    }


    /**
     * 根据退单查询客户收款账户
     *
     * @param rid
     * @return
     */
    @ApiOperation(value = "根据退单查询客户收款账户")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "rid",
            value = "退单Id", required = true)
    @RequestMapping(value = "/customer/account/{rid}", method = RequestMethod.GET)
    public BaseResponse<CustomerAccountVO> findCustomerAccountBy(@PathVariable String rid) {
        ReturnOrderVO returnOrder = returnOrderQueryProvider.getById(ReturnOrderByIdRequest.builder().rid(rid)
                .build()).getContext();

//        TradeVO tradeVO = tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(returnOrder.getTid()).build()).getContext().getTradeVO();

        if (Objects.isNull(returnOrder)) {
            //退单不存在
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        CustomerAccountVO customerAccount = null;

        //提货订单的代客退单直接返回空
//        if (!TradeActivityTypeEnum.STOCKUP.toActivityType().equals(tradeVO.getActivityType())
//                && !Platform.CUSTOMER.getDesc().equals(returnOrder.getPlatform().getDesc())){
//            return BaseResponse.success(customerAccount);
//        }

        if (Objects.isNull(returnOrder.getCustomerAccount())) {
            //不存在临时账号，从流水获取客户收款账号
            RefundOrderByReturnCodeResponse refundOrder = refundOrderQueryProvider.getByReturnOrderCode(new RefundOrderByReturnOrderCodeRequest(rid)).getContext();

            if (Objects.isNull(refundOrder.getRefundBill()) || Objects.isNull(refundOrder.getRefundBill().getCustomerAccountId())) {
                // 没有流水 活着
                throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
            }

            refundOrder.getRefundBill().getCustomerAccountId();
            CustomerAccountRequest customerAccountRequest = new CustomerAccountRequest();
            customerAccountRequest.setCustomerAccountId(refundOrder.getRefundBill().getCustomerAccountId());
            BaseResponse<CustomerAccountResponse> baseResponse = customerAccountQueryProvider.getByCustomerAccountId(customerAccountRequest);
            CustomerAccountResponse customerAccountResponse = baseResponse.getContext();
            KsBeanUtil.copyPropertiesThird(customerAccountResponse, customerAccount);
        } else {
            customerAccount = returnOrder.getCustomerAccount();
        }
        return BaseResponse.success(customerAccount);
    }

    @Data
    private  static class SupplierReceiveRequest {
        private static final long serialVersionUID = -8854195407231322771L;

        @ApiModelProperty(value = "退单id")
        private String rid;
    }

    public static void main(String[] args) {
        String result = "{\"Response\":{\"return\":{\"returnCode\":\"0001\",\"returnDesc\":\"接口异常\",\"returnFlag\":0}}}";
        JSONObject response = JSONObject.parseObject(result,JSONObject.class);

        JSONObject returnJsonObj = response.getJSONObject("Response").getJSONObject("return");
        String returnCode = returnJsonObj.getString("returnCode");

        System.out.println("111");
    }
}
