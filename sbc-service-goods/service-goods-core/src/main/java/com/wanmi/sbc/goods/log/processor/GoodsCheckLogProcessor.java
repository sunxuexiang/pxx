package com.wanmi.sbc.goods.log.processor;

import com.wanmi.sbc.goods.log.GoodsCheckLog;
import com.wanmi.sbc.goods.log.GoodsCheckLogRepository;
import com.wanmi.sbc.mongo.core.process.DefaultProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2019-04-08 17:52
 */
@Component
public class GoodsCheckLogProcessor extends DefaultProcessor<GoodsCheckLog,String> {

    @Autowired
    public GoodsCheckLogProcessor(GoodsCheckLogRepository goodsCheckLogRepository){
        mongoRepository = goodsCheckLogRepository;
    }
}
