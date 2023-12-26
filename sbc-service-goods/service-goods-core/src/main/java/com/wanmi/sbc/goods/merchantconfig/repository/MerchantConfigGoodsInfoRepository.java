package com.wanmi.sbc.goods.merchantconfig.repository;
import com.wanmi.sbc.goods.merchantconfig.root.RecommendGoodsInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>商品推荐商品DAO</p>
 * @author sgy
 * @date 2019-09-07 10:53:36
 */
@Repository
public interface MerchantConfigGoodsInfoRepository extends JpaRepository<RecommendGoodsInfo, String>,
        JpaSpecificationExecutor<RecommendGoodsInfo> {
    @Modifying
    @Query(value = "delete from merchant_recommend_goods where goods_info_id = ?1  and company_info_id = ?2 "   ,nativeQuery = true)
    int  deleteByGoodsInfoId(String goodsInfoId, String string);



    @Modifying
    @Query("update RecommendGoodsInfo a set a.sort =?2  where a.recommendId = ?1")
    void updateSortById(String id, int sort);
}
