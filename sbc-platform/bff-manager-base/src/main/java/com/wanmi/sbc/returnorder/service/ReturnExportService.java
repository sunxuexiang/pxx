package com.wanmi.sbc.returnorder.service;

import com.google.common.collect.Lists;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.excel.Column;
import com.wanmi.sbc.common.util.excel.ExcelHelper;
import com.wanmi.sbc.common.util.excel.impl.SpelColumnRender;
import com.wanmi.sbc.order.bean.vo.ReturnItemVO;
import com.wanmi.sbc.order.bean.vo.ReturnOrderVO;
import com.wanmi.sbc.order.bean.vo.ReturnPriceVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 退单导出service
 * Created by jinwei on 6/5/2017.
 */
@Service
public class ReturnExportService {

    public void export(List<ReturnOrderVO> returnOrderList, OutputStream outputStream, boolean existSupplier) {
        ExcelHelper excelHelper = new ExcelHelper();
        if(existSupplier){
            Column[] columns = {
                    new Column("退单编号", new SpelColumnRender<ReturnOrderVO>("id")),
                    new Column("申请时间", new SpelColumnRender<ReturnOrderVO>("createTime")),
                    new Column("订单编号", new SpelColumnRender<ReturnOrderVO>("tid")),
                    new Column("子单编号", new SpelColumnRender<ReturnOrderVO>("ptid")),
                    new Column("商家", (cell,object)->{
                        ReturnOrderVO returnOrderVO=(ReturnOrderVO) object;
                        String supplierName=returnOrderVO.getCompany().getSupplierName();
                        String supplierCode=returnOrderVO.getCompany().getCompanyCode();
                        cell.setCellValue(supplierName+" "+supplierCode);
                    }),
                    new Column("供应商", (cell,object)->{
                        ReturnOrderVO returnOrderVO=(ReturnOrderVO) object;
                        String providerName=returnOrderVO.getProviderName()!=null?returnOrderVO.getProviderName():StringUtils.EMPTY;
                        String providerCode=returnOrderVO.getProviderCode()!=null?returnOrderVO.getProviderCode():StringUtils.EMPTY;
                        cell.setCellValue(providerName+" "+providerCode);
                    }),
                    new Column("客户名称", new SpelColumnRender<ReturnOrderVO>("buyer.name")),
                    new Column("客户账号", new SpelColumnRender<ReturnOrderVO>("buyer.account")),
                    new Column("客户级别", new SpelColumnRender<ReturnOrderVO>("buyer.levelName")),
                    new Column("退货原因", new SpelColumnRender<ReturnOrderVO>("returnReason.getDesc()")),
                    new Column("退货说明", new SpelColumnRender<ReturnOrderVO>("description")),
                    new Column("退货方式", new SpelColumnRender<ReturnOrderVO>("returnWay.getDesc()")),
                    new Column("退货商品SKU编码", (cell, object) -> {
                        String skuNos = ((ReturnOrderVO) object)
                                .getReturnItems()
                                .stream()
                                .map(ReturnItemVO::getSkuNo)
                                .collect(Collectors.joining(";"));
                        cell.setCellValue(skuNos);
                    }),
                    new Column("退货商品种类", new SpelColumnRender<ReturnOrderVO>("returnItems.size()")),
                    new Column("退货商品总数量", (cell, object) -> {
                        Optional<Long> size = ((ReturnOrderVO) object)
                                .getReturnItems()
                                .stream()
                                .map(ReturnItemVO::getNum)
                                .reduce((sum, item) -> {
//                                    sum = sum.add(item);
                                    sum = sum+item;
                                    return sum;
                                });
                        cell.setCellValue(size.get().doubleValue());
                    }),
    //                new Column("退货商品总额", new SpelColumnRender<ReturnOrderVO>("returnPrice.applyStatus ? returnPrice.actualReturnPrice : returnPrice.totalPrice")),
    //                new Column("申请退款金额", new SpelColumnRender<ReturnOrderVO>("returnPrice.applyStatus ? returnPrice.applyPrice: \"\"")),
                    new Column("应退金额", new SpelColumnRender<ReturnOrderVO>("returnPrice.totalPrice")),
                    new Column("实退金额", new SpelColumnRender<ReturnOrderVO>("null != returnPrice.actualReturnPrice ? returnPrice.actualReturnPrice : \"\"")),
                    new Column("退单状态", new SpelColumnRender<ReturnOrderVO>("transformReturnFlowState(returnFlowState)")),
                    new Column("业务员", new SpelColumnRender<ReturnOrderVO>("employeeName")),
                    new Column("ERP编码", (cell, object) -> {
                        String skuNos = ((ReturnOrderVO) object)
                                .getReturnItems()
                                .stream()
                                .map(ReturnItemVO::getErpSkuNo)
                                .collect(Collectors.joining(";"));
                        cell.setCellValue(skuNos);
                    }),
                    new Column("商品名称", (cell, object) -> {
                        String skuNos = ((ReturnOrderVO) object)
                                .getReturnItems()
                                .stream()
                                .map(ReturnItemVO::getSkuName)
                                .collect(Collectors.joining(";"));
                        cell.setCellValue(skuNos);
                    }),
                    new Column("规格", (cell, object) -> {
                        String skuNos = ((ReturnOrderVO) object)
                                .getReturnItems()
                                .stream()
                                .map(ReturnItemVO::getGoodsSubtitle)
                                .collect(Collectors.joining(";"));
                        cell.setCellValue(skuNos);
                    }),
            };
            excelHelper.addSheet(
                    "退单",
                    columns,
                    returnOrderList
            );
        }else {
            Column[] columns = {
                    new Column("退单编号", new SpelColumnRender<ReturnOrderVO>("id")),
                    new Column("申请时间", new SpelColumnRender<ReturnOrderVO>("createTime")),
                    new Column("订单编号", new SpelColumnRender<ReturnOrderVO>("tid")),
                    new Column("子单编号", new SpelColumnRender<ReturnOrderVO>("ptid")),
                    new Column("供应商", (cell,object)->{
                        ReturnOrderVO returnOrderVO=(ReturnOrderVO) object;
                        String providerName=returnOrderVO.getProviderName();
                        String providerCode=returnOrderVO.getProviderCode();
                        cell.setCellValue(providerName+" "+providerCode);
                    }),
                    new Column("客户名称", new SpelColumnRender<ReturnOrderVO>("buyer.name")),
                    new Column("客户账号", new SpelColumnRender<ReturnOrderVO>("buyer.account")),
                    new Column("客户级别", new SpelColumnRender<ReturnOrderVO>("buyer.levelName")),
                    new Column("退货原因", new SpelColumnRender<ReturnOrderVO>("returnReason.getDesc()")),
                    new Column("退货说明", new SpelColumnRender<ReturnOrderVO>("description")),
                    new Column("退货方式", new SpelColumnRender<ReturnOrderVO>("returnWay.getDesc()")),
                    new Column("退货商品SKU编码", (cell, object) -> {
                        String skuNos = ((ReturnOrderVO) object)
                                .getReturnItems()
                                .stream()
                                .map(ReturnItemVO::getSkuNo)
                                .collect(Collectors.joining(";"));
                        cell.setCellValue(skuNos);
                    }),
                    new Column("退货商品种类", new SpelColumnRender<ReturnOrderVO>("returnItems.size()")),
                    new Column("退货商品总数量", (cell, object) -> {
                        Optional<Long> size = ((ReturnOrderVO) object)
                                .getReturnItems()
                                .stream()
                                .map(ReturnItemVO::getNum)
                                .reduce((sum, item) -> {
//                                    sum = sum.add(item);
                                    sum = sum + item;
                                    return sum;
                                });
                        cell.setCellValue(size.get().doubleValue());
                    }),
                    new Column("应退金额", new SpelColumnRender<ReturnOrderVO>("returnPrice.totalPrice")),
                    new Column("实退金额", new SpelColumnRender<ReturnOrderVO>("null != returnPrice.actualReturnPrice ? returnPrice.actualReturnPrice : \"\"")),
                    new Column("退单状态", new SpelColumnRender<ReturnOrderVO>("transformReturnFlowState(returnFlowState)"))
            };
            excelHelper.addSheet(
                    "退单",
                    columns,
                    returnOrderList
            );

        }


        excelHelper.write(outputStream);
    }


    public void exportDetail(List<ReturnOrderVO> returnOrderList, OutputStream outputStream) {
        List<ReturnOrderVO> list = new ArrayList<>();
        Set<String> ids = new HashSet<>();
        returnOrderList.forEach(o -> o.getReturnItems().forEach(i -> {
            final ReturnOrderVO convert = KsBeanUtil.convert(o, ReturnOrderVO.class);
            // 只在第一行显示
            if (ids.contains(o.getId())) convert.setReturnPrice(new ReturnPriceVO());
            convert.setReturnItems(Lists.newArrayList(i));
            list.add(convert);
            ids.add(o.getId());
        }));
        ExcelHelper excelHelper = new ExcelHelper();
        Column[] columns = {
                new Column("退单编号", new SpelColumnRender<ReturnOrderVO>("id")),
                new Column("申请时间", new SpelColumnRender<ReturnOrderVO>("createTime")),
                new Column("订单编号", new SpelColumnRender<ReturnOrderVO>("tid")),
                new Column("子单编号", new SpelColumnRender<ReturnOrderVO>("ptid")),
                new Column("商家", (cell, object) -> {
                    ReturnOrderVO returnOrderVO = (ReturnOrderVO) object;
                    String supplierName = returnOrderVO.getCompany().getSupplierName();
                    String supplierCode = returnOrderVO.getCompany().getCompanyCode();
                    cell.setCellValue(supplierName + " " + supplierCode);
                }),
                new Column("供应商", (cell, object) -> {
                    ReturnOrderVO returnOrderVO = (ReturnOrderVO) object;
                    String providerName = returnOrderVO.getProviderName() != null ? returnOrderVO.getProviderName() : StringUtils.EMPTY;
                    String providerCode = returnOrderVO.getProviderCode() != null ? returnOrderVO.getProviderCode() : StringUtils.EMPTY;
                    cell.setCellValue(providerName + " " + providerCode);
                }),
                new Column("客户名称", new SpelColumnRender<ReturnOrderVO>("buyer.name")),
                new Column("客户账号", new SpelColumnRender<ReturnOrderVO>("buyer.account")),
                new Column("客户级别", new SpelColumnRender<ReturnOrderVO>("buyer.levelName")),
                new Column("退货原因", new SpelColumnRender<ReturnOrderVO>("returnReason.getDesc()")),
                new Column("退货说明", new SpelColumnRender<ReturnOrderVO>("description")),
                new Column("退货方式", new SpelColumnRender<ReturnOrderVO>("returnWay.getDesc()")),
                new Column("退货商品SKU编码", (cell, object) -> {
                    String skuNos = ((ReturnOrderVO) object)
                            .getReturnItems()
                            .stream()
                            .map(ReturnItemVO::getSkuNo)
                            .collect(Collectors.joining(";"));
                    cell.setCellValue(skuNos);
                }),
                new Column("退货商品总数量", (cell, object) -> {
                    Optional<Long> size = ((ReturnOrderVO) object)
                            .getReturnItems()
                            .stream()
                            .map(ReturnItemVO::getNum)
                            .reduce((sum, item) -> {
//                                    sum = sum.add(item);
                                sum = sum + item;
                                return sum;
                            });
                    cell.setCellValue(size.get().doubleValue());
                }),
                new Column("退货单价", (cell, object) -> {
                    BigDecimal price = ((ReturnOrderVO) object)
                            .getReturnItems()
                            .get(0).getPrice();
                    cell.setCellValue(Objects.toString(price, ""));
                }),
                new Column("实退商品金额", (cell, object) -> {
                    BigDecimal splitPrice = ((ReturnOrderVO) object)
                            .getReturnItems()
                            .get(0).getSplitPrice();
                    cell.setCellValue(Objects.toString(splitPrice, ""));
                }),
                new Column("主单退鲸币总额", new SpelColumnRender<ReturnOrderVO>("returnPrice.actualBalanceReturnPrice != null ? returnPrice.actualBalanceReturnPrice : \"\" ")),
                new Column("主单实退运费", new SpelColumnRender<ReturnOrderVO>("returnPrice.deliveryPrice != null ? returnPrice.deliveryPrice : \"\"")),
                new Column("主单实退包装费", new SpelColumnRender<ReturnOrderVO>("returnPrice.packingPrice != null ? returnPrice.packingPrice : \"\"")),
                new Column("退单状态", new SpelColumnRender<ReturnOrderVO>("transformReturnFlowState(returnFlowState)")),
                new Column("业务员", new SpelColumnRender<ReturnOrderVO>("employeeName")),
                new Column("ERP编码", (cell, object) -> {
                     String erpSkuNo = ((ReturnOrderVO) object)
                            .getReturnItems()
                            .get(0).getErpSkuNo();
                    cell.setCellValue(Objects.toString(erpSkuNo, ""));
                }),
                new Column("商品名称", (cell, object) -> {
                    String skuName = ((ReturnOrderVO) object)
                            .getReturnItems()
                            .get(0).getSkuName();
                    cell.setCellValue(Objects.toString(skuName, ""));
                }),
                new Column("规格", (cell, object) -> {
                    String goodsSubtitle = ((ReturnOrderVO) object)
                            .getReturnItems()
                            .get(0).getGoodsSubtitle();
                    cell.setCellValue(Objects.toString(goodsSubtitle, ""));
                }),
        };
        excelHelper.addSheet(
                "退单明细",
                columns,
                list
        );
        excelHelper.write(outputStream);
    }
}
