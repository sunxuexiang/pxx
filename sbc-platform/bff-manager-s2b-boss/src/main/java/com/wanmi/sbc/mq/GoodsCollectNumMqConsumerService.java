package com.wanmi.sbc.mq;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.constant.MQConstant;
import com.wanmi.sbc.es.elastic.EsBulkGoodsInfoElasticService;
import com.wanmi.sbc.es.elastic.EsGoodsInfoElasticService;
import com.wanmi.sbc.es.elastic.request.EsGoodsInfoRequest;
import com.wanmi.sbc.goods.api.provider.goods.BulkGoodsProvider;
import com.wanmi.sbc.goods.api.provider.goods.GoodsProvider;
import com.wanmi.sbc.goods.api.request.goods.GoodsModifyCollectNumRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;

/**
 * @ClassName GoodsCollectNumMqConsumerService
 * @Description 统计商品收藏量mq 消费者service
 * @Author lvzhenwei
 * @Date 2019/4/12 10:47
 **/
@Service
@EnableBinding(GoodsAboutNumSink.class)
public class GoodsCollectNumMqConsumerService {

    @Autowired
    private GoodsProvider goodsProvider;

    @Autowired
    private BulkGoodsProvider bulkGoodsProvider;
    
    @Autowired
    private EsGoodsInfoElasticService esGoodsInfoElasticService;

    @Autowired
    private EsBulkGoodsInfoElasticService bulkGoodsInfoElasticService;

    /**
     * @Author lvzhenwei
     * @Description 商品收藏量mq 对应消费方法
     * @Date 10:49 2019/4/12
     * @Param []
     * @return void
     **/
    @StreamListener(MQConstant.GOODS_COLLECT_NUM)
    public void goodsCollectNumMqConsumer(String msg){
        GoodsModifyCollectNumRequest request = JSONObject.parseObject(msg,GoodsModifyCollectNumRequest.class);
        if(2 == request.getSubType()){// 2是散批
            bulkGoodsProvider.updateGoodsCollectNum(request);
            bulkGoodsInfoElasticService.initEsBulkGoodsInfo(EsGoodsInfoRequest.builder().goodsId(request.getGoodsId()).build());
            return;
        }
        goodsProvider.updateGoodsCollectNum(request);
        esGoodsInfoElasticService.initEsGoodsInfo(EsGoodsInfoRequest.builder().goodsId(request.getGoodsId()).build());
    }
}
