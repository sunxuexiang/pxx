package com.wanmi.sbc.job;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.SpecialSymbols;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.goods.api.provider.goodsRecommendBackups.GoodsRecommendBackupsSaveProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.GoodsRecommendBackups.GoodsRecommendBackupsAddRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoByIdRequest;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoByIdResponse;
import com.wanmi.sbc.order.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.order.api.request.trade.TradeListAllRequest;
import com.wanmi.sbc.order.api.response.trade.OrderRecommendCount;
import com.wanmi.sbc.order.api.response.trade.OrderRecommendSkuCount;
import com.wanmi.sbc.order.bean.dto.TradeQueryDTO;
import com.wanmi.sbc.redis.RedisService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>千人千面商户推荐</p>
 * @Author shiGuangYi
 * @createDate 2023-06-16 16:34
 * @Description: 根据订单智能化
 * @Version 2.0
 */

@Component
@Slf4j
@JobHandler(value="merchantRecommendHandler")
public class MerchantRecommendHandler extends IJobHandler {
    @Autowired
    private RedisService redisService;

    @Autowired
    private TradeQueryProvider tradeQueryProvider;

    @Autowired
    private GoodsRecommendBackupsSaveProvider goodsRecommendBackupsSaveProvider;
    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;
    @Override
    public ReturnT<String> execute(String s) throws Exception {
        merchantRecommendHandler();
        return SUCCESS;
    }
    /**
     * <p>统计每个用户的推荐数据</p>
     * <p>用户数据 来着订单30天之内<p>
     *
     * */


    public BaseResponse<String> merchantRecommendHandler() {

        //仅仅查询订单30天之内的用户数据
        BaseResponse<List<OrderRecommendCount>> listBaseResponse = tradeQueryProvider.recommendTypeGetCustomerId();
        //每个用户开始缓存
        if (CollectionUtils.isNotEmpty(listBaseResponse.getContext())){
            //优先删除数据库备份
            goodsRecommendBackupsSaveProvider.deleteAll();
            listBaseResponse.getContext().forEach(c->{
                recommendTypeByUserId(c.getUserId());
            });
        }
        //反写缓存
        BaseResponse<List<OrderRecommendCount>> listBaseResponse1 = tradeQueryProvider.recommendByUserIdAndSku();
        if (CollectionUtils.isNotEmpty(listBaseResponse1.getContext())){
            recommendUserId(listBaseResponse1.getContext());
        }
        log.info("推荐定时任务完成");

        return BaseResponse.success(Constants.yes.toString());
    }

    private void recommendUserId(List<OrderRecommendCount> orderRecommendCounts) {
        Map<String, String> collect = orderRecommendCounts.stream().collect(Collectors.toMap(OrderRecommendCount::getSkuId, OrderRecommendCount::getUserId, (key1, key2) -> key2));
        for(Object key : collect.keySet()) {
            String key1 = CacheKeyConstant.SCREEN_ORDER_ADD_LAST_USERID+key+SpecialSymbols.UNDERLINE.toValue();
            redisService.setString(key1,collect.get(key));
        }

    }

    /**
     * 缓存每个用户的sku
     * */

    private void recommendTypeByUserId(String userId) {

        String key = CacheKeyConstant.SCREEN_ORDER_ADD_LAST_TIME+userId+ SpecialSymbols.UNDERLINE.toValue();
         TradeQueryDTO tradeQueryDTO = new TradeQueryDTO();
        tradeQueryDTO.setBuyerId(userId);
        BaseResponse<List<OrderRecommendSkuCount>> listBaseResponse = tradeQueryProvider.recommendTypeByCustomerId(TradeListAllRequest.builder().tradeQueryDTO(tradeQueryDTO).build());
        if (Objects.nonNull(listBaseResponse.getContext())){
            listBaseResponse.getContext().stream().forEach(l -> {

                    //入库
                    GoodsRecommendBackupsAddRequest goodsRecommendBackupsAddRequest =new GoodsRecommendBackupsAddRequest();
                    goodsRecommendBackupsAddRequest.setCustomerId(userId);
                    goodsRecommendBackupsAddRequest.setCompanyId(l.getSupplierId());
                    goodsRecommendBackupsAddRequest.setSkuId(l.getSkuIdOne());
                    goodsRecommendBackupsAddRequest.setCount(l.getCount());
                    goodsRecommendBackupsAddRequest.setCreateTime(l.getCreateTime());
                    goodsRecommendBackupsSaveProvider.add(goodsRecommendBackupsAddRequest);


             });
            redisService.setString(key,JSONObject.toJSONString(listBaseResponse.getContext()));
        }
    }


}
