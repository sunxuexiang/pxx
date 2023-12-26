package com.wanmi.sbc.ares;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanmi.ares.provider.BuyRankingListProvider;
import com.wanmi.ares.request.BuyRankingListRequest;
import com.wanmi.ares.view.trade.BuyRankingListView;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.goodswarestock.GoodsWareStockQueryProvider;
import com.wanmi.sbc.order.api.provider.trade.PickGoodsProvide;
import com.wanmi.sbc.order.api.request.trade.PickGoodsRequest;
import com.wanmi.sbc.order.api.response.trade.*;
import com.wanmi.sbc.order.response.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Api(tags = "PickGoodsController", description = "提货、囤货统计 Api")
@RestController
@RequestMapping("/pickGoods")
@Slf4j
public class PickGoodsController {
    @Autowired
    private PickGoodsProvide pickGoodsProvide;

    @Autowired
    private BuyRankingListProvider buyRankingListProvider;

    @Autowired
    private GoodsWareStockQueryProvider goodsWareStockQueryProvider;

    @SneakyThrows
    @ApiOperation(value = "提货笔数、提货件数、囤货笔数、囤货件数及囤货金额")
    @RequestMapping(value = "/pileAndTradeStatistics/{encrypted}", method = RequestMethod.GET)
    public void pileAndTradeStatistics(@PathVariable String encrypted, HttpServletResponse response) {
        ExportPileAndTradeStatisticsExcel exportPileAndTradeStatisticsExcel = new ExportPileAndTradeStatisticsExcel();

        String decrypted = new String(Base64.getUrlDecoder().decode(encrypted.getBytes()));
        PickGoodsRequest tradeExportRequest = JSON.parseObject(decrypted, PickGoodsRequest.class);
        // log.info("pileAndTradeStatistics---------->"+ JSONObject.toJSONString(tradeExportRequest));
        BaseResponse<PileAndTradeStatisticsResponse> responseBaseResponse = pickGoodsProvide.pileAndTradeStatistics(tradeExportRequest);

        List<ExportPileAndTradeStatisticsExcel> list = new ArrayList<>();
        if(Objects.nonNull(responseBaseResponse.getContext())){
            BeanUtils.copyProperties(responseBaseResponse.getContext(),exportPileAndTradeStatisticsExcel);
            list.add(exportPileAndTradeStatisticsExcel);
        }
        SimpleDateFormat fDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("提货笔数、提货件数、囤货笔数、囤货件数及囤货金额" + fDate.format(new Date()), "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), ExportPileAndTradeStatisticsExcel.class).sheet("模板").doWrite(list);

    }

    @ApiOperation(value = "购买排行")
    @RequestMapping(value = "/getBuyRankingList/{encrypted}", method = RequestMethod.GET)
    public void getBuyRankingList(@PathVariable String encrypted, HttpServletResponse response) throws IOException {

        String decrypted = new String(Base64.getUrlDecoder().decode(encrypted.getBytes()));
        PickGoodsRequest request = JSON.parseObject(decrypted, PickGoodsRequest.class);
        // log.info("getBuyRankingList---------->"+ JSONObject.toJSONString(request));
        //购买排行信息
        BuyRankingListRequest buyRankingListReques = new BuyRankingListRequest();

        buyRankingListReques.setSort(request.getSort());
        buyRankingListReques.setSize(request.getSize());
        buyRankingListReques.setEndTime(request.getEndTime());
        buyRankingListReques.setBeginTime(request.getBeginTime());

        com.wanmi.ares.base.BaseResponse<List<BuyRankingListView>> buyRankingList = buyRankingListProvider.
                getBuyRankingList(buyRankingListReques);
        List<BuyRankingListExcel> list = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(buyRankingList.getContext())){

            List<BuyRankingListView> context = buyRankingList.getContext();
            context.forEach(var->{
                BuyRankingListExcel buyRankingListExcel = new BuyRankingListExcel();
                BeanUtils.copyProperties(var,buyRankingListExcel);
                list.add(buyRankingListExcel);
            });
        }
        SimpleDateFormat fDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("购买排行" + fDate.format(new Date()), "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), BuyRankingListExcel.class).sheet("模板").doWrite(list);
    }

    @ApiOperation(value = "商品库存数据（等货中）")
    @RequestMapping(value = "/getInventory", method = RequestMethod.GET)
    public void getInventory(HttpServletResponse response) throws IOException {

        List<InventoryExcel> list = new ArrayList<>();
        //商品库存数据（等货中）
        BaseResponse<List<Object[]>> inventory = goodsWareStockQueryProvider.getInventory();
        if(CollectionUtils.isNotEmpty(inventory.getContext())){

            List<Object[]> context = inventory.getContext();
            context.forEach(var->{
                InventoryExcel buyRankingListExcel = new InventoryExcel();
                buyRankingListExcel.setErpGoodsInfoNo(var[0].toString());
                buyRankingListExcel.setSkuName(var[1].toString());
                buyRankingListExcel.setNum(var[2].toString());
                buyRankingListExcel.setPileNum(var[3].toString());
                buyRankingListExcel.setPendingNum(var[4].toString());
                list.add(buyRankingListExcel);
            });
        }

        SimpleDateFormat fDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("商品库存数据" + fDate.format(new Date()), "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), InventoryExcel.class).sheet("模板").doWrite(list);
    }

    @ApiOperation(value = "商品每日提货记录")
    @RequestMapping(value = "/statisticPickUpLog/{encrypted}", method = RequestMethod.GET)
    public void statisticPickUpLog(@PathVariable String encrypted,HttpServletResponse response) throws IOException  {
        String decrypted = new String(Base64.getUrlDecoder().decode(encrypted.getBytes()));
        PickGoodsRequest request = JSON.parseObject(decrypted, PickGoodsRequest.class);
        // log.info("statisticPickUpLog---------->"+ JSONObject.toJSONString(request));
        //商品库存数据（等货中）
        BaseResponse<List<StatisticPickUpLogResponse>> listBaseResponse = pickGoodsProvide.statisticPickUpLog(request);

        List<StatisticPickUpLogExcel> list = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(listBaseResponse.getContext())){

            List<StatisticPickUpLogResponse> context = listBaseResponse.getContext();
            context.forEach(var->{
                StatisticPickUpLogExcel buyRankingListExcel = new StatisticPickUpLogExcel();
                BeanUtils.copyProperties(var,buyRankingListExcel);
                list.add(buyRankingListExcel);
            });
        }
        SimpleDateFormat fDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("商品每日提货记录" + fDate.format(new Date()), "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), StatisticPickUpLogExcel.class).sheet("模板").doWrite(list);
    }

    @ApiOperation(value = "囤货未提数据，实时")
    @RequestMapping(value = "/statisticRecordItemPriceNumNoPile/{encrypted}", method = RequestMethod.GET)
    public void statisticRecordItemPriceNumNoPile(@PathVariable String encrypted,HttpServletResponse response) throws IOException  {
        String decrypted = new String(Base64.getUrlDecoder().decode(encrypted.getBytes()));
        PickGoodsRequest request = JSON.parseObject(decrypted, PickGoodsRequest.class);
        // log.info("statisticRecordItemPriceNumNoPile---------->"+ JSONObject.toJSONString(request));
        //商品库存数据（等货中）
        BaseResponse<List<StatisticRecordItemPriceNumNoPileResponse>> listBaseResponse = pickGoodsProvide.statisticRecordItemPriceNumNoPile();

        List<StatisticRecordItemPriceNumNoPileExcel> list = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(listBaseResponse.getContext())){

            List<StatisticRecordItemPriceNumNoPileResponse> context = listBaseResponse.getContext();
            context.forEach(var->{
                StatisticRecordItemPriceNumNoPileExcel buyRankingListExcel = new StatisticRecordItemPriceNumNoPileExcel();
                BeanUtils.copyProperties(var,buyRankingListExcel);
                list.add(buyRankingListExcel);
            });
        }
        SimpleDateFormat fDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("囤货未提数据" + fDate.format(new Date()), "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), StatisticRecordItemPriceNumNoPileExcel.class).sheet("模板").doWrite(list);
    }

    @ApiOperation(value = "/*囤货未提 客户*/")
    @RequestMapping(value = "/statisticRecordItemPriceNumNoPileUser/{encrypted}", method = RequestMethod.GET)
    public void statisticRecordItemPriceNumNoPileUser(@PathVariable String encrypted,HttpServletResponse response) throws IOException  {
        String decrypted = new String(Base64.getUrlDecoder().decode(encrypted.getBytes()));
        PickGoodsRequest request = JSON.parseObject(decrypted, PickGoodsRequest.class);
        // log.info("statisticRecordItemPriceNumNoPileUser---------->"+ JSONObject.toJSONString(request));
        //商品库存数据（等货中）
        BaseResponse<List<StatisticRecordItemPriceNumNoPileUserResponse>> listBaseResponse = pickGoodsProvide.statisticRecordItemPriceNumNoPileUser();

        List<StatisticRecordItemPriceNumNoPileUserExcel> list = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(listBaseResponse.getContext())){
            List<StatisticRecordItemPriceNumNoPileUserResponse> context = listBaseResponse.getContext();
            context.forEach(var->{
                StatisticRecordItemPriceNumNoPileUserExcel buyRankingListExcel = new StatisticRecordItemPriceNumNoPileUserExcel();
                BeanUtils.copyProperties(var,buyRankingListExcel);
                list.add(buyRankingListExcel);
            });
        }
        SimpleDateFormat fDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("囤货未提" + fDate.format(new Date()), "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), StatisticRecordItemPriceNumNoPileUserExcel.class).sheet("模板").doWrite(list);
    }


    @ApiOperation(value = "/*囤货未提 客户（新）*/")
    @RequestMapping(value = "/statisticNewPileTradeNoPileUser/{encrypted}", method = RequestMethod.GET)
    public void statisticNewPileTradeNoPileUser(@PathVariable String encrypted,HttpServletResponse response) throws IOException  {
        String decrypted = new String(Base64.getUrlDecoder().decode(encrypted.getBytes()));
        PickGoodsRequest request = JSON.parseObject(decrypted, PickGoodsRequest.class);
        // log.info("statisticRecordItemPriceNumNoPileUser---------->"+ JSONObject.toJSONString(request));
        //商品库存数据（等货中）
        BaseResponse<List<StatisticRecordItemPriceNumNoPileUserResponse>> listBaseResponse = pickGoodsProvide.statisticNewPileTradeNoPileUser(request);

        List<StatisticRecordItemPriceNumNoPileUserExcel> list = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(listBaseResponse.getContext())){
            List<StatisticRecordItemPriceNumNoPileUserResponse> context = listBaseResponse.getContext();
            context.forEach(var->{
                StatisticRecordItemPriceNumNoPileUserExcel buyRankingListExcel = new StatisticRecordItemPriceNumNoPileUserExcel();
                BeanUtils.copyProperties(var,buyRankingListExcel);
                list.add(buyRankingListExcel);
            });
        }
        SimpleDateFormat fDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("客户囤货未提数据" + fDate.format(new Date()), "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), StatisticRecordItemPriceNumNoPileUserExcel.class).sheet("模板").doWrite(list);
    }

    @ApiOperation(value = "囤货未提数据，实时（新）")
    @RequestMapping(value = "/statisticNewPileTradeNoPile/{encrypted}", method = RequestMethod.GET)
    public void statisticNewPileTradeNoPile(@PathVariable String encrypted,HttpServletResponse response) throws IOException  {
        String decrypted = new String(Base64.getUrlDecoder().decode(encrypted.getBytes()));
        PickGoodsRequest request = JSON.parseObject(decrypted, PickGoodsRequest.class);
        // log.info("statisticNewPileTradeNoPile---------->"+ JSONObject.toJSONString(request));
        BaseResponse<List<NewPileTradeNoPileExcel>> listBaseResponse = pickGoodsProvide.statisticNewPileTradeNoPile(request);

        List<NewPileTradeNoPileExcelResponse> list = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(listBaseResponse.getContext())){
            List<NewPileTradeNoPileExcel> responseContext = listBaseResponse.getContext();
            // 根据erpNo+skuNo分组
            Map<String, List<NewPileTradeNoPileExcel>> listMap = responseContext.stream().collect(Collectors.groupingBy(item -> item.getErpGoodsInfoNo() + ":" + item.getSkuNo()+":"+item.getWareName()));

            for (String erpNoAndSkuNo : listMap.keySet()) {
                NewPileTradeNoPileExcelResponse excelResponse = new NewPileTradeNoPileExcelResponse();
                List<NewPileTradeNoPileExcel> newPileTradeNoPileExcels = listMap.get(erpNoAndSkuNo);
                //设置囤货未提总数
                excelResponse.setNoPickNum(newPileTradeNoPileExcels.stream().map(item -> item.getNoPickNum()).reduce(Long::sum).get());
                // 设置囤货未提实际支付的总金额
                excelResponse.setActualPrice(newPileTradeNoPileExcels.stream().map(item -> item.getActualPrice()).reduce(BigDecimal::add).get());
                NewPileTradeNoPileExcel newPileTradeNoPileExcel = newPileTradeNoPileExcels.get(0);

                excelResponse.setProductCate(newPileTradeNoPileExcel.getProductCate());
                excelResponse.setSkuName(newPileTradeNoPileExcel.getSkuName());
                excelResponse.setErpGoodsInfoNo(newPileTradeNoPileExcel.getErpGoodsInfoNo());
                excelResponse.setWareName(newPileTradeNoPileExcel.getWareName());
                list.add(excelResponse);
            }
        }
        SimpleDateFormat fDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("商品囤货未提数据" + fDate.format(new Date()), "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), NewPileTradeNoPileExcelResponse.class).sheet("模板").doWrite(list);
    }


    @ApiOperation(value = "总囤货数据，实时（新）")
    @RequestMapping(value = "/statisticNewPileTradeTotal/{encrypted}", method = RequestMethod.GET)
    public void statisticNewPileTradeTotal(@PathVariable String encrypted,HttpServletResponse response) throws IOException  {
        String decrypted = new String(Base64.getUrlDecoder().decode(encrypted.getBytes()));
        PickGoodsRequest request = JSON.parseObject(decrypted, PickGoodsRequest.class);
        // log.info("statisticNewPileTradeTotal---------->"+ JSONObject.toJSONString(request));
        BaseResponse<List<NewPileTradeTotalPileExcel>> listBaseResponse = pickGoodsProvide.statisticNewPileTradePileTotal(request);

        List<NewPileTradeTotalPileExcelResponse> list = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(listBaseResponse.getContext())){
            list = KsBeanUtil.convertList(listBaseResponse.getContext(),NewPileTradeTotalPileExcelResponse.class);
        }
        SimpleDateFormat fDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("总囤货数据" + fDate.format(new Date()), "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), NewPileTradeTotalPileExcelResponse.class).sheet("模板").doWrite(list);
    }
}
