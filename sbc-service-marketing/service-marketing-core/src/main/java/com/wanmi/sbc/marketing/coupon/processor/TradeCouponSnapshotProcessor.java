package com.wanmi.sbc.marketing.coupon.processor;

import com.wanmi.sbc.marketing.coupon.model.entity.TradeCouponSnapshot;
import com.wanmi.sbc.marketing.coupon.mongorepository.TradeCouponSnapshotRepository;
import com.wanmi.sbc.mongo.core.process.DefaultProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2019-04-08 20:54
 */
@Component
public class TradeCouponSnapshotProcessor extends DefaultProcessor<TradeCouponSnapshot,String> {

    @Autowired
    public TradeCouponSnapshotProcessor(TradeCouponSnapshotRepository tradeCouponSnapshotRepository){
        mongoRepository = tradeCouponSnapshotRepository;
    }
}
