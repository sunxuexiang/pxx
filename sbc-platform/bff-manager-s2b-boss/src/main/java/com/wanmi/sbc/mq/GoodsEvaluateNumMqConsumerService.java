package com.wanmi.sbc.mq;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.constant.MQConstant;
import com.wanmi.sbc.es.elastic.EsGoodsInfoElasticService;
import com.wanmi.sbc.es.elastic.request.EsGoodsInfoRequest;
import com.wanmi.sbc.goods.api.provider.goods.GoodsProvider;
import com.wanmi.sbc.goods.api.request.goods.GoodsModifyEvaluateNumRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;

/**
 * @ClassName GoodsEvaluateNumMqConsumerService
 * @Description 统计商品评论数mq 消费者service
 * @Author lvzhenwei
 * @Date 2019/4/12 11:18
 **/
@Service
@EnableBinding(GoodsAboutNumSink.class)
public class GoodsEvaluateNumMqConsumerService {

    @Autowired
    private GoodsProvider goodsProvider;

    @Autowired
    private EsGoodsInfoElasticService esGoodsInfoElasticService;

    @StreamListener(MQConstant.GOODS_EVALUATE_NUM)
    public void goodsEvaluateNumMqConsumer(String msg){
        GoodsModifyEvaluateNumRequest request = JSONObject.parseObject(msg,GoodsModifyEvaluateNumRequest.class);
        goodsProvider.updateGoodsFavorableCommentNum(request);
        esGoodsInfoElasticService.initEsGoodsInfo(EsGoodsInfoRequest.builder().goodsId(request.getGoodsId()).build());
    }

}
