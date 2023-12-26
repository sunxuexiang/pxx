package com.wanmi.sbc.returnorder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.account.api.provider.wallet.CustomerWalletQueryProvider;
import com.wanmi.sbc.account.api.request.wallet.WalletByCustomerIdQueryRequest;
import com.wanmi.sbc.account.bean.vo.CustomerWalletVO;
import com.wanmi.sbc.common.annotation.MultiSubmit;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import com.wanmi.sbc.marketing.api.provider.coupon.CoinActivityProvider;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponCodeQueryProvider;
import com.wanmi.sbc.marketing.api.request.coupon.CouponsByCodeIdsRequest;
import com.wanmi.sbc.marketing.bean.dto.CoinActivityRecordDetailDto;
import com.wanmi.sbc.marketing.bean.dto.CouponCodeDTO;
import com.wanmi.sbc.order.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.order.api.request.trade.TradeGetByIdRequest;
import com.wanmi.sbc.order.api.request.trade.TradeListAllRequest;
import com.wanmi.sbc.order.bean.dto.TradeQueryDTO;
import com.wanmi.sbc.order.bean.dto.TradeStateDTO;
import com.wanmi.sbc.order.bean.enums.DeliverStatus;
import com.wanmi.sbc.order.bean.enums.FlowState;
import com.wanmi.sbc.order.bean.enums.PayState;
import com.wanmi.sbc.order.bean.vo.TradeStateVO;
import com.wanmi.sbc.order.bean.vo.TradeVO;
import com.wanmi.sbc.returnorder.api.provider.returnorder.ReturnOrderLogisticsProvider;
import com.wanmi.sbc.returnorder.api.provider.returnorder.ReturnOrderProvider;
import com.wanmi.sbc.returnorder.api.provider.returnorder.ReturnOrderQueryProvider;
import com.wanmi.sbc.returnorder.api.request.returnorder.CanReturnItemNumByTidRequest;
import com.wanmi.sbc.returnorder.api.request.returnorder.ReturnLogisticsRequest;
import com.wanmi.sbc.returnorder.api.request.returnorder.ReturnOrderAddRequest;
import com.wanmi.sbc.returnorder.api.request.trade.TradeCanReturnNumRequest;
import com.wanmi.sbc.returnorder.api.response.returnorder.CanReturnItemNumByTidResponse;
import com.wanmi.sbc.returnorder.api.response.returnorder.ReturnLogisticsResponse;
import com.wanmi.sbc.returnorder.bean.dto.*;
import com.wanmi.sbc.returnorder.bean.enums.ReturnReason;
import com.wanmi.sbc.returnorder.bean.enums.ReturnWay;
import com.wanmi.sbc.returnorder.bean.vo.InventoryDetailSamountTradeVO;
import com.wanmi.sbc.returnorder.bean.vo.SupplierVO;
import com.wanmi.sbc.returnorder.bean.vo.TradeItemVO;
import com.wanmi.sbc.setting.api.provider.AuditQueryProvider;
import com.wanmi.sbc.setting.api.request.TradeConfigGetByTypeRequest;
import com.wanmi.sbc.setting.api.response.TradeConfigGetByTypeResponse;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
import com.wanmi.sbc.tms.api.RemoteTmsOrderService;
import com.wanmi.sbc.tms.api.domain.vo.TmsOrderVO;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/12/6 9:20
 */
@Slf4j
@RestController
@RequestMapping("/return/batch")
@Api(tags = "批量退单")
public class BatchReturnOrderController {

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private TradeQueryProvider tradeQueryProvider;

    @Autowired
    private AuditQueryProvider auditQueryProvider;

    @Autowired
    private ReturnOrderQueryProvider returnOrderQueryProvider;

    @Autowired
    private CouponCodeQueryProvider couponCodeQueryProvider;

    @Autowired
    private RemoteTmsOrderService remoteTmsOrderService;

    @Autowired
    private ReturnOrderLogisticsProvider returnOrderLogisticsProvider;

    @Autowired
    private ReturnOrderProvider returnOrderProvider;

    @Autowired
    private CoinActivityProvider coinActivityProvider;

    @Autowired
    private CustomerWalletQueryProvider walletQueryProvider;



    @ApiOperation(value = "店铺列表")
    @GetMapping(value = "/store/list")
    public BaseResponse<List<Map<String, Object>>> storeList() {
        String operatorId = commonUtil.getOperatorId();
        List<TradeVO> tradeVOS = queryCanReturnOrderList(operatorId, null);
        Map<Long, List<String>> listMap = tradeVOS
                .stream()
                .collect(Collectors.groupingBy(t -> t.getSupplier().getStoreId(), Collectors.mapping(TradeVO::getId, Collectors.toList())));
        List<Map<String, Object>> result = tradeVOS.stream()
                .filter(o -> Objects.nonNull(o.getSupplier()))
                .peek(o -> {
                    if (Objects.isNull(o.getOrderTimeOut())) {
                        o.setOrderTimeOut(LocalDateTime.now());
                    }
                })
                .sorted(Comparator.comparing(TradeVO::getOrderTimeOut, Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        t -> t.getSupplier().getStoreId(),
                        t -> t.getSupplier().getStoreName(),
                        (o1, o2) -> o1,
                        LinkedHashMap::new
                )).entrySet().stream()
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("storeId", entry.getKey());
                    map.put("storeName", entry.getValue());
                    map.put("tidList", listMap.get(entry.getKey()));
                    return map;
                })
                .collect(Collectors.toList());
        return BaseResponse.success(result);
    }

    @ApiOperation(value = "根据店铺ID查询可退订单列表")
    @GetMapping(value = "/list/{storeId}")
    public BaseResponse<List<String>> listByStore(@PathVariable Long storeId) {
        String operatorId = commonUtil.getOperatorId();
        List<TradeVO> tradeVOS = queryCanReturnOrderList(operatorId, storeId);
        List<String> result = tradeVOS.stream()
                .filter(o -> Objects.nonNull(o.getSupplier()))
                .peek(o -> {
                    if (Objects.isNull(o.getOrderTimeOut())) {
                        o.setOrderTimeOut(LocalDateTime.now());
                    }
                })
                .sorted(Comparator.comparing(TradeVO::getOrderTimeOut, Comparator.reverseOrder()))
                .map(TradeVO::getId).collect(Collectors.toList());
        return BaseResponse.success(result);
    }

    @ApiOperation(value = "批量创建退单")
    @PostMapping(value = "/add")
    @MultiSubmit
    @LcnTransaction
    public BaseResponse createBatch(@RequestBody List<ReturnOrderDTO> request) {
        // 校验是否有重复订单
        StopWatch stopWatch = new StopWatch("app-批量退单");
        stopWatch.start("校验是否重复");
        checkDuplicateTids(request);
        stopWatch.stop();
        stopWatch.start("校验活动");
        verifyIsReturnable(request);
        stopWatch.stop();
        stopWatch.start("构建退单");
        buildReturnOrderDto(request);
        stopWatch.stop();

        stopWatch.start("退单");
        Operator operator = commonUtil.getOperator();
        createReturnOrder(request, operator);
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());
        return BaseResponse.SUCCESSFUL();
    }

    private void createReturnOrder(List<ReturnOrderDTO> request,Operator operator) {
        for (ReturnOrderDTO dto : request) {
            ReturnOrderAddRequest addRequest = ReturnOrderAddRequest
                    .builder()
                    .operator(operator)
                    .returnOrder(dto)
                    .build();
            returnOrderProvider.add(addRequest);
        }
    }

    public void buildReturnOrderDto(List<ReturnOrderDTO> request) {
        for (ReturnOrderDTO dto : request) {
            String tid = dto.getTid();
            CanReturnItemNumByTidResponse tradeVo = returnOrderQueryProvider.queryCanReturnDevanningItemNumByTid(CanReturnItemNumByTidRequest
                            .builder().tid(tid).build())
                    .getContext();
            if (Objects.isNull(tradeVo)) {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "订单" + tid + "不能退货退款");
            }
            Map<String, TradeItemVO> tradeItemMap = tradeVo.getTradeItems().stream().collect(Collectors.toMap(TradeItemVO::getSkuId, Function.identity()));
            // Map<String, TradeItemVO> giftsItemMap = tradeVo.getGifts().stream().collect(Collectors.toMap(TradeItemVO::getSkuId, Function.identity()));
            dto.setWareId(tradeVo.getWareId());
            List<ReturnItemDTO> returnGifts = new ArrayList<>();
            List<ReturnItemDTO> returnItems = new ArrayList<>();

            BigDecimal walletPrice = BigDecimal.ZERO;
            BigDecimal moneyPrice = BigDecimal.ZERO;
            for (ReturnItemDTO item : dto.getReturnItems()) {
                String skuId = item.getSkuId();
                BigDecimal num = item.getNum();
                if (Objects.isNull(num)) {
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "订单" + tid + "退单商品数量不能为空");
                }
                TradeItemVO tradeItemVO = tradeItemMap.get(skuId);
                if (Objects.isNull(tradeItemVO)) {
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "订单" + tid + "退单商品不满足退单条件");
                }
                BigDecimal canReturnNum = tradeItemVO.getCanReturnNum();
                if (num.compareTo(canReturnNum) > 0) {
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "订单" + tid + "退单商品不足");
                }

                List<InventoryDetailSamountTradeVO> wallet = new ArrayList<>();
                List<InventoryDetailSamountTradeVO> money = new ArrayList<>();
                List<InventoryDetailSamountTradeVO> inventoryDetailSamountTrades = tradeItemVO.getInventoryDetailSamountTrades();
                for (InventoryDetailSamountTradeVO samountTrade : inventoryDetailSamountTrades) {
                    int moneyType = samountTrade.getMoneyType();
                    if (moneyType == 0) {
                        // 鲸币
                        if (wallet.size() < num.intValue()) {
                            samountTrade.setReturnFlag(2);
                            wallet.add(samountTrade);
                            walletPrice = walletPrice.add(samountTrade.getAmortizedExpenses().setScale(2, RoundingMode.HALF_UP));
                        }
                    }else {
                        // 钱
                        if (money.size() < num.intValue()) {
                            samountTrade.setReturnFlag(2);
                            money.add(samountTrade);
                            moneyPrice = moneyPrice.add(samountTrade.getAmortizedExpenses().setScale(2, RoundingMode.HALF_UP));
                        }
                    }
                }

                ReturnItemDTO returnItemDTO = KsBeanUtil.convert(tradeItemVO, ReturnItemDTO.class);
                returnItemDTO.setNum(num);
                money.addAll(wallet);
                returnItemDTO.setInventoryDetailSamountTrades(KsBeanUtil.convertList(money, InventoryDetailSamountTradeDTO.class));
                returnItems.add(returnItemDTO);
            }

            // returnGifts
            dto.setReturnGifts(returnGifts);
            // returnItems
            dto.setReturnItems(returnItems);
            // returnPoints
            dto.setReturnPoints(ReturnPointsDTO.builder().actualPoints(0L).applyPoints(0L).build());
            // returnPrice
            BigDecimal totalPrice = moneyPrice.add(walletPrice);
            ReturnPriceDTO returnPriceDTO = ReturnPriceDTO
                    .builder()
                    .totalPrice(totalPrice)
                    .balanceReturnPrice(walletPrice)
                    .applyStatus(false)
                    .deliveryPrice(BigDecimal.ZERO)
                    .build();
            dto.setReturnPrice(returnPriceDTO);
            // description
            // images
            // returnReason
            dto.setReturnReason(ReturnReason.OTHER);
            // returnWay
            dto.setReturnWay(ReturnWay.OTHER);
            SupplierVO supplier = tradeVo.getSupplier();
            CompanyDTO companyDTO = CompanyDTO
                    .builder()
                    .companyInfoId(supplier.getSupplierId())
                    .companyCode(supplier.getSupplierCode())
                    .supplierName(supplier.getSupplierName())
                    .storeId(supplier.getStoreId())
                    .storeName(supplier.getStoreName())
                    .build();
            dto.setCompany(companyDTO);
            dto.setChannelType(tradeVo.getChannelType());
            dto.setDistributorId(tradeVo.getDistributorId());
            dto.setDistributorName(tradeVo.getDistributorName());
            dto.setInviteeId(tradeVo.getInviteeId());
            dto.setShopName(tradeVo.getShopName());
            dto.setDistributeItems(tradeVo.getDistributeItems());
            if (CollectionUtils.isNotEmpty(tradeVo.getSendCouponCodeIds())) {
                dto.setSendCouponCodeIds(tradeVo.getSendCouponCodeIds());
            }
            dto.setActivityType(tradeVo.getActivityType());
            dto.setBatchFlag(BoolFlag.YES);
        }

    }

    @ApiOperation(value = "查询退单物流信息")
    @PostMapping(value = "/logistics/info")
    public BaseResponse<ReturnLogisticsResponse> queryLogistics(@RequestBody @Valid ReturnLogisticsRequest request) {
        String tid = request.getTid();
        TradeVO tradeVO = tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO();
        ReturnLogisticsResponse returnLogisticsResponse = new ReturnLogisticsResponse();
        if (Objects.nonNull(tradeVO) && Objects.nonNull(tradeVO.getDeliverWay()) && DeliverWay.isTmsDelivery(tradeVO.getDeliverWay())) {
            // 查询TMS信息
            TmsOrderVO data = remoteTmsOrderService.getInfo(tid).getData();
            if (Objects.nonNull(data)) {
                returnLogisticsResponse.setCompanyName("大众物流");
                returnLogisticsResponse.setPickAddr(data.getShipmentSiteName());
                // todo 电话
                returnLogisticsResponse.setPickPhone(null);
                returnLogisticsResponse.setTmsFlag(BoolFlag.YES);
            }
        }else {
            String operatorId = commonUtil.getOperatorId();
            request.setCustomerId(operatorId);
            // 历史记录
            ReturnLogisticsResponse response = returnOrderLogisticsProvider.findReturnLogisticsByHistory(request).getContext();
            if (Objects.nonNull(response)) {
                response.setTmsFlag(BoolFlag.NO);
                returnLogisticsResponse = response;
            }
        }
        return BaseResponse.success(returnLogisticsResponse);
    }

    private void verifyIsReturnable(List<ReturnOrderDTO> dtoList) {
        TradeConfigGetByTypeRequest request = new TradeConfigGetByTypeRequest();
        request.setConfigType(ConfigType.ORDER_SETTING_APPLY_REFUND);
        TradeConfigGetByTypeResponse config = auditQueryProvider.getTradeConfigByType(request).getContext();
        for (ReturnOrderDTO returnOrderDTO : dtoList) {
            String tid = returnOrderDTO.getTid();
            TradeVO trade = tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO();
            commonUtil.checkIfStore(trade.getSupplier().getStoreId());
            if(CollectionUtils.isNotEmpty(trade.getSendCouponCodeIds())) {
                List<CouponCodeDTO> couponCodeList = couponCodeQueryProvider.findCouponsByCouponCodeIds(CouponsByCodeIdsRequest
                        .builder().couponCodeIds(trade.getSendCouponCodeIds()).build()).getContext().getCouponCodeList();
                boolean useStatus = couponCodeList.stream().anyMatch(coupon -> coupon.getUseStatus().equals(DefaultFlag.YES));
                if (useStatus) {
                    throw new SbcRuntimeException("K-050320", "订单" + tid + "包含已使用的赠券，不可退单退货，如有疑问请联系客服！");
                }
            }
            if (Objects.nonNull(trade.getTradeState().getDeliverStatus()) && (trade.getTradeState().getDeliverStatus() == DeliverStatus.SHIPPED || trade.getTradeState().getDeliverStatus() == DeliverStatus.PART_SHIPPED)) {
                if (config.getStatus() == 0) {
                    throw new SbcRuntimeException("K-050208", "订单" + tid + "不支持退货或是已经超过可退时间");
                }
                JSONObject content = JSON.parseObject(config.getContext());
                Integer day = content.getObject("day", Integer.class);

                if (Objects.isNull(trade.getTradeState().getEndTime())) {
                    throw new SbcRuntimeException("K-050002", "订单" + tid + "状态已改变，请关闭页面后重试!");
                }
                if (trade.getTradeState().getEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() < LocalDateTime.now().minusDays(day).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()) {
                    throw new SbcRuntimeException("K-050208", "订单" + tid + "不支持退货或是已经超过可退时间");
                }
            }
            // 校验鲸币活动
            List<String> skuIds = returnOrderDTO.getReturnItems().stream().map(ReturnItemDTO::getSkuId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(skuIds)) {
                List<CoinActivityRecordDetailDto> coinDetails = coinActivityProvider.queryCoinActivityRecordByOrderIdAndSkuIds(trade.getId(), skuIds).getContext();
                if (CollectionUtils.isNotEmpty(coinDetails)) {
                    BigDecimal returnCoin = coinDetails.stream().map(CoinActivityRecordDetailDto::getCoinNum).reduce(BigDecimal.ZERO, BigDecimal::add);
                    if (returnCoin.compareTo(BigDecimal.ZERO) > 0) {
                        CustomerWalletVO customerWalletVO = walletQueryProvider.getBalanceByCustomerId(WalletByCustomerIdQueryRequest.builder().customerId(trade.getBuyer().getId()).build())
                                .getContext().getCustomerWalletVO();
                        if (returnCoin.compareTo(customerWalletVO.getBalance()) > 0) {
                            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "订单" + tid + "参加了返鲸币活动，您的鲸币余额已不足退货退款抵扣，请联系客服处理！");
                        }
                    }
                }
            }
        }


    }

    public void checkDuplicateTids(List<ReturnOrderDTO> request) {
        Map<String, Long> countByTid = request.stream().collect(Collectors.groupingBy(ReturnOrderDTO::getTid, Collectors.counting()));
        List<String> duplicateTids = countByTid.entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (!duplicateTids.isEmpty()) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "存在重复的订单号：" + duplicateTids);
        }
    }

    public List<TradeVO> queryCanReturnOrderList(String buyerId, Long storeId) {
        TradeQueryDTO dto = TradeQueryDTO.builder()
                .buyerId(buyerId)
                .tradeState(TradeStateDTO.builder().payState(PayState.PAID).build())
                .flowStates(Collections.singletonList(FlowState.COMPLETED))
                .build();
        if (Objects.nonNull(storeId)) {
            dto.setStoreId(storeId);
        }
        dto.setStoreId(storeId);

        TradeListAllRequest request = TradeListAllRequest
                .builder().tradeQueryDTO(dto)
                .build();

        List<TradeVO> tradeVOList = tradeQueryProvider.listAll(request).getContext().getTradeVOList();

        TradeConfigGetByTypeRequest configRequest = new TradeConfigGetByTypeRequest();
        configRequest.setConfigType(ConfigType.ORDER_SETTING_APPLY_REFUND);
        TradeConfigGetByTypeResponse config = auditQueryProvider.getTradeConfigByType(configRequest).getContext();
        final boolean flag = config.getStatus() == 1;
        int days = JSONObject.parseObject(config.getContext()).getInteger("day");

        return tradeVOList.stream().filter(t -> {
            TradeStateVO tradeState = t.getTradeState();
            boolean canReturnFlag =
                    tradeState.getFlowState() == FlowState.COMPLETED || (tradeState.getPayState() == PayState.PAID
                            && tradeState.getDeliverStatus() == DeliverStatus.NOT_YET_SHIPPED && tradeState
                            .getFlowState() != FlowState.VOID);
            canReturnFlag = isCanReturnTime(flag, days, tradeState, canReturnFlag);
            canReturnFlag = canReturnFlag && DefaultFlag.NO == t.getStoreBagsFlag();
            if (canReturnFlag && !DeliverStatus.NOT_YET_SHIPPED.equals(t.getTradeState().getDeliverStatus())) {
                canReturnFlag = returnOrderQueryProvider.canReturnFlag(TradeCanReturnNumRequest.builder()
                        .trade(KsBeanUtil.convert(t, TradeDTO.class)).build()).getContext().getCanReturnFlag();
            }
            if (canReturnFlag) {
                t.setCanReturnFlag(canReturnFlag);
                return true;
            }
            return false;
        }).collect(Collectors.toList());
    }

    private boolean isCanReturnTime(boolean flag, int days, TradeStateVO tradeState, boolean canReturnFlag) {
        if (canReturnFlag && tradeState.getFlowState() == FlowState.COMPLETED) {
            if (flag) {
                if (Objects.nonNull(tradeState.getFinalTime())) {
                    //是否可退根据订单完成时配置为准
                    flag = tradeState.getFinalTime().isAfter(LocalDateTime.now());
                } else if (Objects.nonNull(tradeState.getEndTime())) {
                    //容错-历史数据
                    //判断是否在可退时间范围内
                    LocalDateTime endTime = tradeState.getEndTime();
                    return endTime.plusDays(days).isAfter(LocalDateTime.now());
                }
            } else {
                return false;
            }
            return flag;
        }
        return canReturnFlag;
    }
}
