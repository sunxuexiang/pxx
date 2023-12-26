package com.wanmi.sbc.marketing.coupon.service;

import com.wanmi.sbc.marketing.coupon.model.entity.TradeCouponSnapshot;
import com.wanmi.sbc.marketing.coupon.mongorepository.TradeCouponSnapshotRepository;
import com.wanmi.sbc.mongo.annotation.MongoRollback;
import com.wanmi.sbc.mongo.core.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2019-04-08 20:50
 */
@Service
public class TradeCouponSnapshotService {

    @Autowired
    private TradeCouponSnapshotRepository tradeCouponSnapshotRepository;

    /**
     * 新增文档
     * 专门用于数据新增服务,不允许数据修改的时候调用
     *
     * @param returnOrder
     */
    @MongoRollback(persistence = TradeCouponSnapshot.class, operation = Operation.ADD)
    public void addTradeCouponSnapshot(TradeCouponSnapshot returnOrder) {
        tradeCouponSnapshotRepository.save(returnOrder);
    }

    /**
     * 删除文档
     *
     * @param id
     */
    @MongoRollback(persistence = TradeCouponSnapshot.class, idExpress = "id", operation = Operation.UPDATE)
    public void deleteTradeCouponSnapshot(String id) {
        tradeCouponSnapshotRepository.deleteById(id);
    }

}
