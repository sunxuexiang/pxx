package com.wanmi.sbc.goods.info.repository;

import com.wanmi.sbc.goods.info.model.root.BulkGoods;
import com.wanmi.sbc.goods.info.model.root.RetailGoods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @Description: 零售商品spu持久层
 * @Author: XinJiang
 * @Date: 2022/3/8 15:59
 */
public interface BulkGoodsRepository extends JpaRepository<BulkGoods,String>, JpaSpecificationExecutor<BulkGoods> {

    /**
     * 根据序列号和商品ID查询商品序号是否重复
     * @param goodsSeqNum
     * @param goodsId
     * @return
     */
    @Query("from BulkGoods  where goodsSeqNum=?1 and goodsId<>?2 and delFlag=0")
    List<BulkGoods> getExistByGoodsSeqNum(Integer goodsSeqNum, String goodsId);


    /**
     * 编辑商品排序序号
     * @param goodsSeqNum
     * @param goodsId
     */
    @Modifying
    @Query("update BulkGoods set goodsSeqNum=?1 where goodsId=?2")
    void modifyGoodsSeqNum(Integer goodsSeqNum, String goodsId);


    /**
     * 根据多个商品ID编号更新上下架状态
     * @param goodsIds 商品ID
     */
    @Modifying
    @Query("update BulkGoods w set w.addedFlag = ?1, w.updateTime = now(), w.addedTime = now() where w.goodsId in ?2")
    void updateAddedFlagByGoodsIds(Integer addedFlag, List<String> goodsIds);

    /**
     * 根据多个商品ID编号更新上下架状态
     * @param goodsIds 商品ID
     */
    @Modifying
    @Query("update BulkGoods w set w.addedFlag = ?1, w.updateTime = now(), w.addedTime = now() where w.providerGoodsId in ?2")
    void updateAddedFlagByPrividerGoodsIds(Integer addedFlag, List<String> goodsIds);


    /**
     * 根据多个商品ID编号进行删除
     * @param goodsIds 商品ID
     */
    @Modifying
    @Query("update BulkGoods w set w.delFlag = '1', w.updateTime = now() where w.goodsId in ?1")
    void deleteByGoodsIds(List<String> goodsIds);

    /**
     * 根据供应商商品id查询关联商品
     * @param providerGoodsId
     * @return
     */
    @Query
    List<BulkGoods> findAllByProviderGoodsId(String providerGoodsId);

    @Modifying
    @Query("update BulkGoods g set g.goodsCollectNum = g.goodsCollectNum + ?1, g.updateTime = now()  where g.goodsId = ?2 and g.goodsCollectNum > 0")
    void updateGoodsCollectNum(@Param("goodsCollectNum") Long goodsCollectNum, @Param("goodsId") String GoodsId);


    /**
     * 修改spu 类目
     * @param cateId
     * @param goodsIds
     */
    @Modifying
    @Query("update BulkGoods w set w.cateId = ?1, w.updateTime = now() where w.goodsId in ?2")
    void batchModifyCate(Long cateId, List<String> goodsIds);
}
