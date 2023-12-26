package com.wanmi.sbc.returnorder.settlement;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.account.api.provider.finance.record.SettlementDetailProvider;
import com.wanmi.sbc.account.api.provider.finance.record.SettlementProvider;
import com.wanmi.sbc.account.api.provider.finance.record.SettlementQueryProvider;
import com.wanmi.sbc.account.api.request.finance.record.SettlementAddRequest;
import com.wanmi.sbc.account.api.request.finance.record.SettlementDeleteRequest;
import com.wanmi.sbc.account.api.request.finance.record.SettlementDetailListAddRequest;
import com.wanmi.sbc.account.api.request.finance.record.SettlementLastByStoreIdRequest;
import com.wanmi.sbc.account.api.response.finance.record.SettlementLastResponse;
import com.wanmi.sbc.account.bean.dto.SettleGoodDTO;
import com.wanmi.sbc.account.bean.dto.SettleTradeDTO;
import com.wanmi.sbc.account.bean.dto.SettlementDetailDTO;
import com.wanmi.sbc.account.bean.enums.*;
import com.wanmi.sbc.common.enums.OrderType;
import com.wanmi.sbc.common.enums.StoreType;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.UUIDUtil;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.store.ListStoreForSettleRequest;
import com.wanmi.sbc.customer.api.request.store.StoreByIdRequest;
import com.wanmi.sbc.customer.api.response.store.StoreByIdResponse;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.goods.bean.enums.DistributionGoodsAudit;
import com.wanmi.sbc.returnorder.api.request.settlement.SettlementAnalyseRequest;
import com.wanmi.sbc.returnorder.bean.enums.ReturnFlowState;
import com.wanmi.sbc.returnorder.returnorder.model.entity.ReturnItem;
import com.wanmi.sbc.returnorder.returnorder.model.root.ReturnOrder;
import com.wanmi.sbc.returnorder.returnorder.model.value.ReturnPrice;
import com.wanmi.sbc.returnorder.returnorder.service.ReturnOrderService;
import com.wanmi.sbc.returnorder.trade.model.entity.TradeItem;
import com.wanmi.sbc.returnorder.trade.model.root.Trade;
import com.wanmi.sbc.returnorder.trade.model.root.TradeDistributeItem;
import com.wanmi.sbc.returnorder.trade.service.TradeService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by hht on 2017/12/7.
 */
@Service
@Slf4j
public class SettlementAnalyseService {

    private static Logger logger = LoggerFactory.getLogger(SettlementAnalyseService.class);

    /**
     * 后台步长
     */
    private static final int STEP = 1000;

    @Autowired
    private SettlementDetailProvider settlementDetailProvider;

    @Autowired
    private SettlementProvider settlementProvider;

    @Autowired
    private SettlementQueryProvider settlementQueryProvider;

    @Autowired
    private StoreQueryProvider storeQueryProvider;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private ReturnOrderService returnOrderService;


    @Data
    private static class SettlementHelper {
        private BigDecimal salePrice = BigDecimal.ZERO;
        private BigDecimal returnPrice = BigDecimal.ZERO;
        private long saleNum = 0L;
        private long returnNum = 0L;
        private BigDecimal platformPrice = BigDecimal.ZERO;
        private BigDecimal deliveryPrice = BigDecimal.ZERO;
        private BigDecimal commonCouponPrice = BigDecimal.ZERO;
        // 积分抵扣总额
        private BigDecimal pointPrice = BigDecimal.ZERO;
        // 分销佣金总额
        private BigDecimal commissionPrice = BigDecimal.ZERO;
        // 商品实付总额
        private BigDecimal splitPayPrice = BigDecimal.ZERO;
        // 店铺应收总额
        private BigDecimal storePrice = BigDecimal.ZERO;
        // 供货总额
        private BigDecimal providerPrice = BigDecimal.ZERO;
    }

    public void analyseSettlement(Date targetDate, SettlementAnalyseRequest request) {
        String param =request.getParam();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(targetDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (StringUtils.isNotEmpty(param)) {
            calendar.add(Calendar.DATE, 1);
            logger.info("包含今日订单的结算");
        }

        //获取当天的日期，几号
        int targetDay = calendar.get(Calendar.DAY_OF_MONTH);

        List<StoreVO> storeList = storeQueryProvider
                .listForSettle(
                        ListStoreForSettleRequest.builder()
                                .targetDay(targetDay)
                                .storeType(request.getStoreType())
                                .build()
                ).getContext().getStoreVOList();
        log.info("商家列表>>>"+JSON.toJSONString(storeList));
        if (CollectionUtils.isEmpty(storeList)) {
            return;
        }
        for (StoreVO store : storeList) {
            try {
                //根据结束时间获取账期的开始时间
                Date startDate = this.getStartDate(store);
                if (startDate != null) {
                    store.setStoreType(request.getStoreType());
                    analyseSettlementByStore(store, startDate, calendar.getTime());
                }
            } catch (Exception e) {
                logger.error("结算数据异常", e);
            }
        }

    }


    /**
     * 解析入口
     *
     * @param store
     * @param startTime
     * @param endTime
     */
    private void analyseSettlementByStore(StoreVO store, Date startTime, Date endTime) {
        log.info("开始时间>>>{}结束时间>>>{}",formatDate(startTime),formatDate(getSettleEndDateForView(endTime)));
        //生成之前先删除，防止重复
        settlementProvider.delete(
                SettlementDeleteRequest.builder()
                        .storeId(store.getStoreId())
                        .startTime(formatDate(startTime))
                        .endTime(formatDate(getSettleEndDateForView(endTime))).build()
        );

        //生成结算单uuid，用作插入结算单明细
        String uuid = UUIDUtil.getUUID();


        int pageNum = 0;
        //解析订单信息
        SettlementHelper settlementHelper = new SettlementHelper();
        while (true) {
            if (!analyseForTrade(pageNum, store, startTime, endTime, uuid, settlementHelper)) {
                break;
            }
            pageNum++;
        }

        // 订单、退单结算状态业务上已经用不到，这边不做更改
//        tradeService.updateSettlementStatus(store.getStoreId(), startTime, endTime);
//        returnOrderService.updateSettlementStatus(store.getStoreId(), startTime, endTime);

        //插入settlement结算表
        SettlementAddRequest settlement = new SettlementAddRequest();
        settlement.setSettleUuid(uuid);
        settlement.setCreateTime(endTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        settlement.setStartTime(formatDate(startTime));
        settlement.setEndTime(formatDate(getSettleEndDateForView(endTime)));
        settlement.setStoreId(store.getStoreId());
        settlement.setStoreType(store.getStoreType());
        settlement.setSalePrice(settlementHelper.getSalePrice());
        settlement.setSaleNum(settlementHelper.getSaleNum());
        settlement.setReturnPrice(settlementHelper.getReturnPrice());
        settlement.setReturnNum(settlementHelper.getReturnNum());
        settlement.setPlatformPrice(settlementHelper.getPlatformPrice());
        settlement.setStorePrice(settlementHelper.getStorePrice());
        settlement.setDeliveryPrice(settlementHelper.getDeliveryPrice());
        settlement.setSplitPayPrice(settlementHelper.getSplitPayPrice());
        settlement.setCommonCouponPrice(settlementHelper.getCommonCouponPrice());
        settlement.setCommissionPrice(settlementHelper.getCommissionPrice());
        settlement.setPointPrice(settlementHelper.getPointPrice());
        settlement.setSettleStatus(SettleStatus.NOT_SETTLED);
        settlement.setProviderPrice(settlementHelper.getProviderPrice());
        settlementProvider.add(settlement);
    }


    /**
     * 处理订单信息，插入结算明细表
     *
     * @param pageNum   步长
     * @param startTime
     * @param endTime
     * @param uuid      结算uuid
     * @return
     */
    private boolean analyseForTrade(int pageNum, StoreVO store, Date startTime, Date endTime, String uuid,
                                    SettlementHelper settlement) {
        PageRequest pageRequest = PageRequest.of(pageNum, STEP);
        Long storeId = store.getStoreId();
        StoreType storeType = store.getStoreType();
        //查询订单
        List<Trade> tradeList = tradeService.findTradeListForSettlement(storeId, startTime, endTime, pageRequest);

        if (CollectionUtils.isEmpty(tradeList)) {
            return false;
        }

        // 1.从结算单中获取金额信息
        // 运费总额
        BigDecimal totalDeliveryPrice = settlement.getDeliveryPrice();
        // 平台佣金总额
        BigDecimal totalPlatformPrice = settlement.getPlatformPrice();
        // 通用券优惠总额
        BigDecimal totalCommonCouponPrice = settlement.getCommonCouponPrice();
        // 分销佣金总额
        BigDecimal totalCommissionPrice = settlement.getCommissionPrice();
        // 积分抵扣总额
        BigDecimal totalPointPrice = settlement.getPointPrice();
        // 商品实付总额
        BigDecimal totalSplitPayPrice = settlement.getSplitPayPrice();
        // 店铺应收总额
        BigDecimal totalStorePrice = settlement.getStorePrice();
        // 供货总额
        BigDecimal totalProviderPrice = settlement.getProviderPrice();


        // 2.处理每笔订单，生成结算单明细；并将相应金额累加至结算单
        List<SettlementDetailDTO> settlementDetailList = new ArrayList<>();
        for (Trade trade : tradeList) {

            BigDecimal deliveryPrice = trade.getTradePrice().getDeliveryPrice() == null ? BigDecimal.ZERO :
                    trade.getTradePrice().getDeliveryPrice();

            // 过滤订单营销信息
            //trimTradeMarketing(trade);

            // 2.1.查询订单关联的退单，并按sku分组退货信息
            List<ReturnOrder> returnOrders = returnOrderService.findReturnByTid(trade.getId());
            returnOrders = returnOrders.stream().filter(
                    item -> ReturnFlowState.COMPLETED.equals(item.getReturnFlowState())).collect(Collectors.toList());
            Map<String, List<ReturnItem>> returnItemsMap = returnOrders.stream()
                    .flatMap(item -> item.getReturnItems().stream()).collect(Collectors.groupingBy(ReturnItem::getSkuId));

            // 2.2.构建结算单明细中的商品列表，并将相应金额累加至结算单
            List<SettleGoodDTO> settleGoods = transTradeItems(trade, returnItemsMap, trade.getOrderType());

            // 2.3.计算退单改价差额
            BigDecimal returnSpecialPrice = BigDecimal.ZERO;
            for (ReturnOrder returnOrder : returnOrders) {
                ReturnPrice returnPrice = returnOrder.getReturnPrice();
                if (returnPrice.getApplyStatus()) {
                    returnSpecialPrice = returnSpecialPrice.add(
                            returnPrice.getTotalPrice().subtract(returnPrice.getActualReturnPrice())
                    );
                }
            }

            // 2.4.计算订单的通用券优惠金额、积分抵扣金额、平台佣金、分销佣金、供货价
            BigDecimal commonCouponPrice = BigDecimal.ZERO;
            BigDecimal pointPrice = BigDecimal.ZERO;
            BigDecimal platformPrice = BigDecimal.ZERO;
            BigDecimal commissionPrice = BigDecimal.ZERO;
            BigDecimal splitPayPrice = BigDecimal.ZERO;
            BigDecimal providerPrice = BigDecimal.ZERO;

            for (SettleGoodDTO goodsInfo : settleGoods) {
                // 通用券累加
                if (CollectionUtils.isNotEmpty(goodsInfo.getCouponSettlements())) {
                    Optional<SettleGoodDTO.CouponSettlementDTO> couponSettle = goodsInfo.getCouponSettlements().stream()
                            .filter(item -> item.getCouponType() == SettleCouponType.GENERAL_VOUCHERS).findFirst();
                    if (couponSettle.isPresent()) {
                        commonCouponPrice = commonCouponPrice.add(couponSettle.get().getReducePrice());
                    }
                }

                // 积分抵扣累加
                if (Objects.nonNull(goodsInfo.getPointPrice())) {
                    pointPrice = pointPrice.add(goodsInfo.getPointPrice());
                }

                // 平台佣金累加
                    platformPrice = goodsInfo.getSplitPayPrice() != null ? platformPrice.add(goodsInfo.getSplitPayPrice().multiply(goodsInfo.getCateRate())
                                    .divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_DOWN))
                            : BigDecimal.ZERO;


                // 分销佣金累加
                if (Objects.nonNull(goodsInfo.getCommission())) {
                    commissionPrice = commissionPrice.add(goodsInfo.getCommission());
                }

                // 实付金额累加
                splitPayPrice = splitPayPrice.add(goodsInfo.getSplitPayPrice() != null ?
                        goodsInfo.getSplitPayPrice() : BigDecimal.ZERO);

                // 供货金额
                providerPrice =  providerPrice.add(goodsInfo.getProviderPrice() != null ?
                        goodsInfo.getSplitPayPrice() : BigDecimal.ZERO);
            }

            // 2.5.计算店铺应收金额 = 实付金额 + 实付运费 + 通用券金额 + 积分抵扣金额 - 平台佣金 - 分销佣金 + 退单改价差额
            BigDecimal storePrice = BigDecimal.ZERO;
            if(StoreType.SUPPLIER.equals(storeType)){
                storePrice = storePrice.add(splitPayPrice).add(deliveryPrice)
                        .add(commonCouponPrice).add(pointPrice)
                        .subtract(platformPrice).subtract(commissionPrice).add(returnSpecialPrice);

            }else {
                //供货总额-平台佣金；
                storePrice = providerPrice;
            }

            // 2.6.新增结算明细
            settlementDetailList.add(
                    //结算明细
                    new SettlementDetailDTO(
                            null,
                            uuid,
                            formatDate(startTime),
                            formatDate(getSettleEndDateForView(endTime)),
                            storeId,
                            trade.getTradePrice().isSpecial(),
                            // 组装结算明细 - 订单明细
                            new SettleTradeDTO(
                                    trade.getTradeState().getPayTime(),
                                    trade.getTradeState().getCreateTime(),
                                    trade.getTradeState().getEndTime(),
                                    trade.getTradeState().getFinalTime(),
                                    trade.getId(),
                                    TradeType.NORMAL_TRADE,
                                    trade.getOrderType(),
                                    GatherType.PLATFORM,
                                    trade.getTradePrice().getDeliveryPrice(),
                                    BigDecimal.ZERO,
                                    BigDecimal.ZERO,
                                    returnSpecialPrice,
                                    storePrice,
                                    providerPrice
                            ),
                            // 组装结算明细 - 商品明细
                            settleGoods
                            , true)
            );

            // 2.7.累加结算单中的金额信息
            totalDeliveryPrice = totalDeliveryPrice.add(deliveryPrice);
            // 商品实付总额
            totalSplitPayPrice = totalSplitPayPrice.add(splitPayPrice);
            // 平台佣金总额
            totalPlatformPrice = totalPlatformPrice.add(platformPrice);
            // 通用券优惠总额
            totalCommonCouponPrice = totalCommonCouponPrice.add(commonCouponPrice);
            // 分销佣金总额
            totalCommissionPrice = totalCommissionPrice.add(commissionPrice);
            // 积分抵扣总额
            totalPointPrice = totalPointPrice.add(pointPrice);
            // 店铺应收总额
            totalStorePrice = totalStorePrice.add(storePrice);
            // 商品供货总额
            totalProviderPrice = totalStorePrice.add(providerPrice);


        }
        List<SettlementDetailDTO> settlementDetailDTOList = KsBeanUtil.convert(settlementDetailList,
                SettlementDetailDTO.class);
        settlementDetailProvider.addList(new SettlementDetailListAddRequest(settlementDetailDTOList));

        // 3.重新设回累加后的金额
        settlement.setDeliveryPrice(totalDeliveryPrice);
        settlement.setSplitPayPrice(totalSplitPayPrice);
        settlement.setPlatformPrice(totalPlatformPrice);
        settlement.setCommonCouponPrice(totalCommonCouponPrice);
        settlement.setCommissionPrice(totalCommissionPrice);
        settlement.setPointPrice(totalPointPrice);
        settlement.setStorePrice(totalStorePrice);
        settlement.setProviderPrice(totalProviderPrice);
        return true;
    }

    /**
     * 构建结算单明细中的商品列表，并将相应金额累加至结算单
     *
     * @param trade          订单
     * @param returnItemsMap 退单商品退货信息map(key:skuId, value:退货信息列表)
     * @return
     */
    private static List<SettleGoodDTO> transTradeItems(
            Trade trade, Map<String, List<ReturnItem>> returnItemsMap, OrderType orderType) {

        List<TradeItem> tradeItemList = trade.getTradeItems();

        // 1.构建结算单明细商品列表
        List<SettleGoodDTO> newSettleGoodList = new ArrayList<>();
        for (TradeItem item : tradeItemList) {

            // 1.1.从订单商品中获取相应字段
            // 购买数量
            long buyCount = item.getNum();
            // 营销信息(满减、满折)
            List<SettleGoodDTO.MarketingSettlementDTO> marketings =
                    convertMarketingType(item.getMarketingSettlements());
            // 优惠券信息(店铺券、通用券)
            List<SettleGoodDTO.CouponSettlementDTO> coupons = convertCouponType(item.getCouponSettlements());

            // 订单为积分订单，积分抵扣金额为结算价，普通订单则累加积分抵扣
            BigDecimal pointPrice;
            if (orderType == OrderType.POINTS_ORDER) {
                pointPrice = item.getSettlementPrice();
            } else {
                pointPrice = item.getPointsPrice();
            }

            // 实付金额
            BigDecimal splitPayPrice = item.getSplitPrice();

            //供货价
            BigDecimal supplyPrice = item.getSupplyPrice();

            //供货金额
            BigDecimal providerPrice = BigDecimal.ZERO;

            if(item.getSupplyPrice() !=null){
                providerPrice = supplyPrice.multiply(new BigDecimal(buyCount));
            }

            // 分销佣金
            BigDecimal commission = BigDecimal.ZERO;
            if (DistributionGoodsAudit.CHECKED.equals(item.getDistributionGoodsAudit())) {
                Optional<TradeDistributeItem> distributeItem = trade.getDistributeItems().stream()
                        .filter(i -> i.getGoodsInfoId().equals(item.getSkuId())).findFirst();
                if (distributeItem.isPresent()) {
                    commission = distributeItem.get().getTotalCommission();
                }
            }
            // 商品退货信息
            List<ReturnItem> returnItems = returnItemsMap.get(item.getSkuId());

            // 1.2.如果商品有退货，计算排除退货后的价格
            if (CollectionUtils.isNotEmpty(returnItems)) {

                // 获取退货数量
                BigDecimal returnNum = returnItems.stream().map(ReturnItem::getNum).reduce(BigDecimal.ZERO, (a, b) -> a.add(b));
                // 重算购买数量
                buyCount = item.getNum() - returnNum.longValue();
                // 重算营销信息(满减、满折)
                if (CollectionUtils.isNotEmpty(marketings)) {
                    marketings.forEach(marketing ->
                            marketing.setSplitPrice(
                                    calcSubReturnPrice(marketing.getSplitPrice(), item.getNum(), returnNum)
                            )
                    );
                }
                // 重算优惠券信息(店铺券、通用券)
                if (CollectionUtils.isNotEmpty(coupons)) {
                    coupons.forEach(coupon -> {
                        coupon.setSplitPrice(
                                calcSubReturnPrice(coupon.getSplitPrice(), item.getNum(), returnNum)
                        );
                        coupon.setReducePrice(
                                calcSubReturnPrice(coupon.getReducePrice(), item.getNum(), returnNum)
                        );
                    });
                }
                // 重算积分抵扣、实付金额
                if (Objects.nonNull(pointPrice)) {
                    pointPrice = calcSubReturnPrice(pointPrice, item.getNum(), returnNum);
                }
                // 重算实付金额
                splitPayPrice = calcSubReturnPrice(item.getSplitPrice(), item.getNum(), returnNum);

                // 重算分销佣金
                if (Objects.nonNull(commission)) {
                    commission = calcSubReturnPrice(commission, item.getNum(), returnNum);
                }
            }

            // 1.3.添加结算单明细商品
            newSettleGoodList.add(
                    new SettleGoodDTO(
                            item.getSkuName(),
                            item.getSkuNo(),
                            item.getSpecDetails(),
                            item.getLevelPrice(),
                            item.getCateName(),
                            item.getCateRate(),
                            buyCount,
                            splitPayPrice,
                            0L,
                            0L,
                            BigDecimal.ZERO,
                            item.getSkuId(),
                            null,
                            marketings,
                            coupons,
                            pointPrice,
                            item.getCommissionRate(),
                            commission,
                            providerPrice,
                            supplyPrice
                    )
            );

        }

        return newSettleGoodList;
    }

    /**
     * 计算排除退货后的价格
     *
     * @param originPrice 原价
     * @param buyNum      购买数量
     * @param returnNum   退货数量
     * @return
     */
    private static BigDecimal calcSubReturnPrice(BigDecimal originPrice, Long buyNum, BigDecimal returnNum) {
        if (buyNum - returnNum.longValue() == 0) {
            return BigDecimal.ZERO;
        }
        // 金额 = 原价 - (原价 / 购买数量) * 退货数量
        return originPrice.subtract(
                originPrice
                        .divide(new BigDecimal(buyNum), 2, BigDecimal.ROUND_DOWN)
                        .multiply(returnNum));
    }

    /**
     * 传入store对象，获取开始时间
     *
     * @return
     */
    private static Date handleWithAccountDay(StoreVO store, Calendar targetCalendar) {
        int targetDay = targetCalendar.get(Calendar.DAY_OF_MONTH);
        String accountDay = store.getAccountDay();
        Long storeId = store.getStoreId();
        if (StringUtils.isEmpty(accountDay)) {
            logger.error("store={}, account_day is empty", storeId); // 账期为空的店铺
            return null;
        }
        List<Integer> accountDayArray =
                Arrays.stream(accountDay.split(",")).map(Integer::parseInt).sorted().collect(Collectors.toList());
        int targetDayPos = accountDayArray.indexOf(targetDay);
        int startDay;
        if (targetDayPos == -1) {
            logger.error("store={}, account_day={}, targetDay={} is not exist error", storeId, accountDay, targetDay); // abnormal error
            return null;
        } else if (targetDayPos == 0) {
            startDay = accountDayArray.get(accountDayArray.size() - 1);
        } else {
            startDay = accountDayArray.get(targetDayPos - 1);
        }

        if (startDay < targetDay) {// 开始日小于目标日，正常情况
            targetCalendar.set(Calendar.DAY_OF_MONTH, startDay);
        } else {// 开始日等于或大于目标日，需要往上个月翻
            targetCalendar.set(Calendar.DAY_OF_MONTH, 1);// 日期设置到一号
            do {
                targetCalendar.add(Calendar.MONTH, -1);
            } while (targetCalendar.getActualMaximum(Calendar.DAY_OF_MONTH) < startDay);
            targetCalendar.set(Calendar.DAY_OF_MONTH, startDay);
        }
        return targetCalendar.getTime();
    }

    /**
     * 获取结算单最后一天日期
     *
     * 目前设定新的业务逻辑:
     * 准备生成结算单: 获取开始时间的逻辑
     * 1. 查询上一次结算单的终止时间
     * 2. 如果没有, 则认为该店铺没有结算过, 则设为该店铺的签约起始日期
     *
     * @param store
     * @return
     * @throws ParseException
     */
    private Date getStartDate(StoreVO store) throws ParseException {
        SettlementLastResponse response =
                settlementQueryProvider.getLastSettlementByStoreId(SettlementLastByStoreIdRequest.
                        builder().storeId(store.getStoreId()).build()).getContext();
        if (Objects.nonNull(response)) {
            String str = response.getEndTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(str);
            return date;
        } else {
            if(store.getContractStartDate() ==null ){
                StoreByIdResponse storeByIdResponse =storeQueryProvider.getById(StoreByIdRequest.builder().storeId(store.getStoreId()).build()).getContext();
                if(store.getContractStartDate()==null){
                    if(storeByIdResponse!=null &&storeByIdResponse.getStoreVO().getContractStartDate()!=null){
                        store.setContractStartDate(storeByIdResponse.getStoreVO().getContractStartDate());
                    }
                }
            }
            ZoneId zoneId = ZoneId.systemDefault();
            LocalDateTime localDateTime = store.getContractStartDate();
            ZonedDateTime zdt = localDateTime.atZone(zoneId);
            return Date.from(zdt.toInstant());
        }
    }


    /**
     * 因为实际账期如果是10，前台和明细表中记录的结束时间是9
     *
     * @param targetDate
     * @return
     */
    private static Date getSettleEndDateForView(Date targetDate) {
        //账期结束时间，显示的其实是前一天，比如10号截至的账期，显示的是 xxx～9号的账期
        Calendar settleEndDateForView = Calendar.getInstance();
        settleEndDateForView.setTime(targetDate);
        settleEndDateForView.add(Calendar.DAY_OF_MONTH, -1);
        return settleEndDateForView.getTime();
    }

    /**
     * 格式化时间
     *
     * @param date
     * @return
     */
    private static String formatDate(Date date) {
        return DateUtil.format(date, DateUtil.FMT_DATE_1);
    }


    /**
     * 两个模块的marketing不能同步，将值转换成财务模块的marketingType
     *
     * @param list
     * @return
     */
    private static List<SettleGoodDTO.MarketingSettlementDTO> convertMarketingType(List<TradeItem.MarketingSettlement> list) {
        if (list != null && !list.isEmpty()) {
            return list.stream().map(item ->
                    SettleGoodDTO.MarketingSettlementDTO.builder()
                            .marketingType(SettleMarketingType.fromValue(item.getMarketingType().toValue()))
                            .splitPrice(item.getSplitPrice()).build()).collect(Collectors.toList());
        } else {
            return null;
        }
    }

    /**
     * 两个模块的coupon不能同步，将值转换成财务模块的couponType
     *
     * @param list
     * @return
     */
    private static List<SettleGoodDTO.CouponSettlementDTO> convertCouponType(List<TradeItem.CouponSettlement> list) {
        if (list != null && !list.isEmpty()) {
            return list.stream().map(item ->
                    SettleGoodDTO.CouponSettlementDTO.builder()
                            .couponCodeId(item.getCouponCodeId())
                            .couponCode(item.getCouponCode())
                            .couponType(SettleCouponType.fromValue(item.getCouponType().toValue()))
                            .splitPrice(item.getSplitPrice())
                            .reducePrice(item.getReducePrice())
                            .build()).collect(Collectors.toList());
        } else {
            return null;
        }
    }

    /**
     * 过滤订单信息
     *
     * @param trade
     */
    private static void trimTradeMarketing(Trade trade) {
        if (trade != null && trade.getTradePrice().isSpecial()) {
            //如果是特价的单子，抹去营销和优惠券的优惠
            trade.getTradeItems().forEach(item -> {
                item.setMarketingSettlements(null);
                item.setCouponSettlements(null);
            });
        }
    }

    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        //获取当天的日期，几号
        int targetDay = calendar.get(Calendar.DAY_OF_MONTH);
        System.out.println(">>>"+calendar.getTime());

        String str = "2020-03-31 00:00:00";
        ZoneId zoneId = ZoneId.systemDefault();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(str,formatter);
        ZonedDateTime zdt = localDateTime.atZone(zoneId);
        System.out.println(">>>"+Date.from(zdt.toInstant()));
    }
}
