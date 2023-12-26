package com.wanmi.sbc.goods.goodsrecommendgoods.repository;

import com.wanmi.sbc.goods.goodsrecommendgoods.model.root.GoodsRecommendGoods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * <p>商品推荐商品DAO</p>
 * @author chenyufei
 * @date 2019-09-07 10:53:36
 */
@Repository
public interface GoodsRecommendGoodsRepository extends JpaRepository<GoodsRecommendGoods, String>,
        JpaSpecificationExecutor<GoodsRecommendGoods> {

    /**
     * 通过wareId获取推荐商品信息
     * @param wareId
     * @return
     */
    List<GoodsRecommendGoods> findByWareId(Long wareId);

    /**
     * 通过wareId删除推荐商品信息
     * @param wareId
     * @return
     */
    int deleteByWareId(Long wareId);
}
