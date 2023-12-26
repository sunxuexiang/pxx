package com.wanmi.sbc.order.inventorydetailsamounttrade.repository;

import com.wanmi.sbc.order.inventorydetailsamounttrade.model.root.InventoryDetailSamountTrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
public interface InventoryDetailSamountTradeRepository extends JpaRepository<InventoryDetailSamountTrade, Long>, JpaSpecificationExecutor<InventoryDetailSamountTrade> {

    /**
     * 获取所有的订单商品价格信息
     *
     * @param tradeId
     * @return
     */
    List<InventoryDetailSamountTrade> findByTradeId(String tradeId);

    /***
     * @desc  改价删除数据
     * @author shiy  2023/8/2 11:17
    */
     void deleteByTradeId(String tradeId);

    /**
     * 获取没退货退款的商品
     *
     * @param goodsInfoId
     * @param tid
     * @return
     */
    @Query(value = "SELECT * from inventory_details_amount_trade c  where c.goods_info_id = ?1 and c.trade_id = ?2 and c.return_flag = ?3", nativeQuery = true)
    List<InventoryDetailSamountTrade> getBytidAndGoodsInfoId(String goodsInfoId, String tid, int returnFlag);


    /**
     * 设置商品成未退款状态
     *
     * @param returnId
     * @return
     */

    @Transactional
    @Modifying
    @Query(value = "update inventory_details_amount_trade set return_flag = 0 where return_id = ?1 and return_flag = 1", nativeQuery = true)
    int updateInventory(String returnId);

    /**
     * 获取商品批量(通过退单id)
     *
     * @param returnId
     * @return
     */
    @Query(value = "SELECT * from inventory_details_amount_trade c  where c.return_id = ?1", nativeQuery = true)
    List<InventoryDetailSamountTrade> getByReturnId(String returnId);

    /**
     * 获取商品批量
     *
     * @param tradeId
     * @return
     */
    @Query(value = "SELECT * from inventory_details_amount_trade c  where c.trade_id = ?1 and c.return_flag = ?2", nativeQuery = true)
    List<InventoryDetailSamountTrade> getInventoryByOId(String tradeId, int returnFlag);


    /**
     * 批量获取商品批量
     *
     * @param goodsInfoIds
     * @param tid
     * @return
     */
    @Query(value = "SELECT * from inventory_details_amount_trade c  where c.goods_info_id in (?1) and c.trade_id = ?2 and c.return_flag = ?3", nativeQuery = true)
    List<InventoryDetailSamountTrade> getBytidAndGoodsInfoIds(List<String> goodsInfoIds, String tid, int returnFlag);

}
