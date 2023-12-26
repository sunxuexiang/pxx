package com.wanmi.sbc.goods.cate.repository;

import com.wanmi.sbc.goods.cate.model.root.RetailGoodsCateRecommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @Description: 散批推荐商品分类持久层接口
 * @Author: XinJiang
 * @Date: 2022/4/21 11:06
 */
@Repository
public interface RetailGoodsCateRecommendRepository extends JpaRepository<RetailGoodsCateRecommend,Long>, JpaSpecificationExecutor<RetailGoodsCateRecommend> {

    /**
     * 散批推荐商品分类排序
     *
     * @param cateId  商品分类Id
     * @param sort         商品分类顺序
     */
    @Query(" update RetailGoodsCateRecommend c set c.sort = ?2 where c.cateId = ?1 ")
    @Modifying
    void updateCateSort(Long cateId, Integer sort);

}
