package com.wanmi.sbc.returnorder.util;

import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.excel.Column;
import com.wanmi.sbc.common.util.excel.ExcelHelper;
import com.wanmi.sbc.common.util.excel.impl.SpelColumnRender;
import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import com.wanmi.sbc.returnorder.bean.enums.FlowState;
import com.wanmi.sbc.returnorder.bean.vo.ReturnOrderVO;
import com.wanmi.sbc.returnorder.bean.vo.TradeIsNewUserVO;
import com.wanmi.sbc.returnorder.bean.vo.TradeItemVO;
import com.wanmi.sbc.returnorder.bean.vo.TradeVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
@Component
@Slf4j
public class TradeExportUtil {
    public ByteArrayOutputStream exportToByteArrayOutputStream(List<TradeVO> tradeList)throws Exception{
        ExcelHelper excelHelper = new ExcelHelper();
        Column[] columns = {
                new Column("订单编号", new SpelColumnRender<TradeVO>("id")),
                new Column("下单时间", new SpelColumnRender<TradeVO>("tradeState.createTime")),
                new Column("客户名称", new SpelColumnRender<TradeVO>("buyer.name")),
                new Column("客户账号", new SpelColumnRender<TradeVO>("buyer.account")),
                new Column("客户级别", new SpelColumnRender<TradeVO>("buyer.levelName")),
                new Column("收货人", new SpelColumnRender<TradeVO>("consignee.name")),
                new Column("收货人手机", new SpelColumnRender<TradeVO>("consignee.phone")),
                new Column("收货人地址", new SpelColumnRender<TradeVO>("consignee.detailAddress")),
                new Column("支付方式", new SpelColumnRender<TradeVO>("payInfo.desc")),
                new Column("配送方式",  (cell, object) -> {
                    TradeVO trade = (TradeVO) object;
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
                new Column("配送费用", new SpelColumnRender<TradeVO>("tradePrice.deliveryPrice != null ? tradePrice.deliveryPrice : '0.00'")),
                new Column("订单商品金额", new SpelColumnRender<TradeVO>("tradePrice.goodsPrice")),
                new Column("订单特价金额", new SpelColumnRender<TradeVO>("tradePrice.special ? tradePrice.privilegePrice : '0.00'")),
                new Column("订单应付金额", new SpelColumnRender<TradeVO>("tradePrice.totalPrice")),
                new Column("优惠券ID",new SpelColumnRender<TradeVO>("tradeCoupon.couponCode")),
//                new Column("优惠券名称",new SpelColumnRender<TradeVO>("tradeCoupon.couponCode")),
                new Column("优惠金额",new SpelColumnRender<TradeVO>("tradeCoupon.discountsAmount")),
                new Column("商品SKU", (cell, object) -> {
                    TradeVO trade = (TradeVO) object;
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
                new Column("付款状态", new SpelColumnRender<TradeVO>("tradeState.payState.getDescription()")),
                new Column("发货状态", new SpelColumnRender<TradeVO>("tradeState.deliverStatus.getDescription()")),
                new Column("发票类型", new SpelColumnRender<TradeVO>("invoice.type == 0 ? '普通发票' : invoice.type == 1 ?'增值税发票':'不需要发票' ")),
                new Column("开票项目", new SpelColumnRender<TradeVO>("invoice.projectName")),
                new Column("发票抬头", new SpelColumnRender<TradeVO>("invoice.type == 0 ? invoice.generalInvoice.title:" +
                        "invoice.specialInvoice.companyName")),
        };
        List<Column> columnList = Stream.of(columns).collect(Collectors.toList());
        columnList.add(
                new Column("商家", new SpelColumnRender<ReturnOrderVO>("supplier.supplierName"))
        );
        columns = columnList.toArray(new Column[0]);
        excelHelper
                .addSheet(
                        "订单导出",
                        columns,
                        tradeList
                );
        HSSFWorkbook work = excelHelper.getWork();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        work.write(byteArrayOutputStream);
        return byteArrayOutputStream;
    }


    /**
     * 发送新用户首单, 转excel
     * @return
     * @throws Exception
     */
    public ByteArrayOutputStream exportToByteArrayOutputStream2(List<TradeVO> tradeListOld)throws Exception{
        List<TradeIsNewUserVO> tradeList = KsBeanUtil.convertList(tradeListOld, TradeIsNewUserVO.class);

        ExcelHelper excelHelper = new ExcelHelper();
        Column[] columns = {
                new Column("订单编号", new SpelColumnRender<TradeIsNewUserVO>("id")),
                new Column("下单时间", new SpelColumnRender<TradeIsNewUserVO>("tradeState.createTime")),
                new Column("客户名称", new SpelColumnRender<TradeIsNewUserVO>("buyer.name")),
                new Column("客户账号", new SpelColumnRender<TradeIsNewUserVO>("buyer.account")),
                new Column("客户级别", new SpelColumnRender<TradeIsNewUserVO>("buyer.levelName")),
                new Column("收货人", new SpelColumnRender<TradeIsNewUserVO>("consignee.name")),
                new Column("收货人手机", new SpelColumnRender<TradeIsNewUserVO>("consignee.phone")),
                new Column("收货人地址", new SpelColumnRender<TradeIsNewUserVO>("consignee.detailAddress")),
                new Column("支付方式", new SpelColumnRender<TradeIsNewUserVO>("payInfo.desc")),
                new Column("配送方式",  (cell, object) -> {
                    TradeIsNewUserVO trade = (TradeIsNewUserVO) object;
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
                new Column("配送费用", new SpelColumnRender<TradeIsNewUserVO>("tradePrice.deliveryPrice != null ? tradePrice.deliveryPrice : '0.00'")),
                new Column("订单商品金额", new SpelColumnRender<TradeIsNewUserVO>("tradePrice.goodsPrice")),
                new Column("订单特价金额", new SpelColumnRender<TradeIsNewUserVO>("tradePrice.special ? tradePrice.privilegePrice : '0.00'")),
                new Column("订单应付金额", new SpelColumnRender<TradeIsNewUserVO>("tradePrice.totalPrice")),
                new Column("优惠券ID",new SpelColumnRender<TradeIsNewUserVO>("tradeCoupon.couponCode")),
//                new Column("优惠券名称",new SpelColumnRender<TradeIsNewUserVO>("tradeCoupon.couponCode")),
                new Column("优惠金额",new SpelColumnRender<TradeIsNewUserVO>("tradeCoupon.discountsAmount")),
                //商品SKU
                new Column("商品SKU", (cell, object) -> {
                    TradeIsNewUserVO trade = (TradeIsNewUserVO) object;
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
                //商品SKU的名称
                new Column("商品SKU名称", (cell, object) -> {
                    TradeIsNewUserVO trade = (TradeIsNewUserVO) object;
                    StringBuilder sb = new StringBuilder((trade.getTradeItems().size() + trade.getGifts().size()) * 32);
                    trade.getTradeItems()
                            .forEach(i -> {
                                sb.append(i.getSkuName()).append(",");
                            });
                    trade.getGifts().forEach(j ->{
                        sb.append(j.getSkuName()).append(",");
                    });
                    sb.trimToSize();
                    cell.setCellValue(sb.substring(0, sb.length() - 1));
                }),
                new Column("商品种类", (cell, object) -> {
                    TradeIsNewUserVO trade = (TradeIsNewUserVO) object;
                    cell.setCellValue(trade.skuItemMap().size());
                }),
                new Column("商品总数量", (cell, object) -> {
                    Long size = ((TradeIsNewUserVO) object)
                            .getTradeItems()
                            .stream()
                            .map(TradeItemVO::getNum)
                            .reduce((sum, item) -> {
                                sum += item;
                                return sum;
                            }).get();
                    //赠品数量
                    long sum = ((TradeIsNewUserVO) object)
                            .getGifts()
                            .stream()
                            .mapToLong(TradeItemVO::getNum).sum();
                    size = size + sum;
                    cell.setCellValue((double) size);
                }),
                new Column("买家备注", new SpelColumnRender<TradeIsNewUserVO>("buyerRemark")),
                new Column("卖家备注", new SpelColumnRender<TradeIsNewUserVO>("sellerRemark")),
                new Column("订单状态", (cell, object) -> {
                    TradeIsNewUserVO trade = (TradeIsNewUserVO) object;
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
                new Column("付款状态", new SpelColumnRender<TradeIsNewUserVO>("tradeState.payState.getDescription()")),
                new Column("发货状态", new SpelColumnRender<TradeIsNewUserVO>("tradeState.deliverStatus.getDescription()")),
                new Column("发票类型", new SpelColumnRender<TradeIsNewUserVO>("invoice.type == 0 ? '普通发票' : invoice.type == 1 ?'增值税发票':'不需要发票' ")),
                new Column("开票项目", new SpelColumnRender<TradeIsNewUserVO>("invoice.projectName")),
                new Column("发票抬头", new SpelColumnRender<TradeIsNewUserVO>("invoice.type == 0 ? invoice.generalInvoice.title:" +
                        "invoice.specialInvoice.companyName")),
        };
        List<Column> columnList = Stream.of(columns).collect(Collectors.toList());
        columnList.add(
                new Column("商家", new SpelColumnRender<ReturnOrderVO>("supplier.supplierName"))
        );
        columnList.add(
                new Column("是否新用户首单", new SpelColumnRender<TradeIsNewUserVO>("isNewUser"))
        );
        columns = columnList.toArray(new Column[0]);
        excelHelper
                .addSheet(
                        "订单导出",
                        columns,
                        tradeList
                );
        HSSFWorkbook work = excelHelper.getWork();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        work.write(byteArrayOutputStream);
        return byteArrayOutputStream;
    }




}
