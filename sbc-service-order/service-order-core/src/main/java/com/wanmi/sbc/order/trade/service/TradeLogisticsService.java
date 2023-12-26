package com.wanmi.sbc.order.trade.service;

import com.wanmi.sbc.order.api.response.trade.OrderSalesRankingSort;
import com.wanmi.sbc.order.trade.model.root.OrderLogistics;
import com.wanmi.sbc.order.trade.repository.OrderLogisticsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: open 物流处理服务层
 * @description:
 * @author: Mr.Tian
 * @create: 2020-05-18 15:18
 **/
@Service
@Slf4j
public class TradeLogisticsService {
    @Autowired
    private OrderLogisticsRepository orderLogisticsRepository;

    public OrderLogistics findByLogisticId(String logisticsId){
        return orderLogisticsRepository.findByLogisticId(logisticsId);
    }

    public List<OrderLogistics> findByLogisticIds(List<String> logisticsIds){
        return orderLogisticsRepository.findByLogisticIds(logisticsIds);
    }

}
