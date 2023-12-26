package com.wanmi.sbc.goods.brand.repository;

import com.wanmi.sbc.goods.brand.model.root.GoodsBrandRecommend;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface GoodsBrandRecommendRepository extends JpaRepository<GoodsBrandRecommend, Long>, JpaSpecificationExecutor<GoodsBrandRecommend> {


    /**
     * 根据品牌id查看列表
     *
     * @param brandIdList
     * @return
     */
    List<GoodsBrandRecommend> findGoodsBrandRecommendsByBrandIdIn(List<Long> brandIdList);

    /**
     * 根据主键查询列表
     *
     * @param goodsBrandRecommendIdList
     * @return
     */
    List<GoodsBrandRecommend> findGoodsBrandRecommendsByGoodsBrandRecommendIdIn(List<Long> goodsBrandRecommendIdList);

    Page<GoodsBrandRecommend> findGoodsBrandRecommendsByDelFlag(Integer delFlag, Pageable pageable);

    Page<GoodsBrandRecommend> findGoodsBrandRecommendsByDelFlagAndAddedFlag(Integer delFlag, Integer addedFlag, Pageable pageable);

    @Query(nativeQuery = true, value = "update chain_goods_brand_recommend set name_status = :nameStatus where goods_brand_recommend_id = 1")
    @Modifying
    @Transactional
    void updateGoodsBrandRecommendNameStatus(@Param("nameStatus") Integer nameStatus);
}
