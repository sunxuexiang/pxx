package com.wanmi.sbc.goods.standard.repository;

import com.wanmi.sbc.goods.standard.model.root.StandardGoods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品库SPU数据源
 * Created by daiyitian on 2017/04/11.
 */
@Repository
public interface StandardGoodsRepository extends JpaRepository<StandardGoods, String>, JpaSpecificationExecutor<StandardGoods>{

    /**
     * 根据多个商品ID编号进行删除
     * @param goodsIds 商品ID
     */
    @Modifying
    @Query("update StandardGoods w set w.delFlag = '1', w.updateTime = now() where w.goodsId in ?1")
    void deleteByGoodsIds(List<String> goodsIds);



    /**
     * 根据多个分类ID编号更新分类
     * @param newCateId 分类ID
     * @param cateIds 多个分类ID
     */
    @Modifying
    @Query("update StandardGoods w set w.cateId = ?1, w.updateTime = now() where w.cateId in ?2")
    void updateCateByCateIds(Long newCateId, List<Long> cateIds);

    /**
     * 根据品牌id 批量把spu品牌置为null
     * @param brandId
     */
    @Modifying
    @Query("update StandardGoods g set g.brandId = null where g.brandId = :brandId")
    void updateBrandByBrandId(@Param("brandId") Long brandId);


    /**
     * 根据类别id查询SPU
     * @param cateId
     * @return
     */
    @Query
    List<StandardGoods> findAllByCateId(Long cateId);

    /**
     * 商品库id 查找
     * @param standardGoodsIds
     * @return
     */
    @Query
    List<StandardGoods> findByGoodsIdIn(List<String> standardGoodsIds);

    @Modifying
    @Query("update StandardGoods g set g.delFlag = '0' , g.updateTime = now() where g.delFlag = '1' and g.goodsId = ?1 ")
    void updateDelFlag(String standardId);

    @Query("from StandardGoods g where g.delFlag = '0' and g.goodsId in ?1")
    List<StandardGoods> findByGoodsIds(List<String> goodsIds);


    @Query(value="SELECT goods_id FROM standard_goods WHERE del_flag = 0 and cate_id in :cateId",nativeQuery = true)
    List<String> findIdByList(@Param("cateId") List<Long> cateId, Pageable pageable);


    @Modifying
    @Query("update StandardGoods set cateId = ?1 where goodsId = ?2")
    int updateCateIdByGoodsId(Long cateId, String goodsId);
}
