package com.wanmi.sbc.marketing.suittobuy.repository;

import com.wanmi.sbc.marketing.suittobuy.model.root.MarketingSuitDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @description: 套装购买商品详细信息持久层
 * @author: XinJiang
 * @time: 2022/2/4 16:37
 */
@Repository
public interface MarketingSuitDetialRepository extends JpaRepository<MarketingSuitDetail,Long>, JpaSpecificationExecutor<MarketingSuitDetail> {

    int deleteByMarketingId(Long marketingId);

    List<MarketingSuitDetail> findMarketingSuitDetailByMarketingId(Long marketingId);

    @Query(value = "SELECT\n" +
            "ms.* \n" +
            "FROM\n" +
            "marketing m " +
            "LEFT JOIN marketing_suit_detail ms ON m.marketing_id = ms.marketing_id \n" +
            "WHERE\n" +
            "m.sub_type = 9 \n" +
            "AND m.del_flag = 0 \n" +
            "AND m.is_pause = 0 \n" +
            "AND m.termination_flag = 0 \n" +
            "AND m.begin_time <= now() and m.end_time >= now() \n" +
            "AND ms.goods_info_id IN ?1", nativeQuery = true)
    List<MarketingSuitDetail> getSuitToBuyByGoodInfoIds(@Param("goodsInfoIds") List<String> goodsInfoIds);

    @Query(value = "SELECT * from marketing_suit_detail where goods_marketing_id in ?1", nativeQuery = true)
    List<MarketingSuitDetail> queryMarketingSuitDetailsByGoodsMarketingIds(List<Long> goodsMarketingIds);

    /**
     * 通过套装营销id获取关联商品信息
     * @param marketingId
     * @return
     */
    @Query(value = "from MarketingSuitDetail where marketingId = ?1")
    List<MarketingSuitDetail> queryAllByMarketingId(Long marketingId);
}
