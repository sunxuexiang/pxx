package com.wanmi.sbc.shopcart.historytownshiporder.service;

import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.wanmi.sbc.shopcart.historytownshiporder.repository.HistoryTownShipOrderRepository;
import com.wanmi.sbc.shopcart.historytownshiporder.response.TrueStock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class HistoryTownShipOrderService {

    @Autowired
    private HistoryTownShipOrderRepository historyTownShipOrderRepository;


    /**
     * 库存表减去乡镇件表
     * @param skuids
     * @return
     */
    public List<TrueStock> getskusstock(List<String> skuids){
        List<Object> getskusstock = historyTownShipOrderRepository.getskusstock(skuids);
        return getskusstock.stream().map(v->{
           return TrueStock.builder()
                    .stock((BigDecimal) ((Object[]) v)[0])
                    .skuid((String) ((Object[]) v)[1])
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * 获取乡镇件挤压库存
     * @param skuids
     * @return
     */
    public List<TrueStock> getskusJiYastock(List<String> skuids){
        List<Object> getskusstock = historyTownShipOrderRepository.getskusJiYastock(skuids);
        return getskusstock.stream().map(v->{
            return TrueStock.builder()
                    .stock((BigDecimal) ((Object[]) v)[0])
                    .skuid((String) ((Object[]) v)[1])
                    .build();
        }).collect(Collectors.toList());
    }



    /**
     * 库存表减去乡镇件表
     * @param skuids
     * @return
     */
    public List<TrueStock> getskusstocklingshou(List<String> skuids){
        List<Object> getskusstock = historyTownShipOrderRepository.getskusstockbylingshou(skuids);
        return getskusstock.stream().map(v->{
            return TrueStock.builder()
                    .stock((BigDecimal) ((Object[]) v)[0])
                    .skuid((String) ((Object[]) v)[1])
                    .build();
        }).collect(Collectors.toList());
    }




    /**
     * 库存表减去乡镇件表
     * @param skuids
     * @return
     */
    public List<TrueStock> getskusstockbybulk(List<String> skuids){
        List<Object> getskusstock = historyTownShipOrderRepository.getskusstockbybulk(skuids);
        return getskusstock.stream().map(v->{
            return TrueStock.builder()
                    .stock((BigDecimal) ((Object[]) v)[0])
                    .skuid((String) ((Object[]) v)[1])
                    .build();
        }).collect(Collectors.toList());
    }



    /**
     * 通过订单号修改wms_flag
     * @param tid
     */
    @Transactional
    public void reduceTownStock(String tid){
        log.info("HistoryTownShipOrderService.reduceTownStock修改历史乡镇件历史订单开始订单为="+tid);
        if (CollectionUtils.isEmpty(historyTownShipOrderRepository.getOrderBytid(tid))){
            log.info("com.wanmi.sbc.order.historytownshiporder.service.HistoryTownShipOrderService.reduceTownStock=================未查询到数据或者已经推送wms，tid="+tid);
        }else {
            historyTownShipOrderRepository.updateWmsFlag(tid);
        }
    }

    /**
     * 通过订单号修改cancel_flag
     * @param tid
     */
    @Transactional
    public void CancelTownStock(String tid){
        log.info("HistoryTownShipOrderService.reduceTownStock取消历史乡镇件历史订单开始订单为="+tid);
        historyTownShipOrderRepository.updateThWmsFlag(tid);
    }


}
