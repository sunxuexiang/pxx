package com.wanmi.sbc.goods.devanninggoodsinfo.repository;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.goods.devanninggoodsinfo.model.root.DevanningGoodsInfo;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>竞价配置DAO</p>
 * @author baijz
 * @date 2020-08-05 16:27:45
 */
@Repository
public interface DevanningGoodsInfoRepository extends JpaRepository<DevanningGoodsInfo, Long>,
        JpaSpecificationExecutor<DevanningGoodsInfo> {

    @Query(value = "set @i \\:=1,@rowtruckid \\:=null;\n" +
            "SELECT  table2.devanning_id,table2.goods_info_id,table2.goods_id,table2.add_step,table2.devanning_unit,table2.market_price\n" +
            "from (\n" +
            "select\n" +
            "case when @rowtruckid = t.goods_id then @i \\:=@i+1\n" +
            "when @rowtruckid \\:= t.goods_id then @i \\:=1\n" +
            "end rank_no, t.*\n" +
            "from( \n" +
            "SELECT  t1.*\n" +
            "from devanning_goods_info t1\n" +
            "WHERE t1.goods_id in (?1)\n" +
            "ORDER BY t1.goods_id ,t1.add_step desc\n" +
            ")t  \n" +
            ") table2 WHERE table2.rank_no=1\n",nativeQuery = true)
    List<Object>  getGoodsDevanningMaxstep(List<String> SpuId);


    @Query(value = "SELECT t1.goods_id,count(1)\n" +
            "from devanning_goods_info t1\n" +
            "WHERE t1.goods_id \n" +
            "in (?1)\n" +
            "group by t1.goods_id \n" +
            "HAVING count(1)=1",nativeQuery = true)
    List<Object> getGoodsNorms(List<String> Sukids);

    @Query(value = "SELECT t1.goods_id,count(1)\n" +
            "from devanning_goods_info t1\n" +
            "WHERE t1.goods_id \n" +
            "in (?1)\n" +
            "group by t1.goods_id \n" +
            "HAVING count(1)>1",nativeQuery = true)
    List<Object> getBigGoodsNorms(List<String> Sukids);


    @Query(value = "set @i \\:=1,@rowtruckid \\:=null;\n" +
            "SELECT  table2.devanning_id,table2.goods_info_id,table2.goods_id,table2.add_step,table2.devanning_unit,table2.market_price\n" +
            "from (\n" +
            "select\n" +
            "case when @rowtruckid = t.goods_id then @i \\:=@i+1\n" +
            "when @rowtruckid \\:= t.goods_id then @i \\:=1\n" +
            "end rank_no, t.*\n" +
            "from( \n" +
            "SELECT  t1.*\n" +
            "from devanning_goods_info t1\n" +
            "WHERE t1.goods_info_id in (?1)\n" +
            "ORDER BY t1.goods_info_id ,t1.add_step desc\n" +
            ")t  \n" +
            ") table2 WHERE table2.rank_no=1",nativeQuery = true)
    List<Object>  getGoodsDevanningMaxstepbysku(List<String> GoodsInfoids);


    @Query(value = "select * from devanning_goods_info WHERE devanning_id in ?1",nativeQuery = true)
    List<DevanningGoodsInfo> getDevanningGoodsInfoByIds(List<Long> goodsInfoIds);

    @Query(value = "select * from devanning_goods_info WHERE goods_info_id = ?1  AND add_step= ?2",nativeQuery = true)
    DevanningGoodsInfo getInfoByIdAndStep(String goodsInfoId, BigDecimal addStep);


    @Query(value = "select * from devanning_goods_info WHERE goods_info_id = ?1",nativeQuery = true)
    List<DevanningGoodsInfo> getByInfoId(String goodsInfoId);

    /**
     * 根据多个商品ID编号更新上下架状态
     * @param goodsIds 商品ID
     */
    @Modifying
    @Query("update DevanningGoodsInfo w set w.addedFlag = ?1, w.updateTime = now(), w.addedTime = now() where w.goodsId in ?2")
    void updateAddedFlagByGoodsIds(Integer addedFlag, List<String> goodsIds);

    /**
     * 根据多个商品ID编号更新上下架状态
     * @param goodsInfoId 商品ID
     */
    @Modifying
    @Query("update DevanningGoodsInfo w set w.addedFlag = ?1, w.updateTime = now(), w.addedTime = now() where w.goodsInfoId in ?2")
    void updateAddedFlagByGoodsInfoIds(Integer addedFlag, List<String> goodsInfoId);


    @Query(value = "SELECT b.goods_id,a.goods_info_id,a.goods_info_img,b.goods_img,a.market_price,b.goods_unit,a.goods_info_name," +
            "a.del_flag,a.added_flag,a.audit_status,a.checked_added_flag,a.devanning_id,a.divisor_flag,1 " +
            "FROM devanning_goods_info a LEFT JOIN goods b ON a.goods_id=b.goods_id  " +
            "WHERE a.devanning_id in (:devanningIds)",nativeQuery = true)
    List<Object> findDevanningGoodsInfoByDids(@Param("devanningIds") List<Long> goodsInfoIds);


    /**
     * 更新商品的批次号
     * @param goodsInfoId
     * @param goodsInfoBatchNo
     * @return
     */
    @Modifying
    @Query(value = "update devanning_goods_info as g set g.goods_info_batch_no = ?1 where g.del_flag = 0 and g.goods_info_id = ?2", nativeQuery = true)
    int updateGoodsInfoBatchNo(String goodsInfoBatchNo, String goodsInfoId);

    /**
     * 根据商品SKU编号加库存
     * @param stock 库存数
     * @param goodsInfoId 商品ID
     */
    @Modifying
    @Query("update DevanningGoodsInfo w set w.stock = w.stock + ?1, w.updateTime = now() where w.goodsInfoId = ?2")
    int addStockById(BigDecimal stock, String goodsInfoId);

    /**
     * 根据商品SKU编号减库存
     * @param stock 库存数
     * @param goodsInfoId 商品ID
     */
    @Modifying
    @Query("update DevanningGoodsInfo w set w.stock = w.stock - ?1, w.updateTime = now() where w.goodsInfoId = ?2 and w.stock  >= ?1")
    int subStockById(BigDecimal stock, String goodsInfoId);

    @Query(value = "select * from devanning_goods_info WHERE parent_goods_info_id = ?1",nativeQuery = true)
    List<DevanningGoodsInfo> queryGoodsInfoByParentId(String parentId);

    @Query(value = "select * from devanning_goods_info WHERE goods_id = ?1",nativeQuery = true)
    List<DevanningGoodsInfo> findByGoodsId(String goodsId);
    @EntityGraph(value="DevanningGoodsInfo.Graph", type = EntityGraph.EntityGraphType.FETCH)
    List<DevanningGoodsInfo> findAllByWareIdAndDelFlag(Long wareId, DeleteFlag deleteFlag);

    /**
     * 查询店铺ID
     * @param devanning_id
     * @return
     */
    @Query(value = "select devanning_id from devanning_goods_info where devanning_id in (:devanningIds) and store_id=:storeId", nativeQuery = true)
    List<Long> getIdsByStoreId(@Param("devanningIds") List<Long> devanningIds, @Param("storeId") Long storeId);
    /**
     * 根据goods id 删除对应产品信息
     * @param goodsIds
     * @return
     */
    @Modifying
    @Query("update DevanningGoodsInfo w set w.delFlag = '1' ,w.updateTime = now() where w.goodsId in ?1")
    void deleteByGoodsIdList(List<String> goodsIds);
}
