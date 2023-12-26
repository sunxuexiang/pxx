package com.wanmi.ares.screen.dao;

import com.wanmi.ares.request.screen.RealTimeOrderRequest;
import com.wanmi.ares.request.screen.model.ScreenOrder;
import com.wanmi.ares.request.screen.model.ScreenOrderDetail;
import com.wanmi.ares.response.screen.PreSaleDataResponse;
import com.wanmi.ares.response.screen.RealTimeOrderResponse;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Repository
public interface BigScreenMapper {

    /**
     * 批量新增订单
     * @param screenOrders
     */
    void saveScreenOrderBatch(@Param("screenOrders") List<ScreenOrder> screenOrders);


    /**
     * 批量生成订单详情数据
     * @param orderDetails
     */
    void saveScreenOrderDetailBatch(@Param("orderDetails")List<ScreenOrderDetail> orderDetails);

    /**
     * 返回今日预售数据
     * @param today
     * @return
     */
    PreSaleDataResponse queryTodayPreSaleData(@Param("today") String today);

    /**
     * 查询实时订单数据
     * @return
     */
    List<RealTimeOrderResponse> queryRealTimeOrder(@Param("dateTime") String dateTime);

    /**
     * 查询区域预售金额
     * @return
     */
    List<Map> queryAreaPreSaleTotalMoney();

    /**
     * 品类数据总金额
     * @return
     */
    Map<String, BigDecimal> queryCateSaleTotalPrice();

    /**
     * 分类统计商品数据
     * @return
     */
    List<Map> queryCateSale();

    /*获取数据库最新时间*/
    Map<String, String> queryScreenLastTime();

    void deleteScreenOrder();

    void deleteScreenOrderDetail();

    void initScreenOrder();

    void initScreenOrderDetail();
}
