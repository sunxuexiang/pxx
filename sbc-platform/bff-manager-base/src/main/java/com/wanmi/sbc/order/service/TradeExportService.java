package com.wanmi.sbc.order.service;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.util.excel.Column;
import com.wanmi.sbc.common.util.excel.ExcelHelper;
import com.wanmi.sbc.common.util.excel.impl.SpelColumnRender;
import com.wanmi.sbc.goods.api.provider.devanninggoodsinfo.DevanningGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.devanningGoodsInfo.DevanningGoodsInfoListByConditionRequest;
import com.wanmi.sbc.goods.api.response.devanninggoodsinfo.DevanningGoodsInfoListResponse;
import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import com.wanmi.sbc.goods.bean.vo.DevanningGoodsInfoVO;
import com.wanmi.sbc.marketing.bean.enums.MarketingType;
import com.wanmi.sbc.order.bean.enums.FlowState;
import com.wanmi.sbc.order.bean.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by mac on 2017/5/6.
 */
@Service
@Slf4j
public class TradeExportService {

    @Autowired
    private DevanningGoodsInfoQueryProvider devanningGoodsInfoQueryProvider;

    /**
     * @param tradeList
     * @param outputStream
     */
    public void export(List<TradeVO> tradeList, OutputStream outputStream, boolean existSupplier) {
        ExcelHelper excelHelper = new ExcelHelper();
        Column[] columns = {
                new Column("订单编号", new SpelColumnRender<TradeVO>("id")),
                new Column("父订单编号", new SpelColumnRender<TradeVO>("parentId")),
                new Column("支付单号", new SpelColumnRender<TradeVO>("payOrderNo")),
                new Column("店铺名称", new SpelColumnRender<TradeVO>("supplier.storeName")),
                new Column("下单时间", new SpelColumnRender<TradeVO>("tradeState.createTime")),
                new Column("客户名称", new SpelColumnRender<TradeVO>("buyer.name")),
                new Column("客户账号", new SpelColumnRender<TradeVO>("buyer.account")),
                new Column("客户级别", new SpelColumnRender<TradeVO>("buyer.levelName")),
                new Column("收货人", new SpelColumnRender<TradeVO>("consignee.name")),
                new Column("收货人手机", new SpelColumnRender<TradeVO>("consignee.phone")),
                new Column("收货人地址", new SpelColumnRender<TradeVO>("consignee.detailAddress")),
                new Column("支付方式", new SpelColumnRender<TradeVO>("payInfo.desc")),
                new Column("配送方式", (cell, object) -> {
                    TradeVO trade = (TradeVO) object;
                    DeliverWay deliverWay = trade.getDeliverWay();
                    String cellValue = deliverWay != null ? deliverWay.getDesc():"其他";
                    cell.setCellValue(cellValue);
                }),
                new Column("配送费用", new SpelColumnRender<TradeVO>("tradePrice.deliveryPrice != null ? tradePrice.deliveryPrice : '0.00'")),
                new Column("配送单号", (cell, object) -> {
                    TradeVO trade = (TradeVO) object;
                    cell.setCellValue(setLogisticNo(trade));
                }),
                new Column("订单商品金额", new SpelColumnRender<TradeVO>("tradePrice.goodsPrice")),
                new Column("订单特价金额", new SpelColumnRender<TradeVO>("tradePrice.special ? tradePrice.privilegePrice : '0.00'")),
                new Column("鲸币抵扣", new SpelColumnRender<TradeVO>("tradePrice.balancePrice != null && tradePrice.balancePrice > 0 ? tradePrice.balancePrice : '0.00'")),
                new Column("是否使用鲸币", new SpelColumnRender<TradeVO>("tradePrice.balancePrice != null && tradePrice.balancePrice > 0 ? '是':'否'")),
                new Column("订单应付金额", new SpelColumnRender<TradeVO>("tradePrice.totalPrice")),
                new Column("优惠券ID", new SpelColumnRender<TradeVO>("tradeCoupon.couponCode")),
                new Column("优惠券名称", new SpelColumnRender<TradeVO>("tradeCoupon.couponName")),
                new Column("优惠金额", new SpelColumnRender<TradeVO>("tradeCoupon.discountsAmount")),
                new Column("满减金额", (cell, object) -> {
                    TradeVO trade = (TradeVO) object;
                    if(CollectionUtils.isNotEmpty(trade.getTradePrice().getDiscountsPriceDetails())){
                        trade.getTradePrice().getDiscountsPriceDetails()
                                .stream().filter(item -> item.getMarketingType() == MarketingType.REDUCTION).findFirst()
                                .ifPresent(val -> {
                                    cell.setCellValue(val.getDiscounts().doubleValue());
                                });
                    }else {
                        cell.setCellValue(0.00);
                    }
                }),
                new Column("满折金额", (cell, object) -> {
                    TradeVO trade = (TradeVO) object;
                    if(CollectionUtils.isNotEmpty(trade.getTradePrice().getDiscountsPriceDetails())){
                        trade.getTradePrice().getDiscountsPriceDetails()
                                .stream().filter(item -> item.getMarketingType() == MarketingType.DISCOUNT).findFirst()
                                .ifPresent(val -> {
                                    cell.setCellValue(val.getDiscounts().doubleValue());
                                });

                    }else {
                        cell.setCellValue(0.00);
                    }
                }),

                new Column("纸箱费", new SpelColumnRender<TradeVO>("tradePrice.packingPrice")),
                new Column("商品优惠金额", (cell, object) -> {
                    TradeVO trade = (TradeVO) object;
                    BigDecimal cellValue = Objects.nonNull(trade.getTradePrice().getCmbDiscountsPrice()) ? trade.getTradePrice().getCmbDiscountsPrice() : BigDecimal.ZERO;
                    if ("4".equals(trade.getActivityType())) {
                        BigDecimal goodsPrice = Objects.nonNull(trade.getTradePrice().getGoodsPrice()) ? trade.getTradePrice().getGoodsPrice() : BigDecimal.ZERO;
                        BigDecimal paidPrice = Objects.nonNull(trade.getTradePrice().getPaidPrice()) ? trade.getTradePrice().getPaidPrice() : BigDecimal.ZERO;
                        cellValue = goodsPrice.subtract(paidPrice).setScale(2, BigDecimal.ROUND_DOWN);
                    }
                    cell.setCellValue(cellValue.toString());
                }),
                new Column("囤货已付金额", new SpelColumnRender<TradeVO>("tradePrice.paidPrice")),
                new Column("商品SKU", (cell, object) -> {
                    TradeVO trade = (TradeVO) object;
                    StringBuilder sb = new StringBuilder((trade.getTradeItems().size() + trade.getGifts().size()) * 32);
                    trade.getTradeItems()
                            .forEach(i -> {
                                sb.append(i.getSkuNo()).append(",");
                            });
                    trade.getGifts().forEach(j -> {
                        sb.append(j.getSkuNo()).append(",");
                    });
                    sb.trimToSize();
                    cell.setCellValue(sb.substring(0, sb.length() - 1));
                }),
                new Column("商品种类", (cell, object) -> {
                    TradeVO trade = (TradeVO) object;
                    cell.setCellValue(trade.skuItemMap().size());
                }),
                new Column("商品总数量", (cell, object) -> {
                    Long size = ((TradeVO) object)
                            .getTradeItems()
                            .stream()
                            .map(TradeItemVO::getNum)
                            .reduce((sum, item) -> {
                                sum += item;
                                return sum;
                            }).get();
                    //赠品数量
                    long sum = ((TradeVO) object)
                            .getGifts()
                            .stream()
                            .mapToLong(TradeItemVO::getNum).sum();
                    size = size + sum;
                    cell.setCellValue((double) size);
                }),
                new Column("买家备注", new SpelColumnRender<TradeVO>("buyerRemark")),
                new Column("卖家备注", new SpelColumnRender<TradeVO>("sellerRemark")),
                new Column("业务代表", new SpelColumnRender<TradeVO>("employeeName")),
                new Column("白鲸管家", new SpelColumnRender<TradeVO>("managerName")),
                new Column("入驻商家代表", new SpelColumnRender<TradeVO>("investmentManager")),
                new Column("订单状态", (cell, object) -> {
                    TradeVO trade = (TradeVO) object;
                    FlowState flowState = trade.getTradeState().getFlowState();
                    String cellValue = "";
                    switch (flowState) {
                        case INIT:
                            cellValue = "待审核";
                            break;
                        case AUDIT:
                        case DELIVERED_PART:
                            cellValue = "待发货";
                            break;
                        case DELIVERED:
                            cellValue = "待收货";
                            break;
                        case CONFIRMED:
                            cellValue = "已收货";
                            break;
                        case COMPLETED:
                            cellValue = "已完成";
                            break;
                        case VOID:
                            cellValue = "已作废";
                            break;
                        default:
                    }

                    cell.setCellValue(cellValue);
                }),
                new Column("发货仓", new SpelColumnRender<TradeVO>("wareName")),
                new Column("付款状态", new SpelColumnRender<TradeVO>("tradeState.payState.getDescription()")),
                new Column("发货状态", new SpelColumnRender<TradeVO>("tradeState.deliverStatus.getDescription()")),
                new Column("发票类型", new SpelColumnRender<TradeVO>("invoice.type == 0 ? '普通发票' : invoice.type == 1 ?'增值税发票':'不需要发票' ")),
                new Column("开票项目", new SpelColumnRender<TradeVO>("invoice.projectName")),
                new Column("发票抬头", new SpelColumnRender<TradeVO>("invoice.type == 0 ? invoice.generalInvoice.title:" +
                        "invoice.specialInvoice.companyName")),
        };
        if (existSupplier) {
            List<Column> columnList = Stream.of(columns).collect(Collectors.toList());
            columnList.add(
                    new Column("商家", new SpelColumnRender<ReturnOrderVO>("supplier.supplierName"))
            );
            columns = columnList.toArray(new Column[0]);
        }
        excelHelper
                .addSheet(
                        "订单导出",
                        columns,
                        tradeList
                );
        excelHelper.write(outputStream);
    }


    /**
     * @param tradeList
     * @param outputStream
     */
    public void export(List<TradeVO> tradeList, Map<String, String> erpNoMap, OutputStream outputStream, boolean existSupplier, boolean isDetailed,Map<String, BigDecimal> coinMap) {
        Map<String,DevanningGoodsInfoVO> goodsInfoVOMap = new HashMap<>(tradeList.size()*10);
        List<TradeDetailedExportVO> detailedExportVOList = new ArrayList<>();
        for (TradeVO vo : tradeList) {
            if (isDetailed) {
                initGoodsInfoVoToMap(vo,goodsInfoVOMap);
                for (int i = 0; i < vo.getTradeItems().size(); i++) {
                    TradeItemVO itemVO = vo.getTradeItems().get(i);
                    log.info("====报表itemVO：{}，", JSONObject.toJSONString(itemVO));
                    log.info("====报表金额：{}，{},{},{}", itemVO.getPrice(), itemVO.getSplitPrice(), itemVO.getLevelPrice(), itemVO.getNum());

                    //折扣金额
                    BigDecimal levelPrice = itemVO.getPrice().multiply(new BigDecimal(itemVO.getNum())).setScale(2, BigDecimal.ROUND_DOWN);
                    levelPrice = levelPrice.subtract(itemVO.getSplitPrice()).setScale(2, BigDecimal.ROUND_DOWN);
                    //实付金额
                    BigDecimal price = itemVO.getPrice().multiply(new BigDecimal(itemVO.getNum())).setScale(2, BigDecimal.ROUND_DOWN);
                    price = price.subtract(itemVO.getSplitPrice()).setScale(2, BigDecimal.ROUND_DOWN);
                    BigDecimal lelPrice = itemVO.getLevelPrice().multiply(new BigDecimal(itemVO.getNum())).setScale(2, BigDecimal.ROUND_DOWN);
                    lelPrice = lelPrice.subtract(price).setScale(2, BigDecimal.ROUND_DOWN);
                    //金额小计
                    BigDecimal xjPrice = itemVO.getLevelPrice().multiply(new BigDecimal(itemVO.getNum())).setScale(2, BigDecimal.ROUND_DOWN);

                    TradeDetailedExportVO exportVO = new TradeDetailedExportVO();
                    exportVO.setParentId(vo.getParentId());
                    exportVO.setActivityType(vo.getActivityType());
                    exportVO.setId(vo.getId());
                    exportVO.setPayOrderNo(vo.getPayOrderNo());
                    exportVO.setTradeState(vo.getTradeState());
                    exportVO.setBuyer(vo.getBuyer());
                    exportVO.setConsignee(vo.getConsignee());
                    exportVO.setPayInfo(vo.getPayInfo());
                    exportVO.setTradePrice(vo.getTradePrice());
                    exportVO.setTradeCoupon(vo.getTradeCoupon());
                    exportVO.setBuyerRemark(vo.getBuyerRemark());
                    exportVO.setSellerRemark(vo.getSellerRemark());
                    exportVO.setEmployeeName(vo.getEmployeeName());
                    exportVO.setInvestmentManager(vo.getInvestmentManager());
                    exportVO.setManagerName(vo.getManagerName());
                    exportVO.setWareName(vo.getWareName());
                    exportVO.setInvoice(vo.getInvoice());
                    exportVO.setDeliverWay(vo.getDeliverWay());
                    exportVO.setSupplierName(Objects.nonNull(vo.getSupplier()) ? vo.getSupplier().getSupplierName() : "");
                    exportVO.setStoreName(Objects.nonNull(vo.getSupplier()) ? vo.getSupplier().getStoreName() : "");
                    exportVO.setSkuNo(itemVO.getSkuNo());
                    exportVO.setSkuName(itemVO.getSkuName());
                    exportVO.setGoodsSubtitle(itemVO.getGoodsSubtitle());
                    exportVO.setNum(itemVO.getNum());
                    exportVO.setSplitPrice(xjPrice);
                    exportVO.setLevelPrice(levelPrice);
                    exportVO.setCostPrice(itemVO.getCost());
                    exportVO.setActualPrice(itemVO.getSplitPrice());
                    exportVO.setPrice(itemVO.getPrice());
                    exportVO.setErpGoodsInfoNo(erpNoMap.get(itemVO.getSkuId()));
                 //   exportVO.setGoodsInfoBarcode(goodsInfoVOMap.get(itemVO.getSkuId())!=null? goodsInfoVOMap.get(itemVO.getSkuId()).getGoodsInfoBarcode():null);
                    exportVO.setStockName(itemVO.getStockName());
                    //新增反鲸币
                    if (Objects.nonNull(coinMap.get(itemVO.getSkuId()))) {
                        exportVO.setReturnCoin(coinMap.get(itemVO.getSkuId()));
                    }else {
                        exportVO.setReturnCoin(BigDecimal.ZERO);
                    }
                    exportVO.setLogisticNo(setLogisticNo(vo));

                    detailedExportVOList.add(exportVO);

//                    if(i == 0){
//                        exportVO.setSkuNo(itemVO.getSkuNo());
//                        exportVO.setSkuName(itemVO.getSkuName());
//                        exportVO.setGoodsSubtitle(itemVO.getGoodsSubtitle());
//                        exportVO.setNum(itemVO.getNum());
//                        exportVO.setPrice(lelPrice);
//                        exportVO.setSplitPrice(xjPrice);
//                        exportVO.setLevelPrice(levelPrice);
//                        exportVO.setActualPrice(lelPrice);
//                        exportVO.setSplitPrice(xjPrice);
//                        exportVO.setLevelPrice(levelPrice);
//                        exportVO.setPrice(itemVO.getPrice());
//                        exportVO.setErpGoodsInfoNo(erpNoMap.get(itemVO.getSkuId()));
//                        detailedExportVOList.add(exportVO);
//                    }else{
//                        TradeDetailedExportVO exportVoOhter = new TradeDetailedExportVO();
//                        exportVoOhter.setSkuNo(itemVO.getSkuNo());
//                        exportVoOhter.setSkuName(itemVO.getSkuName());
//                        exportVoOhter.setGoodsSubtitle(itemVO.getGoodsSubtitle());
//                        exportVoOhter.setNum(itemVO.getNum());
//                        exportVoOhter.setActualPrice(lelPrice);
//                        exportVoOhter.setSplitPrice(xjPrice);
//                        exportVoOhter.setLevelPrice(levelPrice);
//                        exportVoOhter.setPrice(itemVO.getPrice());
//                        exportVoOhter.setErpGoodsInfoNo(erpNoMap.get(itemVO.getSkuId()));
//                        detailedExportVOList.add(exportVoOhter);
//                    }
                }
                for (int i = 0; i < vo.getGifts().size(); i++) {
                    TradeItemVO itemVO = vo.getGifts().get(i);
                    log.info("====报表itemVO：{}，", JSONObject.toJSONString(itemVO));
                    log.info("====报表金额：{}，{},{},{}", itemVO.getPrice(), itemVO.getSplitPrice(), itemVO.getLevelPrice(), itemVO.getNum());

                    //折扣金额
                    BigDecimal levelPrice = itemVO.getPrice().multiply(new BigDecimal(itemVO.getNum())).setScale(2, BigDecimal.ROUND_DOWN);
                    levelPrice = levelPrice.subtract(itemVO.getSplitPrice()).setScale(2, BigDecimal.ROUND_DOWN);
                    //实付金额
                    BigDecimal price = itemVO.getPrice().multiply(new BigDecimal(itemVO.getNum())).setScale(2, BigDecimal.ROUND_DOWN);
                    price = price.subtract(itemVO.getSplitPrice()).setScale(2, BigDecimal.ROUND_DOWN);
                    BigDecimal lelPrice = itemVO.getLevelPrice().multiply(new BigDecimal(itemVO.getNum())).setScale(2, BigDecimal.ROUND_DOWN);
                    lelPrice = lelPrice.subtract(price).setScale(2, BigDecimal.ROUND_DOWN);
                    //金额小计
                    BigDecimal xjPrice = itemVO.getLevelPrice().multiply(new BigDecimal(itemVO.getNum())).setScale(2, BigDecimal.ROUND_DOWN);

                    TradeDetailedExportVO exportVO = new TradeDetailedExportVO();
                    exportVO.setParentId(vo.getParentId());
                    exportVO.setId(vo.getId());
                    exportVO.setPayOrderNo(vo.getPayOrderNo());
                    exportVO.setTradeState(vo.getTradeState());
                    exportVO.setBuyer(vo.getBuyer());
                    exportVO.setConsignee(vo.getConsignee());
                    exportVO.setPayInfo(vo.getPayInfo());
                    exportVO.setTradePrice(vo.getTradePrice());
                    exportVO.setTradeCoupon(vo.getTradeCoupon());
                    exportVO.setBuyerRemark(vo.getBuyerRemark());
                    exportVO.setSellerRemark(vo.getSellerRemark());
                    exportVO.setEmployeeName(vo.getEmployeeName());
                    exportVO.setInvestmentManager(vo.getInvestmentManager());
                    exportVO.setManagerName(vo.getManagerName());
                    exportVO.setWareName(vo.getWareName());
                    exportVO.setInvoice(vo.getInvoice());
                    exportVO.setDeliverWay(vo.getDeliverWay());
                    exportVO.setSupplierName(Objects.nonNull(vo.getSupplier()) ? vo.getSupplier().getSupplierName() : "");
                    exportVO.setStoreName(Objects.nonNull(vo.getSupplier()) ? vo.getSupplier().getStoreName() : "");

                    exportVO.setSkuNo(itemVO.getSkuNo());
                    exportVO.setSkuName(itemVO.getSkuName());
                    exportVO.setGoodsSubtitle(itemVO.getGoodsSubtitle());
                    exportVO.setNum(itemVO.getNum());
                    exportVO.setActualPrice(itemVO.getSplitPrice());
                    exportVO.setSplitPrice(xjPrice);
                    exportVO.setLevelPrice(levelPrice);
                    exportVO.setCostPrice(itemVO.getCost());
                    exportVO.setPrice(itemVO.getPrice());
                    exportVO.setErpGoodsInfoNo(erpNoMap.get(itemVO.getSkuId()));
                    exportVO.setReturnCoin(vo.getReturnCoin());
                    // exportVO.setGoodsInfoBarcode(goodsInfoVOMap.get(itemVO.getSkuId())!=null? goodsInfoVOMap.get(itemVO.getSkuId()).getGoodsInfoBarcode():null);
                    exportVO.setLogisticNo(setLogisticNo(vo));
                    detailedExportVOList.add(exportVO);
                }
            }
//            else{
//                detailedExportVOList.add(exportVO);
//            }

        }


        ExcelHelper excelHelper = new ExcelHelper();
        Column[] columns = {
                new Column("订单编号", new SpelColumnRender<TradeDetailedExportVO>("id")),
                new Column("父订单编号", new SpelColumnRender<TradeDetailedExportVO>("parentId")),
                new Column("支付单号", new SpelColumnRender<TradeDetailedExportVO>("payOrderNo")),
                new Column("店铺名称", new SpelColumnRender<TradeDetailedExportVO>("storeName")),
                new Column("下单时间", new SpelColumnRender<TradeDetailedExportVO>("tradeState.createTime")),
                new Column("客户名称", new SpelColumnRender<TradeDetailedExportVO>("buyer.name")),
                new Column("客户账号", new SpelColumnRender<TradeDetailedExportVO>("buyer.account")),
                new Column("客户级别", new SpelColumnRender<TradeDetailedExportVO>("buyer.levelName")),
                new Column("收货人", new SpelColumnRender<TradeDetailedExportVO>("consignee.name")),
                new Column("收货人手机", new SpelColumnRender<TradeDetailedExportVO>("consignee.phone")),
                new Column("收货人地址", new SpelColumnRender<TradeDetailedExportVO>("consignee.detailAddress")),
                new Column("支付方式", new SpelColumnRender<TradeDetailedExportVO>("payInfo.desc")),
                new Column("配送方式", (cell, object) -> {
                    TradeDetailedExportVO trade = (TradeDetailedExportVO) object;
                    DeliverWay deliverWay = trade.getDeliverWay();
                    String cellValue = deliverWay != null ? deliverWay.getDesc():"其他";
                    cell.setCellValue(cellValue);
                }),
                new Column("配送费用", new SpelColumnRender<TradeDetailedExportVO>("tradePrice.deliveryPrice != null ? tradePrice.deliveryPrice : '0.00'")),
                new Column("配送单号", new SpelColumnRender<TradeDetailedExportVO>("logisticNo")),
                new Column("订单商品金额", new SpelColumnRender<TradeDetailedExportVO>("tradePrice.goodsPrice")),
                new Column("订单特价金额", new SpelColumnRender<TradeDetailedExportVO>("tradePrice.special ? tradePrice.privilegePrice : '0.00'")),
                new Column("鲸币抵扣", new SpelColumnRender<TradeDetailedExportVO>("tradePrice.balancePrice != null && tradePrice.balancePrice > 0 ? tradePrice.balancePrice : '0.00'")),
                new Column("是否使用鲸币", new SpelColumnRender<TradeDetailedExportVO>("tradePrice.balancePrice != null && tradePrice.balancePrice > 0 ? '是':'否'")),
                new Column("优惠券ID", new SpelColumnRender<TradeDetailedExportVO>("tradeCoupon.couponCode")),
                new Column("优惠券名称", new SpelColumnRender<TradeDetailedExportVO>("tradeCoupon.couponName")),
                new Column("优惠金额", new SpelColumnRender<TradeDetailedExportVO>("tradeCoupon.discountsAmount")),
                new Column("满减金额", (cell, object) -> {
                    TradeDetailedExportVO trade = (TradeDetailedExportVO) object;
                    if(CollectionUtils.isNotEmpty(trade.getTradePrice().getDiscountsPriceDetails())){
                        trade.getTradePrice().getDiscountsPriceDetails()
                                .stream().filter(item -> item.getMarketingType() == MarketingType.REDUCTION).findFirst()
                                .ifPresent(val -> {
                                    cell.setCellValue(val.getDiscounts().doubleValue());
                                });

                    }else {
                        cell.setCellValue(0.00);
                    }
                }),
                new Column("满折金额", (cell, object) -> {
                    TradeDetailedExportVO trade = (TradeDetailedExportVO) object;
                    if(CollectionUtils.isNotEmpty(trade.getTradePrice().getDiscountsPriceDetails())){
                        trade.getTradePrice().getDiscountsPriceDetails()
                                .stream().filter(item -> item.getMarketingType() == MarketingType.DISCOUNT).findFirst()
                                .ifPresent(val -> {
                                    cell.setCellValue(val.getDiscounts().doubleValue());
                                });

                    }else {
                        cell.setCellValue(0.00);
                    }
                }),
                new Column("纸箱费", new SpelColumnRender<TradeDetailedExportVO>("tradePrice.packingPrice")),
                new Column("商品优惠金额", (cell, object) -> {
                    TradeDetailedExportVO trade = (TradeDetailedExportVO) object;
                    BigDecimal cellValue = Objects.nonNull(trade.getTradePrice().getCmbDiscountsPrice()) ? trade.getTradePrice().getCmbDiscountsPrice() : BigDecimal.ZERO;
                    if ("4".equals(trade.getActivityType())) {
                        BigDecimal goodsPrice = Objects.nonNull(trade.getTradePrice().getGoodsPrice()) ? trade.getTradePrice().getGoodsPrice() : BigDecimal.ZERO;
                        BigDecimal paidPrice = Objects.nonNull(trade.getTradePrice().getPaidPrice()) ? trade.getTradePrice().getPaidPrice() : BigDecimal.ZERO;
                        cellValue = goodsPrice.subtract(paidPrice).setScale(2, BigDecimal.ROUND_DOWN);
                    }
                    cell.setCellValue(cellValue.toString());
                }),
                new Column("囤货已付金额", new SpelColumnRender<TradeDetailedExportVO>("tradePrice.paidPrice")),
                new Column("买家备注", new SpelColumnRender<TradeDetailedExportVO>("buyerRemark")),
                new Column("卖家备注", new SpelColumnRender<TradeDetailedExportVO>("sellerRemark")),
                new Column("业务代表", new SpelColumnRender<TradeDetailedExportVO>("employeeName")),
                new Column("白鲸管家", new SpelColumnRender<TradeDetailedExportVO>("managerName")),
                new Column("入驻商家代表", new SpelColumnRender<TradeDetailedExportVO>("investmentManager")),
                new Column("订单状态", (cell, object) -> {
                    TradeDetailedExportVO trade = (TradeDetailedExportVO) object;
                    String cellValue = "";
                    if (trade.getTradeState() != null) {
                        FlowState flowState = trade.getTradeState().getFlowState();
                        switch (flowState) {
                            case INIT:
                                cellValue = "待审核";
                                break;
                            case AUDIT:
                            case DELIVERED_PART:
                                cellValue = "待发货";
                                break;
                            case DELIVERED:
                                cellValue = "待收货";
                                break;
                            case CONFIRMED:
                                cellValue = "已收货";
                                break;
                            case COMPLETED:
                                cellValue = "已完成";
                                break;
                            case VOID:
                                cellValue = "已作废";
                                break;
                            default:
                        }
                    }
                    cell.setCellValue(cellValue);
                }),
                new Column("发货仓", new SpelColumnRender<TradeDetailedExportVO>("wareName")),
                new Column("付款状态", new SpelColumnRender<TradeDetailedExportVO>("tradeState.payState.getDescription()")),
                new Column("发货状态", new SpelColumnRender<TradeDetailedExportVO>("tradeState.deliverStatus.getDescription()")),
                new Column("发票类型", new SpelColumnRender<TradeDetailedExportVO>("invoice.type == 0 ? '普通发票' : invoice.type == 1 ?'增值税发票':'不需要发票' ")),
                new Column("开票项目", new SpelColumnRender<TradeDetailedExportVO>("invoice.projectName")),
                new Column("发票抬头", new SpelColumnRender<TradeDetailedExportVO>("invoice.type == 0 ? invoice.generalInvoice.title:" +
                        "invoice.specialInvoice.companyName")),

        };
        if (existSupplier) {
            List<Column> columnList = Stream.of(columns).collect(Collectors.toList());
            columnList.add(
                    new Column("店铺名称", new SpelColumnRender<TradeDetailedExportVO>("storeName"))
            );
            columns = columnList.toArray(new Column[0]);
        }
        if (isDetailed) {
            List<Column> columnList = Stream.of(columns).collect(Collectors.toList());
            // 商品SKU	ERP编码	商品名称	规格	单价	数量	折扣金额	实付金额	金额小计 返鲸币
            columnList.add(new Column("商品SKU", new SpelColumnRender<TradeDetailedExportVO>("skuNo")));
            columnList.add(new Column("ERP编码", new SpelColumnRender<TradeDetailedExportVO>("erpGoodsInfoNo")));
            columnList.add(new Column("商品名称", new SpelColumnRender<TradeDetailedExportVO>("skuName")));
            columnList.add(new Column("规格", new SpelColumnRender<TradeDetailedExportVO>("goodsSubtitle")));
            columnList.add(new Column("条形码", new SpelColumnRender<TradeDetailedExportVO>("goodsInfoBarcode")));
            columnList.add(new Column("单价", new SpelColumnRender<TradeDetailedExportVO>("price")));
            columnList.add(new Column("成本价", new SpelColumnRender<TradeDetailedExportVO>("costPrice")));
            columnList.add(new Column("数量", new SpelColumnRender<TradeDetailedExportVO>("num")));
            columnList.add(new Column("折扣金额", new SpelColumnRender<TradeDetailedExportVO>("levelPrice")));
            columnList.add(new Column("实付金额", new SpelColumnRender<TradeDetailedExportVO>("actualPrice")));
            columnList.add(new Column("金额小计", new SpelColumnRender<TradeDetailedExportVO>("splitPrice")));
            columnList.add(new Column("返鲸币", new SpelColumnRender<TradeDetailedExportVO>("returnCoin")));
            columnList.add(new Column("库位名称", new SpelColumnRender<TradeDetailedExportVO>("stockName")));
            columns = columnList.toArray(new Column[0]);
        }
        excelHelper
                .addSheet(
                        "订单导出",
                        columns,
                        detailedExportVOList
                );
        excelHelper.write(outputStream);
    }

    private static String setLogisticNo(TradeVO vo) {
        String logisticNo = "";
        if (DeliverWay.isTmsDelivery(vo.getDeliverWay())) {
            TradeDeliverVO deliverVO = vo.getTradeDelivers().stream().findFirst().orElse(null);
            if (Objects.nonNull(deliverVO) && Objects.nonNull(deliverVO.getLogistics()) && StringUtils.isNotBlank(deliverVO.getLogistics().getLogisticNo())) {
                logisticNo = deliverVO.getLogistics().getLogisticNo();
            }
        }
        return logisticNo;
    }

    private void initGoodsInfoVoToMap(TradeVO vo,Map<String,DevanningGoodsInfoVO> goodsInfoVOMap) {

        List<String> tradeSkuIds = new ArrayList<>(vo.getTradeItems().size()*2);
        List<String> skuIds = vo.getTradeItems().stream().map(TradeItemVO::getSkuId).distinct().collect(Collectors.toList());
        tradeSkuIds.addAll(skuIds);
        if(CollectionUtils.isNotEmpty(vo.getGifts())){
            skuIds = vo.getGifts().stream().map(TradeItemVO::getSkuId).distinct().collect(Collectors.toList());
            tradeSkuIds.addAll(skuIds);
        }
        if(org.apache.commons.collections.CollectionUtils.isNotEmpty(tradeSkuIds)) {
             List<String> newSkuIds = new ArrayList<>(tradeSkuIds.size());
            tradeSkuIds.forEach(skuId->{
                DevanningGoodsInfoVO goodsInfoVO = goodsInfoVOMap.get(skuId);
                if(goodsInfoVO==null){
                    newSkuIds.add(skuId);
                }
            });
            if(newSkuIds.size()>0){
                DevanningGoodsInfoListResponse goodsInfoListResponse = devanningGoodsInfoQueryProvider.listByCondition(DevanningGoodsInfoListByConditionRequest.builder().goodsInfoIds(newSkuIds).build()).getContext();
                if (goodsInfoListResponse != null && org.apache.commons.collections4.CollectionUtils.isNotEmpty(goodsInfoListResponse.getDevanningGoodsInfoVOS())) {
                    goodsInfoListResponse.getDevanningGoodsInfoVOS().forEach(devanningGoodsInfoVO -> {
                        goodsInfoVOMap.put(devanningGoodsInfoVO.getGoodsInfoId(),devanningGoodsInfoVO);
                    });
                }
            }
        }
    }

}
