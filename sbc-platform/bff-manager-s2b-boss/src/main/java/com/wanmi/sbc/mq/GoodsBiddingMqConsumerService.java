package com.wanmi.sbc.mq;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.es.elastic.EsGoodsInfoElasticService;
import com.wanmi.sbc.es.elastic.request.EsGoodsInfoRequest;
import com.wanmi.sbc.goods.api.constant.BiddingCacheConstants;
import com.wanmi.sbc.goods.api.constant.GoodsJmsDestinationConstants;
import com.wanmi.sbc.goods.api.provider.bidding.BiddingProvider;
import com.wanmi.sbc.goods.api.provider.bidding.BiddingQueryProvider;
import com.wanmi.sbc.goods.api.request.bidding.BiddingByIdRequest;
import com.wanmi.sbc.goods.api.request.bidding.BiddingDelByIdRequest;
import com.wanmi.sbc.goods.api.request.bidding.BiddingModifyRequest;
import com.wanmi.sbc.goods.api.response.bidding.BiddingByIdResponse;
import com.wanmi.sbc.goods.bean.enums.ActivityStatus;
import com.wanmi.sbc.goods.bean.enums.BiddingType;
import com.wanmi.sbc.goods.bean.vo.BiddingGoodsVO;
import com.wanmi.sbc.goods.bean.vo.BiddingVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.binding.BinderAwareChannelResolver;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @ClassName GoodsBiddingMqConsumerService
 * @Description 商品竞价开始结束活动的 消费者service
 * @Author lvzhenwei
 * @Date 2019/4/12 11:17
 **/
@Service
@EnableBinding(GoodsBiddingSink.class)
public class GoodsBiddingMqConsumerService {


    @Autowired
    private BinderAwareChannelResolver resolver;

    @Autowired
    private EsGoodsInfoElasticService esGoodsInfoElasticService;

    @Autowired
    private BiddingQueryProvider biddingQueryProvider;

    @Autowired
    private BiddingProvider biddingProvider;

    @Autowired
    private RedisTemplate redisTemplate;

    @StreamListener(GoodsJmsDestinationConstants.Q_GOODS_SERVICE_START_BIDDING_ACTIVITY_CONSUMER)
    public void startBiddingActivity(String biddingId){
        BiddingByIdResponse response = biddingQueryProvider.getById(BiddingByIdRequest.builder().biddingId(biddingId).build()).getContext();
        //1. 校验：判断当前时间在活动内，活动是未开始状态
        if(this.startValidate(response)){
            BiddingVO biddingVO = response.getBiddingVO();
            biddingVO.setBiddingStatus(ActivityStatus.SALE);
            //2. 开启活动，更新redis
            biddingProvider.modify(KsBeanUtil.convert(biddingVO,BiddingModifyRequest.class));
            List<String> goodsInfoIds = biddingVO.getBiddingGoodsVOS().stream().map(BiddingGoodsVO::getGoodsInfoId).collect(Collectors.toList());
            this.redisUpdateBidding(biddingVO);
            //3. 更新es
            esGoodsInfoElasticService.initEsGoodsInfo(EsGoodsInfoRequest.builder().skuIds(goodsInfoIds).build());
            //4. 发送结束的延时队列
            Long millis = biddingVO.getEndTime().toInstant(ZoneOffset.of("+8")).toEpochMilli() - System.currentTimeMillis();
            resolver.resolveDestination(GoodsJmsDestinationConstants.Q_GOODS_SERVICE_FINISH_BIDDING_ACTIVITY_PRODUCER).send
                    (MessageBuilder.withPayload(biddingId).setHeader("x-delay", millis ).build());

        }
    }


    @StreamListener(GoodsJmsDestinationConstants.Q_GOODS_SERVICE_FINISH_BIDDING_ACTIVITY_CONSUMER)
    public void finishBiddingActivity(String biddingId){
        BiddingByIdResponse response = biddingQueryProvider.getById(BiddingByIdRequest.builder().biddingId(biddingId).build()).getContext();
        //1. 结束活动
        BiddingVO biddingVO = response.getBiddingVO();
        biddingVO.setBiddingStatus(ActivityStatus.COMPLETED);
        biddingProvider.finishBiddingActivity(BiddingDelByIdRequest.builder().biddingId(biddingId).build());
        //2. 更新redis
        this.redisUpdateBidding(biddingVO);
        //3. 更新es
        List<String> goodsInfoIds = biddingVO.getBiddingGoodsVOS().stream().map(BiddingGoodsVO::getGoodsInfoId).collect(Collectors.toList());
        esGoodsInfoElasticService.initEsGoodsInfo(EsGoodsInfoRequest.builder().skuIds(goodsInfoIds).build());
    }

    /**
     * 判断是否可以开启活动
     * @param response 在活动期间且活动未开始
     * @return
     */
    private boolean startValidate(BiddingByIdResponse response){
        if(Objects.nonNull(response) && Objects.nonNull(response.getBiddingVO())){
            BiddingVO biddingVO = response.getBiddingVO();
            LocalDateTime startTime = biddingVO.getStartTime();
            LocalDateTime finishTime = biddingVO.getEndTime();
            if(ActivityStatus.ABOUT_TO_START.equals(biddingVO.getBiddingStatus()) && DeleteFlag.NO.equals(biddingVO.getDelFlag())){
                if(startTime.isBefore(LocalDateTime.now()) && LocalDateTime.now().isBefore(finishTime)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 跟新redis中的缓存信息
     * @param bidding
     */
    public void redisUpdateBidding(BiddingVO bidding){
        String[] keywords = bidding.getKeywords().split(",");
        List<String> keywordsList = Arrays.asList(keywords);
        SetOperations<String, String> operations = redisTemplate.opsForSet();
        //活动结束删除指定元素
        if(ActivityStatus.COMPLETED.equals(bidding.getBiddingStatus())){
            if(BiddingType.KEY_WORDS_TYPE.equals(bidding.getBiddingType())){
                keywordsList.stream().forEach(k->operations.remove(BiddingCacheConstants.GOODS_KEY_WORDS,k));
            }else if (BiddingType.CATE_WORDS_TYPE.equals(bidding.getBiddingType())){
                keywordsList.stream().forEach(k->operations.remove(BiddingCacheConstants.GOODS_CATE_KEYS,k));
            }
        }
        //修改、新增 —— 指定元素(重复的元素不会被入redis)
        if(BiddingType.KEY_WORDS_TYPE.equals(bidding.getBiddingType())){
            if(BiddingType.KEY_WORDS_TYPE.equals(bidding.getBiddingType())){
                keywordsList.stream().forEach(k->operations.add(BiddingCacheConstants.GOODS_KEY_WORDS,k));
            }else if (BiddingType.CATE_WORDS_TYPE.equals(bidding.getBiddingType())){
                keywordsList.stream().forEach(k->operations.add(BiddingCacheConstants.GOODS_CATE_KEYS,k));
            }
        }
    }

}
