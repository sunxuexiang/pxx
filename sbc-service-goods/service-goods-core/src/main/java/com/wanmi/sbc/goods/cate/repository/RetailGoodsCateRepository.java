package com.wanmi.sbc.goods.cate.repository;

import com.wanmi.sbc.goods.cate.model.root.RetailGoodsCate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @Description: 散批分类持久层接口
 * @Author: XinJiang
 * @Date: 2022/5/5 15:22
 */
@Repository
public interface RetailGoodsCateRepository extends JpaRepository<RetailGoodsCate,Long>, JpaSpecificationExecutor<RetailGoodsCate> {

    /**
     * 散批商品分类排序
     *
     * @param cateId  商品分类Id
     * @param sort         商品分类顺序
     */
    @Query(" update RetailGoodsCate c set c.sort = ?2 where c.cateId = ?1 ")
    @Modifying
    void updateCateSort(Long cateId, Integer sort);
}
