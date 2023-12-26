package com.wanmi.sbc.order.stockupaction.service;

import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.order.api.request.trade.BatchAddStockupActionRequest;
import com.wanmi.sbc.order.api.request.trade.StockupActionDeleteRequest;
import com.wanmi.sbc.order.stockupaction.model.entity.StockupAction;
import com.wanmi.sbc.order.stockupaction.repository.StockupActionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @description: 提货明细业务逻辑service
 * @author: XinJiang
 * @time: 2021/12/17 16:25
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class StockupActionService {

    @Autowired
    private StockupActionRepository stockupActionRepository;

    /**
     * 批量删除
     * @param request
     */
    public void delStockupAction(StockupActionDeleteRequest request){
        stockupActionRepository.deleteByCreateTime(request.getBeginTime(),request.getEndTime());
    }


    /**
     * 批量新增
     * @param request
     */
    public void batchAddStockupAction(BatchAddStockupActionRequest request){
        List<StockupAction> stockupActions = KsBeanUtil.convertList(request.getStockupActionVOS(),StockupAction.class);
        stockupActionRepository.saveAll(stockupActions);
    }
}
