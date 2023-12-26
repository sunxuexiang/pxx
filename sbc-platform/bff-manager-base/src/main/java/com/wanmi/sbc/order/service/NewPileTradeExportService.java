package com.wanmi.sbc.order.service;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.util.excel.Column;
import com.wanmi.sbc.common.util.excel.ExcelHelper;
import com.wanmi.sbc.common.util.excel.impl.SpelColumnRender;
import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import com.wanmi.sbc.order.api.provider.trade.newPileTrade.NewPileTradeProvider;
import com.wanmi.sbc.order.api.request.trade.newpile.GoodsPickStockIdsRequest;
import com.wanmi.sbc.order.api.response.trade.GoodsPickStockResponse;
import com.wanmi.sbc.order.bean.enums.FlowState;
import com.wanmi.sbc.order.bean.enums.NewPileFlowState;
import com.wanmi.sbc.order.bean.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
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
public class NewPileTradeExportService {

    @Autowired
    private NewPileTradeProvider newPileTradeProvider;

    /**
     * @param tradeList
     * @param outputStream
     */
    public void export(List<NewPileTradeVO> tradeList, OutputStream outputStream, boolean existSupplier) {
        ExcelHelper excelHelper = new ExcelHelper();
        Column[] columns = {
                new Column("订单编号", new SpelColumnRender<NewPileTradeVO>("id")),
                new Column("父订单编号",new SpelColumnRender<NewPileTradeVO>("parentId")),
                new Column("支付单号",new SpelColumnRender<NewPileTradeVO>("payOrderNo")),
                new Column("下单时间", new SpelColumnRender<NewPileTradeVO>("tradeState.createTime")),
                new Column("客户名称", new SpelColumnRender<NewPileTradeVO>("buyer.name")),
                new Column("客户账号", new SpelColumnRender<NewPileTradeVO>("buyer.account")),
                new Column("客户级别", new SpelColumnRender<NewPileTradeVO>("buyer.levelName")),
                new Column("收货人", new SpelColumnRender<NewPileTradeVO>("consignee.name")),
                new Column("收货人手机", new SpelColumnRender<NewPileTradeVO>("consignee.phone")),
                new Column("收货人地址", new SpelColumnRender<NewPileTradeVO>("consignee.detailAddress")),
                new Column("支付方式", new SpelColumnRender<NewPileTradeVO>("payInfo.desc")),
                new Column("配送方式",  (cell, object) -> {
                    NewPileTradeVO trade = (NewPileTradeVO) object;
                    DeliverWay deliverWay = trade.getDeliverWay();
                    String cellValue = "";
                    switch (deliverWay) {
                        case PICK_SELF:
                            cellValue = "自提";
                            break;
                        case LOGISTICS:
                            cellValue ="物流";
                            break;
                        case EXPRESS:
                            cellValue = "快递到家";
                            break;
                        case DELIVERY_HOME:
                            cellValue = "本地配送";
                            break;
                        default:
                            cellValue = "其他";
                            break;
                    }

                    cell.setCellValue(cellValue);
                }),
                new Column("配送费用", new SpelColumnRender<NewPileTradeVO>("tradePrice.deliveryPrice != null ? tradePrice.deliveryPrice : '0.00'")),
                new Column("订单商品金额", new SpelColumnRender<NewPileTradeVO>("tradePrice.goodsPrice")),
                new Column("订单特价金额", new SpelColumnRender<NewPileTradeVO>("tradePrice.special ? tradePrice.privilegePrice : '0.00'")),
                new Column("订单应付金额", new SpelColumnRender<NewPileTradeVO>("tradePrice.totalPrice")),
                new Column("鲸币抵扣", new SpelColumnRender<NewPileTradeVO>("tradePrice.balancePrice != null && tradePrice.balancePrice > 0 ? tradePrice.balancePrice : '0.00'")),
                new Column("是否使用鲸币", new SpelColumnRender<NewPileTradeVO>("tradePrice.balancePrice != null && tradePrice.balancePrice > 0 ? '是':'否'")),
                new Column("优惠券ID",new SpelColumnRender<NewPileTradeVO>("tradeCoupon.couponCode")),
                new Column("优惠券名称",new SpelColumnRender<NewPileTradeVO>("tradeCoupon.couponName")),
                new Column("满减优惠",new SpelColumnRender<NewPileTradeVO>("tradePrice.discountsPrice - tradePrice.couponPrice")),
                new Column("优惠券",new SpelColumnRender<NewPileTradeVO>("tradePrice.couponPrice")),
                new Column("商品SKU", (cell, object) -> {
                    NewPileTradeVO trade = (NewPileTradeVO) object;
                    StringBuilder sb = new StringBuilder((trade.getTradeItems().size() + trade.getGifts().size()) * 32);
                    trade.getTradeItems()
                            .forEach(i -> {
                                sb.append(i.getSkuNo()).append(",");
                            });
                    trade.getGifts().forEach(j ->{
                        sb.append(j.getSkuNo()).append(",");
                    });
                    sb.trimToSize();
                    cell.setCellValue(sb.substring(0, sb.length() - 1));
                }),
                new Column("商品种类", (cell, object) -> {
                    NewPileTradeVO trade = (NewPileTradeVO) object;
                    cell.setCellValue(trade.skuItemMap().size());
                }),
                new Column("商品总数量", (cell, object) -> {
                    Long size = ((NewPileTradeVO) object)
                            .getTradeItems()
                            .stream()
                            .map(TradeItemVO::getNum)
                            .reduce((sum, item) -> {
                                sum += item;
                                return sum;
                            }).get();
                    //赠品数量
                    long sum = ((NewPileTradeVO) object)
                            .getGifts()
                            .stream()
                            .mapToLong(TradeItemVO::getNum).sum();
                    size = size + sum;
                    cell.setCellValue((double) size);
                }),
                new Column("买家备注", new SpelColumnRender<NewPileTradeVO>("buyerRemark")),
                new Column("卖家备注", new SpelColumnRender<NewPileTradeVO>("sellerRemark")),
                new Column("业务员",new SpelColumnRender<NewPileTradeVO>("employeeName")),
                new Column("订单状态", (cell, object) -> {
                    NewPileTradeVO trade = (NewPileTradeVO) object;
                    String cellValue = "";
                    NewPileFlowState flowState = trade.getTradeState().getFlowState();
                    cellValue = (Objects.nonNull(flowState) ? flowState.getDescription() : "");
                    cell.setCellValue(cellValue);
                }),
                new Column("发货仓", new SpelColumnRender<NewPileTradeVO>("wareName")),
                new Column("付款状态", new SpelColumnRender<NewPileTradeVO>("tradeState.payState.getDescription()")),
                //new Column("发货状态", new SpelColumnRender<NewPileTradeVO>("tradeState.deliverStatus.getDescription()")),
                new Column("发票类型", new SpelColumnRender<NewPileTradeVO>("invoice.type == 0 ? '普通发票' : invoice.type == 1 ?'增值税发票':'不需要发票' ")),
                new Column("开票项目", new SpelColumnRender<NewPileTradeVO>("invoice.projectName")),
                new Column("发票抬头", new SpelColumnRender<NewPileTradeVO>("invoice.type == 0 ? invoice.generalInvoice.title:" +
                        "invoice.specialInvoice.companyName"))
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
                        "囤货订单导出",
                        columns,
                        tradeList
                );
        excelHelper.write(outputStream);
    }


    /**
     * @param tradeList
     * @param outputStream
     */
    public void export(List<NewPileTradeVO> tradeList, Map<String, String> erpNoMap, OutputStream outputStream, boolean existSupplier, boolean isDetailed) {
        List<NewPileTradeDetailedExportVO> detailedExportVOList = new ArrayList<>();
        List<String> newPileTradeIds = tradeList.stream().map(item -> item.getId()).collect(Collectors.toList());

        List<GoodsPickStockResponse> goodsPickStocks = newPileTradeProvider.findByNewPileTradeNos(GoodsPickStockIdsRequest.builder().newPileTradeIds(newPileTradeIds).build()).getContext();
        for (NewPileTradeVO vo: tradeList) {
            if(isDetailed){
                List<GoodsPickStockResponse> curGoodsPickStock = goodsPickStocks.stream().filter(item -> Objects.equals(vo.getId(), item.getNewPileTradeNo())).collect(Collectors.toList());
                for(int i = 0; i < vo.getTradeItems().size(); i++){
                    TradeItemVO itemVO = vo.getTradeItems().get(i);
                    log.info("====报表itemVO：{}，", JSONObject.toJSONString(itemVO));
                    log.info("====报表金额：{}，{},{},{}",itemVO.getPrice(),itemVO.getSplitPrice(),itemVO.getLevelPrice(),itemVO.getNum());

                    //折扣金额
                    BigDecimal levelPrice = itemVO.getPrice().multiply(new BigDecimal(itemVO.getNum())).setScale(2,BigDecimal.ROUND_DOWN);
                    levelPrice =  levelPrice.subtract(itemVO.getSplitPrice()).setScale(2,BigDecimal.ROUND_DOWN);
                    //实付金额
                    BigDecimal price = itemVO.getPrice().multiply(new BigDecimal(itemVO.getNum())).setScale(2,BigDecimal.ROUND_DOWN);
                    price =  price.subtract(itemVO.getSplitPrice()).setScale(2,BigDecimal.ROUND_DOWN);
                    BigDecimal lelPrice = itemVO.getLevelPrice().multiply(new BigDecimal(itemVO.getNum())).setScale(2,BigDecimal.ROUND_DOWN);
                    lelPrice = lelPrice.subtract(price).setScale(2,BigDecimal.ROUND_DOWN);

                    Long pickNum = itemVO.getNum();
                    if(CollectionUtils.isNotEmpty(curGoodsPickStock)){
                        Optional<GoodsPickStockResponse> optional = curGoodsPickStock.stream().filter(item -> Objects.equals(item.getGoodsInfoNo(), itemVO.getSkuNo())).findFirst();
                        if(optional.isPresent()){
                            Long stock = optional.get().getStock();// 剩余可提
                            pickNum = (itemVO.getNum() - stock) > 0 ? (itemVO.getNum() - stock) : 0l;
                        }
                    }
                    NewPileTradeDetailedExportVO exportVO = new NewPileTradeDetailedExportVO();
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
                    exportVO.setWareName(vo.getWareName());
                    exportVO.setInvoice(vo.getInvoice());
                    exportVO.setDeliverWay(vo.getDeliverWay());
                    exportVO.setTradePrice(vo.getTradePrice());
                    exportVO.setSupplierName(Objects.nonNull(vo.getSupplier())?vo.getSupplier().getSupplierName(): "");

                    exportVO.setSkuNo(itemVO.getSkuNo());
                    exportVO.setErpGoodsInfoNo(erpNoMap.get(itemVO.getSkuId()));
                    exportVO.setSkuName(itemVO.getSkuName());
                    exportVO.setGoodsSubtitle(itemVO.getGoodsSubtitle());
                    exportVO.setPrice(itemVO.getPrice());
                    exportVO.setNum(itemVO.getNum());
                    exportVO.setPickNum(pickNum);// 已提数量
                    exportVO.setGoodTotalPrice(itemVO.getPrice().multiply(BigDecimal.valueOf(Double.valueOf(itemVO.getNum()))));// 商品总价
                    exportVO.setLevelPrice(levelPrice);
                    exportVO.setActualPrice(lelPrice);
                    detailedExportVOList.add(exportVO);

                }
                for(int i = 0; i < vo.getGifts().size(); i++){
                    TradeItemVO itemVO = vo.getGifts().get(i);
                    log.info("====报表itemVO：{}，", JSONObject.toJSONString(itemVO));
                    log.info("====报表金额：{}，{},{},{}",itemVO.getPrice(),itemVO.getSplitPrice(),itemVO.getLevelPrice(),itemVO.getNum());

                    //折扣金额
                    BigDecimal levelPrice = itemVO.getPrice().multiply(new BigDecimal(itemVO.getNum())).setScale(2,BigDecimal.ROUND_DOWN);
                    levelPrice = levelPrice.subtract(itemVO.getSplitPrice()).setScale(2,BigDecimal.ROUND_DOWN);
                    //实付金额
                    BigDecimal price = itemVO.getPrice().multiply(new BigDecimal(itemVO.getNum())).setScale(2,BigDecimal.ROUND_DOWN);
                    price = price.subtract(itemVO.getSplitPrice()).setScale(2,BigDecimal.ROUND_DOWN);
                    BigDecimal lelPrice = itemVO.getLevelPrice().multiply(new BigDecimal(itemVO.getNum())).setScale(2,BigDecimal.ROUND_DOWN);
                    lelPrice = lelPrice.subtract(price).setScale(2,BigDecimal.ROUND_DOWN);
                    //金额小计
                    BigDecimal xjPrice = itemVO.getLevelPrice().multiply(new BigDecimal(itemVO.getNum())).setScale(2,BigDecimal.ROUND_DOWN);

                    NewPileTradeDetailedExportVO exportVO = new NewPileTradeDetailedExportVO();
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
                    exportVO.setWareName(vo.getWareName());
                    exportVO.setInvoice(vo.getInvoice());
                    exportVO.setDeliverWay(vo.getDeliverWay());
                    exportVO.setTradePrice(vo.getTradePrice());
                    exportVO.setSupplierName(Objects.nonNull(vo.getSupplier())?vo.getSupplier().getSupplierName(): "");

                    exportVO.setSkuNo(itemVO.getSkuNo());
                    exportVO.setSkuName(itemVO.getSkuName());
                    exportVO.setGoodsSubtitle(itemVO.getGoodsSubtitle());
                    exportVO.setNum(itemVO.getNum());
                    exportVO.setActualPrice(lelPrice);
                    exportVO.setSplitPrice(xjPrice);
                    exportVO.setLevelPrice(levelPrice);
                    exportVO.setPrice(itemVO.getPrice());
                    exportVO.setErpGoodsInfoNo(erpNoMap.get(itemVO.getSkuId()));
                    detailedExportVOList.add(exportVO);
                }
            }
        }


        ExcelHelper excelHelper = new ExcelHelper();
        Column[] columns = {
                new Column("订单编号", new SpelColumnRender<NewPileTradeDetailedExportVO>("id")),
                new Column("父订单编号",new SpelColumnRender<NewPileTradeDetailedExportVO>("parentId")),
                new Column("支付单号",new SpelColumnRender<NewPileTradeDetailedExportVO>("payOrderNo")),
                new Column("下单时间", new SpelColumnRender<NewPileTradeDetailedExportVO>("tradeState.createTime")),
                new Column("客户名称", new SpelColumnRender<NewPileTradeDetailedExportVO>("buyer.name")),
                new Column("客户账号", new SpelColumnRender<NewPileTradeDetailedExportVO>("buyer.account")),
                new Column("客户级别", new SpelColumnRender<NewPileTradeDetailedExportVO>("buyer.levelName")),
                new Column("收货人", new SpelColumnRender<NewPileTradeDetailedExportVO>("consignee.name")),
                new Column("收货人手机", new SpelColumnRender<NewPileTradeDetailedExportVO>("consignee.phone")),
                new Column("收货人地址", new SpelColumnRender<NewPileTradeDetailedExportVO>("consignee.detailAddress")),
                new Column("支付方式", new SpelColumnRender<NewPileTradeDetailedExportVO>("payInfo.desc")),
                new Column("配送方式",  (cell, object) -> {
                    NewPileTradeDetailedExportVO trade = (NewPileTradeDetailedExportVO) object;
                    DeliverWay deliverWay = trade.getDeliverWay();
                    String cellValue = "";
                    if(deliverWay != null){
                        switch (deliverWay) {
                            case PICK_SELF:
                                cellValue = "自提";
                                break;
                            case LOGISTICS:
                                cellValue ="物流";
                                break;
                            case EXPRESS:
                                cellValue = "快递到家";
                                break;
                            case DELIVERY_HOME:
                                cellValue = "本地配送";
                                break;
                            default:
                                cellValue = "其他";
                                break;
                        }
                    }
                    cell.setCellValue(cellValue);
                }),
                new Column("配送费用", new SpelColumnRender<NewPileTradeDetailedExportVO>("tradePrice.deliveryPrice != null ? tradePrice.deliveryPrice : '0.00'")),
                new Column("订单商品金额", new SpelColumnRender<NewPileTradeDetailedExportVO>("tradePrice.goodsPrice")),
                new Column("订单特价金额", new SpelColumnRender<NewPileTradeDetailedExportVO>("tradePrice.special ? tradePrice.privilegePrice : '0.00'")),
                new Column("订单应付金额", new SpelColumnRender<NewPileTradeDetailedExportVO>("tradePrice.totalPrice")),
                new Column("鲸币抵扣", new SpelColumnRender<NewPileTradeDetailedExportVO>("tradePrice.balancePrice != null && tradePrice.balancePrice > 0 ? tradePrice.balancePrice : '0.00'")),
                new Column("是否使用鲸币", new SpelColumnRender<NewPileTradeDetailedExportVO>("tradePrice.balancePrice != null && tradePrice.balancePrice > 0 ? '是':'否'")),
                new Column("优惠券ID",new SpelColumnRender<NewPileTradeDetailedExportVO>("tradeCoupon.couponCode")),
                new Column("优惠券名称",new SpelColumnRender<NewPileTradeDetailedExportVO>("tradeCoupon.couponName")),
                new Column("满减优惠",new SpelColumnRender<NewPileTradeDetailedExportVO>("tradePrice.discountsPrice - tradePrice.couponPrice")),
                new Column("优惠券",new SpelColumnRender<NewPileTradeDetailedExportVO>("tradePrice.couponPrice")),
                new Column("买家备注", new SpelColumnRender<NewPileTradeDetailedExportVO>("buyerRemark")),
                new Column("卖家备注", new SpelColumnRender<NewPileTradeDetailedExportVO>("sellerRemark")),
                new Column("业务员",new SpelColumnRender<NewPileTradeDetailedExportVO>("employeeName")),
                new Column("订单状态", (cell, object) -> {
                    NewPileTradeDetailedExportVO trade = (NewPileTradeDetailedExportVO) object;
                    String cellValue = "";
                    if(trade.getTradeState() != null){
                        NewPileFlowState flowState = trade.getTradeState().getFlowState();
                        cellValue = (Objects.nonNull(flowState) ? flowState.getDescription() : "");
                    }
                    cell.setCellValue(cellValue);
                }),
                new Column("发货仓", new SpelColumnRender<NewPileTradeDetailedExportVO>("wareName")),
                new Column("付款状态", new SpelColumnRender<NewPileTradeDetailedExportVO>("tradeState.payState.getDescription()")),
//                new Column("发货状态", new SpelColumnRender<NewPileTradeDetailedExportVO>("tradeState.deliverStatus.getDescription()")),
                new Column("发票类型", new SpelColumnRender<NewPileTradeDetailedExportVO>("invoice.type == 0 ? '普通发票' : invoice.type == 1 ?'增值税发票':'不需要发票' ")),
                new Column("开票项目", new SpelColumnRender<NewPileTradeDetailedExportVO>("invoice.projectName")),
                new Column("发票抬头", new SpelColumnRender<NewPileTradeDetailedExportVO>("invoice.type == 0 ? invoice.generalInvoice.title:" +
                        "invoice.specialInvoice.companyName"))
        };
        if (existSupplier) {
            List<Column> columnList = Stream.of(columns).collect(Collectors.toList());
            columnList.add(
                    new Column("商家", new SpelColumnRender<NewPileTradeDetailedExportVO>("supplierName"))
            );
            columns = columnList.toArray(new Column[0]);
        }
        if(isDetailed){
            List<Column> columnList = Stream.of(columns).collect(Collectors.toList());
            // 商品SKU	ERP编码	商品名称	规格	单价	数量	折扣金额	实付金额	金额小计
            columnList.add(new Column("商品SKU",new SpelColumnRender<NewPileTradeDetailedExportVO>("skuNo")));
            columnList.add(new Column("ERP编码",new SpelColumnRender<NewPileTradeDetailedExportVO>("erpGoodsInfoNo")));
            columnList.add(new Column("商品名称",new SpelColumnRender<NewPileTradeDetailedExportVO>("skuName")));
            columnList.add(new Column("规格",new SpelColumnRender<NewPileTradeDetailedExportVO>("goodsSubtitle")));
            columnList.add(new Column("单价",new SpelColumnRender<NewPileTradeDetailedExportVO>("price")));
            columnList.add(new Column("囤货数量",new SpelColumnRender<NewPileTradeDetailedExportVO>("num")));
            columnList.add(new Column("已提数量",new SpelColumnRender<NewPileTradeDetailedExportVO>("pickNum")));
            columnList.add(new Column("商品总价",new SpelColumnRender<NewPileTradeDetailedExportVO>("goodTotalPrice")));
            columnList.add(new Column("折扣金额",new SpelColumnRender<NewPileTradeDetailedExportVO>("levelPrice")));
            columnList.add(new Column("实付金额",new SpelColumnRender<NewPileTradeDetailedExportVO>("actualPrice")));
            columns = columnList.toArray(new Column[0]);
        }
        excelHelper
                .addSheet(
                        "囤货订单导出",
                        columns,
                        detailedExportVOList
                );
        excelHelper.write(outputStream);
    }


}
