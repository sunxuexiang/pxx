package com.wanmi.sbc.mq;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.es.elastic.EsGoodsInfoElasticService;
import com.wanmi.sbc.es.elastic.request.EsGoodsInfoRequest;
import com.wanmi.sbc.order.api.constant.JmsDestinationConstants;
import com.wanmi.sbc.order.api.request.trade.TradeRefreshInventoryRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;

/**
 * <p>描述<p>
 *
 * @author zhaowei
 * @date 2021/6/18
 */
@Service
@Slf4j
@EnableBinding(BossSink.class)
public class ESSKURefreshService {

    @Autowired
    private EsGoodsInfoElasticService esGoodsInfoElasticService;

    @StreamListener(JmsDestinationConstants.Q_ORDER_SERVICE_STOCK_REFRESH_ES)
    public void wmsAutoChargeback(String json) {
        TradeRefreshInventoryRequest request = JSON.parseObject(json, TradeRefreshInventoryRequest.class);

        if(CollectionUtils.isNotEmpty(request.getSkuIds())){
            try {
                Thread.sleep(20000);
                log.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~更改库存刷新ES~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + json);
                esGoodsInfoElasticService.initEsGoodsInfo(EsGoodsInfoRequest.builder().skuIds(request.getSkuIds()).build());
            } catch (Exception e) {
                log.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~更改库存刷新失败~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + e);
            }
        }
    }
}
