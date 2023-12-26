package com.wanmi.sbc.goods.cate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.wanmi.sbc.goods.cate.model.root.GoodsCateRecommend;

/**
 * @description: 推荐商品分类持久层
 * @author: XinJiang
 * @time: 2022/3/1 11:46
 */
@Repository
public interface GoodsCateRecommendRepository
		extends JpaRepository<GoodsCateRecommend, Long>, JpaSpecificationExecutor<GoodsCateRecommend> {

	/**
	 * 推荐商品分类排序
	 *
	 * @param cateId 商品分类Id
	 * @param sort   商品分类顺序
	 */
	@Query(" update GoodsCateRecommend c set c.sort = ?2 where c.cateId = ?1 ")
	@Modifying
	void updateCateSort(Long cateId, Integer sort);

}
