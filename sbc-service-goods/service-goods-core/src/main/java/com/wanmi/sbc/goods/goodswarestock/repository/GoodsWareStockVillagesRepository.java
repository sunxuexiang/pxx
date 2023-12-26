package com.wanmi.sbc.goods.goodswarestock.repository;

import com.wanmi.sbc.goods.bidding.model.root.Bidding;
import com.wanmi.sbc.goods.goodswarestock.model.root.GoodsWareStock;
import com.wanmi.sbc.goods.goodswarestock.model.root.GoodsWareStockVillages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: 乡镇件分仓库存DAO
 * @Author: XinJiang
 * @Date: 2022/4/27 10:16
 */
@Repository
public interface GoodsWareStockVillagesRepository extends JpaRepository<GoodsWareStockVillages,Long>, JpaSpecificationExecutor<GoodsWareStockVillages> {


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
            " goods_ware_stock_villages gws" +
            " WHERE gws.del_flag = 0 and gws.goods_info_id in ?2 and gws.ware_id = ?1", nativeQuery = true)
    List<Object> getGoodsStockByAreaIdAndGoodsInfoIds(Long wareId, List<String> goodsInfoIds);

    /**
     * 根据skuId获取乡镇件库存信息
     * @param goodsInfoIds
     * @return
     */
    List<GoodsWareStockVillages> findByGoodsInfoIdIn(List<String> goodsInfoIds);

    /**
     * 批量增加库存
     * @param stock
     * @param goodsInfoId
     * @param wareId
     * @return
     */
    @Modifying
    @Query("update GoodsWareStockVillages set stock = stock+ ?1, updateTime=now() where goodsInfoId in ?2 and wareId = ?3")
    int addStockByWareIdAndGoodsInfoId(BigDecimal stock, String goodsInfoId, Long wareId);

    /**
     * 批量扣减库存
     * @param stock
     * @param goodsInfoId
     * @param wareId
     * @return
     */
    @Modifying
    @Query("update GoodsWareStockVillages set stock = stock- ?1, updateTime=now() where stock >= ?1 and goodsInfoId in ?2 and wareId = ?3")
    int subStockByWareIdAndGoodsInfoId(BigDecimal stock, String goodsInfoId, Long wareId);
}
