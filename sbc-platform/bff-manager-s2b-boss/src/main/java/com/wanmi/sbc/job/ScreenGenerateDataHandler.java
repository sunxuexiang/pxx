package com.wanmi.sbc.job;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanmi.ares.provider.BigScreenProvider;
import com.wanmi.ares.request.screen.GenerateOrderRequest;
import com.wanmi.ares.request.screen.model.ScreenOrder;
import com.wanmi.ares.request.screen.model.ScreenOrderDetail;
import com.wanmi.ares.response.screen.PreSaleDataResponse;
import com.wanmi.ares.response.screen.ScreenLastTimeResponse;
import com.wanmi.ares.util.RandomPhoneNumber;
import com.wanmi.sbc.common.constant.Area;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.job.util.RandomAddressUtil;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.stockout.CityCode;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * 数据大屏数据生成
 *
 * @author lm
 * @date 2022/09/22 15:40
 */
@JobHandler(value = "screenGenerateDataHandler")
@Component
@Slf4j
//@RequestMapping("/screen")
public class ScreenGenerateDataHandler extends IJobHandler {

    /*是否已经执行了初始化方法*/
    public static final String INIT_SCREEN_ORDER_CONFIG = "INIT_SCREEN_ORDER_CONFIG";

    public static final String SCREEN_ORDER_CURRENT_EXECUTE_STATE = "SCREEN_ORDER_CURRENT_EXECUTE_STATE";

    public static final String SCREEN_ORDER_ADD_LAST_TIME = "SCREEN_ORDER_ADD_LAST_TIME";
    private BigScreenProvider bigScreenProvider;

    private RedisService redisService;


    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public ScreenGenerateDataHandler(BigScreenProvider bigScreenProvider,RedisService redisService) {
        this.bigScreenProvider = bigScreenProvider;
        this.redisService = redisService;
    }

    @Override
//    @GetMapping("/execute")
    public ReturnT<String> execute(String executeParam) throws Exception {
        String param = redisService.getString(INIT_SCREEN_ORDER_CONFIG);
        if (StringUtils.isBlank(param)) {
            XxlJobLogger.log("ScreenGenerateDataHandler->params参数不能为空,{}",param);
            return FAIL;
        }
        try {
            boolean openInterval = false; // 开启阶段性新增
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime dbLastTime = null;
            String lastTimeStr = redisService.getString(SCREEN_ORDER_ADD_LAST_TIME);
            if(StringUtils.isBlank(lastTimeStr)){
                // 查询数据库最新时间
                ScreenLastTimeResponse dbLastTimeStr = bigScreenProvider.queryScreenLastTime();
                dbLastTime = ( dbLastTimeStr == null ? now : dbLastTimeStr.getLocalDateTime());
                redisService.setString(SCREEN_ORDER_ADD_LAST_TIME,dateTimeFormatter.format(dbLastTime));
            }else{
                dbLastTime = LocalDateTime.parse(lastTimeStr,dateTimeFormatter);
            }

            // 查看数据库最新时间是否在当前阶段内
//            if (dbLastTime != null && now.isBefore(dbLastTime)) {
//                XxlJobLogger.log("该{}阶段已被执行", now);
//                return SUCCESS;
//            }
            XxlJobLogger.log("当前时间段：{}，数据库最新时间：{}", now,dbLastTime);
            JSONObject executePart = null; // 定义当前正在执行的阶段
            JSONArray params = JSONArray.parseArray(param);
            // 查找当前时间所在阶段
            for (int i = 0; i < params.size(); i++) {
                JSONObject currentOpt = params.getJSONObject(i);
                LocalDateTime startTime = LocalDateTime.parse(currentOpt.getString("startTime"), dateTimeFormatter);
                if (!startTime.isBefore(dbLastTime)) { // 当前时间还未到该阶段开始时间
                    continue;
                }
                String endTimeStr = currentOpt.getString("endTime");
                if (StringUtils.isNotBlank(endTimeStr)) {
                    LocalDateTime endTime = LocalDateTime.parse(endTimeStr, dateTimeFormatter);
                    if(currentOpt.getInteger("type") == 2){
                        if (endTime.isAfter(dbLastTime)) { // 当前时间是否在该选项的结束时间段内
                            openInterval = true;
                            executePart = currentOpt;
                            break;
                        }
                    }else{
                        openInterval = false;
                        if (endTime.isAfter(dbLastTime)) { // 当前时间是否在该选项的结束时间段内
                            executePart = currentOpt;
                            break;
                        }
                    }
                    continue; // 该阶段已完成
                }
                if (currentOpt.getInteger("type") == 2) {// 该区间执行类型为间隔执行
                    executePart = currentOpt;
                    openInterval = true;
                    break;
                }
            }
            if (executePart == null || executePart.isEmpty()) {
                XxlJobLogger.log("未找到需要执行的阶段");
                return FAIL;
            }
            XxlJobLogger.log("screenGenerateDataHandler：开始生成订单，订单阶段：{}",executePart);
            List<ScreenOrder> screenOrders = new ArrayList<>();
            List<ScreenOrderDetail> screenOrderDetails = new ArrayList<>();
            if (openInterval) {
                generateOrderWithInterval(executePart, dbLastTime, screenOrders, screenOrderDetails);
            } else {
                // 获取当前数据库总金额
                PreSaleDataResponse dataResponse = bigScreenProvider.queryTodayPreSaleData().getContext();
                LocalDateTime startTime = LocalDateTime.parse(executePart.getString("startTime"), dateTimeFormatter);
                LocalDateTime endTime = LocalDateTime.parse(executePart.getString("endTime"), dateTimeFormatter);
                BigDecimal curTotalPrice = new BigDecimal(dataResponse.getTodayTotalMoney());
                BigDecimal targetPrice = executePart.getBigDecimal("targetPrice");// 目标金额
                // 计算当前阶段应当生成的总额
                BigDecimal shouldAddPrice = targetPrice.subtract(curTotalPrice);
                if (targetPrice.compareTo(curTotalPrice) < 0) { // 目标金额
                    log.info("目标金额{}已小于当前库总额{}", targetPrice, curTotalPrice);
                }
                generateOrder(startTime,endTime,shouldAddPrice, screenOrders, screenOrderDetails,0);
            }

            GenerateOrderRequest request = new GenerateOrderRequest();
            request.setScreenOrders(screenOrders);
            request.setScreenOrderDetails(screenOrderDetails);
//            if (screenOrders.size() > 0 && screenOrderDetails.size() > 0) {
            if (screenOrders.size() > 0) {
                bigScreenProvider.saveScreenOrderAndDetail(request);
                ScreenOrder screenOrder = screenOrders.get(screenOrders.size() - 1);
                redisService.setString(SCREEN_ORDER_ADD_LAST_TIME,dateTimeFormatter.format(screenOrder.getCreateTime()));
            }
            XxlJobLogger.log("screenGenerateDataHandler：生成订单结束");
        } catch (Exception e) {
            XxlJobLogger.log("ScreenGenerateDataHandler->param参数格式错误，{}", e.getMessage());
            e.printStackTrace();
        }
        return SUCCESS;
    }


    /**
     *  阶段性新增订单
     */
    private void generateOrderWithInterval(JSONObject options, LocalDateTime dbLastTime, List<ScreenOrder> screenOrders, List<ScreenOrderDetail> screenOrderDetails) {
        LocalDateTime startTime = LocalDateTime.parse(options.getString("startTime"), dateTimeFormatter);
        Integer interval = options.getInteger("intervalTime");
        LocalDateTime endTime = StringUtils.isNotBlank(options.getString("endTime")) ?  LocalDateTime.parse(options.getString("endTime"), dateTimeFormatter) : null;

        if(endTime != null && dbLastTime.isAfter(endTime)){
            XxlJobLogger.log("该区间执行已过");
            return;
        }
        if(dbLastTime.isAfter(startTime)){
            startTime = dbLastTime;
        }
        String price = options.getString("price");
        if(startTime != null && StringUtils.isNotBlank(price) ){
            generateOrder(startTime,startTime.plusSeconds(interval),new BigDecimal(price),screenOrders,screenOrderDetails,0);
        }
    }

    /**
     * 指定时间段新增方式
     *
     */
    public void generateOrder(LocalDateTime startTime,LocalDateTime endTime, BigDecimal shouldAddPrice, List<ScreenOrder> screenOrders, List<ScreenOrderDetail> screenOrderDetails,Integer type) {

        // 计算每秒生成单数的平均金额
        long seconds = Math.abs(endTime.until(startTime, ChronoUnit.SECONDS));
        BigDecimal generateAvgPrice = BigDecimal.valueOf(shouldAddPrice.doubleValue() / Double.valueOf(seconds));
        int average = Math.abs(generateAvgPrice.intValue());

        Map<String, Integer> wareHouseCode = new HashMap<>();
        List<String> wareHouseList = Arrays.asList("WH03", "WH02", "WH01");
        wareHouseList.forEach(item -> wareHouseCode.put(item, 0));

        DecimalFormat decimalFormat = new DecimalFormat("#.00");

        // 已添加的订单金额
        BigDecimal finishedAddOrder = BigDecimal.ZERO;

        while (startTime.isBefore(endTime)) {
            // 随机订单数
            Integer orderNum = RandomUtils.nextInt(0, 11);
            for (int i = 0; i < orderNum; i++) {
                if(finishedAddOrder.compareTo(shouldAddPrice) > 0){
                    break;
                }
                double orderPrice = RandomUtils.nextDouble(average >> 4, (average>>2) + (average >> 3));
                if (redisService.setNx("screen_big_order","tag",600l)) {
                    orderPrice = RandomUtils.nextDouble(average >> 2, average << 1);
                }
                if(orderPrice < 130){
                    orderPrice = RandomUtils.nextDouble(150,360);
                }
                finishedAddOrder.add(BigDecimal.valueOf(orderPrice));

                ScreenOrder screenOrder = new ScreenOrder();
                int index = RandomUtils.nextInt(0, 3);
                screenOrder.setAccount(RandomPhoneNumber.createMobile(index)); // 设置订单账号
                LocalDateTime createTime = startTime.plusSeconds(12);
                screenOrder.setCreateTime(createTime);
                // 设置随机仓库
                String wareId = getWareId(wareHouseCode, wareHouseList, index);
                screenOrder.setWareHouseCode(wareId);

                // 设置订单金额
                screenOrder.setPrice(new BigDecimal(decimalFormat.format(orderPrice)));

                String provinceCode = "430000";
                String address = "湖南省";
                if ("WH03".equals(wareId)) {
                    provinceCode = "420000";
                    address = "湖北省";
                } else if ("WH02".equals(wareId)) {
                    provinceCode = "360000";
                    address = "江西省";
                }
                List<CityCode> cityDataList = RandomAddressUtil.getCityData(provinceCode);
                CityCode city = cityDataList.get(RandomUtils.nextInt(0, cityDataList.size()));

                List<Area> areaData = RandomAddressUtil.getAreaData(city.getCode());
                Area area = areaData.get(RandomUtils.nextInt(0, areaData.size()));
                screenOrder.setAddress(address + city.getName() + area.getName());
                // 创建订单号
                String tid = UUID.randomUUID().toString().replace("-", "");
                screenOrder.setOrderId(tid);
                screenOrders.add(screenOrder);
                // 生成品类数据
//                List<ScreenOrderDetail> details = generateOrderDetail(tid, orderPrice, createTime);
//                screenOrderDetails.addAll(details);
            }
            String executeState = redisService.getString(SCREEN_ORDER_CURRENT_EXECUTE_STATE);
            if(type == 0 && StringUtils.isNotBlank(executeState)){
                bigScreenProvider.deleteScreenOrderAndDetail();
                throw new SbcRuntimeException("终止定时生成订单");
            }
            startTime = startTime.plusSeconds(1l);
            if(finishedAddOrder.compareTo(shouldAddPrice) > 0){
                break;
            }
        }
    }

    /**
     * 生成订单数据
     *
     * @param tid
     * @param orderPrice
     * @return
     */
    private List<ScreenOrderDetail> generateOrderDetail(String tid, double orderPrice, LocalDateTime dateTime) {
        int detailNum = RandomUtils.nextInt(3, 11);
        List<ScreenOrderDetail> details = new ArrayList<>();
        JSONArray cateList = RandomAddressUtil.getCateList();
        BigDecimal curPrice = BigDecimal.valueOf(orderPrice);
        for (int i = 0; i < detailNum; i++) {
            ScreenOrderDetail orderDetail = new ScreenOrderDetail();
            orderDetail.setOrderId(tid);
            orderDetail.setCreateTime(dateTime);
            int cateIndex = RandomUtils.nextInt(0, cateList.size());
            JSONObject jsonObject = cateList.getJSONObject(cateIndex);

            BigDecimal productPrice = jsonObject.getBigDecimal("productPrice");
            Integer integer = calcCateNumber(curPrice, productPrice);

            if(productPrice.compareTo(curPrice) > 0){
                productPrice = curPrice;
            }
            curPrice = curPrice.subtract(productPrice.multiply(BigDecimal.valueOf(integer)));

            if(curPrice.compareTo(BigDecimal.ZERO) >= 0){
                orderDetail.setProductPrice(productPrice);
                orderDetail.setProductNum(integer);
            }else if(curPrice.compareTo(productPrice) > 0){
                orderDetail.setProductPrice(productPrice);
                orderDetail.setProductNum(1);
            }else{
                double aDouble = RandomUtils.nextDouble(10, 40);
                orderDetail.setProductPrice(BigDecimal.valueOf(aDouble));
                orderDetail.setProductNum(RandomUtils.nextInt(1,3));
            }
            orderDetail.setCateId(jsonObject.getLong("cateId"));
            orderDetail.setCateName(jsonObject.getString("cateName"));
            details.add(orderDetail);
        }
        return details;
    }

    private Integer calcCateNumber(BigDecimal orderPrice, BigDecimal productPrice) {
        // 生成随机数量的商品数
        int nextInt = RandomUtils.nextInt(1, 15);
        if (orderPrice.subtract(productPrice.multiply(BigDecimal.valueOf(nextInt))).compareTo(BigDecimal.ZERO) > 0) {
            return nextInt;
        } else {
            return 1;
        }
    }

    /**
     * 按比列计算仓库id
     *
     * @param wareHouseCode
     * @param wareHouseList
     * @param index
     * @return
     */
    private String getWareId(Map<String, Integer> wareHouseCode, List<String> wareHouseList, int index) {
        String wareId = wareHouseList.get(index);
        // totalCount
        Integer totalCount = wareHouseCode.values().stream().reduce(0, (a, b) -> a + b);
        if (totalCount >= 100) {
            wareHouseList.forEach(item -> wareHouseCode.put(item, 0));// 重新初始化
            wareHouseCode.put(wareId, wareHouseCode.get(wareId) + 1);
            return wareId;
        }
        if ("WH03".equals(wareId) && wareHouseCode.get(wareId) < 9) { // 9%
            wareHouseCode.put(wareId, wareHouseCode.get(wareId) + 1);
            return wareId;
        } else {
            wareId = "WH02";
        }
        if ("WH02".equals(wareId) && wareHouseCode.get(wareId) < 18) { //18%
            wareHouseCode.put(wareId, wareHouseCode.get(wareId) + 1);
            return wareId;
        } else {
            wareId = "WH01";
        }
        if ("WH01".equals(wareId) && wareHouseCode.get(wareId) < 73) { //73%
            wareHouseCode.put(wareId, wareHouseCode.get(wareId) + 1);
        }
        return wareId;
    }


}
