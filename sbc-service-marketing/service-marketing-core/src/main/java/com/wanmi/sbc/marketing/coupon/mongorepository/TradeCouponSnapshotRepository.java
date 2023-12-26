package com.wanmi.sbc.marketing.coupon.mongorepository;

import com.wanmi.sbc.marketing.coupon.model.entity.TradeCouponSnapshot;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: gaomuwei
 * @Date: Created In 10:48 AM 2018/9/28
 * @Description: 订单优惠券相关信息快照数据持久化
 */
@Repository
public interface TradeCouponSnapshotRepository extends MongoRepository<TradeCouponSnapshot, String> {

    List<TradeCouponSnapshot> findAllByBuyerId(String buyerId);

    List<TradeCouponSnapshot> findAllByBuyerIdAndGoodsInfos_StoreId(String buyerId, Long storeId);

    void deleteByBuyerId(String buyerId);

}
