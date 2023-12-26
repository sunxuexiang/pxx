package com.wanmi.sbc.goods.retailgoodsrecommend.repository;

import com.wanmi.sbc.goods.retailgoodsrecommend.model.root.RetailGoodsRecommendSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description: 散批鲸喜推荐持久层接口
 * @Author: XinJiang
 * @Date: 2022/4/20 9:19
 */
@Repository
public interface RetailGoodsRecommendSettingRepository extends JpaRepository<RetailGoodsRecommendSetting,String>, JpaSpecificationExecutor<RetailGoodsRecommendSetting> {

    /**
     * 推荐商品排序
     * @param recommendId  推荐id
     * @param sortNum      排序顺序
     */
    @Query(" update RetailGoodsRecommendSetting c set c.sortNum = ?2 where c.recommendId = ?1 ")
    @Modifying
    void updateSort(String recommendId, Integer sortNum);

    /**
     * 获取最大的排序顺序
     */
    @Query(value = "select max(sort_num) from retail_goods_recommend", nativeQuery = true)
    Integer maxSortNum();

    List<RetailGoodsRecommendSetting> findByGoodsInfoIdIn(List<String> goodsInfoids);

}
