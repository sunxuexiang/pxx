package com.wanmi.sbc.goods.marketing.repository;

import com.wanmi.sbc.goods.marketing.model.data.GoodsMarketing;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * 商品营销数据操作类
 */
public interface GoodsMarketingRepository extends MongoRepository<GoodsMarketing, String> {

    /**
     * 根据用户编号查询商品使用的营销
     * @param customerId
     * @return
     */
    List<GoodsMarketing> queryGoodsMarketingListByCustomerId(String customerId);

    /**
     * 根据用户编号删除商品使用的营销
     * @param customerId
     * @return
     */
    int deleteAllByCustomerId(String customerId);

    /**
     * 根据用户编号和商品编号列表删除商品使用的营销
     * @param customerId
     * @param goodsInfoIds
     * @return
     */
    int deleteByCustomerIdAndGoodsInfoIdIn(String customerId, List<String> goodsInfoIds);

    /**
     * 根据用户编号和商品编号查询商品使用的营销
     * @param customerId
     * @param goodsInfoId
     * @return
     */
    List<GoodsMarketing> queryByCustomerIdAndGoodsInfoId(String customerId, String goodsInfoId);

    int deleteByMarketingIdAndGoodsInfoId(Long marketingId, String goodsInfoId);
}
