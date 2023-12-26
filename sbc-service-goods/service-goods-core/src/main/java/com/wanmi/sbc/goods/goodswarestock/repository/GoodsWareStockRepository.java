package com.wanmi.sbc.goods.goodswarestock.repository;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.goods.goodswarestock.model.root.GoodsWareStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * <p>sku分仓库存表DAO</p>
 *
 * @author zhangwenchang
 * @date 2020-04-06 17:22:56
 */
@Repository
public interface GoodsWareStockRepository extends JpaRepository<GoodsWareStock, Long>,
        JpaSpecificationExecutor<GoodsWareStock> {

    /**
     * 单个删除sku分仓库存表
     *
     * @author zhangwenchang
     */
    @Modifying
    @Query("update GoodsWareStock set delFlag = 1 where id = ?1")
    void deleteById(Long id);

    /**
     * 批量删除sku分仓库存表
     *
     * @author zhangwenchang
     */
    @Modifying
    @Query("update GoodsWareStock set delFlag = 1 where id in ?1")
    void deleteByIdList(List<Long> idList);

    /**
     * 根据goodsInfoIdList批量删除sku分仓库存表
     *
     * @author zhangwenchang
     */
    @Modifying
    @Query("delete from GoodsWareStock where goodsInfoWareId in ?1")
    void deleteByGoodsInfoWareIdList(List<String> idList);

    Optional<GoodsWareStock> findByIdAndDelFlag(Long id, DeleteFlag delFlag);

    long countByWareIdAndStockGreaterThanAndDelFlag(Long wareId, BigDecimal stock, DeleteFlag deleteFlag);

    /**
     * 根据skuIds查询其在默认仓的库存
     *
     * @param goodsInfoIds
     * @return
     */
    @Query(value = "SELECT a.* FROM goods_ware_stock a " +
            "LEFT JOIN ware_house b ON a.ware_id = b.ware_id " +
            "WHERE a.del_flag = 0 AND b.del_flag = 0 AND a.store_id = ?1 AND a.goods_info_id IN ?2 AND b.default_flag = 1", nativeQuery = true)
    List<GoodsWareStock> findGoodsDefaultStock(Long storeId, List<String> goodsInfoIds);

    /**
     * 根据skuNo查询库存信息
     *
     * @param goodsInfoIds
     * @return
     */
    @Query(value = "SELECT a.* FROM goods_ware_stock a " +
            "WHERE a.del_flag = 0  AND a.ware_id = ?1 AND a.goods_info_no IN ?2  ", nativeQuery = true)
    List<GoodsWareStock> findGoodsDefaultStockByNo(Long wareId, List<String> goodsInfoIds);


    /**
     * 根据skuNo查询库存信息
     *
     * @param goodsInfoIds
     * @return
     */
    @Query(value = "SELECT a.* FROM goods_ware_stock a " +
            "WHERE a.del_flag = 0  AND a.ware_id = ?1 AND a.goods_info_id IN ?2  ", nativeQuery = true)
    List<GoodsWareStock> findGoodsDefaultStockByGoodsInfoIds(Long wareId, List<String> goodsInfoIds);


    /**
     * 根据地区信息和商品idList查询对应商品库存信息
     *
     * @param goodsInfoIds
     * @param provinceId
     * @param cityId
     * @return
     */
    @Query(value = "SELECT" +
            " gws.goods_info_no as goodsInfoNo," +
            " gws.goods_info_id as goodsInfoId," +
            " gws.stock as stock" +
            " FROM" +
            " goods_ware_stock gws" +
            " LEFT JOIN ware_house wh ON gws.ware_id = wh.ware_id" +
            " LEFT JOIN ware_house_city whc ON whc.ware_id = wh.ware_id" +
            " WHERE wh.del_flag = 0 and gws.del_flag = 0 and gws.store_id = ?1 and gws.goods_info_id in ?2 AND whc.province_id = ?3 AND whc.city_id = ?4", nativeQuery = true)
    List<Object> getGoodsStockByAreaIdsAndGoodsInfoIds(Long storeId, List<String> goodsInfoIds, long provinceId, long cityId);


    /**
     * 根据地区信息和skuIds查询对应商品库存信息
     *
     * @param goodsInfoIds
     * @return
     */
    @Query(value = "SELECT" +
            " gws.goods_info_no as goodsInfoNo," +
            " gws.goods_info_id as goodsInfoId," +
            " gws.stock as stock" +
            " FROM" +
            " goods_ware_stock gws" +
            " WHERE gws.del_flag = 0 and gws.goods_info_id in ?2 and gws.ware_id = ?1", nativeQuery = true)
    List<Object> getGoodsStockByAreaIdAndGoodsInfoIds(Long wareId, List<String> goodsInfoIds);

    /**
     * 根据店铺+仓库+商品信息确定库存
     *
     * @param goodsInfoIds
     * @return
     */
    @Query(value = "SELECT" +
            " gws.goods_info_no as goodsInfoNo," +
            " gws.goods_info_id as goodsInfoId," +
            " gws.stock as stock" +
            " FROM" +
            " goods_ware_stock gws" +
            " WHERE gws.del_flag = 0 and gws.goods_info_id in ?2 and gws.ware_id = ?1 and gws.store_id= ?3", nativeQuery = true)
    List<Object> getGoodsStockByWareIdAndStoreId(Long wareId, List<String> goodsInfoIds,Long storeId);



    /**
     * 根据wareId删除分仓库存表
     *
     * @author huapeiliang
     */
    @Modifying
    @Query("update GoodsWareStock set delFlag = 1 where wareId = ?1 and delFlag = 0")
    void deleteByWareId(Long wareId);

    List<GoodsWareStock> findAllByWareIdAndDelFlag(Long wareId, DeleteFlag deleteFlag);

    Optional<GoodsWareStock> findByIdAndStoreIdAndDelFlag(Long id, Long storeId, DeleteFlag delFlag);

    @Modifying
    @Query("update GoodsWareStock set delFlag = 1, updateTime=now(), updatePerson=?2 where goodsId = ?1 and delFlag " +
            "= 0")
    void deleteStockByGoodsId(String goodsId, String updatePerson);

    void deleteAllByGoodsId(String goodsId);

    void deleteAllByGoodsInfoId(String goodsInfoId);

    List<GoodsWareStock> findAllByGoodsIdAndDelFlag(String goodsId, DeleteFlag deleteFlag);

    List<GoodsWareStock> findAllByGoodsInfoIdAndDelFlag(String goodsInfoId, DeleteFlag deleteFlag);

    @Modifying
    @Query("update GoodsWareStock set delFlag = 1, updateTime=now() where goodsId in ?1 and delFlag " +
            "= 0")
    void deleteByGoodsIds(List<String> goodsId);

    /**
     * 根据skuIds批量删除
     * @param goodsInfoIds
     */
    @Modifying
    @Query("update GoodsWareStock set delFlag = 1, updateTime=now() where goodsInfoId in ?1 and delFlag " +
            "= 0")
    void deleteByGoodsInfoIds(List<String> goodsInfoIds);

    @Modifying
    @Query("update GoodsWareStock set stock = stock- ?1, updateTime=now() where stock >= ?1 and goodsInfoId in ?2 and wareId = ?3")
    int subStockByWareIdAndGoodsInfoId(BigDecimal stock, String goodsInfoId, Long wareId);

    @Modifying
    @Query("update GoodsWareStock set stock = stock+ ?1, updateTime=now() where goodsInfoId in ?2 and wareId = ?3")
    int addStockByWareIdAndGoodsInfoId(BigDecimal stock, String goodsInfoId, Long wareId);

    @Modifying
    @Query("update GoodsWareStock set stock = ?2, updateTime=now() where goodsInfoId = ?1 and wareId = ?3")
    int updateByGoodsInfoId(String goodsInfoId, BigDecimal stock, Long wareId);

    /**
     * @description  修改
     * @author  shiy
     * @date    2023/4/3 18:35
     * @params  [java.lang.String, java.lang.Long]
     * @return  int
     */
    @Modifying
    @Query("update GoodsWareStock set addStep=?3,mainAddStep=?4,saleType=?5,mainSkuId=?6,mainSkuWareId=?7,parentGoodsWareStockId=?8, updateTime=now() where goodsInfoId in ?1 and wareId = ?2")
    int updateStockMutilSpeci(String goodsInfoId, Long wareId, BigDecimal addStep, BigDecimal mainAddStep, Integer saleType, String mainSkuId,Long mainSkuWareId,Long parentGoodsWareStockId);


    @Query(value = "SELECT\n" +
            "\tgoods_info_id goodsInfoId,\n" +
            "\tsum( stock ) stock, \n" +
            "\tsum( lock_stock ) lockStock \n" +
            "FROM\n" +
            "\tgoods_ware_stock \n" +
            "WHERE\n" +
            "\tdel_flag = 0 \n" +
            "\tAND goods_info_id IN ?1\n" +
            "\tGROUP BY goods_info_id;", nativeQuery = true)
    List<Object> findGoodsWareStockByGoodsInfoIds(List<String> goodsInfoIdList);

    @Query("from GoodsWareStock t where t.goodsInfoId in ?1")
    List<GoodsWareStock> findListByGoodsInfoIds(List<String> goodsInfoIdList);


    @Query(value = "SELECT\n" +
            "\tgoods_info_id goodsInfoId,\n" +
            "\tsum( stock ) stock, \n" +
            "\tsum( lock_stock ) lockStock \n" +
            "FROM\n" +
            "\tgoods_ware_stock \n" +
            "WHERE\n" +
            "\tdel_flag = 0 AND ware_id = ?2\n" +
            "\tAND goods_info_id IN ?1\n" +
            "\tGROUP BY goods_info_id;", nativeQuery = true)
    List<Object> findGoodsWareStockByGoodsInfoIdsAndWareId(List<String> goodsInfoIdList,Long wareId);


    @Query("from GoodsWareStock t where t.goodsInfoId in ?1 and t.wareId=?2")
    List<GoodsWareStock> findListByGIdsAndWareId(List<String> goodsInfoIdList,Long wareId);


    List<GoodsWareStock> findByGoodsInfoIdIn(List<String> goodsInfoIds);

    List<GoodsWareStock> findByGoodsInfoId(String goodsInfoId);
    List<GoodsWareStock> findByGoodsInfoNo(String goodsInfoNo);

    Optional<GoodsWareStock> findByGoodsIdAndWareIdAndDelFlag(String goodsId,String wareId,DeleteFlag deleteFlag);

    /**
     * 根据仓库ID和skuId查询库存
     * @param goodsInfoId
     * @param wareId
     * @return
     */
    GoodsWareStock findTopByGoodsInfoIdAndWareId(String goodsInfoId, Long wareId);

    /**
     * 根据商品SKU编号减库存
     * @param stock 库存数
     * @param goodsInfoId 商品ID
     */
    @Modifying
    @Query("update GoodsWareStock w set w.stock = w.stock - ?1, w.updateTime = now() where w.goodsInfoId = ?2 and w.stock  >= ?1 and w.wareId = ?3")
    int subStockById(Long stock, String goodsInfoId,Long wareId);

    /**
     * 根据商品SKU编号加库存
     * @param stock 库存数
     * @param goodsInfoId 商品ID
     */
    @Modifying
    @Query("update GoodsWareStock w set w.stock = w.stock + ?1, w.updateTime = now() where w.goodsInfoId = ?2 and w.wareId = ?3")
    int addStockById(Long stock, String goodsInfoId,Long wareId);


    /**
     * 分组查询SPU库存
     * @param goodsIds
     * @return
     */
    @Query(value = "SELECT goods_id, sum( stock ) stock \n" +
            "FROM goods_ware_stock GROUP BY goods_id HAVING\n" +
            " goods_id IN ?1"
            ,nativeQuery = true)
    List<Object> findByGoodsIdsIn(List<String> goodsIds);

    /**
     * 商品库存数据（等货中）
     */
    @Query(value = "select t5.erp_goods_info_no as 'erp编码',t5.goods_info_name as '商品名称',t5.stock as '库存',t5.goods_num as '囤货数量',t5.relstock as '等货商品数量=库存-囤货数量' from\n" +
            "(select t3.erp_goods_info_no,t3.goods_info_name,t3.stock,IFNULL(t4.goods_num,0) as goods_num,(t3.stock-IFNULL(t4.goods_num,0)) as relstock from \n" +
            "(select t1.goods_info_id,t2.erp_goods_info_no,t2.goods_info_name,sum(t1.stock) stock from `sbc-goods`.goods_ware_stock t1 LEFT JOIN `sbc-goods`.goods_info t2 on t1.goods_info_id = t2.goods_info_id where t2.del_flag = 0 group by t1.goods_info_id,t2.erp_goods_info_no,t2.goods_info_name) t3 \n" +
            "left join (select goods_info_id,sum(goods_num) goods_num from `sbc-order`.pile_purchase where goods_num > 0 GROUP BY goods_info_id) t4\n" +
            "on t3.goods_info_id = t4.goods_info_id) t5 where t5.relstock < 1",nativeQuery = true)
    List<Object[]> getInventory();

    /**
     * 实时查询等货中商品
     */
    @Query(value = "SELECT\n" +
            "\tg.goods_info_id,\n" +
            "\tg.goods_info_no,\n" +
            "\tg.erp_goods_info_no,\n" +
            "\tg.goods_info_name,\n" +
            "\tIFNULL( g.brand_id, 0 ) brand_id,\n" +
            "\tg.cate_id \n" +
            "FROM\n" +
            "\t( SELECT goods_info_id, sum( stock ) stock FROM `sbc-goods`.goods_ware_stock GROUP BY goods_info_id ) t\n" +
            "\tLEFT JOIN `sbc-goods`.goods_info g ON t.goods_info_id = g.goods_info_id \n" +
            "WHERE\n" +
            "\tt.stock < 1 \n" +
            "\tAND g.added_flag = 1 \n" +
            "\tAND g.del_flag = 0",nativeQuery = true)
    List<Object[]> getShortagesGoodsInfos();



    @Query(value = "SELECT\n" +
            "\tsum( t1.stock )- ( CASE WHEN t2.townstock IS NULL THEN 0 ELSE t2.townstock END ) - ( CASE WHEN t1.lock_stock IS NULL THEN 0 ELSE t1.lock_stock END ) AS stock,\n" +
            "\tt1.goods_info_id \n" +
            "FROM\n" +
            "\t`sbc-goods`.goods_ware_stock t1\n" +
            "\tLEFT JOIN (\n" +
            "\tSELECT\n" +
            "\t\tsum(\n" +
            "\t\tROUND( nt1.num *( CASE WHEN nt1.divisor_flag IS NULL THEN 1 ELSE nt1.divisor_flag END ), 2 )) AS townstock,\n" +
            "\t\tnt1.sku_id \n" +
            "\tFROM\n" +
            "\t\t`sbc-order`.history_town_ship_order nt1 \n" +
            "\tWHERE\n" +
            "\t\tnt1.wms_flag = 0 \n" +
            "\tGROUP BY\n" +
            "\t\tnt1.sku_id \n" +
            "\t) t2 ON t1.goods_info_id = t2.sku_id \n" +
            "\t\n" +
            " inner join goods_info t3 on t1.goods_info_id =t3.goods_info_id\n" +
            "\t\n" +
            "WHERE\n" +
            "\tt1.goods_info_id IN (?1 ) \n" +
            "\tand t1.del_flag = 0 and t1.ware_id = t3.ware_id\n" +
            "GROUP BY\n" +
            "\tt1.goods_info_id",nativeQuery = true)
    List<Object> getskusstock(List<String> skuids);



    @Query(value = "SELECT\n" +
            "\tsum( t1.stock )- ( CASE WHEN t2.townstock IS NULL THEN 0 ELSE t2.townstock END ) AS stock,\n" +
            "\tt1.goods_info_id \n" +
            "FROM\n" +
            "\t`sbc-goods`.goods_ware_stock t1\n" +
            "\tLEFT JOIN (\n" +
            "\tSELECT\n" +
            "\t\tsum(\n" +
            "\t\tROUND( nt1.num *( CASE WHEN nt1.divisor_flag IS NULL THEN 1 ELSE nt1.divisor_flag END ), 2 )) AS townstock,\n" +
            "\t\tnt1.sku_id \n" +
            "\tFROM\n" +
            "\t\t`sbc-order`.history_town_ship_order nt1 \n" +
            "\tWHERE\n" +
            "\t\tnt1.wms_flag = 0 \n" +
            "\tGROUP BY\n" +
            "\t\tnt1.sku_id \n" +
            "\t) t2 ON t1.goods_info_id = t2.sku_id \n" +
            "\t\n" +
            " inner join `sbc-goods`.retail_goods_info t3 on t1.goods_info_id =t3.goods_info_id\n" +
            "\t\n" +
            "WHERE\n" +
            "\tt1.goods_info_id IN (?1) \n" +
            "\tand t1.del_flag = 0 and t1.ware_id = t3.ware_id\n" +
            "GROUP BY\n" +
            "\tt1.goods_info_id",nativeQuery = true)
    List<Object> getskusstockbylingshou(List<String> skuids);



    @Query(value = "\t\tSELECT\n" +
            "\t\tsum(\n" +
            "\t\tROUND( nt1.num *( CASE WHEN nt1.divisor_flag IS NULL THEN 1 ELSE nt1.divisor_flag END ), 2 )) AS stock,\n" +
            "\t\tnt1.sku_id as goods_info_id \n" +
            "\tFROM\n" +
            "\t\t`sbc-order`.history_town_ship_order nt1 \n" +
            "\tWHERE\n" +
            "\t\tnt1.wms_flag = 0  and nt1.sku_id in (?1)\n" +
            "\tGROUP BY\n" +
            "\t\tnt1.sku_id ",nativeQuery = true)
    List<Object> getskusJiYastock(List<String> skuids);

    @Modifying
    @Query("update GoodsWareStock set lock_stock = IFNULL(lock_stock,0)+ ?1, updateTime=now() where goodsInfoId = ?2 and wareId = ?3 and stock - IFNULL(lock_stock,0) -?1 >=0")
    int lockStockByWareIdAndGoodsInfoId(BigDecimal stock, String goodsInfoId, Long wareId);

    @Modifying
    @Query("update GoodsWareStock set lock_stock = lock_stock- ?1, updateTime=now() where goodsInfoId = ?2 and wareId = ?3 and lock_stock- ?1 >= 0")
    int unlockStockByWareIdAndGoodsInfoId(BigDecimal stock, String goodsInfoId, Long wareId);
}
