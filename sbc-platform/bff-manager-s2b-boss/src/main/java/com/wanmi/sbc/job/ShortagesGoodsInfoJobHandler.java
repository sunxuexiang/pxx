package com.wanmi.sbc.job;

import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.goods.api.provider.goodswarestock.GoodsWareStockQueryProvider;
import com.wanmi.sbc.goods.api.provider.shortages.ShortagesGoodsInfoProvider;
import com.wanmi.sbc.goods.api.provider.shortages.ShortagesGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.shortages.ShortagesGoodsInfoAddRequest;
import com.wanmi.sbc.goods.api.request.shortages.ShortagesGoodsInfoQueryRequest;
import com.wanmi.sbc.goods.api.response.shortages.ShortagesGoodsInfoResponse;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 定时任务Handler（Bean模式）
 * 等货中商品
 *
 * @author liaozhaohong 2022-01-21
 */
@JobHandler(value="shortagesGoodsInfoJobHandler")
@Component
@Slf4j
public class ShortagesGoodsInfoJobHandler extends IJobHandler {

    @Autowired
    private GoodsWareStockQueryProvider goodsWareStockQueryProvider;

    @Autowired
    private ShortagesGoodsInfoProvider shortagesGoodsInfoProvider;

    @Autowired
    private ShortagesGoodsInfoQueryProvider shortagesGoodsInfoQueryProvider;


    @Override
    public ReturnT<String> execute(String s) throws Exception {

        ShortagesGoodsInfoResponse shortagesGoodsInfos = goodsWareStockQueryProvider.getShortagesGoodsInfos().getContext();

        if(Objects.isNull(shortagesGoodsInfos) || CollectionUtils.isEmpty(shortagesGoodsInfos.getGoodsInfos())){
            return ReturnT.SUCCESS;
        }

        ShortagesGoodsInfoQueryRequest shortagesGoodsInfoQueryRequest = new ShortagesGoodsInfoQueryRequest();
        shortagesGoodsInfoQueryRequest.setCheckTime(DateUtil.parseDayTime(DateUtil.getStartToday()));
        ShortagesGoodsInfoResponse context = shortagesGoodsInfoQueryProvider.queryShortagesGoodsInfoByCheckTime(
                shortagesGoodsInfoQueryRequest).getContext();

        if(Objects.nonNull(context) && CollectionUtils.isNotEmpty(context.getGoodsInfos())){
            //删除当天等货中商品记录
            shortagesGoodsInfoProvider.deleteByCheckTime(shortagesGoodsInfoQueryRequest);
        }

        //等货中商品入库
        ShortagesGoodsInfoAddRequest shortagesGoodsInfoAddRequest = new ShortagesGoodsInfoAddRequest();

        shortagesGoodsInfoAddRequest.setGoodsInfoVOList(shortagesGoodsInfos.getGoodsInfos());
        shortagesGoodsInfoProvider.saveAll(shortagesGoodsInfoAddRequest);
        return ReturnT.SUCCESS;
    }
}
