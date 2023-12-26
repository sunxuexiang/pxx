package com.wanmi.sbc.goods.standard.repository;

import com.wanmi.sbc.goods.images.GoodsImage;
import com.wanmi.sbc.goods.standard.model.root.StandardGoodsRel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品库与商品库数据源
 *
 * @auther daiyitian
 * @create 2018/03/20 10:04
 */
@Repository
public interface StandardGoodsRelRepository extends JpaRepository<StandardGoodsRel, Long> {

    /**
     * 根据多个商品ID查询
     *
     * @param goodsIds 商品ID
     * @return
     */
    @Query("from StandardGoodsRel w where w.goodsId in ?1")
    List<StandardGoodsRel> findByGoodsIds(List<String> goodsIds);

    /**
     * 根据多个商品ID统计
     *
     * @param goodsIds 商品ID
     * @return
     */
    @Query("select count(0) from StandardGoodsRel w where w.goodsId in ?1")
    Long countByGoodsIds(List<String> goodsIds);

    /**
     * 根据多个商品ID查询
     *
     * @param standardIds 商品库ID
     * @return
     */
    @Query("from StandardGoodsRel w where w.standardId in ?1 and  w.storeId in ?2")
    List<StandardGoodsRel> findByStandardIds(List<String> standardIds, List<Long> storeIds);

    /**
     * 根据standardIds查询
     *
     * @param standardIds 商品库ID
     * @return
     */
    @Query("from StandardGoodsRel w where w.standardId in ?1 ")
    List<StandardGoodsRel> findByStandardIds(List<String> standardIds);

    /**
     * 根据多个商品库ID进行删除统计
     *
     * @param standardIds 商品库ID
     * @return
     */
    @Query("select count(0) from StandardGoodsRel w where w.standardId in ?1")
    Long countByStandardIds(List<String> standardIds);

    /**
     * 根据多个商品库ID和店铺ID进行删除统计
     *
     * @param standardIds 商品库ID
     * @param storeIds 店铺ID
     * @return
     */
    @Query("select count(0) from StandardGoodsRel w where w.standardId in ?1 and  w.storeId in ?2")
    Long countByStandardAndStoreIds(List<String> standardIds, List<Long> storeIds);

    /**
     * 根据多个商品ID进行删除
     *
     * @param goodsIds 商品ID
     */
    @Modifying
    @Query("delete from StandardGoodsRel w where w.goodsId in ?1")
    void deleteByGoodsIds(List<String> goodsIds);

    /**
     * 根据多个商品库ID进行删除
     *
     * @param standardIds 商品库ID
     */
    @Modifying
    @Query("delete from StandardGoodsRel w where w.standardId in ?1")
    void deleteByStandardIds(List<String> standardIds);

    /**
     * 根据商品id查询关系
     * @param goodsId
     * @return
     */
    @Query
    StandardGoodsRel findByGoodsId(String goodsId);
}
