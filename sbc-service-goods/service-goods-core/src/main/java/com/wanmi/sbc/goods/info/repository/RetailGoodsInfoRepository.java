package com.wanmi.sbc.goods.info.repository;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.goods.info.model.root.GoodsInfo;
import com.wanmi.sbc.goods.info.model.root.RetailGoodsInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @Description: 零售商品数据持久层
 * @Author: XinJiang
 * @Date: 2022/3/8 15:45
 */
@Repository
public interface RetailGoodsInfoRepository extends JpaRepository<RetailGoodsInfo,String>, JpaSpecificationExecutor<RetailGoodsInfo> {
    /**
     * 根据多个商品ID编号更新上下架状态
     * @param goodsIds 商品ID
     */
    @Modifying
    @Query("update RetailGoodsInfo w set w.addedFlag = ?1, w.updateTime = now(), w.addedTime = now() where w.goodsId in ?2")
    void updateAddedFlagByGoodsIds(Integer addedFlag, List<String> goodsIds);

    /**
     * 根据多个商品ID编号进行删除
     * @param goodsIds 商品ID
     */
    @Modifying
    @Query("update RetailGoodsInfo w set w.delFlag = '1' ,w.updateTime = now() where w.goodsId in ?1")
    void deleteByGoodsIds(List<String> goodsIds);

    /**
     * 根据spuIdList查询sku
     * @param goodsIdList
     */
    List<RetailGoodsInfo> findByGoodsIdIn(List<String> goodsIdList);

    /**
     * 根据供应商商品infoId找到
     * @param delInfoIds
     * @return
     */
    List<RetailGoodsInfo> findByProviderGoodsInfoIdIn(List<String> delInfoIds);


    /**
     * 根据多个商品ID编号进行删除
     * @param goodsIds 商品ID
     */
    @Modifying
    @Query("update RetailGoodsInfo w set w.delFlag = '1' ,w.updateTime = now() where w.providerGoodsInfoId in ?1")
    void deleteByProviderGoodsInfoId(List<String> goodsIds);

    Optional<RetailGoodsInfo> findByGoodsInfoIdAndStoreIdAndDelFlag(String goodsInfoId, Long storeId, DeleteFlag deleteFlag);


    /**
     * 根据供应商商品详情找到商品详情
     * @param goodsInfoId
     */
    @Query
    RetailGoodsInfo findByProviderGoodsInfoId(String goodsInfoId);

    /**
     * 根据spuIdList查询sku(不包含已删除的)
     * @param goodsIdList
     */
    @Query("from RetailGoodsInfo w where w.delFlag = '0' and w.goodsId in ?1")
    List<RetailGoodsInfo> findByGoodsIds(List<String> goodsIdList);

    @Query(value = "SELECT b.goods_id,a.goods_info_id,a.goods_info_img,b.goods_img,a.market_price,b.goods_unit,a.goods_info_name,a.del_flag,a.added_flag,a.audit_status,a.checked_added_flag,1 " +
            "FROM retail_goods_info a LEFT JOIN retail_goods b ON a.goods_id=b.goods_id  " +
            "WHERE a.goods_info_id in (:goodsInfoIds)",nativeQuery = true)
    List<Object> findGoodsInfoByIds(@Param("goodsInfoIds") List<String> goodsInfoIds);

    /**
     * 根据skuIdList查询sku(包含已删除的)
     * @param goodsInfoIdList
     */
    @Query("from RetailGoodsInfo w where w.goodsInfoId in ?1")
    List<RetailGoodsInfo> findByGoodsInfoIds(List<String> goodsInfoIdList);

    /**
     * 更新商品的批次号
     * @param goodsInfoId
     * @param goodsInfoBatchNo
     * @return
     */
    @Modifying
    @Query(value = "update retail_goods_info as g set g.goods_info_batch_no = ?1 where g.del_flag = 0 and g.goods_info_id = ?2", nativeQuery = true)
    int updateGoodsInfoBatchNo(String goodsInfoBatchNo, String goodsInfoId);

    /**
     * 通过skuId修改图片地址
     * @param skuId
     * @param imageUrl
     * @return
     */
    @Modifying
    @Query(value = "update RetailGoodsInfo r set r.goodsInfoImg = ?2 where r.goodsInfoId = ?1")
    int updateGoodsInfoImg(String skuId,String imageUrl);

    /**
     * 查询erp无为空的商品
     */
    @Query("from RetailGoodsInfo w where w.erpGoodsInfoNo is not null and w.delFlag = 0 group by w.erpGoodsInfoNo,w.goodsId")
    List<RetailGoodsInfo> findAllByErp();

    /**
     * 通过erp编码、上下架状态、删除状态查询商品信心
     * @param erpNo
     * @param addedFlag
     * @param delFlag
     * @return
     */
    @Query
    RetailGoodsInfo findRetailGoodsInfoByErpGoodsInfoNoAndAddedFlagAndDelFlag(String erpNo,Integer addedFlag, DeleteFlag delFlag);

    /**
     * 根据erpGoodsInfoNo来查所有商品
     * @param erpGoodsInfoNos
     */
    @Query("from RetailGoodsInfo w where w.delFlag = 0 and w.erpGoodsInfoNo in ?1")
    List<RetailGoodsInfo> findAllGoodsByErpNos(List<String>  erpGoodsInfoNos);

    /**
     * 批量修改 sku 类目
     * @param cateId
     * @param goodsIds
     */
    @Modifying
    @Query("update RetailGoodsInfo g set g.cateId = ?1, g.updateTime = now() where g.goodsId in ?2")
    void batchModifyCate(Long cateId, List<String> goodsIds);
}
