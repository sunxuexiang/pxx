package com.wanmi.sbc.returnorder.orderpicking.service;

import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.returnorder.api.request.orderpicking.OrderPickingRequest;
import com.wanmi.sbc.returnorder.orderpicking.model.root.OrderPicking;
import com.wanmi.sbc.returnorder.orderpicking.repository.OrderPickingRepository;
import com.wanmi.sbc.returnorder.returnorder.model.root.ReturnOrder;
import com.wanmi.sbc.returnorder.returnorder.repository.ReturnOrderRepository;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/4/12 14:34
 */
@Service
public class OrderPickingService {

    @Autowired
    private OrderPickingRepository orderPickingRepository;

    @Autowired
    private ReturnOrderRepository returnOrderRepository;


    public void sava(OrderPickingRequest request) {

        List<ReturnOrder> returnOrders = returnOrderRepository.findByTid(request.getTid());

        if (CollectionUtils.isNotEmpty(returnOrders)) {
            throw new SbcRuntimeException("K-050145", new Object[]{request.getTid()});
        }

        Optional<OrderPicking> picking = orderPickingRepository.findByTradeId(request.getTid());
        if (!picking.isPresent()) {
            OrderPicking orderPicking = new OrderPicking();
            orderPicking.setTradeId(request.getTid());
            orderPicking.setStatus(1);
            orderPickingRepository.save(orderPicking);
        }

    }


    public List<OrderPicking> getByTidList(List<String> tidList) {
        return orderPickingRepository.getByTidList(tidList);
    }

    public OrderPicking getByTid(String tid) {
        return orderPickingRepository.findByTradeId(tid).orElse(null);
    }

}
