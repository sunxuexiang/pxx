package com.wanmi.ares.screen.service;

import com.wanmi.ares.request.screen.GenerateOrderRequest;
import com.wanmi.ares.request.screen.RealTimeOrderRequest;
import com.wanmi.ares.request.screen.model.ScreenOrderDetail;
import com.wanmi.ares.response.screen.*;
import com.wanmi.ares.screen.dao.BigScreenMapper;
import com.wanmi.ares.request.screen.model.ScreenOrder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lm
 * @date 2022/9/5
 */
@Service
@Slf4j
public class BigScreenServiceImpl {

    @Resource
    private BigScreenMapper bigScreenMapper;


    /**
     * {time}分钟生成{totalMoney}金额订单
     */
    @Transactional
    public void generateScreenOrderInTenMin(){
        bigScreenMapper.initScreenOrder();
        //bigScreenMapper.initScreenOrderDetail();
        log.info("初始化订单时间成功");
    }

    /**
     *  查询今日预售数据接口
     * @return
     */
    public PreSaleDataResponse queryTodayPreSaleData() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return bigScreenMapper.queryTodayPreSaleData(formatter.format(now));
    }

    /**
     * 获取实时订单数据
     * @return
     */
    public List<RealTimeOrderResponse> queryRealTimeOrder() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dateTime = dateTimeFormatter.format(LocalDateTime.now());
        List<RealTimeOrderResponse> responses = bigScreenMapper.queryRealTimeOrder(dateTime);
        return responses;
    }

    /**
     * 查询区域预售金额
     * @return
     */
    public AreaPreSaleTotalMoneyResponse queryAreaPreSaleTotalMoney() {
        List<Map> bigDecimalMap = bigScreenMapper.queryAreaPreSaleTotalMoney();
        Map<String,BigDecimal> selectResult = new HashMap<>();
        for (Map<String, Object> objectMap : bigDecimalMap) {
            selectResult.put(objectMap.get("ware")+"",new BigDecimal(objectMap.get("total")+""));
        }
        AreaPreSaleTotalMoneyResponse areaPreSaleData = new AreaPreSaleTotalMoneyResponse();
        BigDecimal defaultVal = BigDecimal.valueOf(0.00);

        DecimalFormat decimalFormat = new DecimalFormat("#");
        //仓库列表
        List<String> wareList = Arrays.asList("WH01", "WH02", "WH03");
        for (String wareName : wareList) {
            BigDecimal val = selectResult.get(wareName) == null ? defaultVal : selectResult.get(wareName);
            BigDecimal realVal = new BigDecimal(decimalFormat.format(val));
            if("WH01".equals(wareName)){
                areaPreSaleData.setCs(realVal);
            }
            if("WH02".equals(wareName)){
                areaPreSaleData.setNc(realVal);
            }
            if("WH03".equals(wareName)){
                areaPreSaleData.setWh(realVal);
            }
        }
        // 计算总额
        BigDecimal total = areaPreSaleData.getCs().add(areaPreSaleData.getNc().add(areaPreSaleData.getWh()));
        areaPreSaleData.setTotal(total);
        return areaPreSaleData;
    }

    /**
     * 品类销售排名
     * @return
     */
    public List<CateSaleResponse> queryCateSale() {
        // 查询品类销售总数据
//        Map<String,BigDecimal> totalMap = bigScreenMapper.queryCateSaleTotalPrice();
//        double totalPrice = totalMap.get("total_price").doubleValue();
//        // 分类统计商品数据
//        List<Map> cateSaleList = bigScreenMapper.queryCateSale();
//        cateSaleList = cateSaleList.stream()
//                .sorted((a,b) -> ((BigDecimal) b.get("sale_price")).compareTo((BigDecimal) a.get("sale_price")))
//                .limit(5).collect(Collectors.toList());
//        List<CateSaleResponse> responseList = new ArrayList<>();
//        for (int i = 0; i < cateSaleList.size(); i++){
//            CateSaleResponse saleResponse = new CateSaleResponse();
//            Map map = cateSaleList.get(i);
//            saleResponse.setCateName(map.get("cate_name")+"");
//            saleResponse.setTotalPrice((BigDecimal) map.get("sale_price"));
//            double percent = (saleResponse.getTotalPrice().doubleValue() / totalPrice) * 100;
//            saleResponse.setPercent(Integer.valueOf(Math.round(percent)+""));
//            responseList.add(saleResponse);
//        }
//        return responseList;
        Map<String,BigDecimal> totalMap = bigScreenMapper.queryCateSaleTotalPrice();
        CateSaleResponse cateSaleResponse = new CateSaleResponse();
        cateSaleResponse.setTotalPrice(totalMap.get("total_price"));
        return Arrays.asList(cateSaleResponse);
    }

    public ScreenLastTimeResponse queryScreenLastTime() {
        Map<String,String> lastTimeMap = bigScreenMapper.queryScreenLastTime();
        if(lastTimeMap == null || lastTimeMap.isEmpty()){
            return null;
        }
        String last = lastTimeMap.get("last");
        LocalDateTime dateTime = LocalDateTime.parse(last, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        ScreenLastTimeResponse response = new ScreenLastTimeResponse(dateTime);
        return response;
    }


    @Transactional
    public void saveScreenOrderAndDetail(GenerateOrderRequest orderRequest) {
        log.info("saveScreenOrderAndDetail:批量保存订单数据，数据量：{}",orderRequest.getScreenOrders().size());
        bigScreenMapper.saveScreenOrderBatch(orderRequest.getScreenOrders());
//        log.info("批量保存订单详情数据，数据量：{}",orderRequest.getScreenOrderDetails().size());
//        bigScreenMapper.saveScreenOrderDetailBatch(orderRequest.getScreenOrderDetails());
        log.info("保存成功");
    }

    @Transactional
    public void deleteScreenOrderAndDetail() {
        bigScreenMapper.deleteScreenOrder();
        //bigScreenMapper.deleteScreenOrderDetail();
    }
}
