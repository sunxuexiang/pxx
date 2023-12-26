package com.wanmi.sbc.es.elastic;

import com.wanmi.sbc.es.elastic.request.EsGoodsInfoRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>异步刷新es<p>
 *
 * @author zhaowei
 * @date 2021/6/15
 */
@Service
public class EsGoodsModifyInventoryService {
    @Autowired
    private EsGoodsInfoElasticService esGoodsInfoElasticService;

    @Autowired
    private EsRetailGoodsInfoElasticService esRetailGoodsInfoElasticService;

    @Autowired
    private EsBulkGoodsInfoElasticService esBulkGoodsInfoElasticService;

    @Async
    public void  modifyInventory(List<String> skuIds) {
        esGoodsInfoElasticService.initEsGoodsInfo(EsGoodsInfoRequest.builder().skuIds(skuIds).build());
    }

    @Async
    public void modifyInventory(List<String> skuIds,String goodsId){
        esGoodsInfoElasticService.initEsGoodsInfo(EsGoodsInfoRequest.builder().goodsId(goodsId).skuIds(skuIds).build());
    }

    @Async
    public void modifyRetailInventory(List<String> skuIds,String goodsId) {
        esRetailGoodsInfoElasticService.initEsRetailGoodsInfo(EsGoodsInfoRequest.builder().goodsId(goodsId).skuIds(skuIds).build());
    }

    @Async
    public void modifyBulkInventory(List<String> skuIds,String goodsId) {
        esBulkGoodsInfoElasticService.initEsBulkGoodsInfo(EsGoodsInfoRequest.builder().goodsId(goodsId).skuIds(skuIds).build());
    }

}
