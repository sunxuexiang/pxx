package com.wanmi.sbc.screen;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanmi.ares.base.ResultCode;
import com.wanmi.ares.provider.BigScreenProvider;
import com.wanmi.ares.base.BaseResponse;
import com.wanmi.ares.request.screen.GenerateOrderRequest;
import com.wanmi.ares.request.screen.model.ScreenOrder;
import com.wanmi.ares.request.screen.model.ScreenOrderDetail;
import com.wanmi.ares.response.screen.AreaPreSaleTotalMoneyResponse;
import com.wanmi.ares.response.screen.CateSaleResponse;
import com.wanmi.ares.response.screen.PreSaleDataResponse;
import com.wanmi.ares.response.screen.RealTimeOrderResponse;
import com.wanmi.sbc.job.ScreenGenerateDataHandler;
import com.wanmi.sbc.job.util.RandomAddressUtil;
import com.wanmi.sbc.redis.RedisService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 大屏数据接口
 *
 * @author lm
 * @date 2022/09/07 11:10
 */
@RestController
@RequestMapping("/screen")
@Api(tags = "ScreenOrderController", description = "大屏数据统计接口")
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class ScreenOrderController {

    /*是否已经执行了初始化方法*/
    public static final String INIT_SCREEN_ORDER_CONFIG = "INIT_SCREEN_ORDER_CONFIG";

    public static final String INIT_SCREEN_ORDER_CONFIG_RESET = "INIT_SCREEN_ORDER_CONFIG_RESET";

    // 当前订单数据执行状态
    public static final String SCREEN_ORDER_CURRENT_EXECUTE_STATE = "SCREEN_ORDER_CURRENT_EXECUTE_STATE";

    public static final String SCREEN_ORDER_ADD_LAST_TIME = "SCREEN_ORDER_ADD_LAST_TIME";

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private BigScreenProvider bigScreenProvider;
    private RedisService redisService;

    private ScreenGenerateDataHandler screenGenerateDataHandler;

    @Autowired
    public ScreenOrderController(BigScreenProvider bigScreenProvider,
                                 ScreenGenerateDataHandler screenGenerateDataHandler,
                                 RedisService redisService) {
        this.bigScreenProvider = bigScreenProvider;
        this.redisService = redisService;
        this.screenGenerateDataHandler = screenGenerateDataHandler;
    }


    @ApiOperation("开始执行定时任务")
    @PostMapping("/startGenerateOrder")
    public JSONObject startGenerateOrder() {
        JSONObject result = new JSONObject();
        // 查看redis定时任务是否已经初始化
        String jobConfig = redisService.getString(INIT_SCREEN_ORDER_CONFIG);
        if (StringUtils.isBlank(jobConfig)) {
            redisService.setString(INIT_SCREEN_ORDER_CONFIG, getConfig(0).toJSONString());
        }
        result.put("code", ResultCode.SUCCESSFUL);
        result.put("message", "初始化完成");
        result.put("data", JSONArray.parseArray(redisService.getString(INIT_SCREEN_ORDER_CONFIG)));
        return result;
    }


    /**
     * 重置配置
     *
     * @return
     */
    @ApiOperation("重置配置")
    @PostMapping("/resetScreenOrder")
    public BaseResponse resetScreenOrder() {
        try {
            if (redisService.setNx(SCREEN_ORDER_CURRENT_EXECUTE_STATE, SCREEN_ORDER_CURRENT_EXECUTE_STATE, 60 * 3l)) {
                redisService.delete(INIT_SCREEN_ORDER_CONFIG);
                redisService.delete(SCREEN_ORDER_ADD_LAST_TIME);
                // 删除历史数据
                bigScreenProvider.deleteScreenOrderAndDetail();
                // 修改初始数据的时间
                bigScreenProvider.initOrderData();
                JSONArray config = getConfig(1);
                redisService.setString(INIT_SCREEN_ORDER_CONFIG_RESET, config.toJSONString());
                List<ScreenOrder> screenOrders = new ArrayList<>();
                List<ScreenOrderDetail> screenOrderDetails = new ArrayList<>();
                for (int i = 0; i < config.size(); i++) {
                    JSONObject executePart = config.getJSONObject(i);
                    // 获取当前数据库总金额
                    CateSaleResponse listBaseResponse = bigScreenProvider.queryCateSale().getContext().get(0);
                    LocalDateTime startTime = LocalDateTime.parse(executePart.getString("startTime"), formatter);
                    LocalDateTime endTime = LocalDateTime.parse(executePart.getString("endTime"), formatter);
                    BigDecimal curTotalPrice = listBaseResponse.getTotalPrice();
                    BigDecimal targetPrice = executePart.getBigDecimal("targetPrice");// 目标金额
                    // 计算当前阶段应当生成的总额
                    BigDecimal shouldAddPrice = targetPrice.subtract(curTotalPrice);
                    screenGenerateDataHandler.generateOrder(startTime, endTime, shouldAddPrice, screenOrders, screenOrderDetails,1);
                    if (!screenOrders.isEmpty()) {
                        GenerateOrderRequest request = new GenerateOrderRequest();
                        request.setScreenOrders(screenOrders);
                        request.setScreenOrderDetails(screenOrderDetails);
                        bigScreenProvider.saveScreenOrderAndDetail(request);
                        ScreenOrder screenOrder = screenOrders.get(screenOrders.size() - 1);
                        redisService.setString(SCREEN_ORDER_ADD_LAST_TIME, formatter.format(screenOrder.getCreateTime()));
                    }
                }
            }
        } finally {
            redisService.delete(SCREEN_ORDER_CURRENT_EXECUTE_STATE);
        }
        return BaseResponse.success("OK");
    }

    private JSONArray getConfig(Integer type) {
        JSONArray item = new JSONArray();
        if (type == 1) {
            // 阶段1 当前时间段开始，5分钟内波1亿
            JSONObject itemOptOne = new JSONObject();
            LocalDateTime currentDate = LocalDateTime.now().minusSeconds(6l);
            itemOptOne.put("startTime", formatter.format(currentDate));
            itemOptOne.put("targetPrice", 1_0000_0000);
            LocalDateTime itemOptOneEndTime = currentDate.plusMinutes(5l);
            itemOptOne.put("endTime", formatter.format(itemOptOneEndTime));
            itemOptOne.put("type", 1);
            item.add(itemOptOne);
        } else {
            String resetStr = redisService.getString(INIT_SCREEN_ORDER_CONFIG_RESET);
            if (StringUtils.isNotEmpty(resetStr)) {
                JSONObject jsonObject = JSONArray.parseArray(resetStr).getJSONObject(0);
                String endTimeStr = jsonObject.getString("endTime");
                LocalDateTime currentDate = LocalDateTime.parse(endTimeStr, formatter);
                //阶段2,8分钟破2亿
                JSONObject itemOptTwo = new JSONObject();
                itemOptTwo.put("startTime", formatter.format(currentDate.plusSeconds(1l)));
                itemOptTwo.put("targetPrice", 1_2000_0000);
                LocalDateTime itemOptTwoTime = currentDate.plusMinutes(6l);
                itemOptTwo.put("endTime", formatter.format(itemOptTwoTime));
                itemOptTwo.put("type", 1);
                item.add(itemOptTwo);
                // 阶段3，每三分钟3-4千w
                JSONObject itemOptThree = new JSONObject();
                itemOptThree.put("startTime", formatter.format(itemOptTwoTime.plusSeconds(1l)));
                itemOptThree.put("price", 3000_0000);
                itemOptThree.put("intervalTime", 180);
                LocalDateTime itemOptThreeTime = itemOptTwoTime.plusMinutes(30l);
                itemOptThree.put("endTime", formatter.format(itemOptThreeTime));
                itemOptThree.put("type", 2);
                item.add(itemOptThree);
                // 阶段4，达5亿后每天增长20w
                JSONObject itemOptFour = new JSONObject();
                itemOptFour.put("startTime", formatter.format(itemOptThreeTime.plusSeconds(1l)));
                itemOptFour.put("intervalTime", 60);
                itemOptFour.put("price", 10000);
                itemOptFour.put("type", 2);
                item.add(itemOptFour);
            }
        }
        return item;
    }


    @ApiOperation("查询今日预售数据")
    @PostMapping("/todayPreSaleData")
    public BaseResponse queryPreSaleData() {
        return BaseResponse.success(bigScreenProvider.queryTodayPreSaleData().getContext());
    }


    /**
     * 实时订单接口
     *
     * @return
     */
    @ApiOperation("查询实时订单")
    @PostMapping("/realTimeOrder")
    public BaseResponse queryRealTimeOrder() {
        BaseResponse<List<RealTimeOrderResponse>> baseResponse = bigScreenProvider.queryRealTimeOrder();
        List<RealTimeOrderResponse> realTimeOrderResponses = baseResponse.getContext();
        if (CollectionUtils.isNotEmpty(realTimeOrderResponses)) {
            LocalDateTime localDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm:ss");
            realTimeOrderResponses.forEach(item -> {
                item.setCreateTime(formatter.format(localDateTime));
            });
        }
        return BaseResponse.success(realTimeOrderResponses);
    }

    /**
     * 区域预售金额分析接口
     *
     * @return
     */
    @ApiOperation("查询区域预售金额")
    @PostMapping("/areaPreSaleTotalMoney")
    public BaseResponse<AreaPreSaleTotalMoneyResponse> queryAreaPreSaleTotalMoney() {
        AreaPreSaleTotalMoneyResponse areaPreSale = bigScreenProvider.queryAreaPreSaleTotalMoney().getContext();
        return BaseResponse.success(areaPreSale);
    }


    /**
     * 品类销售排行
     *
     * @return
     */
    @ApiOperation("查询品类销售排行")
    @PostMapping("/queryCateSale")
    public BaseResponse queryCateSale() {
        DecimalFormat format = new DecimalFormat("#");
        AreaPreSaleTotalMoneyResponse areaPreSale = bigScreenProvider.queryAreaPreSaleTotalMoney().getContext();
//        BaseResponse<List<CateSaleResponse>> baseResponse = bigScreenProvider.queryCateSale();

        List<CateSaleResponse> cateSaleResponseList = new ArrayList<>();
        // 获取总金额
        BigDecimal total = areaPreSale.getTotal();
        JSONArray cateList = RandomAddressUtil.getCateList();
        for (int i = 0; i < cateList.size(); i++) {
            JSONObject cateObj = cateList.getJSONObject(i);
            CateSaleResponse cateSaleResponse = new CateSaleResponse();
            cateSaleResponse.setCateName(cateObj.getString("cateName"));
            cateSaleResponse.setPercent(new BigDecimal(cateObj.getString("percent")));
            // 根据百分比计算金额
            double newPercent = cateSaleResponse.getPercent().doubleValue() / 100;
            cateSaleResponse.setTotalPrice(new BigDecimal(format.format(total.multiply(BigDecimal.valueOf(newPercent)))));
            cateSaleResponseList.add(cateSaleResponse);
        }

//        baseResponse.getContext().forEach(cateSaleResponse -> {
//            Integer percent = cateSaleResponse.getPercent();
//            double newPer = percent.doubleValue() / 100;
//            cateSaleResponse.setTotalPrice(new BigDecimal(format.format(total.multiply(BigDecimal.valueOf(newPer)))));
//        });
        cateSaleResponseList.sort((b,a) -> a.getTotalPrice().compareTo(b.getTotalPrice()));
        return BaseResponse.success(cateSaleResponseList);
    }

}
