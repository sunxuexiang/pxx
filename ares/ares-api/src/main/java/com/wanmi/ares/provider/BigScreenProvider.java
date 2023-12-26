package com.wanmi.ares.provider;

import com.wanmi.ares.base.BaseResponse;
import com.wanmi.ares.request.screen.GenerateOrderRequest;
import com.wanmi.ares.request.screen.RealTimeOrderRequest;
import com.wanmi.ares.request.screen.model.ScreenOrder;
import com.wanmi.ares.request.screen.model.ScreenOrderDetail;
import com.wanmi.ares.response.screen.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 数据大屏服务提供接口
 * @author lm
 * @date 2022-09-06
 */
@FeignClient(value = "${application.ares.name}", url="${feign.url.ares:#{null}}",contextId = "BigScreenProvider")
public interface BigScreenProvider {

    /**
     * 初始化订单数据
     * @return
     */
    @PostMapping("/ares/${application.ares.version}/dataScreen/initOrderData")
    BaseResponse<?>  initOrderData();

    /**
     * 今日预售数据接口
     * @return
     *  {
     *     code: 'R-000000',
     *     message: null,
     *     context: {
     *         todayTotalMoney: 123456, //今日成交金额
     *         todayTotalNum: 123 //今日成交订单
     *     }
     *  }
     */
    @PostMapping("/ares/${application.ares.version}/dataScreen/todayPreSaleData")
    BaseResponse<PreSaleDataResponse>  queryTodayPreSaleData();

    /**
     * 实时订单接口
     * @return
     *  {
     *     code: 'R-000000',
     *     message: null,
     *     context: [
     *         {
     *             "createTime": "09-12 09:11:41",
     *             "address": "湖南省长沙市雨花区******",
     *             "account": "1731497****",
     *             "price": "0.01"
     *         }
     *     ]
     * }
     */
    @PostMapping("/ares/${application.ares.version}/dataScreen/realTimeOrder")
    BaseResponse<List<RealTimeOrderResponse>>  queryRealTimeOrder();

    /**
     *  区域预售金额分析接口
     * @return
     *  {
     *     code: 'R-000000',
     *     message: null,
     *     context: {
     *         total: 1234,
     *         cs: 1200,
     *         nc: 30,
     *         wh: 4
     *     }
     * }
     */
    @PostMapping("/ares/${application.ares.version}/dataScreen/areaPreSaleTotalMoney")
    BaseResponse<AreaPreSaleTotalMoneyResponse>  queryAreaPreSaleTotalMoney();

    /**
     * 品类销售排名接口
     * @return
     *  {
     *     code: 'R-000000',
     *     message: null,
     *     context: [
     *         {
     *             "cateName": "冲泡", //品类
     *             "totalPrice": 46086.40, // 总金额
     *             "percent": 35 // 百分比
     *         },
     *         {
     *             "cateName": "粮油3-12",
     *             "totalPrice": 26608.07,
     *             "percent": 20
     *         }
     *     ]
     * }
     */
    @PostMapping("/ares/${application.ares.version}/dataScreen/cateSale")
    BaseResponse<List<CateSaleResponse>>  queryCateSale();

    /**
     * 查询数据库最新时间
     * @return
     */
    @PostMapping("/ares/${application.ares.version}/dataScreen/queryScreenLastTime")
    ScreenLastTimeResponse queryScreenLastTime();

    /**
     * 保存订单数据
     * @param orderRequest
     */
    @PostMapping("/ares/${application.ares.version}/dataScreen/saveScreenOrder")
    void saveScreenOrderAndDetail(@RequestBody GenerateOrderRequest orderRequest);

    /**
     * 删除历史数据
     */
    @PostMapping("/ares/${application.ares.version}/dataScreen/deleteScreenOrderAndDetail")
    void deleteScreenOrderAndDetail();
}
