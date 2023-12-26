package com.wanmi.sbc.mq;

import com.wanmi.sbc.common.constant.RedisKeyConstant;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.goods.api.provider.cate.GoodsCateQueryProvider;
import com.wanmi.sbc.goods.api.request.cate.GoodsCateByIdRequest;
import com.wanmi.sbc.goods.api.request.cate.GoodsCateListByConditionRequest;
import com.wanmi.sbc.goods.api.response.cate.GoodsCateByIdResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsCateVO;
import com.wanmi.sbc.order.api.constant.JmsDestinationConstants;
import com.wanmi.sbc.order.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.order.api.request.trade.TradeGetByIdRequest;
import com.wanmi.sbc.order.bean.vo.TradeVO;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.redis.RedisZSetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @description: 热销排行榜MQ消费者
 * @author: XinJiang
 * @time: 2022/5/27 10:04
 */
@Service
@Slf4j
@EnableBinding(BossSink.class)
public class HotSaleAreaConsumerService {

    @Autowired
    private RedisZSetUtil redisZSetUtil;

    @Autowired
    private RedisService redisService;

    @Autowired
    private TradeQueryProvider tradeQueryProvider;

    @Autowired
    private GoodsCateQueryProvider goodsCateQueryProvider;

    @StreamListener(JmsDestinationConstants.Q_HOT_SALE_AREA)
    public void hotSaleArea(String tid) {
        log.info("==========热销排行榜埋点数据 MQ消费者开始：：：{}，订单号：{}", LocalDateTime.now(),tid);

        //获取所有分类
        GoodsCateListByConditionRequest cateRequest = new GoodsCateListByConditionRequest();
        cateRequest.setDelFlag(DeleteFlag.NO.toValue());
        //二级分类
        cateRequest.setCateGrade(2);
        List<GoodsCateVO> goodsCateVOList = goodsCateQueryProvider.listByCondition(cateRequest).getContext().getGoodsCateVOList();
        List<Long> cateIds = goodsCateVOList.stream().map(GoodsCateVO::getCateId).collect(Collectors.toList());

        TradeVO tradeVO = tradeQueryProvider.getOrderById(TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO();

        String nowDate = DateUtil.format(LocalDateTime.now(),DateUtil.FMT_TIME_5);
        String redisKey = RedisKeyConstant.HIT_LIST;
        Long wareId = 1L;
        if (Objects.nonNull(tradeVO.getWareId())) {
            wareId = tradeVO.getWareId();
        }
        final String finalWareId = wareId.toString();
        String totalRedisKey = redisKey.concat("-").concat(finalWareId).concat("-").concat(nowDate);
        //过期时间8天
        final Long expireTime = 60*60*24*8L;

        tradeVO.getTradeItems().forEach(tradeItemVO -> {

            /**每日总排行榜*/
            Long totalSetElement = redisZSetUtil.zRank(totalRedisKey, tradeItemVO.getSkuId());
            if (Objects.nonNull(totalSetElement)) {
                redisZSetUtil.zIncrementScore(totalRedisKey, tradeItemVO.getSkuId(), Double.parseDouble(tradeItemVO.getNum().toString()));
            } else {
                redisZSetUtil.zAdd(totalRedisKey, tradeItemVO.getSkuId(), Double.parseDouble(tradeItemVO.getNum().toString()));
            }

            /**分类排行榜*/
            //递归获取二级分类信息
            GoodsCateByIdResponse goodsCate = goodsCateQueryProvider.getByIdForLevel(GoodsCateByIdRequest
                    .builder().cateId(tradeItemVO.getCateId()).build()).getContext();

            String cateRedisKey = redisKey.concat("-").concat(goodsCate.getCateId().toString())
                    .concat("-").concat(finalWareId).concat("-").concat(nowDate);

            Long cateSetElement = redisZSetUtil.zRank(cateRedisKey, tradeItemVO.getSkuId());
            if (Objects.nonNull(cateSetElement)) {
                redisZSetUtil.zIncrementScore(cateRedisKey, tradeItemVO.getSkuId(), Double.parseDouble(tradeItemVO.getNum().toString()));
            } else {
                redisZSetUtil.zAdd(cateRedisKey, tradeItemVO.getSkuId(), Double.parseDouble(tradeItemVO.getNum().toString()));
            }

            redisService.expireBySeconds(totalRedisKey, expireTime);
            redisService.expireBySeconds(cateRedisKey, expireTime);
        });

        log.info("==========热销排行榜埋点数据 MQ消费者结束：：：{}，订单号：{}", LocalDateTime.now(),tid);
    }
}
