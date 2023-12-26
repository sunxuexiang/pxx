package com.wanmi.sbc.goods.marketing.processor;

import com.wanmi.sbc.goods.marketing.model.data.GoodsMarketing;
import com.wanmi.sbc.goods.marketing.repository.GoodsMarketingRepository;
import com.wanmi.sbc.mongo.core.process.DefaultProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2019-04-08 17:52
 */
@Component
public class GoodsMarketingProcessor extends DefaultProcessor<GoodsMarketing,String> {

    @Autowired
    public GoodsMarketingProcessor(GoodsMarketingRepository goodsMarketingRepository){
        mongoRepository = goodsMarketingRepository;
    }
}
