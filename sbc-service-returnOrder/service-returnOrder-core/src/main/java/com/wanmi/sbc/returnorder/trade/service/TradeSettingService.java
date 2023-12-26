package com.wanmi.sbc.returnorder.trade.service;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.enums.Platform;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.returnorder.bean.enums.FlowState;
import com.wanmi.sbc.returnorder.bean.enums.ReturnFlowState;
import com.wanmi.sbc.returnorder.follow.request.TradeSettingRequest;
import com.wanmi.sbc.returnorder.returnorder.model.root.ReturnOrder;
import com.wanmi.sbc.returnorder.returnorder.service.ReturnOrderService;
import com.wanmi.sbc.returnorder.trade.model.root.Trade;
import com.wanmi.sbc.setting.api.provider.AuditProvider;
import com.wanmi.sbc.setting.api.provider.AuditQueryProvider;
import com.wanmi.sbc.setting.api.request.TradeConfigGetByTypeRequest;
import com.wanmi.sbc.setting.api.request.TradeConfigsModifyRequest;
import com.wanmi.sbc.setting.api.response.OrderAutoReceiveConfigGetResponse;
import com.wanmi.sbc.setting.api.response.TradeConfigGetByTypeResponse;
import com.wanmi.sbc.setting.bean.dto.TradeConfigDTO;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
import com.wanmi.sbc.setting.bean.vo.ConfigVO;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单设置服务
 */
@Service
@Slf4j
@Transactional(readOnly = true, timeout = 10)
public class TradeSettingService {
    @Autowired
    private AuditProvider auditProvider;

    @Autowired
    private AuditQueryProvider auditQueryProvider;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private ReturnOrderService returnOrderService;

    /**
     * 查询订单设置配置
     *
     * @return List<Config>a
     */
    public List<ConfigVO> queryTradeConfigs() {
//        return configRepository.findByConfigKeyAndDelFlag(ConfigKey.ORDERSETTING.toString()
//                , DeleteFlag.NO)
//                .collect(Collectors.toList());
        return auditQueryProvider.listTradeConfig().getContext().getConfigVOList();
    }

    /**
     * 修改订单配置
     *
     * @param tradeSettingRequests tradeSettingRequests
     */
    @Transactional
    public void updateTradeConfigs(List<TradeSettingRequest> tradeSettingRequests) {
        if (CollectionUtils.isEmpty(tradeSettingRequests)) {
            log.warn("tradeSettingRequests params is empty, can't modify");
            return;
        }

//        tradeSettingRequests.forEach(tradeSettingRequest -> configRepository
//                .updateStatusByTypeAndConfigKey(tradeSettingRequest.getConfigType().toString(),
//                        tradeSettingRequest.getConfigKey().toString(), tradeSettingRequest.getStatus(), tradeSettingRequest.getContext()));

        List<TradeConfigDTO> tradeConfigDTOS = new ArrayList<>();
        KsBeanUtil.copyList(tradeSettingRequests, tradeConfigDTOS);

        TradeConfigsModifyRequest request = new TradeConfigsModifyRequest();
        request.setTradeConfigDTOList(tradeConfigDTOS);

        auditProvider.modifyTradeConfigs(request);
    }

    /**
     * 根据type查询config
     *
     * @param configType configType
     * @return config
     */
    public TradeConfigGetByTypeResponse queryTradeConfigByType(ConfigType configType) {
//        return configRepository.findByConfigTypeAndDelFlag(configType.toValue(), DeleteFlag.NO);
        TradeConfigGetByTypeRequest request = new TradeConfigGetByTypeRequest();
        request.setConfigType(configType);

        return auditQueryProvider.getTradeConfigByType(request).getContext();
    }

    /**
     * 订单代发货自动收货
     */
    // @LcnTransaction
    // @Transactional
    public void orderAutoReceive() {
        //查询符合订单
        //批量扭转状态
//        Config config = configRepository.findByConfigTypeAndDelFlag(ConfigType.ORDER_SETTING_AUTO_RECEIVE.toString(), DeleteFlag.NO);
        OrderAutoReceiveConfigGetResponse config =auditQueryProvider.getOrderAutoReceiveConfig().getContext();

        val pageSize = 1000;
        try {
            Integer day = Integer.valueOf(JSON.parseObject(config.getContext()).get("day").toString());
            LocalDateTime endDate = LocalDateTime.now().minusDays(day);

            long total = tradeService.countTradeByDate(endDate, FlowState.DELIVERED);

            log.info("自动确认收货分页订单数: " + total);
            int pageNum = 0;
            boolean loopFlag = true;
            //超过1000条批量处理
            while(loopFlag && total > 0){
                List<Trade> tradeList = tradeService.queryTradeByDate(endDate, FlowState.DELIVERED, pageNum, pageSize);
                if(tradeList != null && !tradeList.isEmpty()){
                    tradeList.forEach(trade -> tradeService.confirmReceive(trade.getId(), Operator.builder().platform(Platform.PLATFORM)
                            .name("system").account("system").platform(Platform.PLATFORM).build()));
                    if(tradeList.size() == pageSize){
                        pageNum++;
                        continue;
                    }
                }
                loopFlag = false;
            }

            log.info("自动确认收货成功");

//            if (page.getTotalElements() > pageSize) {
//                int pageNum = calPage(page.getTotalPages(), pageSize);
//                for (int i = 0; i < pageNum; i++) {
//                    tradeService.queryTradeByPage(endDate, FlowState.DELIVERED, new PageRequest(i, pageSize))
//                            .forEach(trade -> {
//                                log.info("执行的订单号: " + trade.getId());
//                                tradeService.confirmReceive(trade.getId(), Operator.builder().platform(Platform.PLATFORM).build());
//                            });
//                }
//            } else {
//                page.getContent().forEach(trade -> tradeService.confirmReceive(trade.getId(), Operator.builder()
//                        .name("system").platform(Platform.PLATFORM).build()));
//            }

        } catch (Exception ex) {
            log.error("orderAutoReceive schedule error");
            throw new SbcRuntimeException("K-050129");
        }
    }

    /**
     * 退单自动审核
     */
    @Transactional
    public void returnOrderAutoAudit(Integer day) {
        //查询符合订单
        //批量扭转状态
        val pageSize = 1000;
        try {
            LocalDateTime endDate = LocalDateTime.now().minusDays(day);
            int total = returnOrderService.countReturnOrderByEndDate(endDate, ReturnFlowState.INIT);
            log.info("退单自动审核分页订单数: " + total);
            //超过1000条批量处理
            if (total > pageSize) {
                int pageNum = calPage(total, pageSize);
                for (int i = 0; i < pageNum; i++) {
                    returnOrderService.queryReturnOrderByEndDate(endDate, i * pageSize, i + pageSize + pageSize
                            , ReturnFlowState.INIT)
                            .forEach(returnOrder ->{
                                try{
                                    processReturnAutoAction(ReturnFlowState.INIT, returnOrder);
                                } catch (SbcRuntimeException brt){
                                    log.error("rid " +  returnOrder.getTid() + "异常： "+ brt.getMessage());
                                }
                            } );
                }
            } else {
                List<ReturnOrder> returnOrders = returnOrderService.queryReturnOrderByEndDate(endDate, 0, total, ReturnFlowState.INIT);
                returnOrders.forEach(returnOrder -> {
                    log.info("执行的退单号: " + returnOrder.getId());
                    try{
                        processReturnAutoAction(ReturnFlowState.INIT, returnOrder);
                    } catch (SbcRuntimeException brt){
                        log.error("rid " +  returnOrder.getTid() + "异常： "+ brt.getMessage());
                    }
                });
            }
            log.info("退单自动审核成功");
        } catch (Exception ex) {
            log.error("returnOrderAutoAudit schedule error");
            ex.printStackTrace();
            throw new SbcRuntimeException("K-050005");
        }
    }


    /**
     * 退单自动确认收货 由于es索引的问题，用mongodb 分页查询，考虑把订单，退单从es中移除
     * @param day day
     */
    @Transactional
    public void returnOrderAutoReceive(Integer day) {
        val pageSize = 1000;
        LocalDateTime endDate = LocalDateTime.now().minusDays(day);
        int total = returnOrderService.countReturnOrderByEndDate(endDate, ReturnFlowState.DELIVERED);
        log.info("退单去人收货分页订单数: " + total);
        //超过1000条批量处理
        if (total > pageSize) {
            int pageNum = calPage(total, pageSize);
            for (int i = 0; i < pageNum; i++) {
                returnOrderService.queryReturnOrderByEndDate(endDate, i * pageSize, i * pageSize + pageSize, ReturnFlowState.DELIVERED)
                        .forEach(returnOrder -> {
                            log.info("执行的退单号: " + returnOrder.getId());
                            try{
                                processReturnAutoAction(ReturnFlowState.DELIVERED, returnOrder);
                            } catch (SbcRuntimeException brt){
                                log.error("rid " +  returnOrder.getTid() + "异常： "+ brt.getMessage());
                            }
                        });
            }
        } else {
            List<ReturnOrder> returnOrders = returnOrderService.queryReturnOrderByEndDate(endDate, 0, total, ReturnFlowState.DELIVERED);

            returnOrders.forEach(returnOrder
                    -> {
                log.info("执行的退单号: " + returnOrder.getId());
                try{
                    processReturnAutoAction(ReturnFlowState.DELIVERED, returnOrder);
                } catch (SbcRuntimeException brt){
                    log.error("rid " +  returnOrder.getTid() + "异常： "+ brt.getMessage());
                }
            });
        }

        log.info("退单收货审核成功");
    }


    /**
     * 退单自动处理任务
     *
     * @param returnFlowState returnFlowState
     * @param returnOrder     returnOrder
     */
    private void processReturnAutoAction(ReturnFlowState returnFlowState, ReturnOrder returnOrder) {
        if (ReturnFlowState.DELIVERED.equals(returnFlowState)) {
            returnOrderService.receive(returnOrder.getId(),
                    Operator.builder().name("system").account("system").platform(Platform.PLATFORM).build());
        } else if (ReturnFlowState.INIT.equals(returnFlowState)) {
            returnOrderService.audit(returnOrder.getId(),
                    Operator.builder().name("system").account("system").platform(Platform.PLATFORM).build());
        }
    }

    /**
     * 计算页码
     *
     * @param count
     * @param size
     * @return
     */
    private int calPage(int count, int size) {
        int page = count / size;
        if (count % size == 0) {
            return page;
        } else {
            return page + 1;
        }
    }

}
